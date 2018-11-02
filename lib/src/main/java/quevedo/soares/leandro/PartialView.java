package quevedo.soares.leandro;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

public abstract class PartialView extends FrameLayout {

	private Activity parentActivity;
	private View rootView;

	//<editor-fold desc="Constructors" defaultstate="collapsed">
	public PartialView (Context context) {
		super (context);
		initialize (context);
	}

	private void initialize (Context context) {
		// For drawing on preview
		if (this.isInEditMode ()) {
			drawInEditMode (context);
		} else {
			new Handler ().postAtFrontOfQueue (() -> setupViews (context));
		}
	}

	private void drawInEditMode (Context context) {
		// Calls the method to create the view
		this.rootView = this.onCreateView (LayoutInflater.from (context));
		if (this.rootView == null) {
			throw new NullPointerException ("View cannot be null.");
		}

		this.addView (rootView);
	}

	private void setupViews (Context context) {
		// Calls the method to create the view
		this.rootView = this.onCreateView (LayoutInflater.from (context));
		if (this.rootView == null) {
			throw new NullPointerException ("View cannot be null.");
		}


		// Finds the parent activity of this view
		this.parentActivity = getParentActivity ();

		// Inflate the layout
		this.addView (rootView);

		// Calls the event handlers
		this.onCreate ();

		// Wait and call onStart
		post (this::onStart);
	}

	//</editor-fold>

	public abstract View onCreateView (LayoutInflater inflater);

	/**
	 * @return The specified partial's parent Activity
	 */
	public Activity getParentActivity () {
		if (this.parentActivity != null) {
			return this.parentActivity;
		}

		Context context = getContext ();
		while (context instanceof ContextWrapper) {
			if (context instanceof Activity) {
				return (Activity) context;
			}
			context = ((ContextWrapper) context).getBaseContext ();
		}

		return null;
	}

	//<editor-fold desc="Abastract event handlers" defaultstate="collapsed">
	public void onCreate () {
	}

	public PartialView (Context context, @Nullable AttributeSet attrs) {
		super (context, attrs);
		initialize (context);
	}

	public PartialView (Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super (context, attrs, defStyleAttr);
		initialize (context);
	}

	@TargetApi (Build.VERSION_CODES.LOLLIPOP)
	public PartialView (Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super (context, attrs, defStyleAttr, defStyleRes);
		initialize (context);
	}

	@Override
	protected void onDetachedFromWindow () {
		super.onDetachedFromWindow ();
		// Calls the onDestroy event handler
		this.onDestroy ();
	}

	public void onDestroy () {
	}

	public void onStart () {
	}
	//</editor-fold>

}
