package com.leonti.bodyb;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class AddRepsEntry extends Activity {
	
	private EditText mRepsText;
	private EditText mPercentageText;
	private Long mRowId;
	private Long mSetConnectorId;
    private ExcercisesDbAdapter mDbHelper;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDbHelper = new ExcercisesDbAdapter(this);
        mDbHelper.open();
        
        setContentView(R.layout.edit_reps_entry);
       
        mRepsText = (EditText) findViewById(R.id.editText_reps);
        mPercentageText = (EditText) findViewById(R.id.editText_percentage);
     
        Button saveButton = (Button) findViewById(R.id.button_save);
       
        mRowId = savedInstanceState != null ? savedInstanceState.getLong(ExcercisesDbAdapter.KEY_ROWID) 
                : null;
        if (mRowId == null) {
        	Bundle extras = getIntent().getExtras();            
        	mRowId = extras != null ? extras.getLong(ExcercisesDbAdapter.KEY_ROWID, 0) 
        			: null;
        }
        
        mSetConnectorId = savedInstanceState != null ? savedInstanceState.getLong(ExcercisesDbAdapter.KEY_SETS_CONNECTORID) 
                : null;
        if (mSetConnectorId == null) {
        	Bundle extras = getIntent().getExtras();            
        	mSetConnectorId = extras != null ? extras.getLong(ExcercisesDbAdapter.KEY_SETS_CONNECTORID) 
        			: null;
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
            Cursor repsEntry = mDbHelper.fetchRepsEntry(mRowId);
            startManagingCursor(repsEntry);
            mRepsText.setText(repsEntry.getString(
            		repsEntry.getColumnIndexOrThrow(ExcercisesDbAdapter.KEY_REPS)));
            mPercentageText.setText(repsEntry.getString(
            		repsEntry.getColumnIndexOrThrow(ExcercisesDbAdapter.KEY_PERCENTAGE)));
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
        String reps = mRepsText.getText().toString();
        String percentage = mPercentageText.getText().toString();
        
        if(reps.length() > 0 && percentage.length() > 0){
	        if (mRowId == 0) {
	        	long id = mDbHelper.createRepsEntry(mSetConnectorId, Integer.parseInt(reps.trim()), Float.valueOf(percentage.trim()));
	        	if (id > 0) {
	        		mRowId = id;
	        	}
	        } else {
	        	mDbHelper.updateRepsEntry(mRowId, Integer.parseInt(reps.trim()), Float.valueOf(percentage.trim()));
	        }
    	}
    }
}
