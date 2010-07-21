package com.leonti.fitmaestro;

import com.leonti.fitmaestro.R;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class WorkoutsList extends ListActivity {

	private static final int ACTIVITY_CREATE = 0;
	private static final int ACTIVITY_EDIT = 1;
	private static final int INSERT_ID = Menu.FIRST;
	private static final int DELETE_ID = Menu.FIRST + 1;
	private static final int EDIT_ID = Menu.FIRST + 2;

	private ExcercisesDbAdapter mDbHelper;
	private Cursor mSetsCursor;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.set_list);

		mDbHelper = new ExcercisesDbAdapter(this);
		mDbHelper.open();
		fillData();
		registerForContextMenu(getListView());
	}

	private void fillData() {
		mSetsCursor = mDbHelper.fetchFreeSets();
		startManagingCursor(mSetsCursor);
		String[] from = new String[] { ExcercisesDbAdapter.KEY_TITLE };
		int[] to = new int[] { R.id.set_name };
		SimpleCursorAdapter sets = new SimpleCursorAdapter(this,
				R.layout.set_list_row, mSetsCursor, from, to);
		setListAdapter(sets);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuItem insert = menu.add(0, INSERT_ID, 0, R.string.add_set);
		insert.setIcon(android.R.drawable.ic_menu_add);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case INSERT_ID:
			createSet();
			return true;
		}

		return super.onMenuItemSelected(featureId, item);
	}

	private void createSet() {
		Intent i = new Intent(this, WorkoutEdit.class);
		startActivityForResult(i, ACTIVITY_CREATE);
	}

	private void editSet(long id) {
		Intent i = new Intent(this, WorkoutEdit.class);
		i.putExtra(ExcercisesDbAdapter.KEY_ROWID, id);
		startActivityForResult(i, ACTIVITY_EDIT);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		fillData();

		switch (requestCode) {
		case ACTIVITY_EDIT:
			Toast.makeText(this, R.string.set_edited, Toast.LENGTH_SHORT)
					.show();
			break;
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View view,
			ContextMenuInfo menuInfo) {
		AdapterView.AdapterContextMenuInfo info;
		info = (AdapterView.AdapterContextMenuInfo) menuInfo;

		String title = ((TextView) info.targetView).getText().toString();

		menu.setHeaderTitle(title);
		menu.add(0, EDIT_ID, 0, R.string.edit_set);
		menu.add(0, DELETE_ID, 1, R.string.delete_set);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();

		switch (item.getItemId()) {
		case DELETE_ID:
			mDbHelper.deleteSet(info.id);
			fillData();
			return true;

		case EDIT_ID:
			editSet(info.id);
			return true;
		}
		return super.onContextItemSelected(item);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Intent i = new Intent(this, WorkoutView.class);
		i.putExtra(ExcercisesDbAdapter.KEY_ROWID, id);
		startActivityForResult(i, 5);
	}

}
