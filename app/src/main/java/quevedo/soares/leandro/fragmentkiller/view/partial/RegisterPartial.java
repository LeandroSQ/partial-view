package quevedo.soares.leandro.fragmentkiller.view.partial;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import quevedo.soares.leandro.PartialView;
import quevedo.soares.leandro.fragmentkiller.R;
import quevedo.soares.leandro.fragmentkiller.view.activity.RegisterAndListingActvity;

public class RegisterPartial extends PartialView {

	private EditText etText;
	private Button btnRegister;

	//<editor-fold desc="Constructors" defaultstate="collapsed">
	public RegisterPartial (Context context, @Nullable AttributeSet attrs) throws Exception {
		super (context, attrs);
	}

	public RegisterPartial (Context context, @Nullable AttributeSet attrs, int defStyleAttr) throws Exception {
		super (context, attrs, defStyleAttr);
	}

	public RegisterPartial (Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) throws Exception {
		super (context, attrs, defStyleAttr, defStyleRes);
	}
	//</editor-fold>

	@Override
	public View onCreateView (LayoutInflater inflater) {
		View view = inflater.inflate (R.layout.partial_register, null);

		etText = view.findViewById (R.id.etText);
		btnRegister = view.findViewById (R.id.btnRegister);

		btnRegister.setOnClickListener (v -> {
			((RegisterAndListingActvity) getParentActivity ()).notifyUpdate (etText.getText ().toString ());
			etText.setText ("");
		});

		return view;
	}

}
