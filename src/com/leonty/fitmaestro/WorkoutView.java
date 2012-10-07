package com.leonty.fitmaestro;

import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.leonty.fitmaestro.domain.FitmaestroDb;
import com.leonty.fitmaestro.domain.Session;
import com.leonty.fitmaestro.domain.Workout;

public class WorkoutView extends ListActivity {

	private Cursor mExercisesForSetCursor;
	private Long mSetId;
	private Long mProgramsConnectorId;
	private SharedPreferences mPrefs;
	private String mUnits;
	private Dialog mSessionTitleDialog;
	private Percentages mPercentages;
	private DateFormats mDateFormats;

	private static final int ADD_ID = Menu.FIRST;
	private static final int DELETE_ID = Menu.FIRST + 1;
	private static final int START_SESSION_ID = Menu.FIRST + 2;
	private static final int ACTIVITY_ADD = 0;
	private static final int DIALOG_SESSION_TITLE = 13;

	private FitmaestroDb db;
	private Session session;	
	private Workout set;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setview_list);

		db = new FitmaestroDb(this).open();
		session = new Session(db);	
		set = new Workout(db);
		
		mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		mUnits = mPrefs.getString("units", "default");		
		
		Bundle extras = getIntent().getExtras();
		mSetId = savedInstanceState != null ? savedInstanceState
				.getLong(FitmaestroDb.KEY_ROWID) : null;
		if (mSetId == null) {
			mSetId = extras != null ? extras
					.getLong(FitmaestroDb.KEY_ROWID) : null;
		}

		mProgramsConnectorId = savedInstanceState != null ? savedInstanceState
				.getLong("programs_connector_id") : null;

		if (mProgramsConnectorId == null && extras != null) {
			mProgramsConnectorId = extras.getLong("programs_connector_id");
		}		
		
		Double step = Double.valueOf(mPrefs.getString("step", "0.5"));
		Log.i("STEP: ", String.valueOf(step));
		mPercentages = new Percentages(step);
		mDateFormats = new DateFormats(this);
		
		fillData();
		registerForContextMenu(getListView());
	}
	
	@Override
	protected void onDestroy() {
		db.close();
		super.onDestroy();
	}

	private void fillData() {
		mExercisesForSetCursor = set.fetchExercisesForSet(mSetId);
		startManagingCursor(mExercisesForSetCursor);
		String[] from = new String[] { FitmaestroDb.KEY_TITLE };
		int[] to = new int[] { R.id.exercise_name };
		SetViewCursorAdapter excercises = new SetViewCursorAdapter(this,
				R.layout.setview_list_row, mExercisesForSetCursor, from, to);
		setListAdapter(excercises);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		
		if(resultCode == RESULT_OK){
			Bundle extras = intent.getExtras();
			Long ExerciseId = extras != null ? extras
					.getLong(FitmaestroDb.KEY_EXERCISEID) : null;
			set.addExerciseToSet(mSetId, ExerciseId);
			fillData();
			Log.i("EXERCISE ID FROM ACTIVITY: ", String.valueOf(ExerciseId));
			Log.i("SET ID FROM ACTIVITY: ", String.valueOf(mSetId));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuItem insert = menu.add(0, ADD_ID, 0, R.string.add_exercise_to_set);
		insert.setIcon(android.R.drawable.ic_menu_add);
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
			showDialog(DIALOG_SESSION_TITLE);
			break;
		}

		return super.onMenuItemSelected(featureId, item);
	}

	private void addExercise() {
		Intent i = new Intent(this, ExercisesList.class);
		startActivityForResult(i, ACTIVITY_ADD);
	}

	private void startSession(String sessionTitle) {

		//Percentages percentages = new Percentages();
		if (mProgramsConnectorId == null) {
			mProgramsConnectorId = Long.valueOf(0);
		}
		Long sessionId = session.createSession(sessionTitle, getText(R.string.started_from_workout).toString(),
				mProgramsConnectorId);

		// add exercises to session
		mExercisesForSetCursor.moveToFirst();
		for (int i = 0; i < mExercisesForSetCursor.getCount(); i++) {

			Long exerciseId = mExercisesForSetCursor
					.getLong(mExercisesForSetCursor
							.getColumnIndexOrThrow(FitmaestroDb.KEY_EXERCISEID));
			Long setsConnectorId = mExercisesForSetCursor
					.getLong(mExercisesForSetCursor
							.getColumnIndexOrThrow(FitmaestroDb.KEY_ROWID));

			// get exercise info to correctly populate session field
			Long exType = mExercisesForSetCursor.getLong(mExercisesForSetCursor
					.getColumnIndexOrThrow(FitmaestroDb.KEY_TYPE));

			Long maxReps = 0l;
			Double maxWeight = 0d;
			if (exType == 0) {
				maxReps = mExercisesForSetCursor.getLong(mExercisesForSetCursor
						.getColumnIndexOrThrow(FitmaestroDb.KEY_MAX_REPS));
				
			} else {
				maxWeight = mExercisesForSetCursor.getDouble(mExercisesForSetCursor
						.getColumnIndexOrThrow(FitmaestroDb.KEY_MAX_WEIGHT));
			}
			
			long sessionsConnectorId = session.addExerciseToSession(
					sessionId, exerciseId);

			Cursor repsForConnectorCursor = set
					.fetchRepsForConnector(setsConnectorId);
			startManagingCursor(repsForConnectorCursor);

			repsForConnectorCursor.moveToFirst();
			for (int j = 0; j < repsForConnectorCursor.getCount(); j++) {

				Double percentage = repsForConnectorCursor
				.getDouble(repsForConnectorCursor
						.getColumnIndexOrThrow(FitmaestroDb.KEY_PERCENTAGE));
				Double weight = 0d;
				Long reps = 0l;
				
				if(exType == 0){
					reps = mPercentages.getIntValue(percentage, maxReps);
				}else{
					weight = mPercentages.getValueWithPrecision(percentage, maxWeight);
					reps = repsForConnectorCursor
					.getLong(repsForConnectorCursor
							.getColumnIndexOrThrow(FitmaestroDb.KEY_REPS));
				}

				// adding reps to new session
				session.createSessionRepsEntry(sessionsConnectorId, reps,
						weight);

				repsForConnectorCursor.moveToNext();

			}

			mExercisesForSetCursor.moveToNext();
		}

		Intent i = new Intent(this, SessionView.class);
		i.putExtra(FitmaestroDb.KEY_ROWID, sessionId);
		startActivity(i);
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {

		switch (id) {
		case DIALOG_SESSION_TITLE:
			LayoutInflater factory = LayoutInflater.from(this);
			final View sessionPopup = factory.inflate(R.layout.session_popup,
					null);

			final EditText sessionTitle = (EditText) sessionPopup.findViewById(R.id.session_title);
			
			Date date = new Date();
			sessionTitle.setText(mDateFormats.getWithYearFromDate(date));
			
			mSessionTitleDialog = new AlertDialog.Builder(this).setTitle(
					R.string.session_title_caption).setView(sessionPopup)
					.setPositiveButton(R.string.start,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {

									/* User clicked OK so do some stuff */
									if(sessionTitle.getText().length() > 0){
										startSession(sessionTitle.getText().toString());
									}
								}
							}).setNegativeButton(R.string.cancel,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {

									/* User clicked cancel so do some stuff */
								}
							}).create();

			return mSessionTitleDialog;
		}

		return null;
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
			set.deleteExerciseFromSet(info.id);
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
		i.putExtra(FitmaestroDb.KEY_ROWID, id);
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

			//Percentages percentages = new Percentages();
			
			Long setsConnectorId = cursor.getLong(cursor
					.getColumnIndexOrThrow(FitmaestroDb.KEY_ROWID));

			Long exType = cursor.getLong(cursor
					.getColumnIndexOrThrow(FitmaestroDb.KEY_TYPE));

			Cursor repsForConnectorCursor = set
					.fetchRepsForConnector(setsConnectorId);
			startManagingCursor(repsForConnectorCursor);
			
			// remove all rows except for the first one
			TableLayout repsTable = (TableLayout) view
					.findViewById(R.id.reps_table);
			repsTable.removeViews(1, repsTable.getChildCount() - 1);
			
			
			// if there is no results - we just started - don't show the header
			if(repsForConnectorCursor.getCount() == 0){
				repsTable.findViewById(R.id.reps_table_header).setVisibility(
						View.GONE);
				Log.i("REPS COUNT: ", "Count is zero");
			}else{
				repsTable.findViewById(R.id.reps_table_header).setVisibility(
						View.VISIBLE);
			}

			TextView resultText = (TextView) repsTable.findViewById(R.id.weight_col);
			
			Long maxReps = 0l;
			Double maxWeight = 0d;
			
			// if 0 - own weight - don't show percentage values
			if (exType == 0) {
				repsTable.findViewById(R.id.reps_col).setVisibility(
						View.GONE);
				repsTable.findViewById(R.id.x_col).setVisibility(View.GONE);
				resultText.setText(R.string.reps_table);
				maxReps = cursor.getLong(cursor
						.getColumnIndexOrThrow(FitmaestroDb.KEY_MAX_REPS));
				
			} else {
				repsTable.findViewById(R.id.reps_col).setVisibility(
						View.VISIBLE);
				repsTable.findViewById(R.id.x_col).setVisibility(View.VISIBLE);
				resultText.setText(R.string.weight_table);
				maxWeight = cursor.getDouble(cursor
						.getColumnIndexOrThrow(FitmaestroDb.KEY_MAX_WEIGHT));
			}

			repsForConnectorCursor.moveToFirst();			
			for (int i = 0; i < repsForConnectorCursor.getCount(); i++) {

				String reps = repsForConnectorCursor
						.getString(repsForConnectorCursor
								.getColumnIndexOrThrow(FitmaestroDb.KEY_REPS));

				Double percentage = repsForConnectorCursor
						.getDouble(repsForConnectorCursor
								.getColumnIndexOrThrow(FitmaestroDb.KEY_PERCENTAGE));

				// Create a new row to be added.
				TableRow tr = new TableRow(WorkoutView.this);
				LayoutParams trLayoutParams = new LayoutParams(LayoutParams.FILL_PARENT,
						LayoutParams.WRAP_CONTENT, 1);
				
				LayoutParams valueLayoutParamsReps = new LayoutParams(
						LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 1);				
				valueLayoutParamsReps.gravity = Gravity.CENTER;				
				LayoutParams valueLayoutParamsPercentage = new LayoutParams(
						LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 1);				
				valueLayoutParamsPercentage.gravity = Gravity.CENTER;
				LayoutParams valueLayoutParamsResult = new LayoutParams(
						LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 1);				
				valueLayoutParamsPercentage.gravity = Gravity.CENTER;
				
				TextView repsTxt = new TextView(WorkoutView.this);
				repsTxt.setText(reps);
				repsTxt.setGravity(Gravity.CENTER);
				tr.addView(repsTxt, valueLayoutParamsReps);

				LayoutParams xLayoutParams = new LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				xLayoutParams.gravity = Gravity.CENTER;
				TextView xTxt = new TextView(WorkoutView.this);
				xTxt.setText("x");				
				xTxt.setLayoutParams(xLayoutParams);
				tr.addView(xTxt);

				TextView percentageTxt = new TextView(WorkoutView.this);
				percentageTxt.setText(String.valueOf(percentage) + " %");
				percentageTxt.setGravity(Gravity.CENTER);
				tr.addView(percentageTxt, valueLayoutParamsPercentage);
				
				TextView resultTxt = new TextView(WorkoutView.this);
				if(exType == 0){
					resultTxt.setText(String.valueOf(mPercentages.getIntValue(percentage, maxReps)));
				}else{
					resultTxt.setText(String.valueOf(mPercentages.getValueWithPrecision(percentage, maxWeight)) + " " + mUnits);
				}
				
				resultTxt.setGravity(Gravity.CENTER);
				tr.addView(resultTxt, valueLayoutParamsResult);

				// Add row to TableLayout.

				repsTable.addView(tr, trLayoutParams);

				// if 0 - own weight - don't show percentage values
				if (exType == Long.valueOf(0)) {
					xTxt.setVisibility(View.GONE);
					repsTxt.setVisibility(View.GONE);
				}

				repsForConnectorCursor.moveToNext();

			}

		}

	}

}
