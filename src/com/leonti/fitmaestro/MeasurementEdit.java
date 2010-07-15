package com.leonti.fitmaestro;

import com.leonti.fitmaestro.R;

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
    private ExcercisesDbAdapter mDbHelper;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDbHelper = new ExcercisesDbAdapter(this);
        mDbHelper.open();
        
        setContentView(R.layout.edit_measurement_type);
       
        mTitleText = (EditText) findViewById(R.id.edit_name);
        mUnitsText = (EditText) findViewById(R.id.edit_units);
        mDescText = (EditText) findViewById(R.id.edit_description);
      
        Button saveButton = (Button) findViewById(R.id.button_save);
       
        mRowId = savedInstanceState != null ? savedInstanceState.getLong(ExcercisesDbAdapter.KEY_ROWID) 
                : null;
        if (mRowId == null) {
        	Bundle extras = getIntent().getExtras();            
        	mRowId = extras != null ? extras.getLong(ExcercisesDbAdapter.KEY_ROWID) 
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
    	
        if (mRowId != null) {
            Cursor measurement = mDbHelper.fetchMeasurementType(mRowId);
            startManagingCursor(measurement);
            mTitleText.setText(measurement.getString(
                        measurement.getColumnIndexOrThrow(ExcercisesDbAdapter.KEY_TITLE)));
            mUnitsText.setText(measurement.getString(
                    measurement.getColumnIndexOrThrow(ExcercisesDbAdapter.KEY_UNITS)));
            mDescText.setText(measurement.getString(
                    measurement.getColumnIndexOrThrow(ExcercisesDbAdapter.KEY_DESC)));
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
        String title = mTitleText.getText().toString();
        String units = mUnitsText.getText().toString();
        String desc = mDescText.getText().toString();

        if (mRowId == null) {
            long id = mDbHelper.createMeasurementType(title, units, desc);
            if (id > 0) {
                mRowId = id;
            }
        } else {
            mDbHelper.updateMeasurementType(mRowId, title, units, desc);
        }
    }
}
