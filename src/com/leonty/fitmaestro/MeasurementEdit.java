package com.leonty.fitmaestro;

import com.leonty.fitmaestro.domain.FitmaestroDb;
import com.leonty.fitmaestro.domain.Measurement;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MeasurementEdit extends Activity {
	private EditText mTitleText;
	private EditText mUnitsText;
	private EditText mDescText;
	private Long mRowId;

	private FitmaestroDb db;
	private Measurement measurement;		
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		db = new FitmaestroDb(this).open();
		measurement = new Measurement(db);		

		setContentView(R.layout.edit_measurement_type);

		mTitleText = (EditText) findViewById(R.id.edit_name);
		mUnitsText = (EditText) findViewById(R.id.edit_units);
		mDescText = (EditText) findViewById(R.id.edit_description);

		Button saveButton = (Button) findViewById(R.id.button_save);

		mRowId = savedInstanceState != null ? savedInstanceState
				.getLong(FitmaestroDb.KEY_ROWID) : null;
		if (mRowId == null) {
			Bundle extras = getIntent().getExtras();
			mRowId = extras != null ? extras
					.getLong(FitmaestroDb.KEY_ROWID) : null;
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

		if (mRowId != null) {
			Cursor measurementCursor = measurement.fetchMeasurementType(mRowId);
			startManagingCursor(measurementCursor);
			mTitleText.setText(measurementCursor.getString(measurementCursor
					.getColumnIndexOrThrow(FitmaestroDb.KEY_TITLE)));
			mUnitsText.setText(measurementCursor.getString(measurementCursor
					.getColumnIndexOrThrow(FitmaestroDb.KEY_UNITS)));
			mDescText.setText(measurementCursor.getString(measurementCursor
					.getColumnIndexOrThrow(FitmaestroDb.KEY_DESC)));
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putLong(FitmaestroDb.KEY_ROWID, mRowId);
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
		String units = mUnitsText.getText().toString();
		String desc = mDescText.getText().toString();

		if (mRowId == null) {
			long id = measurement.createMeasurementType(title, units, desc);
			if (id > 0) {
				mRowId = id;
			}
		} else {
			measurement.updateMeasurementType(mRowId, title, units, desc);
		}
	}
}
