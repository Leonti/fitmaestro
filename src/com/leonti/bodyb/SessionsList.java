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

public class SessionsList extends ListActivity {
	
    private static final int ACTIVITY_CREATE=0;
    private static final int ACTIVITY_EDIT=1;
    private static final int INSERT_ID = Menu.FIRST;
    private static final int DELETE_ID = Menu.FIRST + 1;
    private static final int EDIT_ID = Menu.FIRST + 2;
    
    private ExcercisesDbAdapter mDbHelper;
    private Cursor mSessionsCursor;
    private String mFilter;
    
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.session_list);
        
        mDbHelper = new ExcercisesDbAdapter(this);
        mDbHelper.open();

        Bundle extras = getIntent().getExtras();
        mFilter = savedInstanceState != null ? savedInstanceState.getString("filter") 
                : null;        
        if (mFilter == null && extras != null) {            
        	mFilter = extras.getString("filter");
        }
        
        fillData();
        registerForContextMenu(getListView());
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("filter", mFilter);
    }
    
    private void fillData() {
    	

		Log.i("SESSION FILTER:", mFilter);
		mSessionsCursor = mDbHelper.fetchFilteredSessions(mFilter);   	
        startManagingCursor(mSessionsCursor);
        String[] from = new String[]{ExcercisesDbAdapter.KEY_TITLE};
        int[] to = new int[]{R.id.session_name};
        SimpleCursorAdapter sessions = 
        	    new SimpleCursorAdapter(this, R.layout.session_list_row, mSessionsCursor, from, to);
        setListAdapter(sessions);
    } 

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, INSERT_ID, 0, R.string.add_session);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch(item.getItemId()) {
        case INSERT_ID:
            createSession();
            return true;
        }
       
        return super.onMenuItemSelected(featureId, item);
    }
    
    private void createSession() {
        Intent i = new Intent(this, SessionEdit.class);
        startActivityForResult(i, ACTIVITY_CREATE);
    }
    
    private void editSession(long id) {
        Intent i = new Intent(this, SessionEdit.class);
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
        	Toast.makeText(this, R.string.session_edited, Toast.LENGTH_SHORT).show();
        	break;
        }
    }
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, 
    		ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info;
        info = (AdapterView.AdapterContextMenuInfo) menuInfo;

        String title = ((TextView) info.targetView).getText().toString();

        menu.setHeaderTitle(title);
        menu.add(0, EDIT_ID, 0, R.string.edit_session);
        menu.add(0, DELETE_ID, 1, R.string.delete_session);
    }
    
    @Override
	public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();

    	switch(item.getItemId()) {
        case DELETE_ID:
             mDbHelper.deleteSession(info.id);
            fillData();
            return true;
        
        case EDIT_ID:
        	editSession(info.id);
        	return true;
        }
		return super.onContextItemSelected(item);
	}
    
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Intent i = new Intent(this, SessionView.class);
        i.putExtra(ExcercisesDbAdapter.KEY_ROWID, id);
        startActivityForResult(i, 5);      
    }

}

