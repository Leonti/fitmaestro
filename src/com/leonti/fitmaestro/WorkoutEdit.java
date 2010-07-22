package com.leonti.fitmaestro;

import com.leonti.fitmaestro.R;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class WorkoutEdit extends Activity {
	private EditText mTitleText;
	private EditText mDescText;
	private Long mRowId;
	private Long mProgramId;
	private Long mDayNumber;
	private ExcercisesDbAdapter mDbHelper;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mDbHelper = new ExcercisesDbAdapter(this);
		mDbHelper.open();

		setContentView(R.layout.edit_set);

		mTitleText = (EditText) findViewById(R.id.edit_name);
		mDescText = (EditText) findViewById(R.id.edit_description);

		Button saveButton = (Button) findViewById(R.id.button_save);

		Bundle extras = getIntent().getExtras();
		mRowId = savedInstanceState != null ? savedInstanceState
				.getLong(ExcercisesDbAdapter.KEY_ROWID) : null;

		if (mRowId == null && extras != null) {
			mRowId = extras.getLong(ExcercisesDbAdapter.KEY_ROWID);
		}

		mProgramId = savedInstanceState != null ? savedInstanceState
				.getLong("program_id") : null;

		if (mProgramId == null && extras != null) {
			mProgramId = extras.getLong("program_id");
		}

		mDayNumber = savedInstanceState != null ? savedInstanceState
				.getLong("day_number") : null;

		if (mDayNumber == null && extras != null) {
			mDayNumber = extras.getLong("day_number");
		}

		populateFields();

		saveButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				setResult(RESULT_OK);
				finish();
			}

		});
	}

	private void populateFields() {
		if (mRowId != null && mRowId > 0) {
			Log.i("Set edit ", "Have mRowId?" + Long.toString(mRowId));
			Cursor set = mDbHelper.fetchSet(mRowId);
			startManagingCursor(set);
			mTitleText.setText(set.getString(set
					.getColumnIndexOrThrow(ExcercisesDbAdapter.KEY_TITLE)));
			mDescText.setText(set.getString(set
					.getColumnIndexOrThrow(ExcercisesDbAdapter.KEY_DESC)));

			// fillExcercises();
		}

		if (mProgramId != null && mProgramId > 0) {
			Log.i("Edit Set", "Have program Id " + Long.toString(mProgramId));
		}
	}

	/*
	 * private void fillExcercises(){ Cursor ExcercisesCursor =
	 * mDbHelper.fetchExcercisesForGroup(1); //for now
	 * startManagingCursor(ExcercisesCursor); String[] from = new
	 * String[]{ExcercisesDbAdapter.KEY_TITLE}; int[] to = new
	 * int[]{R.id.excercise_name}; SimpleCursorAdapter adapter = new
	 * SimpleCursorAdapter(this, R.layout.excercise_list_set_row,
	 * ExcercisesCursor, from, to); ListView exList = (ListView)
	 * findViewById(R.id.ex_list); exList.setAdapter(adapter); }
	 */

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putLong(ExcercisesDbAdapter.KEY_ROWID, mRowId);
		outState.putLong("program_id", mProgramId);
		outState.putLong("day_number", mDayNumber);
	}

	@Override
	protected void onPause() {
		super.onPause();
		saveState();
	}

	@Override
	protected void onResume() {
		super.onResume();
		populateFields();
	}

	private void saveState() {
		String title = mTitleText.getText().toString();
		String desc = mDescText.getText().toString();

		if (mRowId == null || mRowId == 0) {
			if (title.length() > 0) {
				long id = mDbHelper.createSet(title, desc, 0);
				Log.i("NEW SET CREATED: ", Long.toString(id));
				if (id > 0) {
					mRowId = id;
				}
				
				// adding this set to program (if id provided)
				// doing it here because we attach workout to program only on creation
				if (mProgramId != null && mProgramId > 0) {
					mDbHelper.addSetToProgram(mProgramId, mRowId, mDayNumber);

					Log.i("Edit Set", "Set added to program with id: "
							+ Long.toString(mProgramId) + " and day_number: "
							+ Long.toString(mDayNumber) + " and set_id: "
							+ Long.toString(mRowId));
				}
				
			}
		} else {
			mDbHelper.updateSet(mRowId, title, desc, 0);
		}

	}

}
