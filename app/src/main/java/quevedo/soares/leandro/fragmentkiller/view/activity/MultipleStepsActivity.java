package quevedo.soares.leandro.fragmentkiller.view.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ViewFlipper;

import java.util.ArrayList;

import quevedo.soares.leandro.PartialView;
import quevedo.soares.leandro.fragmentkiller.R;
import quevedo.soares.leandro.fragmentkiller.view.partial.StepPartial;

public class MultipleStepsActivity extends AppCompatActivity {

	private TextView tvSteps;
	private ViewFlipper viewFlipper;
	private Button btnBack;
	private Button btnNext;

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate (savedInstanceState);
		setContentView (R.layout.activity_multiple_steps);

		this.loadComponents ();
		this.loadSteps ();
	}

	private void loadComponents () {
		this.tvSteps = findViewById (R.id.tvSteps);
		this.viewFlipper = findViewById (R.id.viewFlipper);
		this.btnBack = findViewById (R.id.btnBack);
		this.btnNext = findViewById (R.id.btnNext);

		this.btnBack.setOnClickListener ((view) -> this.previousStep ());
		this.btnNext.setOnClickListener ((view) -> this.nextStep ());
	}

	private void loadSteps () {
		try {
			ArrayList<PartialView> steps = new ArrayList<PartialView> () {{
				add (new StepPartial (MultipleStepsActivity.this, R.drawable.ic_1, "Passo 1"));
				add (new StepPartial (MultipleStepsActivity.this, R.drawable.ic_2, "Olha só o passo 2"));
				add (new StepPartial (MultipleStepsActivity.this, R.drawable.ic_3, "Imagina esse passo 3"));
			}};

			for (PartialView step : steps) {
				this.viewFlipper.addView (step);
			}

			this.updateLabel ();
		} catch (Exception e) {
			e.printStackTrace ();
		}
	}

	private void previousStep () {
		if (this.viewFlipper.isFlipping ()) {
			return;
		}

		if (this.viewFlipper.getDisplayedChild () > 0) {
			this.viewFlipper.setInAnimation (this, R.anim.slide_right_in);
			this.viewFlipper.setOutAnimation (this, R.anim.slide_right_out);

			this.viewFlipper.showPrevious ();
			this.updateLabel ();
		}
	}

	private void nextStep () {
		if (this.viewFlipper.isFlipping ()) {
			return;
		}

		if (this.viewFlipper.getDisplayedChild () < this.viewFlipper.getChildCount () - 1) {
			this.viewFlipper.setInAnimation (this, R.anim.slide_left_in);
			this.viewFlipper.setOutAnimation (this, R.anim.slide_left_out);

			this.viewFlipper.showNext ();
			this.updateLabel ();
		}
	}

	private void updateLabel () {
		tvSteps.setText (String.format ("%d/%d", this.viewFlipper.getDisplayedChild () + 1, this.viewFlipper.getChildCount ()));
	}

}
