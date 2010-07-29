package com.leonti.fitmaestro;

import com.leonti.fitmaestro.R;

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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class RepsList extends ListActivity {

	private ExcercisesDbAdapter mDbHelper;
	private Cursor mRepsForConnectorCursor;
	private Long mSetConnectorId;
	private Long mExType;
	private Double mMaxWeight;
	private Long mMaxReps;
	private Long mListPosition;
	private Dialog mEditRepsDialog;
	private SharedPreferences mPrefs;
	private String mUnits;
	
	private static final int ACTIVITY_EDIT = 1;
	private static final int DIALOG_EDIT_REPS = 2;
	private static final int INSERT_ID = Menu.FIRST;
	private static final int DELETE_ID = Menu.FIRST + 1;

	private static final String TAG = "RepsList";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reps_list);

		mSetConnectorId = savedInstanceState != null ? savedInstanceState
				.getLong(ExcercisesDbAdapter.KEY_ROWID) : null;
		if (mSetConnectorId == null) {
			Bundle extras = getIntent().getExtras();
			mSetConnectorId = extras != null ? extras
					.getLong(ExcercisesDbAdapter.KEY_ROWID) : null;
		}

		mDbHelper = new ExcercisesDbAdapter(this);
		mDbHelper.open();

		fillData();
		registerForContextMenu(getListView());
	}

	@Override
	protected void onDestroy() {
		mDbHelper.close();
		super.onDestroy();
	}
	
	private void fillData() {

		Cursor exerciseCursor = mDbHelper
				.fetchExerciseForSetsConnector(mSetConnectorId);
		startManagingCursor(exerciseCursor);
		mExType = exerciseCursor.getLong(exerciseCursor
				.getColumnIndexOrThrow(ExcercisesDbAdapter.KEY_TYPE));
		mMaxReps = exerciseCursor.getLong(exerciseCursor
				.getColumnIndexOrThrow(ExcercisesDbAdapter.KEY_MAX_REPS));
		mMaxWeight = exerciseCursor.getDouble(exerciseCursor
				.getColumnIndexOrThrow(ExcercisesDbAdapter.KEY_MAX_WEIGHT));

		Log.i(TAG, mExType.toString() + mMaxWeight.toString()
				+ mMaxReps.toString());
		
		mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		Log.i("MEASUREMENT UNITS: ", mPrefs.getString("units", "default"));
		mUnits = mPrefs.getString("units", "default");
		
		if(mExType == 0){
			findViewById(R.id.reps_col).setVisibility(View.GONE);
			findViewById(R.id.x_col).setVisibility(View.GONE);
			TextView resultText = (TextView) findViewById(R.id.weight_col);
			resultText.setText(R.string.reps_table);
		}

		mRepsForConnectorCursor = mDbHelper
				.fetchRepsForConnector(mSetConnectorId);
		startManagingCursor(mRepsForConnectorCursor);
		String[] from = new String[] { ExcercisesDbAdapter.KEY_REPS,
				ExcercisesDbAdapter.KEY_PERCENTAGE };
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
			mDbHelper.deleteRepsEntry(info.id);
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
											mDbHelper.createRepsEntry(
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
															.getColumnIndexOrThrow(ExcercisesDbAdapter.KEY_ROWID));
											mDbHelper.updateRepsEntry(
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
		
		final Percentages percentages = new Percentages();
		
		// update value on text Change
		percentageText.addTextChangedListener(new TextWatcher() {

			@Override
            public void  afterTextChanged (Editable s){
                    Log.d("seachScreen", "afterTextChanged" + s.toString());
                    
                    Double currentPercentage = s.length() > 0 ? Double.valueOf(s.toString()) : 0;
                    if(mExType ==0){
                    	repsValue.setText(String.valueOf(percentages.getIntValue(currentPercentage, mMaxReps)));
                    }else{
                    	weightValue.setText(String.valueOf(percentages.getValue(currentPercentage, mMaxWeight)));
                    }
            }

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub
				
			}
		}); 
		

		if (mListPosition != null) {
			Log.i("LIST POSITION: ", mListPosition.toString());
			mRepsForConnectorCursor.moveToPosition(mListPosition.intValue());
			repsText
					.setText(mRepsForConnectorCursor
							.getString(mRepsForConnectorCursor
									.getColumnIndexOrThrow(ExcercisesDbAdapter.KEY_REPS)));
			Double percentageValue = mRepsForConnectorCursor
			.getDouble(mRepsForConnectorCursor
					.getColumnIndexOrThrow(ExcercisesDbAdapter.KEY_PERCENTAGE));
			percentageText
					.setText(String.valueOf(percentageValue));
			
			Log.i("MAX WEIGHT: ", String.valueOf(mMaxWeight));
			weightValue.setText(String.valueOf(percentages.getValue(percentageValue, mMaxWeight)));
			repsValue.setText(String.valueOf(percentages.getIntValue(percentageValue, mMaxReps)));
			
		} else {
			percentageText.setText("");

			// Long.valueOf(0)
			if (mExType == 0) {

				repsText.setText("0");
				
			} else {
				percentageText.setText("");
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

			Percentages percentages = new Percentages();
			TextView weightValue = (TextView) view.findViewById(R.id.result_value);
			TextView percentageTxt = (TextView) view.findViewById(R.id.percentage_value);
			Double percentageValue = cursor
			.getDouble(cursor
					.getColumnIndexOrThrow(ExcercisesDbAdapter.KEY_PERCENTAGE));
			percentageTxt.setText(String.valueOf(percentageValue) + " %");
			
			if (mExType == 0) {
				view.findViewById(R.id.reps_value).setVisibility(
						View.GONE);
				view.findViewById(R.id.x_col_value).setVisibility(View.GONE);
				weightValue.setText(String.valueOf(percentages.getIntValue(percentageValue, mMaxReps)));
			}else{
				weightValue.setText(String.valueOf(percentages.getValue(percentageValue, mMaxWeight)) + " " + mUnits);
			}

		}

	}

}
