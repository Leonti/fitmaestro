package com.leonti.fitmaestro;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import com.leonti.fitmaestro.R;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class ByExerciseList extends ListActivity {

	private ExcercisesDbAdapter mDbHelper;
	private Cursor mExercisesForDayCursor;
	DateFormat iso8601Format;
	String TAG = "ByExerciseList";

	private static final int ACTIVITY_SELECT_EX = 0;
	private static final int ACTIVITY_ADDED = 1;
	private static final int INSERT_ID = Menu.FIRST;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.by_exercise_list);

		mDbHelper = new ExcercisesDbAdapter(this);
		mDbHelper.open();
		iso8601Format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		iso8601Format.setTimeZone(TimeZone.getTimeZone("UTC"));
		fillData();
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

		mExercisesForDayCursor = mDbHelper.fetchExercisesForDates(begin, end);
		startManagingCursor(mExercisesForDayCursor);
		String[] from = new String[] { ExcercisesDbAdapter.KEY_TITLE };
		int[] to = new int[] { R.id.exercise_name };
		SimpleCursorAdapter excercises = new SimpleCursorAdapter(this,
				R.layout.by_exercise_list_row, mExercisesForDayCursor, from, to);
		setListAdapter(excercises);
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
			addExercise();
			return true;
		}

		return super.onMenuItemSelected(featureId, item);
	}

	private void addExercise() {
		Intent i = new Intent(this, ExercisesList.class);
		startActivityForResult(i, ACTIVITY_SELECT_EX);
	}

	private void addLogEntry(Long ExerciseId) {
		Intent i = new Intent(this, AddLogEntry.class);
		i.putExtra(ExcercisesDbAdapter.KEY_EXERCISEID, ExerciseId);
		startActivityForResult(i, ACTIVITY_ADDED);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);

		switch (requestCode) {
		case ACTIVITY_SELECT_EX:
			if (resultCode == RESULT_OK) {
				Bundle extras = intent.getExtras();
				Long ExerciseId = extras != null ? extras
						.getLong(ExcercisesDbAdapter.KEY_EXERCISEID) : null;
				// fillData();
				Log
						.i("EXERCISE ID FROM ACTIVITY: ", String
								.valueOf(ExerciseId));
				Log.i("DYG", "Now we should add entry!");
				addLogEntry(ExerciseId);
			}
			break;

		case ACTIVITY_ADDED:
			if (resultCode == RESULT_OK) {
				fillData();
				Log.i("DYG", "eptel");
			}
			break;
		}
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		mExercisesForDayCursor.moveToPosition(position);
		int ExIdIndex = mExercisesForDayCursor
				.getColumnIndexOrThrow(ExcercisesDbAdapter.KEY_EXERCISEID);
		Intent i = new Intent(this, LogEntries.class);
		i.putExtra(ExcercisesDbAdapter.KEY_ROWID, mExercisesForDayCursor
				.getLong(ExIdIndex));
		startActivity(i);
		/*
		 * Intent i = new Intent(this, SetView.class);
		 * i.putExtra(ExcercisesDbAdapter.KEY_ROWID, id);
		 * startActivityForResult(i, 5);
		 */
		// SHOW ADDITIONAL INFO FOR THIS LOG ENTRY
	}

}
