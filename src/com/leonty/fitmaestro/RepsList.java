package com.leonty.fitmaestro;

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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.leonty.fitmaestro.domain.FitmaestroDb;
import com.leonty.fitmaestro.domain.Workout;

public class RepsList extends ListActivity {

	private Cursor mRepsForConnectorCursor;
	private Long mSetConnectorId;
	private Long mExType;
	private Double mMaxWeight;
	private Long mMaxReps;
	private Long mListPosition;
	private Dialog mEditRepsDialog;
	private SharedPreferences mPrefs;
	private String mUnits;
	private Percentages mPercentages;
	
	private static final int ACTIVITY_EDIT = 1;
	private static final int DIALOG_EDIT_REPS = 2;
	private static final int INSERT_ID = Menu.FIRST;
	private static final int DELETE_ID = Menu.FIRST + 1;

	private static final String TAG = "RepsList";

	private FitmaestroDb db;	
	private Workout set;	
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reps_list);

		db = new FitmaestroDb(this).open();
		set = new Workout(db);		
		
		mSetConnectorId = savedInstanceState != null ? savedInstanceState
				.getLong(FitmaestroDb.KEY_ROWID) : null;
		if (mSetConnectorId == null) {
			Bundle extras = getIntent().getExtras();
			mSetConnectorId = extras != null ? extras
					.getLong(FitmaestroDb.KEY_ROWID) : null;
		}

		mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		Log.i("MEASUREMENT UNITS: ", mPrefs.getString("units", "default"));
		mUnits = mPrefs.getString("units", getText(R.string.default_unit).toString());
		Double step = Double.valueOf(mPrefs.getString("step", "0.5"));
		mPercentages = new Percentages(step);

		fillData();
		registerForContextMenu(getListView());
	}

	@Override
	protected void onDestroy() {
		db.close();
		super.onDestroy();
	}
	
	private void fillData() {

		Cursor exerciseCursor = set
				.fetchExerciseForSetsConnector(mSetConnectorId);
		startManagingCursor(exerciseCursor);
		mExType = exerciseCursor.getLong(exerciseCursor
				.getColumnIndexOrThrow(FitmaestroDb.KEY_TYPE));
		mMaxReps = exerciseCursor.getLong(exerciseCursor
				.getColumnIndexOrThrow(FitmaestroDb.KEY_MAX_REPS));
		mMaxWeight = exerciseCursor.getDouble(exerciseCursor
				.getColumnIndexOrThrow(FitmaestroDb.KEY_MAX_WEIGHT));

		Log.i(TAG, mExType.toString() + mMaxWeight.toString()
				+ mMaxReps.toString());
		
		if(mExType == 0){
			findViewById(R.id.reps_col).setVisibility(View.GONE);
			findViewById(R.id.x_col).setVisibility(View.GONE);
			TextView resultText = (TextView) findViewById(R.id.weight_col);
			resultText.setText(R.string.reps_table);
		}

		mRepsForConnectorCursor = set
				.fetchRepsForConnector(mSetConnectorId);
		startManagingCursor(mRepsForConnectorCursor);
		String[] from = new String[] { FitmaestroDb.KEY_REPS,
				FitmaestroDb.KEY_PERCENTAGE };
		int[] to = new int[] { R.id.reps_value, R.id.percentage_value };
		RepsListCursorAdapter reps = new RepsListCursorAdapter(this,
				R.layout.reps_list_row, mRepsForConnectorCursor, from, to);
		setListAdapter(reps);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuItem insert = menu.add(0, INSERT_ID, 0, R.string.add_entry);
		insert.setIcon(android.R.drawable.ic_menu_add);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case INSERT_ID:
			mListPosition = null;
			showDialog(DIALOG_EDIT_REPS);
			populateRepsDialog();
			return true;
		}

		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		fillData();

		switch (requestCode) {
		case ACTIVITY_EDIT:
			Toast
					.makeText(this, R.string.reps_entry_edited,
							Toast.LENGTH_SHORT).show();
			break;
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View view,
			ContextMenuInfo menuInfo) {
		AdapterView.AdapterContextMenuInfo info;
		info = (AdapterView.AdapterContextMenuInfo) menuInfo;

		
		TextView repsText = (TextView) info.targetView
				.findViewById(R.id.reps_value);
		String repsValue = repsText.getText().toString();

		TextView percentageText = (TextView) info.targetView
				.findViewById(R.id.percentage_value);
		String percentageValue = percentageText.getText().toString();

		if (mExType == 0) {
			menu.setHeaderTitle(repsValue);
		} else {
			menu.setHeaderTitle(repsValue + " x " + percentageValue);
		}

		menu.add(0, DELETE_ID, 1, R.string.delete_reps_entry);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();

		switch (item.getItemId()) {
		case DELETE_ID:
			set.deleteRepsEntry(info.id);
			fillData();
			return true;
		}
		return super.onContextItemSelected(item);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Log.i("CLICK test", "clicked!");

		mListPosition = Long.valueOf(position);
		Log.i("LIST POSITION IN EDIT: ", mListPosition.toString());
		showDialog(DIALOG_EDIT_REPS);
		populateRepsDialog();
	}

	@Override
	protected Dialog onCreateDialog(int id) {

		switch (id) {
		case DIALOG_EDIT_REPS:
			LayoutInflater factory = LayoutInflater.from(this);
			final View repsEditView = factory.inflate(R.layout.edit_reps_entry,
					null);
			Log.i("DYG", "Creating dialog");

			final EditText repsText = (EditText) repsEditView
					.findViewById(R.id.editText_reps);
			final EditText percentageText = (EditText) repsEditView
					.findViewById(R.id.editText_percentage);

			if (mExType == 0) {

				repsEditView.findViewById(R.id.text_times).setVisibility(
						View.GONE);
				repsText.setVisibility(View.GONE);
				repsEditView.findViewById(R.id.text_x).setVisibility(View.GONE);
				repsEditView.findViewById(R.id.weight_value_container).setVisibility(
						View.GONE);
			}else{
				repsEditView.findViewById(R.id.reps_value_container).setVisibility(
						View.GONE);
				
			}

			mEditRepsDialog = new AlertDialog.Builder(this).setTitle(
					R.string.edit_reps_entry).setView(repsEditView)
					.setPositiveButton(R.string.done,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {

									/* User clicked OK so do some stuff */

									String reps = repsText.getText().toString();
									String percentage = percentageText
											.getText().toString();
									if (reps.length() > 0
											&& percentage.length() > 0) {
										if (mListPosition == null) {
											Log.i("NEW ENTRY",
													"Creating new entry");
											set.createRepsEntry(
													mSetConnectorId, Integer
															.parseInt(reps
																	.trim()),
													Float.valueOf(percentage
															.trim()));
										} else {
											Log.i("OLD ENTRY",
													"Editing old entry");
											mRepsForConnectorCursor
													.moveToPosition(mListPosition
															.intValue());
											Long setDetailsId = mRepsForConnectorCursor
													.getLong(mRepsForConnectorCursor
															.getColumnIndexOrThrow(FitmaestroDb.KEY_ROWID));
											set.updateRepsEntry(
													setDetailsId, Integer
															.parseInt(reps
																	.trim()),
													Float.valueOf(percentage
															.trim()));
										}

										fillData();
										registerForContextMenu(getListView());
									}

								}
							}).setNegativeButton(R.string.cancel,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {

									/* User clicked cancel so do some stuff */
								}
							}).create();

			return mEditRepsDialog;
		}

		return null;
	}

	public void populateRepsDialog() {

		EditText repsText = (EditText) mEditRepsDialog
				.findViewById(R.id.editText_reps);
		EditText percentageText = (EditText) mEditRepsDialog
				.findViewById(R.id.editText_percentage);
		final TextView weightValue = (TextView) mEditRepsDialog
		.findViewById(R.id.weight_value);
		final TextView repsValue = (TextView) mEditRepsDialog
		.findViewById(R.id.reps_value);
		
		//final Percentages percentages = new Percentages();
		
		// update value on text Change
		percentageText.addTextChangedListener(new TextWatcher() {

			public void afterTextChanged(Editable s) {
                Log.d("seachScreen", "afterTextChanged" + s.toString());
                
                Double currentPercentage = s.length() > 0 ? Double.valueOf(s.toString()) : 0;
                if(mExType ==0){
                	repsValue.setText(String.valueOf(mPercentages.getIntValue(currentPercentage, mMaxReps)));
                }else{
                	weightValue.setText(String.valueOf(mPercentages.getValueWithPrecision(currentPercentage, mMaxWeight)));
                }				
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				
			}

		}); 
		

		if (mListPosition != null) {
			Log.i("LIST POSITION: ", mListPosition.toString());
			mRepsForConnectorCursor.moveToPosition(mListPosition.intValue());
			repsText
					.setText(mRepsForConnectorCursor
							.getString(mRepsForConnectorCursor
									.getColumnIndexOrThrow(FitmaestroDb.KEY_REPS)));
			Double percentageValue = mRepsForConnectorCursor
			.getDouble(mRepsForConnectorCursor
					.getColumnIndexOrThrow(FitmaestroDb.KEY_PERCENTAGE));
			percentageText
					.setText(String.valueOf(percentageValue));
			
			Log.i("MAX WEIGHT: ", String.valueOf(mMaxWeight));
			weightValue.setText(String.valueOf(mPercentages.getValueWithPrecision(percentageValue, mMaxWeight)));
			repsValue.setText(String.valueOf(mPercentages.getIntValue(percentageValue, mMaxReps)));
			
		} else {
			percentageText.setText("");

			// Long.valueOf(0)
			if (mExType == 0) {

				repsText.setText("0");
				
			} else {
				repsText.setText("");
				percentageText.setText("");
				repsText.requestFocus();
			}
		}
	}

	protected class RepsListCursorAdapter extends SimpleCursorAdapter {

		Activity mActivity;

		public RepsListCursorAdapter(Activity activity, int layout, Cursor c,
				String[] from, int[] to) {
			super(activity, layout, c, from, to);

			mActivity = activity;

		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			super.bindView(view, context, cursor);

			//Percentages percentages = new Percentages();
			TextView weightValue = (TextView) view.findViewById(R.id.result_value);
			TextView percentageTxt = (TextView) view.findViewById(R.id.percentage_value);
			Double percentageValue = cursor
			.getDouble(cursor
					.getColumnIndexOrThrow(FitmaestroDb.KEY_PERCENTAGE));
			percentageTxt.setText(String.valueOf(percentageValue) + " %");
			
			if (mExType == 0) {
				view.findViewById(R.id.reps_value).setVisibility(
						View.GONE);
				view.findViewById(R.id.x_col_value).setVisibility(View.GONE);
				weightValue.setText(String.valueOf(mPercentages.getIntValue(percentageValue, mMaxReps)));
			}else{
				weightValue.setText(String.valueOf(mPercentages.getValueWithPrecision(percentageValue, mMaxWeight)) + " " + mUnits);
			}

		}

	}

}
