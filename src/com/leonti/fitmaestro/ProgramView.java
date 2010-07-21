package com.leonti.fitmaestro;

import java.util.HashMap;

import com.leonti.fitmaestro.R;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
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

	private static final int ACTIVITY_ADD_SET = 0;

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

	private void fillData() {
		Log.i("FILL", "filling program view");
		mProgramSetsCursor = mDbHelper.fetchProgramSets(mRowId);
		startManagingCursor(mProgramSetsCursor);
		mProgramSetsCursor.moveToFirst();

		/* Find Tablelayout defined in main.xml */
		TableLayout tl = (TableLayout) findViewById(R.id.program_table);
		tl.removeViews(1, tl.getChildCount() - 1);
		addWeek(tl, 1);
		addWeek(tl, 8);
		addWeek(tl, 15);
	}

	public void addWeek(TableLayout tl, int startDay) {

		/* Create a new row to be added. */
		TableRow tr = new TableRow(this);
		tr.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT));

		for (int i = startDay; i < startDay + 7; i++) {

			HashMap<String, Long> dayData = new HashMap<String, Long>();
			dayData.put("day_number", Long.valueOf(i));

			String toAdd = "";
			if (mProgramSetsCursor.getCount() > 0) {
				// check if we have this day in the db
				Long dayNumber = mProgramSetsCursor
						.getLong(mProgramSetsCursor
								.getColumnIndexOrThrow(ExcercisesDbAdapter.KEY_DAY_NUMBER));

				if (Long.valueOf(i) == dayNumber) {
					Log.i("DAY_NUMBER MATCH: ", Long.toString(dayNumber));
					toAdd = "(!)";
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
			day.setText(Integer.toString(i) + toAdd);
			day.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
					LayoutParams.WRAP_CONTENT));

			day.setTag(dayData);
			day.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Log.i("Listener: ", mRowId + " " + v.getTag().toString());

					HashMap<String, Long> dayClickedData = (HashMap<String, Long>) v
							.getTag();

					Log.i("HASH DATA DAY NUMBER", dayClickedData.get(
							"day_number").toString());

					mDayNumber = dayClickedData.get("day_number");
					if (dayClickedData.get("set_id") != null) {
						Log.i("HASH DATA SET_ID", dayClickedData.get("set_id")
								.toString());
						Intent i = new Intent(ProgramView.this, WorkoutView.class);
						i.putExtra(ExcercisesDbAdapter.KEY_ROWID,
								dayClickedData.get("set_id"));
						i.putExtra("programs_connector_id", dayClickedData
								.get("programs_connector_id"));
						startActivityForResult(i, 5);
					} else {
						addSet();
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
			Toast.makeText(this, R.string.plan_added, Toast.LENGTH_SHORT)
					.show();
			break;
		}
	}

}
