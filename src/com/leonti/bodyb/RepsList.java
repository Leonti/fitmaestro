package com.leonti.bodyb;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class RepsList extends ListActivity {

    private ExcercisesDbAdapter mDbHelper;
    private Cursor mRepsForConnectorCursor;
    private Long mSetConnectorId;
    
    private static final int ACTIVITY_CREATE=0;
    private static final int ACTIVITY_EDIT=1;
    private static final int INSERT_ID = Menu.FIRST;
    private static final int DELETE_ID = Menu.FIRST + 1;
    private static final int EDIT_ID = Menu.FIRST + 2;
	    
    private static final String TAG = "RepsList";
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reps_list);
        
        mSetConnectorId = savedInstanceState != null ? savedInstanceState.getLong(ExcercisesDbAdapter.KEY_ROWID)
        : null;
        if (mSetConnectorId == null) {
        	Bundle extras = getIntent().getExtras();            
        	mSetConnectorId = extras != null ? extras.getLong(ExcercisesDbAdapter.KEY_ROWID) 
        			: null;
        }
         
        mDbHelper = new ExcercisesDbAdapter(this);
        mDbHelper.open();
        fillData(); 
        registerForContextMenu(getListView());
    }
    
    private void fillData() {
    	mRepsForConnectorCursor = mDbHelper.fetchRepsForConnector(mSetConnectorId);
        startManagingCursor(mRepsForConnectorCursor);
        String[] from = new String[]{ExcercisesDbAdapter.KEY_REPS, ExcercisesDbAdapter.KEY_PERCENTAGE};
        int[] to = new int[]{R.id.reps_value, R.id.percentage_value};
        SimpleCursorAdapter reps = 
        	    new SimpleCursorAdapter(this, R.layout.reps_list_row, mRepsForConnectorCursor, from, to);
        setListAdapter(reps); 
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, INSERT_ID, 0, R.string.add_entry);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch(item.getItemId()) {
        case INSERT_ID:
            createEntry();
            return true;
        }
       
        return super.onMenuItemSelected(featureId, item);
    }
    
    private void createEntry() {
        Intent i = new Intent(this, AddRepsEntry.class);
        i.putExtra(ExcercisesDbAdapter.KEY_SETS_CONNECTORID, mSetConnectorId);
        startActivityForResult(i, ACTIVITY_CREATE);
    }
    
    private void editEntry(long id) {
        Intent i = new Intent(this, AddRepsEntry.class);
        i.putExtra(ExcercisesDbAdapter.KEY_ROWID, id);
        startActivityForResult(i, ACTIVITY_EDIT);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, 
                                    Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        fillData();
        
        switch(requestCode) {
        case ACTIVITY_EDIT:
        	Toast.makeText(this, R.string.reps_entry_edited, Toast.LENGTH_SHORT).show();
        	break;
        }
    }
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, 
    		ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info;
        info = (AdapterView.AdapterContextMenuInfo) menuInfo;

       // String title = ((TextView) info.targetView).getText().toString();

        menu.setHeaderTitle("Dyg");
        menu.add(0, EDIT_ID, 0, R.string.edit_reps_entry);
        menu.add(0, DELETE_ID, 1, R.string.delete_reps_entry);
    }
    
    @Override
	public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();

    	switch(item.getItemId()) {
        case DELETE_ID:
            mDbHelper.deleteRepsEntry(info.id);
            fillData();
            return true;
        
        case EDIT_ID:
        	editEntry(info.id);
        	return true;
        }
		return super.onContextItemSelected(item);
	}
    
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
 /*
        Intent i = new Intent(this, SetView.class);
        i.putExtra(ExcercisesDbAdapter.KEY_ROWID, id);
        startActivityForResult(i, 5);    
        */
        // SHOW ADDITIONAL INFO FOR THIS Reps ENTRY
    }
	    
	    
}
