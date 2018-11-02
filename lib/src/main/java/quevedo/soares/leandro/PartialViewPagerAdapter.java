package quevedo.soares.leandro;

import java.util.ArrayList;

public abstract class PartialViewPagerAdapter {

	public abstract PartialView onCreatePartial (int position);

	public abstract int getPartialCount ();

	public void onShowPartial (int position) {}

	public void onHidePartial (int position) {}

}
