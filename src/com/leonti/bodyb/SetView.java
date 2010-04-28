package com.leonti.bodyb;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ExpandableListView.ExpandableListContextMenuInfo;

public class SetView extends ListActivity {

    private ExcercisesDbAdapter mDbHelper;
    private Cursor mExercisesForSetCursor;
    private Long mSetId;
    private Long mProgramsConnectorId;
    
    private static final int ADD_ID = Menu.FIRST;
    private static final int DELETE_ID = Menu.FIRST + 1;
    private static final int START_SESSION_ID = Menu.FIRST + 2;
    private static final int ACTIVITY_ADD = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setview_list);
        
        Bundle extras = getIntent().getExtras();
        mSetId = savedInstanceState != null ? savedInstanceState.getLong(ExcercisesDbAdapter.KEY_ROWID)
        : null;
        if (mSetId == null) {           
        	mSetId = extras != null ? extras.getLong(ExcercisesDbAdapter.KEY_ROWID) 
        			: null;
        }
        
        mProgramsConnectorId = savedInstanceState != null ? savedInstanceState.getLong("programs_connector_id") 
                : null;
        
        if (mProgramsConnectorId == null && extras != null) {            
        	mProgramsConnectorId = extras.getLong("programs_connector_id");
        }
         
        mDbHelper = new ExcercisesDbAdapter(this);
        mDbHelper.open();
        fillData(); 
        registerForContextMenu(getListView());
    }
    
    private void fillData() {
    	mExercisesForSetCursor = mDbHelper.fetchExercisesForSet(mSetId);
        startManagingCursor(mExercisesForSetCursor);
        String[] from = new String[]{ExcercisesDbAdapter.KEY_TITLE};
        int[] to = new int[]{R.id.exercise_name};
        SimpleCursorAdapter excercises = 
        	    new SimpleCursorAdapter(this, R.layout.setview_list_row, mExercisesForSetCursor, from, to);
        setListAdapter(excercises); 
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, 
                                    Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        Bundle extras = intent.getExtras();
    	Long ExerciseId = extras != null ? extras.getLong(ExcercisesDbAdapter.KEY_EXERCISEID) 
    			: null;
    	mDbHelper.addExerciseToSet(mSetId, ExerciseId, 0);
    	fillData();
    	Log.i("EXERCISE ID FROM ACTIVITY: ", String.valueOf(ExerciseId));
    	Log.i("SET ID FROM ACTIVITY: ", String.valueOf(mSetId));
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, ADD_ID, 0, R.string.add_exercise_to_set);
        menu.add(0, START_SESSION_ID, 0, R.string.start_session);
        return true;
    }
    
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch(item.getItemId()) {
        case ADD_ID:
            addExercise();
            break;
            
        case START_SESSION_ID:
            startSession();
            break;
        }
       
        return super.onMenuItemSelected(featureId, item);
    }
    
    private void addExercise() {
        Intent i = new Intent(this, Expandable2.class);
        startActivityForResult(i, ACTIVITY_ADD);
    }
    
    private void startSession() {

    	if(mProgramsConnectorId == null){
    		mProgramsConnectorId = Long.valueOf(0);
    	} 
    	Long sessionId = mDbHelper.createSession("Some session", "Some desc", mProgramsConnectorId);
    	
    	// add exercises to session
    	mExercisesForSetCursor.moveToFirst(); 
    	for (int i=0; i<mExercisesForSetCursor.getCount(); i++) {
    		
    		Long setsConnectorId = mExercisesForSetCursor.getLong(
    				mExercisesForSetCursor.getColumnIndexOrThrow(ExcercisesDbAdapter.KEY_ROWID));
    		
    		Long exerciseId = mExercisesForSetCursor.getLong(
    				mExercisesForSetCursor.getColumnIndexOrThrow(ExcercisesDbAdapter.KEY_EXERCISEID));
    		
    		mDbHelper.addExerciseToSession(sessionId, exerciseId, setsConnectorId);
    		
    		mExercisesForSetCursor.moveToNext(); 
    	} 
    	
        Intent i = new Intent(this, SessionView.class);
        i.putExtra(ExcercisesDbAdapter.KEY_ROWID, sessionId);
        startActivity(i);
    }
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, 
    		ContextMenuInfo menuInfo) {
    	AdapterView.AdapterContextMenuInfo info;

        info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        String title = ((TextView) info.targetView).getText().toString();
        menu.setHeaderTitle(title);        
        menu.add(0, DELETE_ID, 0, R.string.remove_from_set); 
    }
    
    @Override
	public boolean onContextItemSelected(MenuItem item) {
        switch(item.getItemId()) {
        case DELETE_ID:
            AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
            mDbHelper.deleteExerciseFromSet(info.id);
            fillData();
        	Toast.makeText(this, R.string.exercise_removed_from_set, Toast.LENGTH_SHORT).show();
            return true;
        }
		return super.onContextItemSelected(item);
	}
    
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Intent i = new Intent(this, RepsList.class);
        i.putExtra(ExcercisesDbAdapter.KEY_ROWID, id);
        startActivity(i);      
    }
    
}
