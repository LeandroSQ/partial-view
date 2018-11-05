package quevedo.soares.leandro;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Scroller;

import java.util.ArrayList;

public class PartialStepView extends ViewGroup {

	//<editor-fold defaultstate="collapsed" desc="Scroll states">
	/**
	 * Indicates that the stepper is in an idle, settled state. The current page
	 * is fully in view and no animation is in progress.
	 */
	static final int SCROLL_STATE_IDLE = 0;
	/**
	 * Indicates that the stepper is in the process of settling to a final position.
	 */
	static final int SCROLL_STATE_SETTLING = 2;
	//</editor-fold>

	private ArrayList<PartialView> partials;
	private PartialViewPagerAdapter adapter;
	private StepIndicator stepIndicator;

	private int currentItemPosition;

	private Scroller scroller;
	private int scrollOffset;
	private int touchSlop;
	private int scrollState = SCROLL_STATE_IDLE;
	private boolean notifyEvents;

	//<editor-fold defaultstate="collapsed" desc="Constructors">
	public PartialStepView (Context context) {
		super (context);

		init ();
	}

	public PartialStepView (Context context, AttributeSet attrs) {
		super (context, attrs);

		init ();
	}

	public PartialStepView (Context context, AttributeSet attrs, int defStyleAttr) {
		super (context, attrs, defStyleAttr);

		init ();
	}

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	public PartialStepView (Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super (context, attrs, defStyleAttr, defStyleRes);

		init ();
	}
	//</editor-fold>

	private void init () {
		// Setup the scrolling variables
		scroller = new Scroller (getContext (), new DecelerateInterpolator ());

		ViewConfiguration configuration = ViewConfiguration.get (getContext ());
		touchSlop = configuration.getScaledTouchSlop ();

		// Wee need focusability
		setFocusable (true);
		// Scroll is important, but the partials are more
		setDescendantFocusability (FOCUS_AFTER_DESCENDANTS);
	}

	public void setStepIndicator (StepIndicator indicator) {
		this.stepIndicator = indicator;
	}

	public void setAdapter (PartialViewPagerAdapter adapter) {
		this.adapter = adapter;
		this.notifyDataSetChanged ();
	}

	public void notifyDataSetChanged () {
		this.partials = new ArrayList<> ();
		for (int i = 0; i < adapter.getPartialCount (); i++) {
			// Create the partial
			PartialView partial = adapter.onCreatePartial (i);

			// Add to the partial list
			this.partials.add (partial);
			this.addView (partial);
		}

		// Set the current item to first item on list
		this.currentItemPosition = 0;

		// Notify StepIndicator
		if (stepIndicator != null) {
			stepIndicator.onPageChange (-1, this.currentItemPosition);
		}


		forceLayout ();
	}

	public int getPartialCount () {
		// Return the partial count
		return partials == null ? 0 : partials.size ();
	}

	//<editor-fold defaultstate="collapsed" desc="Scrolling behaviour">
	@Override
	public void computeScroll () {
		super.computeScroll ();

		if (!scroller.isFinished () && scroller.computeScrollOffset ()) {
			this.scrollOffset = scroller.getCurrX ();
			setScrollX (this.scrollOffset);

			// We need to invalidate
			ViewCompat.postInvalidateOnAnimation (PartialStepView.this);
		}

		// If we are finishing the fling
		if (scrollState == SCROLL_STATE_SETTLING) {
			// Ignore possible updates
			scroller.abortAnimation ();

			int oldX = getScrollX ();
			scrollOffset = scroller.getCurrX ();

			// Check if we need to move
			if (oldX != scrollOffset) {
				smoothScrollTo (scrollOffset);
			} else {
				// Otherwise flag the scrolling as finished
				scrollState = SCROLL_STATE_IDLE;

				if (notifyEvents) {
					// Notifies the adapter
					adapter.onShowView (this.currentItemPosition);
					notifyEvents = false;
				}

				// Disable the cache
				setScrollingCacheEnabled (false);
			}
		}
	}

	private void smoothScrollTo (int x) {
		int sx = getScrollX ();
		int dx = x - sx;

		// If we don't need to scroll
		if (dx == 0) {
			scrollOffset = x;
			scrollTo (x, 0);
			scrollState = SCROLL_STATE_IDLE;

			if (notifyEvents) {
				// Notifies the adapter
				adapter.onShowView (this.currentItemPosition);
				notifyEvents = false;
			}

			// Disable the cache
			setScrollingCacheEnabled (false);
			return;
		}

		// Otherwise, adjust the final distance
		scrollState = SCROLL_STATE_SETTLING;

		final float pageDelta = (float) Math.abs (dx) / (getMeasuredWidth ());
		int duration = (int) ((pageDelta + 1) * 100);

		// 600 is the max duration allowed
		duration = Math.min (duration, 600);
		scroller.startScroll (sx, 0, dx, 0, duration);

		// We need invalidate
		ViewCompat.postInvalidateOnAnimation (this);
	}

	private void setScrollingCacheEnabled (boolean enabled) {
		final int size = getChildCount ();
		for (int i = 0; i < size; ++i) {
			final View child = getChildAt (i);
			if (child.getVisibility () != GONE) {
				child.setDrawingCacheEnabled (enabled);
			}
		}
	}
	//</editor-fold>

	//<editor-fold defaultstate="collapsed" desc="Stepping behaviour">

	/**
	 * Skips to the specified step
	 *
	 * @param position the target step index
	 **/
	public void skipToStep (int position) {
		if (position < 0 || position >= partials.size ())
			throw new IndexOutOfBoundsException ("The specified index doesn't exists.");

		// Set it to send events targeting the adapter
		notifyEvents = true;

		// Calculate the new page coordinates
		int pageX = position * getMeasuredWidth ();
		// Go to the new page
		smoothScrollTo (pageX);

		// Notify StepIndicator
		if (stepIndicator != null) {
			stepIndicator.onPageChange (this.currentItemPosition, position);
		}

		// Save the new current position
		this.currentItemPosition = position;
	}

	/**
	 * Goes to the next step if possible
	 **/
	public void nextStep () {
		skipToStep (this.currentItemPosition + 1);
	}

	/**
	 * Goes back an step if possible
	 **/
	public void previousStep () {
		skipToStep (this.currentItemPosition - 1);
	}

	/**
	 * Skips a specified step amount
	 *
	 * @param amount the step count to skip
	 **/
	public void skipStepForward (int amount) {
		if (amount <= 0)
			throw new IndexOutOfBoundsException ("Cannot skip forward with negative value");

		skipToStep (this.currentItemPosition + amount);
	}

	/**
	 * Skips a specified step amount
	 *
	 * @param amount the step count to skip
	 **/
	public void skipStepBackward (int amount) {
		if (amount >= 0)
			throw new IndexOutOfBoundsException ("Cannot skip backward with positive value");

		skipToStep (this.currentItemPosition + amount);
	}

	//</editor-fold>

	@Override
	protected void onLayout (boolean changed, int i1, int i2, int i3, int i4) {
		int currentLeft = this.getPaddingLeft ();

		setScrollingCacheEnabled (false);

		for (int i = 0; i < this.getPartialCount (); i++) {
			PartialView partial = this.partials.get (i);

			// Ignore invisible partials
			if (partial.getVisibility () == View.GONE) continue;

			int width = this.getMeasuredWidth () - this.getPaddingRight ();
			int height = this.getMeasuredHeight () - this.getPaddingBottom ();

			int left = currentLeft;
			int top = this.getPaddingTop ();
			int right = left + width;
			int bottom = top + height;

			// Set partial's bounding box
			partial.layout (left, top, right, bottom);

			// Translate the next view to right
			currentLeft += width;
		}
	}

	@Override
	protected void onMeasure (int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure (widthMeasureSpec, heightMeasureSpec);

		for (int i = 0; i < this.getPartialCount (); i++) {
			PartialView partial = this.partials.get (i);

			// Ignore invisible partials
			if (partial.getVisibility () == View.GONE) continue;

			partial.measure (widthMeasureSpec, heightMeasureSpec);
		}
	}
}
