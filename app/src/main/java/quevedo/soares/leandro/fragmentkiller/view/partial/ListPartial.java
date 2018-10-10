package quevedo.soares.leandro.fragmentkiller.view.partial;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

import quevedo.soares.leandro.PartialView;
import quevedo.soares.leandro.fragmentkiller.R;

public class ListPartial extends PartialView {

	private ListView rvItems;

	//<editor-fold desc="Constructors" defaultstate="collapsed">
	public ListPartial (Context context, @Nullable AttributeSet attrs) throws Exception {
		super (context, attrs);
	}

	public ListPartial (Context context, @Nullable AttributeSet attrs, int defStyleAttr) throws Exception {
		super (context, attrs, defStyleAttr);
	}

	public ListPartial (Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) throws Exception {
		super (context, attrs, defStyleAttr, defStyleRes);
	}
	//</editor-fold>


	@Override
	public View onCreateView (LayoutInflater inflater) {
		View view = inflater.inflate (R.layout.partial_list, null);

		rvItems = view.findViewById (R.id.rvItems);

		return view;
	}

	public void updateList (List<String> stringList) {
		rvItems.setAdapter (new ArrayAdapter<> (getContext (), android.R.layout.simple_list_item_1, stringList));
	}
}
