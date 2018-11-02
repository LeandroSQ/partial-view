package quevedo.soares.leandro.fragmentkiller.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import quevedo.soares.leandro.fragmentkiller.R;

public class MainActivity extends AppCompatActivity {

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate (savedInstanceState);
		setContentView (R.layout.activity_main);

	}

	public void openRegisterAndListing (View view) {
		startActivity (new Intent (MainActivity.this, RegisterAndListingActvity.class));
	}

	public void openMultipleSteps (View view) {
		startActivity (new Intent (MainActivity.this, MultipleStepsActivity.class));
	}

	public void openViewPager (View view) {
		startActivity (new Intent (MainActivity.this, PartialViewPagerActivity.class));
	}

}
