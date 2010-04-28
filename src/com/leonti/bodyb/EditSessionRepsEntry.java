package com.leonti.bodyb;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class EditSessionRepsEntry extends Activity {
	
	private EditText mRepsText;
	private EditText mWeightText;
	private Long mRowId;
	private Long mSessionId;
	private Long mExerciseId;
	private Long mSetDetailId;
	
    private ExcercisesDbAdapter mDbHelper;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDbHelper = new ExcercisesDbAdapter(this);
        mDbHelper.open();
        
        setContentView(R.layout.edit_session_reps_entry);
       
        mRepsText = (EditText) findViewById(R.id.editText_reps);
        mWeightText = (EditText) findViewById(R.id.editText_weight);
     
        Button saveButton = (Button) findViewById(R.id.button_save);
       
        Bundle extras = getIntent().getExtras();
        
        mRowId = savedInstanceState != null ? savedInstanceState.getLong(ExcercisesDbAdapter.KEY_ROWID) 
                : null;
        if (mRowId == null) {          
        	mRowId = extras != null ? extras.getLong(ExcercisesDbAdapter.KEY_ROWID, 0) 
        			: null;
        }
        
        mSessionId = savedInstanceState != null ? savedInstanceState.getLong("session_id") 
                : null;
        
        if (mSessionId == null && extras != null) {            
        	mSessionId = extras.getLong("session_id");
        }
        
        mExerciseId = savedInstanceState != null ? savedInstanceState.getLong("exercise_id") 
                : null;
        
        if (mExerciseId == null && extras != null) {            
        	mExerciseId = extras.getLong("exercise_id");
        }
        
        mSetDetailId = savedInstanceState != null ? savedInstanceState.getLong("set_detail_id") 
                : null;
        
        if (mSetDetailId == null && extras != null) {
        	
        	mSetDetailId = extras.getLong("set_detail_id");
        }
        
    	Log.i("SET DETAIL ID:", mSetDetailId.toString());
        
        populateFields();
       
        saveButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                setResult(RESULT_OK);
                finish();
            }
          
        });
     }
    
    private void populateFields() {
    	
    	// get type of exercise and hide/show items accordingly
    	/*
        Cursor repsCursor = (Cursor) mDbHelper.fetchExcercise(mExerciseId);
        startManagingCursor(exerciseCursor);
        
        int type = exerciseCursor.getInt(exerciseCursor.getColumnIndexOrThrow(ExcercisesDbAdapter.KEY_TYPE));
        if (type == 0){ //own weight
        	TextView weightLabel = (TextView) findViewById(R.id.text_weight);
        	mWeightText.setVisibility(View.GONE);
        	mWeightText.setText("0");
        	weightLabel.setVisibility(View.GONE);
        }
        */
       
        if (mRowId != 0) {
            Cursor sessionRepsEntry = mDbHelper.fetchSessionRepsEntry(mRowId);
            startManagingCursor(sessionRepsEntry);
            mRepsText.setText(sessionRepsEntry.getString(
            		sessionRepsEntry.getColumnIndexOrThrow(ExcercisesDbAdapter.KEY_REPS)));
            mWeightText.setText(sessionRepsEntry.getString(
            		sessionRepsEntry.getColumnIndexOrThrow(ExcercisesDbAdapter.KEY_WEIGHT)));
        }
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(ExcercisesDbAdapter.KEY_ROWID, mRowId);
        outState.putLong("session_id", mSessionId);
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
        String reps = mRepsText.getText().toString();
        String weight = mWeightText.getText().toString();
        
        if(reps.length() > 0 && weight.length() > 0){
	        if (mRowId == 0) {
	        	long id = mDbHelper.createSessionRepsEntry(mSessionId, mExerciseId, mSetDetailId, Integer.parseInt(reps.trim()), Float.valueOf(weight.trim()));
	        	if (id > 0) {
	        		mRowId = id;
	        	}
	        } else {
	        	mDbHelper.updateSessionRepsEntry(mRowId, Integer.parseInt(reps.trim()), Float.valueOf(weight.trim()));
	        }
    	}
    }
}
