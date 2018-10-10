package quevedo.soares.leandro.fragmentkiller.view.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;

import quevedo.soares.leandro.fragmentkiller.R;
import quevedo.soares.leandro.fragmentkiller.view.partial.ListPartial;
import quevedo.soares.leandro.fragmentkiller.view.partial.RegisterPartial;

public class RegisterAndListingActvity extends AppCompatActivity {

	private RegisterPartial registerPartial;
	private ListPartial listPartial;

	private ArrayList<String> stringList = new ArrayList<> ();

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate (savedInstanceState);
		setContentView (R.layout.activity_register_and_listing_actvity);

		this.registerPartial = findViewById (R.id.registerPartial);
		this.listPartial = findViewById (R.id.listPartial);
	}

	public void notifyUpdate (String newItem) {
		this.stringList.add (newItem);
		this.listPartial.updateList (stringList);
	}
}
