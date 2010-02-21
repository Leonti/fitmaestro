package com.leonti.bodyb;


import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.ToggleButton;

public class ExcerciseEdit extends Activity {
	
    private static final int ACTIVITY_GROUP_CREATE=0;

	private EditText mTitleText;
	private EditText mDescText;
	private ToggleButton mType;
	private Spinner mGroup;
	private Long mRowId;
	private Long mGroupId;
    private ExcercisesDbAdapter mDbHelper;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {	
        super.onCreate(savedInstanceState);
        mDbHelper = new ExcercisesDbAdapter(this);
        mDbHelper.open();
        setContentView(R.layout.edit_excercise);
        
        mTitleText = (EditText) findViewById(R.id.edit_name);
        mDescText = (EditText) findViewById(R.id.edit_description);
        mType = (ToggleButton) findViewById(R.id.toggle_type);
        mGroup = (Spinner) findViewById(R.id.spinner_group);
      
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
            Cursor excercise = mDbHelper.fetchExcercise(mRowId);
            startManagingCursor(excercise);
            mTitleText.setText(excercise.getString(
            		excercise.getColumnIndexOrThrow(ExcercisesDbAdapter.KEY_TITLE)));
            mDescText.setText(excercise.getString(
            		excercise.getColumnIndexOrThrow(ExcercisesDbAdapter.KEY_DESC)));
            mType.setChecked(excercise.getInt(
            		excercise.getColumnIndexOrThrow(ExcercisesDbAdapter.KEY_TYPE))==1?true:false);
            mGroupId = excercise.getLong(
            		excercise.getColumnIndexOrThrow(ExcercisesDbAdapter.KEY_GROUPID));            
        }
        fillSpinner();
    }
    
    private void fillSpinner(){  	 
    	// populating spinner in any case
        Cursor GroupsCursor = mDbHelper.fetchAllGroups();
        startManagingCursor(GroupsCursor);
        String[] from = new String[]{ExcercisesDbAdapter.KEY_TITLE};
        int[] to = new int[]{android.R.id.text1};
        SimpleCursorAdapter groups = 
        	    new SimpleCursorAdapter(this,android.R.layout.simple_spinner_item, GroupsCursor, from, to);
        groups.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mGroup.setAdapter(groups);	
        
        // if we are editing - set spinner selection to the right item
        if(mRowId != null){
            if(GroupsCursor != null){
            	int id_index = GroupsCursor.getColumnIndexOrThrow(ExcercisesDbAdapter.KEY_ROWID);
            	if(GroupsCursor.moveToFirst()){
            		int i = 0;
            		do{
            			if(GroupsCursor.getLong(id_index) == mGroupId){
            				mGroup.setSelection(i);
            				break;
            			}
            			i++;
            		}while(GroupsCursor.moveToNext());
            	}
            	
            }
        }
    };
   
    @Override
    protected void onActivityResult(int requestCode, int resultCode, 
                                    Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
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
        String desc = mDescText.getText().toString();
        int type = mType.isChecked()?1:0;
        long group_id = mGroup.getSelectedItemId();
               
        if (mRowId == null) {
        	
        	// create exercise only if title is not empty
            if(title.length() > 0){
            	long id = mDbHelper.createExcercise(title, desc, type, group_id, 0);
            	if (id > 0) {
            		mRowId = id;
            	}
            }
        } else {
            mDbHelper.updateExcercise(mRowId, title, desc, type, group_id, 0);
        }
    }
}
