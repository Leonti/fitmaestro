package com.leonti.fitmaestro;

import java.util.Date;

import com.leonti.fitmaestro.R;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SessionEdit extends Activity {

	private EditText mTitleText;
	private EditText mDescText;
	private Long mRowId;
	private Long mProgramsConnectorId;
	private ExcercisesDbAdapter mDbHelper;
	private DateFormats mDateFormats;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mDbHelper = new ExcercisesDbAdapter(this);
		mDbHelper.open();

		setContentView(R.layout.edit_session);

		mTitleText = (EditText) findViewById(R.id.edit_name);
		mDescText = (EditText) findViewById(R.id.edit_description);

		Button saveButton = (Button) findViewById(R.id.button_save);

		Bundle extras = getIntent().getExtras();
		mRowId = savedInstanceState != null ? savedInstanceState
				.getLong(ExcercisesDbAdapter.KEY_ROWID) : null;

		if (mRowId == null && extras != null) {
			mRowId = extras.getLong(ExcercisesDbAdapter.KEY_ROWID);
		}

		mProgramsConnectorId = savedInstanceState != null ? savedInstanceState
				.getLong("programs_connector_id") : null;

		if (mProgramsConnectorId == null && extras != null) {
			mProgramsConnectorId = extras.getLong("programs_connector_id");
		}
		
		mDateFormats = new DateFormats(this);

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
		mDbHelper.close();
		super.onDestroy();
	}

	private void populateFields() {
		if (mRowId != null && mRowId > 0) {

			Cursor session = mDbHelper.fetchSession(mRowId);
			startManagingCursor(session);
			mTitleText.setText(session.getString(session
					.getColumnIndexOrThrow(ExcercisesDbAdapter.KEY_TITLE)));
			mDescText.setText(session.getString(session
					.getColumnIndexOrThrow(ExcercisesDbAdapter.KEY_DESC)));

			mProgramsConnectorId = session
					.getLong(session
							.getColumnIndexOrThrow(ExcercisesDbAdapter.KEY_PROGRAMS_CONNECTORID));
		}else{
			Date date = new Date();
			mTitleText.setText(mDateFormats.getWithYearFromDate(date));
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putLong(ExcercisesDbAdapter.KEY_ROWID, mRowId);
		outState.putLong("programs_connector_id", mProgramsConnectorId);
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

				if (mProgramsConnectorId == null) {
					mProgramsConnectorId = Long.valueOf(0);
				}
				long id = mDbHelper.createSession(title, desc,
						mProgramsConnectorId);
				Log.i("NEW SESSION CREATED: ", Long.toString(id));
				if (id > 0) {
					mRowId = id;
				}
			}
		} else {
			mDbHelper.updateSession(mRowId, title, desc, "INPROGRESS");
		}
	}
}
