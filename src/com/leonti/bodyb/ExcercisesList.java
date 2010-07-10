package com.leonti.bodyb;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class ExcercisesList extends ListActivity {

    private static final int ACTIVITY_CREATE=0;
    private static final int ACTIVITY_EDIT=1;
    private static final int INSERT_ID = Menu.FIRST;
    
    private ExcercisesDbAdapter mDbHelper;
    private Cursor mExcercisesCursor;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.excercise_list);
        
        mDbHelper = new ExcercisesDbAdapter(this);
        mDbHelper.open();
        fillData();
    }
    
    private void fillData() {
        mExcercisesCursor = mDbHelper.fetchExcercisesForGroup(1); //for now
        startManagingCursor(mExcercisesCursor);
        String[] from = new String[]{ExcercisesDbAdapter.KEY_TITLE};
        int[] to = new int[]{R.id.excercise_name};
        SimpleCursorAdapter excercises = 
        	    new SimpleCursorAdapter(this, R.layout.excercise_list_row, mExcercisesCursor, from, to);
        setListAdapter(excercises);
    } 

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, INSERT_ID, 0, R.string.add_excercise);
        return true;
    }  
    
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch(item.getItemId()) {
        case INSERT_ID:
            createExcercise();
            return true;
        }
       
        return super.onMenuItemSelected(featureId, item);
    }
    
    private void createExcercise() {
        Intent i = new Intent(this, ExcerciseEdit.class);
        startActivityForResult(i, ACTIVITY_CREATE);
    }
    
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Intent i = new Intent(this, ExcerciseEdit.class);
        i.putExtra(ExcercisesDbAdapter.KEY_ROWID, id);
        startActivityForResult(i, ACTIVITY_EDIT);        
    }
    
}
