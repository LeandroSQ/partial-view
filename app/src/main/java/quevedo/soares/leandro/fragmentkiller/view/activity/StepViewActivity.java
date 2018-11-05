package quevedo.soares.leandro.fragmentkiller.view.activity;

import android.animation.LayoutTransition;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import quevedo.soares.leandro.PartialStepView;
import quevedo.soares.leandro.PartialView;
import quevedo.soares.leandro.PartialViewPagerAdapter;
import quevedo.soares.leandro.StepIndicator;
import quevedo.soares.leandro.fragmentkiller.R;

public class StepViewActivity extends AppCompatActivity {

	private LinearLayout lnSteps;
	private PartialStepView stepView;
	private Button btnBack;
	private Button btnNext;

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate (savedInstanceState);
		setContentView (R.layout.activity_step_view);
		loadComponents ();
	}

	private void loadComponents () {
		lnSteps = findViewById (R.id.lnSteps);
		stepView = findViewById (R.id.stepView);
		btnBack = findViewById (R.id.btnBack);
		btnNext = findViewById (R.id.btnNext);

		initStepView ();

		this.btnBack.setOnClickListener ((view) -> this.previousStep ());
		this.btnNext.setOnClickListener ((view) -> this.nextStep ());
	}

	private void initStepView () {
		LayoutTransition transition = new LayoutTransition ();
		transition.setDuration (500);
		transition.enableTransitionType (LayoutTransition.CHANGING);
		transition.enableTransitionType (LayoutTransition.CHANGE_APPEARING);
		transition.enableTransitionType (LayoutTransition.CHANGE_DISAPPEARING);
		transition.enableTransitionType (LayoutTransition.APPEARING);
		transition.enableTransitionType (LayoutTransition.DISAPPEARING);
		lnSteps.setLayoutTransition (transition);

		PartialViewPagerAdapter adapter = new PartialViewPagerAdapter () {
			private ArrayList<PartialView> partials = new ArrayList<PartialView> () {{
				int[] colors = new int[]{
						Color.CYAN,
						Color.YELLOW,
						Color.RED
				};

				for (int i = 0; i < colors.length; i++) {
					int finalI = i;
					add (new PartialView (StepViewActivity.this) {
						@Override
						public View onCreateView (LayoutInflater inflater) {
							LinearLayout viewGroup = new LinearLayout (getContext ());
							viewGroup.setOrientation (LinearLayout.VERTICAL);
							viewGroup.setBackgroundColor (colors[finalI]);

							TextView textView = new TextView (getContext ());
							textView.setText ("PARTIAL " + finalI);
							textView.setBackgroundColor (getResources ().getColor (R.color.colorAccent));
							viewGroup.addView (textView);

							Button button = new Button (getContext ());
							button.setText ("PARTIAL " + finalI);
							viewGroup.addView (button);

							EditText editText1 = new EditText (getContext ());
							editText1.setHint ("Digite um texto aqui");
							viewGroup.addView (editText1);

							EditText editText2 = new EditText (getContext ());
							editText2.setHint ("Digite um número aqui");
							editText2.setInputType (InputType.TYPE_CLASS_NUMBER);
							viewGroup.addView (editText2);

							return viewGroup;
						}
					});
				}

			}};

			@Override
			public PartialView onCreatePartial (int position) {
				return partials.get (position);
			}

			@Override
			public int getPartialCount () {
				return partials.size ();
			}
		};
		stepView.setAdapter (adapter);

		stepView.setStepIndicator ((oldPosition, newPosition) -> updateStepIndicator (newPosition));

		updateStepIndicator (0);
	}

	private void updateStepIndicator (int position) {
		for (int i = 0; i < lnSteps.getChildCount (); i++) {
			int color;

			// Check if the position is the current step
			if (i == position) {
				color = getResources ().getColor (R.color.colorPrimaryDark);

				lnSteps.getChildAt (i).animate ().translationY (-2f).start ();
			} else {
				color = getResources ().getColor (R.color.colorDisabled);

				lnSteps.getChildAt (i).animate ().translationY (2f).start ();
			}

			// Change background color
			lnSteps.getChildAt (i).setBackgroundColor (color);
		}
	}

	private void nextStep () {
		stepView.nextStep ();
	}

	private void previousStep () {
		stepView.previousStep ();
	}
}
