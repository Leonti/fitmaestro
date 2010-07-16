package com.leonti.fitmaestro;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import com.leonti.fitmaestro.R;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
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

public class LogEntries extends ListActivity {

	private static final int ACTIVITY_CREATE = 0;
	private static final int ACTIVITY_EDIT = 1;
	private static final int INSERT_ID = Menu.FIRST;
	private static final int DELETE_ID = Menu.FIRST + 1;
	private static final int EDIT_ID = Menu.FIRST + 2;

	private ExcercisesDbAdapter mDbHelper;
	private Cursor mLogCursor;
	private Long mExerciseId;
	DateFormat iso8601Format;
	DateFormat iso8601FormatLocal;
	String TAG = "LogEntries";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.log_entries_list);

		mExerciseId = savedInstanceState != null ? savedInstanceState
				.getLong(ExcercisesDbAdapter.KEY_ROWID) : null;
		if (mExerciseId == null) {
			Bundle extras = getIntent().getExtras();
			mExerciseId = extras != null ? extras
					.getLong(ExcercisesDbAdapter.KEY_ROWID) : null;
		}

		mDbHelper = new ExcercisesDbAdapter(this);
		mDbHelper.open();
		iso8601Format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		iso8601Format.setTimeZone(TimeZone.getTimeZone("UTC"));
		iso8601FormatLocal = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		fillData();
		registerForContextMenu(getListView());
	}

	private void fillData() {

		Calendar beginning = Calendar.getInstance();
		beginning.set(Calendar.HOUR_OF_DAY, 0);
		beginning.set(Calendar.MINUTE, 0);
		beginning.set(Calendar.SECOND, 0);
		Calendar ending = (Calendar) beginning.clone();
		ending.add(Calendar.DAY_OF_YEAR, +1);

		String begin = iso8601Format.format(beginning.getTime());
		String end = iso8601Format.format(ending.getTime());

		mLogCursor = mDbHelper.fetchLogEntriesForExercise(mExerciseId, begin,
				end);
		startManagingCursor(mLogCursor);
		String[] from = new String[] { ExcercisesDbAdapter.KEY_WEIGHT,
				ExcercisesDbAdapter.KEY_TIMES, ExcercisesDbAdapter.KEY_DONE };
		int[] to = new int[] { R.id.log_entry_weight, R.id.log_entry_times,
				R.id.log_entry_time };
		SimpleCursorAdapter daylog = new SimpleCursorAdapter(this,
				R.layout.log_entries_list_row, mLogCursor, from, to);
		daylog.setViewBinder(new MyViewBinder());
		setListAdapter(daylog);
	}

	public class MyViewBinder implements SimpleCursorAdapter.ViewBinder {
		@Override
		public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
			if (columnIndex == cursor
					.getColumnIndex(ExcercisesDbAdapter.KEY_DONE)) {
				TextView timeText = (TextView) view;

				try {
					Date date = iso8601FormatLocal.parse(cursor
							.getString(columnIndex));
					long when = date.getTime();
					int flags = 0;
					flags |= android.text.format.DateUtils.FORMAT_SHOW_TIME;
					String finalDateTime = android.text.format.DateUtils
							.formatDateTime(getBaseContext(), when
									+ TimeZone.getDefault().getOffset(when),
									flags);
					timeText.setText(finalDateTime);
				} catch (ParseException e) {
					Log.e(TAG, "Parsing ISO8601 datetime failed", e);
				}
				return true;
			}
			return false;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, INSERT_ID, 0, R.string.add_entry);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case INSERT_ID:
			createEntry();
			return true;
		}

		return super.onMenuItemSelected(featureId, item);
	}

	private void createEntry() {
		Intent i = new Intent(this, AddLogEntry.class);
		i.putExtra(ExcercisesDbAdapter.KEY_EXERCISEID, mExerciseId);
		startActivityForResult(i, ACTIVITY_CREATE);
	}

	private void editEntry(long id) {
		Intent i = new Intent(this, AddLogEntry.class);
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
			Toast.makeText(this, R.string.log_entry_edited, Toast.LENGTH_SHORT)
					.show();
			break;
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View view,
			ContextMenuInfo menuInfo) {
		AdapterView.AdapterContextMenuInfo info;
		info = (AdapterView.AdapterContextMenuInfo) menuInfo;

		// String title = ((TextView) info.targetView).getText().toString();

		menu.setHeaderTitle("Dyg");
		menu.add(0, EDIT_ID, 0, R.string.edit_log_entry);
		menu.add(0, DELETE_ID, 1, R.string.delete_log_entry);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();

		switch (item.getItemId()) {
		case DELETE_ID:
			mDbHelper.deleteLogEntry(info.id);
			fillData();
			return true;

		case EDIT_ID:
			editEntry(info.id);
			return true;
		}
		return super.onContextItemSelected(item);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		/*
		 * Intent i = new Intent(this, SetView.class);
		 * i.putExtra(ExcercisesDbAdapter.KEY_ROWID, id);
		 * startActivityForResult(i, 5);
		 */
		// SHOW ADDITIONAL INFO FOR THIS LOG ENTRY
	}
}
