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
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class SessionView extends ListActivity {

    private ExcercisesDbAdapter mDbHelper;
    private Cursor mExercisesForSessionCursor;
    private Long mRowId;
    private Long mSetsConnectorId;
    
    private static final int ADD_ID = Menu.FIRST;
    private static final int DELETE_ID = Menu.FIRST + 1;
    private static final int ACTIVITY_ADD = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sessionview_list);
        
        Bundle extras = getIntent().getExtras();
        mRowId = savedInstanceState != null ? savedInstanceState.getLong(ExcercisesDbAdapter.KEY_ROWID) 
                : null;

        if (mRowId == null && extras != null) {      	            
        	mRowId = extras.getLong(ExcercisesDbAdapter.KEY_ROWID);
        }
        
        mSetsConnectorId = savedInstanceState != null ? savedInstanceState.getLong("sets_connector_id") 
                : null;
        
        if (mSetsConnectorId == null && extras != null) {            
        	mSetsConnectorId = extras.getLong("sets_connector_id");
        }
         
        mDbHelper = new ExcercisesDbAdapter(this);
        mDbHelper.open();
        fillData(); 
        registerForContextMenu(getListView());
    }
    
    private void fillData() {
    	mExercisesForSessionCursor = mDbHelper.fetchExercisesForSession(mRowId);
        startManagingCursor(mExercisesForSessionCursor);
        String[] from = new String[]{ExcercisesDbAdapter.KEY_TITLE};
        int[] to = new int[]{R.id.exercise_name};
        SimpleCursorAdapter excercises = 
        	    new SimpleCursorAdapter(this, R.layout.sessionview_list_row, mExercisesForSessionCursor, from, to);
        setListAdapter(excercises); 
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, 
                                    Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        Bundle extras = intent.getExtras();
    	Long ExerciseId = extras != null ? extras.getLong(ExcercisesDbAdapter.KEY_EXERCISEID) 
    			: null;
    	mDbHelper.addExerciseToSession(mRowId, ExerciseId, mSetsConnectorId);
    	fillData();
    	Log.i("EXERCISE ID FROM ACTIVITY: ", String.valueOf(ExerciseId));
    	Log.i("SESSION ID FROM ACTIVITY: ", String.valueOf(mRowId));
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, ADD_ID, 0, R.string.add_exercise_to_session);
        return true;
    }
    
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch(item.getItemId()) {
        case ADD_ID:
            addExercise();
            return true;
        }
       
        return super.onMenuItemSelected(featureId, item);
    }
    
    private void addExercise() {
        Intent i = new Intent(this, Expandable2.class);
        startActivityForResult(i, ACTIVITY_ADD);
    }
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, 
    		ContextMenuInfo menuInfo) {
    	AdapterView.AdapterContextMenuInfo info;

        info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        String title = ((TextView) info.targetView).getText().toString();
        menu.setHeaderTitle(title);        
        menu.add(0, DELETE_ID, 0, R.string.remove_from_session); 
    }
    
    @Override
	public boolean onContextItemSelected(MenuItem item) {
        switch(item.getItemId()) {
        case DELETE_ID:
            AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
            mDbHelper.deleteExerciseFromSession(info.id);
            fillData();
        	Toast.makeText(this, R.string.exercise_removed_from_session, Toast.LENGTH_SHORT).show();
            return true;
        }
		return super.onContextItemSelected(item);
	}
    
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
 /* SHOW REPS FOR SESSION
    	super.onListItemClick(l, v, position, id);
        Intent i = new Intent(this, RepsList.class);
        i.putExtra(ExcercisesDbAdapter.KEY_ROWID, id);
        startActivity(i);    
        */
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(ExcercisesDbAdapter.KEY_ROWID, mRowId);
        outState.putLong("sets_connector_id", mSetsConnectorId);
    }
}
