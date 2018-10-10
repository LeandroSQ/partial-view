package quevedo.soares.leandro.fragmentkiller.view.partial;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import quevedo.soares.leandro.PartialView;
import quevedo.soares.leandro.fragmentkiller.R;

public class StepPartial extends PartialView {

	private ImageView imageView;
	private TextView textView;

	@DrawableRes
	private int drawableResource;
	private String textString;

	//<editor-fold desc="Constructors" defaultstate="collapsed">
	public StepPartial (Context context, @DrawableRes int drawableResource, String textString) throws Exception {
		super (context);
		this.drawableResource = drawableResource;
		this.textString = textString;
	}
	//</editor-fold>

	@Override
	public View onCreateView (LayoutInflater inflater) {
		View view = inflater.inflate (R.layout.partial_step, null);

		this.imageView = view.findViewById (R.id.imageView);
		this.textView = view.findViewById (R.id.textView);

		this.imageView.setImageResource (drawableResource);
		this.textView.setText (textString);

		return view;
	}
}
