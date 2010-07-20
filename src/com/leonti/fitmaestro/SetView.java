package com.leonti.fitmaestro;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.TableRow.LayoutParams;

public class SetView extends ListActivity {

	private ExcercisesDbAdapter mDbHelper;
	private Cursor mExercisesForSetCursor;
	private Long mSetId;
	private Long mProgramsConnectorId;

	private static final int ADD_ID = Menu.FIRST;
	private static final int DELETE_ID = Menu.FIRST + 1;
	private static final int START_SESSION_ID = Menu.FIRST + 2;
	private static final int ACTIVITY_ADD = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setview_list);

		Bundle extras = getIntent().getExtras();
		mSetId = savedInstanceState != null ? savedInstanceState
				.getLong(ExcercisesDbAdapter.KEY_ROWID) : null;
		if (mSetId == null) {
			mSetId = extras != null ? extras
					.getLong(ExcercisesDbAdapter.KEY_ROWID) : null;
		}

		mProgramsConnectorId = savedInstanceState != null ? savedInstanceState
				.getLong("programs_connector_id") : null;

		if (mProgramsConnectorId == null && extras != null) {
			mProgramsConnectorId = extras.getLong("programs_connector_id");
		}

		mDbHelper = new ExcercisesDbAdapter(this);
		mDbHelper.open();
		fillData();
		registerForContextMenu(getListView());
	}

	private void fillData() {
		mExercisesForSetCursor = mDbHelper.fetchExercisesForSet(mSetId);
		startManagingCursor(mExercisesForSetCursor);
		String[] from = new String[] { ExcercisesDbAdapter.KEY_TITLE };
		int[] to = new int[] { R.id.exercise_name };
		SetViewCursorAdapter excercises = new SetViewCursorAdapter(this,
				R.layout.setview_list_row, mExercisesForSetCursor, from, to);
		setListAdapter(excercises);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		Bundle extras = intent.getExtras();
		Long ExerciseId = extras != null ? extras
				.getLong(ExcercisesDbAdapter.KEY_EXERCISEID) : null;
		mDbHelper.addExerciseToSet(mSetId, ExerciseId, 0);
		fillData();
		Log.i("EXERCISE ID FROM ACTIVITY: ", String.valueOf(ExerciseId));
		Log.i("SET ID FROM ACTIVITY: ", String.valueOf(mSetId));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, ADD_ID, 0, R.string.add_exercise_to_set);
		menu.add(0, START_SESSION_ID, 0, R.string.start_session);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case ADD_ID:
			addExercise();
			break;

		case START_SESSION_ID:
			startSession();
			break;
		}

		return super.onMenuItemSelected(featureId, item);
	}

	private void addExercise() {
		Intent i = new Intent(this, ExercisesList.class);
		startActivityForResult(i, ACTIVITY_ADD);
	}

	private void startSession() {

		if (mProgramsConnectorId == null) {
			mProgramsConnectorId = Long.valueOf(0);
		}
		Long sessionId = mDbHelper.createSession("Some session", "Some desc",
				mProgramsConnectorId);

		// add exercises to session
		mExercisesForSetCursor.moveToFirst();
		for (int i = 0; i < mExercisesForSetCursor.getCount(); i++) {

			Long exerciseId = mExercisesForSetCursor
					.getLong(mExercisesForSetCursor
							.getColumnIndexOrThrow(ExcercisesDbAdapter.KEY_EXERCISEID));
			Long setsConnectorId = mExercisesForSetCursor
					.getLong(mExercisesForSetCursor
							.getColumnIndexOrThrow(ExcercisesDbAdapter.KEY_ROWID));

			long sessionsConnectorId = mDbHelper.addExerciseToSession(
					sessionId, exerciseId);

			Cursor repsForConnectorCursor = mDbHelper
					.fetchRepsForConnector(setsConnectorId);

			repsForConnectorCursor.moveToFirst();
			for (int j = 0; j < repsForConnectorCursor.getCount(); j++) {

				Long reps = repsForConnectorCursor
						.getLong(repsForConnectorCursor
								.getColumnIndexOrThrow(ExcercisesDbAdapter.KEY_REPS));

				Float percentage = repsForConnectorCursor
						.getFloat(repsForConnectorCursor
								.getColumnIndexOrThrow(ExcercisesDbAdapter.KEY_PERCENTAGE));

				// adding reps to new session
				mDbHelper.createSessionRepsEntry(sessionsConnectorId, reps,
						percentage);

				repsForConnectorCursor.moveToNext();

			}

			mExercisesForSetCursor.moveToNext();
		}

		Intent i = new Intent(this, SessionView.class);
		i.putExtra(ExcercisesDbAdapter.KEY_ROWID, sessionId);
		startActivity(i);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View view,
			ContextMenuInfo menuInfo) {
		AdapterView.AdapterContextMenuInfo info;
		info = (AdapterView.AdapterContextMenuInfo) menuInfo;

		TextView exerciseName = (TextView) info.targetView
				.findViewById(R.id.exercise_name);
		String title = exerciseName.getText().toString();
		menu.setHeaderTitle(title);
		menu.add(0, DELETE_ID, 0, R.string.remove_from_set);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case DELETE_ID:
			AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
					.getMenuInfo();
			mDbHelper.deleteExerciseFromSet(info.id);
			fillData();
			Toast.makeText(this, R.string.exercise_removed_from_set,
					Toast.LENGTH_SHORT).show();
			return true;
		}
		return super.onContextItemSelected(item);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Intent i = new Intent(this, RepsList.class);
		i.putExtra(ExcercisesDbAdapter.KEY_ROWID, id);
		startActivity(i);
	}

	protected class SetViewCursorAdapter extends SimpleCursorAdapter {

		Activity mActivity;

		public SetViewCursorAdapter(Activity activity, int layout, Cursor c,
				String[] from, int[] to) {
			super(activity, layout, c, from, to);

			mActivity = activity;

		}
		
		

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			super.bindView(view, context, cursor);

			Log.i("BIND", "bind view called");

			Long setsConnectorId = cursor.getLong(cursor
					.getColumnIndexOrThrow(ExcercisesDbAdapter.KEY_ROWID));

			Long exType = cursor.getLong(cursor
					.getColumnIndexOrThrow(ExcercisesDbAdapter.KEY_TYPE));

			Cursor repsForConnectorCursor = mDbHelper
					.fetchRepsForConnector(setsConnectorId);

			// remove all rows except for the first one
			TableLayout repsTable = (TableLayout) view
					.findViewById(R.id.reps_table);
			repsTable.removeViews(1, repsTable.getChildCount() - 1);

			// if 0 - own weight - don't show percentage values
			if (exType == Long.valueOf(0)) {
				repsTable.findViewById(R.id.x_col).setVisibility(View.GONE);
				repsTable.findViewById(R.id.percentage_col).setVisibility(
						View.GONE);
			} else {
				repsTable.findViewById(R.id.x_col).setVisibility(View.VISIBLE);
				repsTable.findViewById(R.id.percentage_col).setVisibility(
						View.VISIBLE);
			}

			repsForConnectorCursor.moveToFirst();
			for (int i = 0; i < repsForConnectorCursor.getCount(); i++) {

				String reps = repsForConnectorCursor
						.getString(repsForConnectorCursor
								.getColumnIndexOrThrow(ExcercisesDbAdapter.KEY_REPS));

				String percentage = repsForConnectorCursor
						.getString(repsForConnectorCursor
								.getColumnIndexOrThrow(ExcercisesDbAdapter.KEY_PERCENTAGE));

				// Create a new row to be added.
				TableRow tr = new TableRow(SetView.this);
				LayoutParams trLayoutParams = new LayoutParams(LayoutParams.FILL_PARENT,
						LayoutParams.WRAP_CONTENT, 1);
				
				LayoutParams valueLayoutParamsReps = new LayoutParams(
						LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 1);				
				valueLayoutParamsReps.gravity = Gravity.CENTER;				
				LayoutParams valueLayoutParamsPercentage = new LayoutParams(
						LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 1);				
				valueLayoutParamsPercentage.gravity = Gravity.CENTER;
				
				TextView repsTxt = new TextView(SetView.this);
				repsTxt.setText(reps);
				repsTxt.setGravity(Gravity.CENTER);
				tr.addView(repsTxt, valueLayoutParamsReps);

				LayoutParams xLayoutParams = new LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				xLayoutParams.gravity = Gravity.CENTER;
				TextView xTxt = new TextView(SetView.this);
				xTxt.setText("x");				
				xTxt.setLayoutParams(xLayoutParams);
				tr.addView(xTxt);

				TextView percentageTxt = new TextView(SetView.this);
				percentageTxt.setText(percentage);
				percentageTxt.setGravity(Gravity.CENTER);
				tr.addView(percentageTxt, valueLayoutParamsPercentage);

				// Add row to TableLayout.

				repsTable.addView(tr, trLayoutParams);

				// if 0 - own weight - don't show percentage values
				if (exType == Long.valueOf(0)) {
					xTxt.setVisibility(View.GONE);
					percentageTxt.setVisibility(View.GONE);
				}

				repsForConnectorCursor.moveToNext();

			}

		}

	}

}
