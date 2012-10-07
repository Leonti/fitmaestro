package com.leonty.fitmaestro;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.leonty.fitmaestro.domain.ExerciseGroup;
import com.leonty.fitmaestro.domain.FitmaestroDb;

public class GroupEdit extends Activity {
	private EditText mTitleText;
	private EditText mDescText;
	private Long mRowId;
	private long mSiteId;

	
	private FitmaestroDb db;
	private ExerciseGroup exerciseGroup;	
		
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		db = new FitmaestroDb(this).open();
		exerciseGroup = new ExerciseGroup(db);	

		setContentView(R.layout.edit_group);

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

		mSiteId = 0;
		if (mRowId != null) {
			Cursor group = exerciseGroup.fetchGroup(mRowId, 0);
			startManagingCursor(group);
			mTitleText.setText(group.getString(group
					.getColumnIndexOrThrow(FitmaestroDb.KEY_TITLE)));
			mDescText.setText(group.getString(group
					.getColumnIndexOrThrow(FitmaestroDb.KEY_DESC)));

			mSiteId = group.getLong(group
					.getColumnIndexOrThrow(FitmaestroDb.KEY_SITEID));
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
			long id = exerciseGroup.createGroup(title, desc, 0);
			if (id > 0) {
				mRowId = id;
			}
		} else {
			exerciseGroup.updateGroup(mRowId, title, desc, mSiteId);
		}
	}

}
