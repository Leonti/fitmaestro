package com.leonty.fitmaestro;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.leonty.fitmaestro.domain.FitmaestroDb;
import com.leonty.fitmaestro.domain.Program;
import com.leonty.fitmaestro.domain.Workout;

public class WorkoutEdit extends Activity {
	private EditText mTitleText;
	private EditText mDescText;
	private Long mRowId;
	private Long mProgramId;
	private Long mDayNumber;

	private FitmaestroDb db;	
	private Workout set;	
	private Program program;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		db = new FitmaestroDb(this).open();
		set = new Workout(db);
		program = new Program(db);

		setContentView(R.layout.edit_set);

		mTitleText = (EditText) findViewById(R.id.edit_name);
		mDescText = (EditText) findViewById(R.id.edit_description);

		Button saveButton = (Button) findViewById(R.id.button_save);

		Bundle extras = getIntent().getExtras();
		mRowId = savedInstanceState != null ? savedInstanceState
				.getLong(FitmaestroDb.KEY_ROWID) : null;

		if (mRowId == null && extras != null) {
			mRowId = extras.getLong(FitmaestroDb.KEY_ROWID);
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
	
	@Override
	protected void onDestroy() {
		db.close();
		super.onDestroy();
	}

	private void populateFields() {
		if (mRowId != null && mRowId > 0) {
			Log.i("Set edit ", "Have mRowId?" + Long.toString(mRowId));
			Cursor setCursor = set.fetchSet(mRowId);
			startManagingCursor(setCursor);
			mTitleText.setText(setCursor.getString(setCursor
					.getColumnIndexOrThrow(FitmaestroDb.KEY_TITLE)));
			mDescText.setText(setCursor.getString(setCursor
					.getColumnIndexOrThrow(FitmaestroDb.KEY_DESC)));

			// fillExcercises();
		}

		if (mProgramId != null && mProgramId > 0) {
			Log.i("Edit Set", "Have program Id " + Long.toString(mProgramId));
			
			// we are adding this workout to a program - title can be prepopulated and hidden
			mTitleText.setText(String.valueOf(mDayNumber) + " day (" + String.valueOf(mProgramId) + ")");
			findViewById(R.id.text_name).setVisibility(View.GONE);
			findViewById(R.id.edit_name).setVisibility(View.GONE);
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putLong(FitmaestroDb.KEY_ROWID, mRowId);
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
				long id = set.createSet(title, desc, 0);
				Log.i("NEW SET CREATED: ", Long.toString(id));
				if (id > 0) {
					mRowId = id;
				}
				
				// adding this set to program (if id provided)
				// doing it here because we attach workout to program only on creation
				if (mProgramId != null && mProgramId > 0) {
					program.addSetToProgram(mProgramId, mRowId, mDayNumber);

					Log.i("Edit Set", "Set added to program with id: "
							+ Long.toString(mProgramId) + " and day_number: "
							+ Long.toString(mDayNumber) + " and set_id: "
							+ Long.toString(mRowId));
				}
				
			}
		} else {
			set.updateSet(mRowId, title, desc, 0);
		}

	}

}
