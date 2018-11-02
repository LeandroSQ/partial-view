package quevedo.soares.leandro.fragmentkiller.view.activity;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import quevedo.soares.leandro.PartialView;
import quevedo.soares.leandro.PartialViewPager;
import quevedo.soares.leandro.PartialViewPagerAdapter;
import quevedo.soares.leandro.fragmentkiller.R;

public class PartialViewPagerActivity extends AppCompatActivity {

	private Button button;
	private PartialViewPager viewPager;

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate (savedInstanceState);
		setContentView (R.layout.activity_partial_view_pager);

		loadComponents ();
	}

	private void loadComponents () {
		button = findViewById (R.id.button);
		viewPager = findViewById (R.id.viewPager);

		PartialViewPagerAdapter adapter = new PartialViewPagerAdapter () {
			private ArrayList<PartialView> partials = new ArrayList<PartialView> () {{
				int[] colors = new int[]{
						Color.CYAN,
						Color.YELLOW,
						Color.RED,
						Color.BLUE
				};

				for (int i = 0; i < colors.length; i++) {
					int finalI = i;
					add (new PartialView (PartialViewPagerActivity.this) {
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
		viewPager.setAdapter (adapter);
	}

	public void onButtonClick (View view) {

	}
}
