package com.leonti.bodyb;



import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

public class SetEdit extends Activity {
	private EditText mTitleText;
	private EditText mDescText;
	private Long mRowId;
    private ExcercisesDbAdapter mDbHelper;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDbHelper = new ExcercisesDbAdapter(this);
        mDbHelper.open();
        
        setContentView(R.layout.edit_set);
       
        mTitleText = (EditText) findViewById(R.id.edit_name);
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
            Cursor set = mDbHelper.fetchSet(mRowId);
            startManagingCursor(set);
            mTitleText.setText(set.getString(
                        set.getColumnIndexOrThrow(ExcercisesDbAdapter.KEY_TITLE)));
            mDescText.setText(set.getString(
                    set.getColumnIndexOrThrow(ExcercisesDbAdapter.KEY_DESC)));
            
            fillExcercises();
        }
    }
    
    private void fillExcercises(){
    	Cursor ExcercisesCursor = mDbHelper.fetchExcercisesForGroup(1); //for now
        startManagingCursor(ExcercisesCursor);
        String[] from = new String[]{ExcercisesDbAdapter.KEY_TITLE};
        int[] to = new int[]{R.id.excercise_name};
        SimpleCursorAdapter adapter = 
        	    new SimpleCursorAdapter(this, R.layout.excercise_list_set_row, ExcercisesCursor, from, to);
        ListView exList = (ListView) findViewById(R.id.ex_list);
        exList.setAdapter(adapter);
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

        if (mRowId == null) {
            long id = mDbHelper.createSet(title, desc, 0);
            if (id > 0) {
                mRowId = id;
            }
        } else {
            mDbHelper.updateSet(mRowId, title, desc, 0);
        }
    }
	
}

