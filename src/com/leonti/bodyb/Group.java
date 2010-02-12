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
import android.widget.AdapterView.AdapterContextMenuInfo;


public class Group extends ListActivity {
	
    private static final int ACTIVITY_CREATE=0;
    private static final int ACTIVITY_EDIT=1;
    private static final int INSERT_ID = Menu.FIRST;
    private static final int DELETE_ID = Menu.FIRST + 1;

    /** The index of the title column */
    private static final int COLUMN_INDEX_TITLE = 1;
    
    private ExcercisesDbAdapter mDbHelper;
    private Cursor mGroupsCursor;
    
    private static final String TAG = "GroupsList";
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_list);
        
        mDbHelper = new ExcercisesDbAdapter(this);
        mDbHelper.open();
        fillData();
        registerForContextMenu(getListView());
    }
    
    private void fillData() {
        mGroupsCursor = mDbHelper.fetchAllGroups();
        startManagingCursor(mGroupsCursor);
        String[] from = new String[]{ExcercisesDbAdapter.KEY_TITLE};
        int[] to = new int[]{R.id.group_name};
        SimpleCursorAdapter groups = 
        	    new SimpleCursorAdapter(this, R.layout.group_list_row, mGroupsCursor, from, to);
        setListAdapter(groups);
    } 

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, INSERT_ID, 0, R.string.add_group);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch(item.getItemId()) {
        case INSERT_ID:
            createGroup();
            return true;
        }
       
        return super.onMenuItemSelected(featureId, item);
    }
    
    private void createGroup() {
        Intent i = new Intent(this, GroupEdit.class);
        startActivityForResult(i, ACTIVITY_CREATE);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, 
                                    Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        fillData();
    }
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, 
    		ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info;
        try {
             info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        } catch (ClassCastException e) {
            Log.e(TAG, "bad menuInfo", e);
            return;
        }

        Cursor cursor = (Cursor) getListAdapter().getItem(info.position);
        if (cursor == null) {
            // For some reason the requested item isn't available, do nothing
            return;
        }
        menu.setHeaderTitle(cursor.getString(COLUMN_INDEX_TITLE));
        menu.add(0, DELETE_ID, 0, R.string.delete_group);
    }
    
    @Override
	public boolean onContextItemSelected(MenuItem item) {
        switch(item.getItemId()) {
        case DELETE_ID:
            AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
            mDbHelper.deleteGroup(info.id);
            fillData();
            return true;
        }
		return super.onContextItemSelected(item);
	}
    
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Intent i = new Intent(this, GroupEdit.class);
        i.putExtra(ExcercisesDbAdapter.KEY_ROWID, id);
        startActivityForResult(i, ACTIVITY_EDIT);        
    }

}
