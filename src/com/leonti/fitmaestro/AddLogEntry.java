package com.leonti.fitmaestro;

import com.leonti.fitmaestro.R;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class AddLogEntry extends Activity {
	private EditText mTimesText;
	private EditText mWeightText;
	private Long mRowId;
	private Long mExerciseId;
	private ExcercisesDbAdapter mDbHelper;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mDbHelper = new ExcercisesDbAdapter(this);
		mDbHelper.open();

		setContentView(R.layout.exercise_info);

		mTimesText = (EditText) findViewById(R.id.editText_times);
		mWeightText = (EditText) findViewById(R.id.editText_weight);

		Button saveButton = (Button) findViewById(R.id.button_save);

		mRowId = savedInstanceState != null ? savedInstanceState
				.getLong(ExcercisesDbAdapter.KEY_ROWID) : null;
		if (mRowId == null) {
			Bundle extras = getIntent().getExtras();
			mRowId = extras != null ? extras.getLong(
					ExcercisesDbAdapter.KEY_ROWID, 0) : null;
		}

		mExerciseId = savedInstanceState != null ? savedInstanceState
				.getLong(ExcercisesDbAdapter.KEY_EXERCISEID) : null;
		if (mExerciseId == null) {
			Bundle extras = getIntent().getExtras();
			mExerciseId = extras != null ? extras
					.getLong(ExcercisesDbAdapter.KEY_EXERCISEID) : null;
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

		Cursor exerciseCursor = (Cursor) mDbHelper.fetchExercise(mExerciseId);
		startManagingCursor(exerciseCursor);

		int type = exerciseCursor.getInt(exerciseCursor
				.getColumnIndexOrThrow(ExcercisesDbAdapter.KEY_TYPE));
		if (type == 0) { // own weight
			TextView weightLabel = (TextView) findViewById(R.id.text_weight);
			mWeightText.setVisibility(View.GONE);
			mWeightText.setText("0");
			weightLabel.setVisibility(View.GONE);
		}

		if (mRowId != 0) {
			Cursor logEntry = mDbHelper.fetchLogEntry(mRowId);
			startManagingCursor(logEntry);
			mTimesText.setText(logEntry.getString(logEntry
					.getColumnIndexOrThrow(ExcercisesDbAdapter.KEY_TIMES)));
			mWeightText.setText(logEntry.getString(logEntry
					.getColumnIndexOrThrow(ExcercisesDbAdapter.KEY_WEIGHT)));
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putLong(ExcercisesDbAdapter.KEY_ROWID, mRowId);
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
		String times = mTimesText.getText().toString();
		String weight = mWeightText.getText().toString();

		if (times.length() > 0 && weight.length() > 0) {
			if (mRowId == 0) {
				long id = mDbHelper.createLogEntry(mExerciseId, Float
						.valueOf(weight.trim()),
						Integer.parseInt(times.trim()), 0, 0);
				if (id > 0) {
					mRowId = id;
				}
			} else {
				mDbHelper.updateLogEntry(mRowId, Float.valueOf(weight.trim()),
						Integer.parseInt(times.trim()));
			}
		}
	}
}
