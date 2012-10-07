package com.leonty.fitmaestro;

import java.util.Date;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.leonty.fitmaestro.domain.FitmaestroDb;
import com.leonty.fitmaestro.domain.Session;

public class SessionEdit extends Activity {

	private EditText mTitleText;
	private EditText mDescText;
	private Long mRowId;
	private Long mProgramsConnectorId;
	private DateFormats mDateFormats;

	private FitmaestroDb db;
	private Session session;	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		db = new FitmaestroDb(this).open();
		session = new Session(db);		
		
		setContentView(R.layout.edit_session);

		mTitleText = (EditText) findViewById(R.id.edit_name);
		mDescText = (EditText) findViewById(R.id.edit_description);

		Button saveButton = (Button) findViewById(R.id.button_save);

		Bundle extras = getIntent().getExtras();
		mRowId = savedInstanceState != null ? savedInstanceState
				.getLong(FitmaestroDb.KEY_ROWID) : null;

		if (mRowId == null && extras != null) {
			mRowId = extras.getLong(FitmaestroDb.KEY_ROWID);
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
		db.close();
		super.onDestroy();
	}

	private void populateFields() {
		if (mRowId != null && mRowId > 0) {

			Cursor sessionCursor = session.fetchSession(mRowId);
			
			startManagingCursor(sessionCursor);
			mTitleText.setText(sessionCursor.getString(sessionCursor
					.getColumnIndexOrThrow(FitmaestroDb.KEY_TITLE)));
			mDescText.setText(sessionCursor.getString(sessionCursor
					.getColumnIndexOrThrow(FitmaestroDb.KEY_DESC)));

			mProgramsConnectorId = sessionCursor
					.getLong(sessionCursor
							.getColumnIndexOrThrow(FitmaestroDb.KEY_PROGRAMS_CONNECTORID));
		}else{
			Date date = new Date();
			mTitleText.setText(mDateFormats.getWithYearFromDate(date));
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putLong(FitmaestroDb.KEY_ROWID, mRowId);
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
				long id = session.createSession(title, desc,
						mProgramsConnectorId);
				Log.i("NEW SESSION CREATED: ", Long.toString(id));
				if (id > 0) {
					mRowId = id;
				}
			}
		} else {
			session.updateSession(mRowId, title, desc, "INPROGRESS");
		}
	}
}
