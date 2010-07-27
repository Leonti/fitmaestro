package com.leonti.fitmaestro;

import java.util.HashMap;

import com.leonti.fitmaestro.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TableRow.LayoutParams;

public class ProgramView extends Activity {

	private ExcercisesDbAdapter mDbHelper;
	private Long mRowId;
	private Long mDayNumber;
	private Cursor mProgramSetsCursor;
	private TableLayout mTl;

	private static final int ACTIVITY_ADD_SET = 0;
	private static final int ACTIVITY_EDIT_SET = 1;
	private static final int ADD_WEEK_ID = Menu.FIRST;
	private static final int DELETE_ID = Menu.FIRST + 1;
	private static final int EDIT_ID = Menu.FIRST + 2;


	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.program_view);

		mDbHelper = new ExcercisesDbAdapter(this);
		mDbHelper.open();

		mRowId = savedInstanceState != null ? savedInstanceState
				.getLong(ExcercisesDbAdapter.KEY_ROWID) : null;
		if (mRowId == null) {
			Bundle extras = getIntent().getExtras();
			mRowId = extras != null ? extras
					.getLong(ExcercisesDbAdapter.KEY_ROWID) : null;
		}

		fillData();
		/*
		 * registerForContextMenu(getListView());
		 */
	}
	
	@Override
	protected void onDestroy() {
		mDbHelper.close();
		super.onDestroy();
	}

	private void fillData() {
		Log.i("FILL", "filling program view");
		mProgramSetsCursor = mDbHelper.fetchProgramSets(mRowId);
		startManagingCursor(mProgramSetsCursor);
		mProgramSetsCursor.moveToFirst();

		/* Find Tablelayout defined in main.xml */
		mTl = (TableLayout) findViewById(R.id.program_table);

		mTl.removeViews(0, mTl.getChildCount());

		
		Log.i("PROGRAM DAYS: ", String.valueOf(mDbHelper.getProgramMaxDay(mRowId)));
		
		// getting number of weeks for the program - 28 days by default(4 weeks) or the max days for the program
		long weeks = new Double(Math.ceil(Math.max((double) 28, (double) mDbHelper.getProgramMaxDay(mRowId)) / 7)).longValue();
		Log.i("PROGRAM WEEKS: ", String.valueOf(weeks));
		
		for(long i = 0; i < weeks; i++){
			addWeek(mTl, i*7 + 1);
		}
	}
	
	@Override
    public void onCreateContextMenu(ContextMenu menu,View v,ContextMenuInfo info)
    {
		@SuppressWarnings("unchecked")
		HashMap<String, Long> dayClickedData = (HashMap<String, Long>) v
		.getTag();
		
		Log.i("WORKOUT_ID - context", dayClickedData.get("set_id")
				.toString());
				
	     menu.setHeaderTitle(dayClickedData.get("day_number").toString() + " day");
	     
	     Intent menuIntent = new Intent();
	     menuIntent.putExtra("workout_id", dayClickedData.get("set_id"));
	     menuIntent.putExtra("programs_connector_id", dayClickedData.get("programs_connector_id"));
	     
	     MenuItem edit = menu.add(0, EDIT_ID, 0, R.string.edit_workout);
	     edit.setIntent(menuIntent);
	     MenuItem remove = menu.add(0, DELETE_ID, 0, R.string.remove_from_program);
	     remove.setIntent(menuIntent);
    }
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		
		Intent menuIntent = item.getIntent();
		Bundle extras = menuIntent.getExtras();
		switch (item.getItemId()) {
		
		case EDIT_ID:
			Intent i = new Intent(this, WorkoutEdit.class);
			i.putExtra("program_id", mRowId);
			i.putExtra("day_number", mDayNumber);
			i.putExtra(ExcercisesDbAdapter.KEY_ROWID, extras.getLong("workout_id"));
			startActivityForResult(i, ACTIVITY_EDIT_SET);
			return true;
			
		case DELETE_ID:
			
			Log.i("WORKOUT ID TO DELETE", extras.get("workout_id").toString());
			Log.i("PROGRAMS CONNECTOR TO DELETE", extras.get("programs_connector_id").toString());

			// First we remove workout from program and then delete workout itself			
			mDbHelper.removeSetFromProgram(extras.getLong("programs_connector_id"));
			mDbHelper.deleteSet(extras.getLong("workout_id"));
			Toast.makeText(this, R.string.plan_removed, Toast.LENGTH_SHORT)
			.show();
			fillData();
			return true;
		}
		
		return super.onContextItemSelected(item);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuItem insert = menu.add(0, ADD_WEEK_ID, 0, R.string.add_week);
		insert.setIcon(android.R.drawable.ic_menu_add);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case ADD_WEEK_ID:
			long currentWeeks = mTl.getChildCount();
			Log.i("WEEKS COUNT: ", String.valueOf(currentWeeks));
			
			addWeek(mTl, currentWeeks*7+1);
			return true;
		}

		return super.onMenuItemSelected(featureId, item);
	}
	
	public void addWeek(TableLayout tl, long startDay) {

		/* Create a new row to be added. */
		TableRow tr = new TableRow(this);
		tr.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT));

		for (long i = startDay; i < startDay + 7; i++) {

			HashMap<String, Long> dayData = new HashMap<String, Long>();
			dayData.put("day_number", Long.valueOf(i));

			boolean match = false;
			if (mProgramSetsCursor.getCount() > 0) {
				// check if we have this day in the db
				Long dayNumber = mProgramSetsCursor
						.getLong(mProgramSetsCursor
								.getColumnIndexOrThrow(ExcercisesDbAdapter.KEY_DAY_NUMBER));

				if (Long.valueOf(i) == dayNumber) {
					Log.i("DAY_NUMBER MATCH: ", Long.toString(dayNumber));
					match = true;
					Long setId = mProgramSetsCursor
							.getLong(mProgramSetsCursor
									.getColumnIndexOrThrow(ExcercisesDbAdapter.KEY_SETID));
					dayData.put("set_id", setId);

					Long programsConnectorId = mProgramSetsCursor
							.getLong(mProgramSetsCursor
									.getColumnIndexOrThrow(ExcercisesDbAdapter.KEY_ROWID));
					dayData.put("programs_connector_id", programsConnectorId);

					if (!mProgramSetsCursor.isLast()) {
						mProgramSetsCursor.moveToNext();
					}
				}
			}

			/* Create a TextView to be the row-content. */
			TextView day = new TextView(this);
			day.setText("\n\n\n" + Long.toString(i));
			
			LayoutParams dayEntryLP = new LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 1);
			dayEntryLP.setMargins(1, 1, 1, 1);

			day.setLayoutParams(dayEntryLP);
			if(match){
				day.setBackgroundColor(Color.YELLOW);
				registerForContextMenu(day);
			}else{
				day.setBackgroundColor(Color.WHITE);
			}
			day.setPadding(2, 2, 2, 2);
			// day.setTextAppearance(this, android.R.attr.textAppearanceLarge);

			day.setTag(dayData);
			
			day.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Log.i("Listener: ", mRowId + " " + v.getTag().toString());

					@SuppressWarnings("unchecked")
					final HashMap<String, Long> dayClickedData = (HashMap<String, Long>) v
							.getTag();

					Log.i("HASH DATA DAY NUMBER", dayClickedData.get(
							"day_number").toString());

					mDayNumber = dayClickedData.get("day_number");
					if (dayClickedData.get("set_id") != null) {
						Log.i("HASH DATA SET_ID", dayClickedData.get("set_id")
								.toString());

						Log.i("HASH DATA CONNECTOR ID:", dayClickedData.get(
						"programs_connector_id").toString());
						

						Intent i = new Intent(ProgramView.this, WorkoutView.class);
						i.putExtra(ExcercisesDbAdapter.KEY_ROWID,
								dayClickedData.get("set_id"));
						i.putExtra("programs_connector_id", dayClickedData
								.get("programs_connector_id"));
						startActivityForResult(i, 5);		
					} else {
						
					    new AlertDialog.Builder(ProgramView.this)
					      .setMessage(R.string.create_workout)
					      .setPositiveButton(R.string.create, new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
																
									addSet();	
								}})
					      .setOnCancelListener(new OnCancelListener() {
					        public void onCancel(DialogInterface dialog) {
					          
					        }})
					      .setNegativeButton(R.string.cancel,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {

									/* User clicked cancel so do some stuff */
								}
							})
					      .show();
					}
				}
			});
			
			
			day.setOnLongClickListener(new OnLongClickListener(){

				@Override
				public boolean onLongClick(View v) {
					
					@SuppressWarnings("unchecked")
					HashMap<String, Long> dayClickedData = (HashMap<String, Long>) v
					.getTag();

					Log.i("HASH DATA DAY NUMBER FROM LONG CLICK", dayClickedData.get(
					"day_number").toString());

					// if there is a workout attached - return false, so context menu can be proceed
					// if it's an empty day - just return true and menu will not show
					mDayNumber = dayClickedData.get("day_number");
					if (dayClickedData.get("set_id") != null) {
						Log.i("HASH DATA SET_ID", dayClickedData.get("set_id")
								.toString());
						return false;	
					}else{
						return true;
					}
					
				}
				
			});
			
			/* Add TextView to row. */
			tr.addView(day);
		}

		/* Add row to TableLayout. */
		tl.addView(tr, new TableLayout.LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT));
	}

	private void addSet() {
		Intent i = new Intent(this, WorkoutEdit.class);
		i.putExtra("program_id", mRowId);
		i.putExtra("day_number", mDayNumber);
		startActivityForResult(i, ACTIVITY_ADD_SET);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		fillData();

		switch (requestCode) {
		case ACTIVITY_ADD_SET:
			Log.i("RESULT CODE: ", String.valueOf(resultCode));
			if(resultCode == RESULT_OK){
				Toast.makeText(this, R.string.plan_added, Toast.LENGTH_SHORT)
				.show();
			}
			
			break;
		case ACTIVITY_EDIT_SET:
			Log.i("RESULT CODE: ", String.valueOf(resultCode));

			Toast.makeText(this, R.string.set_edited, Toast.LENGTH_SHORT)
			.show();
			
			break;
		}
	}

}
