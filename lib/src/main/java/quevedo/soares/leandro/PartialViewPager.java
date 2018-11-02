package quevedo.soares.leandro;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.EdgeEffect;
import android.widget.Scroller;

import java.util.ArrayList;

public class PartialViewPager extends ViewGroup {

	//<editor-fold defaultstate="collapsed" desc="Scroll states">
	/**
	 * Indicates that the pager is in an idle, settled state. The current page
	 * is fully in view and no animation is in progress.
	 */
	public static final int SCROLL_STATE_IDLE = 0;
	/**
	 * Indicates that the pager is currently being dragged by the user.
	 */
	public static final int SCROLL_STATE_DRAGGING = 1;
	/**
	 * Indicates that the pager is in the process of settling to a final position.
	 */
	public static final int SCROLL_STATE_SETTLING = 2;
	//</editor-fold>

	private ArrayList<PartialView> partials;
	private PartialViewPagerAdapter adapter;

	private int currentItemPosition;

	private Scroller scroller;
	private int touchPointerId;
	private int scrollOffset;
	private float lastMotionX;
	private int touchSlop;

	private EdgeEffect leftEdgeEffect;
	private EdgeEffect rightEdgeEffect;

	private int scrollState = SCROLL_STATE_IDLE;

	//<editor-fold defaultstate="collapsed" desc="Constructors">
	public PartialViewPager (@NonNull Context context) {
		super (context);

		init ();
	}

	public PartialViewPager (@NonNull Context context, @Nullable AttributeSet attrs) {
		super (context, attrs);

		init ();
	}

	public PartialViewPager (@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super (context, attrs, defStyleAttr);

		init ();
	}

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	public PartialViewPager (@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super (context, attrs, defStyleAttr, defStyleRes);

		init ();
	}
	//</editor-fold>

	private void init () {
		// Debug
		setBackgroundColor (Color.GRAY);

		// Setup the scrolling variables
		leftEdgeEffect = new EdgeEffect (getContext ());
		rightEdgeEffect = new EdgeEffect (getContext ());
		scroller = new Scroller (getContext ());
		ViewConfiguration configuration = ViewConfiguration.get (getContext ());
		touchSlop = configuration.getScaledTouchSlop ();

		// We will use the method onDraw, so flag it
		setWillNotDraw (false);
		// Wee need focusability
		setFocusable (true);
		// Scroll is important, but the partials are more
		setDescendantFocusability (FOCUS_AFTER_DESCENDANTS);
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

		forceLayout ();
	}

	public int getPartialCount () {
		// Return the partial count
		return partials == null ? 0 : partials.size ();
	}

	//<editor-fold defaultstate="collapsed" desc="Scrolling behaviour">
	@Override
	public boolean onInterceptTouchEvent (MotionEvent ev) {
		// Only intercept if we are dragging or settling
		if (scrollState != SCROLL_STATE_IDLE) return false;

		return super.onInterceptTouchEvent (ev);
	}

	@Override
	public boolean onTouchEvent (MotionEvent event) {
		if (event.getAction () == MotionEvent.ACTION_DOWN && event.getEdgeFlags () != 0) {
			// Don't handle edge touches immediately -- they may actually belong to one of our descendants.
			return false;
		}

		switch (event.getAction () & MotionEventCompat.ACTION_MASK) {
			case MotionEvent.ACTION_DOWN:
				Log.d ("partial", "ACTION_DOWN");

				// We are now dragging
				scrollState = SCROLL_STATE_DRAGGING;

				// Save the touch pointer, aka finger number
				int pointerIndex = ((event.getAction () & MotionEvent.ACTION_POINTER_ID_MASK) >> MotionEvent.ACTION_POINTER_ID_SHIFT);
				touchPointerId = event.getPointerId (pointerIndex);

				// Ignore previous scrolling animations
				scroller.abortAnimation ();

				// Save the motion event
				lastMotionX = event.getX ();
				break;
			case MotionEvent.ACTION_MOVE:
				Log.d ("partial", "ACTION_MOVE");

				if (scrollState == SCROLL_STATE_IDLE) {
					// If we did drag te minimum value and only on horizontal axis
					if (event.getX (touchPointerId) > touchSlop && event.getX (touchPointerId) > event.getY (touchPointerId)) {
						scrollState = SCROLL_STATE_DRAGGING;
					}
				}

				// If we are now dragging
				if (scrollState == SCROLL_STATE_DRAGGING) {
					performDrag (event.getX (touchPointerId));
				}
				break;
			case MotionEvent.ACTION_UP:
				if (scrollState == SCROLL_STATE_DRAGGING) {
					Log.d ("partial", "ACTION_UP");

					// Save the motion event
					lastMotionX = event.getX (touchPointerId);

					endDrag ();

					leftEdgeEffect.onRelease ();
					rightEdgeEffect.onRelease ();
				}

				break;
			case MotionEvent.ACTION_CANCEL:
				if (scrollState == SCROLL_STATE_DRAGGING) {
					Log.d ("partial", "ACTION_CANCEL");

					lastMotionX = event.getX (touchPointerId);

					// Scroll to the current page, ignore scrolling
					int currentPageX = this.currentItemPosition * this.getMeasuredWidth ();
					scroller.startScroll (getScrollX (), 0, currentPageX - getScrollX (), 0);
					ViewCompat.postInvalidateOnAnimation (this);

					scrollState = SCROLL_STATE_SETTLING;

					leftEdgeEffect.onRelease ();
					rightEdgeEffect.onRelease ();
				}
				break;
		}

		return true;
	}

	private void performDrag (float x) {
		// Calculates the delta between the current event and the last saved one
		float delta = this.lastMotionX - x;
		this.lastMotionX = x;

		// Increment the scroll offset by the amount of pixels dragged
		this.scrollOffset += delta;

		// Define our screen bounds, just to sugar syntax
		int leftBound = 0;
		int rightBound = Math.max (0, partials.size () - 1) * getMeasuredWidth ();

		// Clamp the scrollOffset to doesn't overscroll
		if (this.scrollOffset < leftBound) {
			this.scrollOffset = leftBound;

			leftEdgeEffect.onPull (Math.abs (leftBound - this.scrollOffset) / getMeasuredWidth ());
		} else if (this.scrollOffset > rightBound) {
			this.scrollOffset = rightBound;

			rightEdgeEffect.onPull (Math.abs (this.scrollOffset - rightBound) / getMeasuredWidth ());
		}

		// Scroll to the specified offset
		setScrollX (this.scrollOffset);

		// Invalidate
		ViewCompat.postInvalidateOnAnimation (this);
	}

	private void endDrag () {
		// Calculate the current page
		int currentPageX = this.currentItemPosition * this.getMeasuredWidth ();
		int targetPage;


		if (scrollOffset >= currentPageX + this.getMeasuredWidth () / 2) {
			// If the scrolling is more on right side
			targetPage = this.currentItemPosition + 1;
		} else if (scrollOffset <= currentPageX - this.getMeasuredWidth () / 2) {
			// If the scrolling is more on left side
			targetPage = this.currentItemPosition - 1;
		} else {
			// Maintain the same page
			targetPage = this.currentItemPosition;
		}

		// Clamp the page values
		if (targetPage < 0) targetPage = 0;
		else if (targetPage >= this.partials.size ()) targetPage = this.partials.size () - 1;

		// Calculate the page ScollX coordinates
		int pageX = (targetPage) * this.getMeasuredWidth ();
		int diff = pageX - scrollOffset;

		// Set the new current page
		this.currentItemPosition = targetPage;

		Log.d ("partial", "diff: " + diff);

		// Start the scrolling
		scroller.forceFinished (true);
		scroller.startScroll (Math.round (scrollOffset), 0, diff, 0);

		// We need to invalidate
		ViewCompat.postInvalidateOnAnimation (PartialViewPager.this);


		// We are no more dragging at this time, set it to false
		scrollState = SCROLL_STATE_SETTLING;
	}

	@Override
	public void computeScroll () {
		super.computeScroll ();

		if (!scroller.isFinished () && scroller.computeScrollOffset ()) {
			this.scrollOffset = scroller.getCurrX ();
			setScrollX (this.scrollOffset);

			// We need to invalidate
			ViewCompat.postInvalidateOnAnimation (PartialViewPager.this);
		}

		// If we are finishing the fling
		if (scrollState == SCROLL_STATE_SETTLING) {
			// Ignore possible updates
			scroller.abortAnimation ();

			int oldX = getScrollX ();
			int x = scroller.getCurrX ();

			// Check if we need to move
			if (oldX != x) {
				scrollOffset = x;
				smoothScrollTo (x);
			} else {
				// Otherwise flag the scrolling as finished
				scrollState = SCROLL_STATE_IDLE;
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

	public void setCurrentItemPosition (int newPosition) {
		if (newPosition < 0 || newPosition >= partials.size ()) throw new IndexOutOfBoundsException ("The specified index doesn't exists.");

		// Calculate the new page coordinates
		int pageX = newPosition * getMeasuredWidth ();
		// Go to the new page
		smoothScrollTo (pageX);
		// Save the new current position
		this.currentItemPosition = newPosition;
	}
	//</editor-fold>


	@Override
	public void draw (Canvas canvas) {
		super.draw (canvas);

		leftEdgeEffect.draw (canvas);
		rightEdgeEffect.draw (canvas);
	}

	@Override
	protected void onLayout (boolean changed, int i1, int i2, int i3, int i4) {
		int currentLeft = this.getPaddingLeft () - scrollOffset;

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
