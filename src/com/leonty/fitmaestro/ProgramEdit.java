package com.leonty.fitmaestro;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.leonty.fitmaestro.domain.FitmaestroDb;
import com.leonty.fitmaestro.domain.Program;

public class ProgramEdit extends Activity {
	private EditText mTitleText;
	private EditText mDescText;
	private Long mRowId;

	private FitmaestroDb db;
	private Program program;	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		db = new FitmaestroDb(this).open();
		program = new Program(db);

		setContentView(R.layout.edit_program);

		mTitleText = (EditText) findViewById(R.id.edit_name);
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
			Cursor programCursor = program.fetchProgram(mRowId);
			startManagingCursor(programCursor);
			mTitleText.setText(programCursor.getString(programCursor
					.getColumnIndexOrThrow(FitmaestroDb.KEY_TITLE)));
			mDescText.setText(programCursor.getString(programCursor
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
		String desc = mDescText.getText().toString();

		if (mRowId == null) {
			long id = program.createProgram(title, desc);
			if (id > 0) {
				mRowId = id;
			}
		} else {
			program.updateProgram(mRowId, title, desc);
		}
	}
}
