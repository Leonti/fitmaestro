package com.leonty.fitmaestro;

import android.app.Activity;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import com.leonty.fitmaestro.domain.Exercise;
import com.leonty.fitmaestro.domain.ExerciseGroup;
import com.leonty.fitmaestro.domain.FitmaestroDb;

public class ExerciseView extends Activity {

	private TextView mTitleText;
	private TextView mDescText;
	private TextView mMax;
	private TextView mType;
	private TextView mGroup;
	private WebView  mImageHtml;
	private Long mRowId;
	private Long mGroupId;
	private int mTypeVal;
	private SharedPreferences mPrefs;
	private String mUnits;

	private FitmaestroDb db;
	private Exercise exercise;	
	private ExerciseGroup exerciseGroup;	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		db = new FitmaestroDb(this).open();
		exercise = new Exercise(db);
		exerciseGroup = new ExerciseGroup(db);		
		
		setContentView(R.layout.exercise_view);

		mTitleText = (TextView) findViewById(R.id.exercise_name);
		mDescText = (TextView) findViewById(R.id.exercise_desc);
		mMax = (TextView) findViewById(R.id.exercise_max);
		mType = (TextView) findViewById(R.id.exercise_type);
		mGroup = (TextView) findViewById(R.id.exercise_group);
		mImageHtml = (WebView) findViewById(R.id.exercise_image);

		mRowId = savedInstanceState != null ? savedInstanceState
				.getLong(FitmaestroDb.KEY_ROWID) : null;
		if (mRowId == null) {
			Bundle extras = getIntent().getExtras();
			mRowId = extras != null ? extras
					.getLong(FitmaestroDb.KEY_ROWID) : null;
		}
		
		mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		mUnits = mPrefs.getString("units", getText(R.string.default_unit).toString());

		fillData();

	}
	
	@Override
	protected void onDestroy() {
		db.close();
		super.onDestroy();
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putLong(FitmaestroDb.KEY_ROWID, mRowId);
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		fillData();
	}
	
	private void fillData() {


		Cursor excercise = exercise.fetchExercise(mRowId);
		startManagingCursor(excercise);
		mTitleText.setText(excercise.getString(excercise
				.getColumnIndexOrThrow(FitmaestroDb.KEY_TITLE)));
		mDescText.setText(excercise.getString(excercise
				.getColumnIndexOrThrow(FitmaestroDb.KEY_DESC)));

		mTypeVal = excercise
		.getInt(excercise
				.getColumnIndexOrThrow(FitmaestroDb.KEY_TYPE));
		
		if (mTypeVal == 1) {
			
			// hiding max reps caption
			findViewById(R.id.max_reps).setVisibility(View.GONE);
			
			mMax.setText(excercise
					.getString(excercise
							.getColumnIndexOrThrow(FitmaestroDb.KEY_MAX_WEIGHT)) + " " + mUnits);
			mType.setText(getText(R.string.with_weight));
		}else{
			mMax.setText(excercise.getString(excercise
					.getColumnIndexOrThrow(FitmaestroDb.KEY_MAX_REPS)));
			findViewById(R.id.max_weight).setVisibility(View.GONE);
			mType.setText(getText(R.string.own_weight));
		}		


		mGroupId = excercise.getLong(excercise
				.getColumnIndexOrThrow(FitmaestroDb.KEY_GROUPID));
		Cursor groupCursor = exerciseGroup.fetchGroup(mGroupId, 0);
		startManagingCursor(groupCursor);
		mGroup.setText(groupCursor.getString(groupCursor
				.getColumnIndexOrThrow(FitmaestroDb.KEY_TITLE)));
		
		  String mimetype = "text/html";
		  String encoding = "UTF-8";
		  //data/data/com.leonti.fitmaestro/files
		  String htmldata = "<html><body>Here is the image: <img height=\"42\" width=\"42\" src=\"file:///data/data/com.leonti.fitmaestro/files/ieroglify.jpg\"></img>end of image</body></html>";

		  mImageHtml.loadData(htmldata,
	               mimetype,
	               encoding);
	}
	
}
