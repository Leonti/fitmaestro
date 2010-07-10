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
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class MeasurementsList extends ListActivity {

    private static final int ACTIVITY_CREATE=0;
    private static final int ACTIVITY_EDIT=1;
    private static final int INSERT_ID = Menu.FIRST;
    private static final int DELETE_ID = Menu.FIRST + 1;
    private static final int EDIT_ID = Menu.FIRST + 2;
    
    private ExcercisesDbAdapter mDbHelper;
    private Cursor mMeasurementsCursor;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.meas_types_list);
        
        mDbHelper = new ExcercisesDbAdapter(this);
        mDbHelper.open();
        fillData();
        registerForContextMenu(getListView());
    }
    
    private void fillData() {
        mMeasurementsCursor = mDbHelper.fetchAllMeasurementTypes();
        startManagingCursor(mMeasurementsCursor);
        String[] from = new String[]{ExcercisesDbAdapter.KEY_TITLE};
        int[] to = new int[]{R.id.measurement_name};
        SimpleCursorAdapter measurements = 
        	    new SimpleCursorAdapter(this, R.layout.meas_types_list_row, mMeasurementsCursor, from, to);
        setListAdapter(measurements);
    } 

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, INSERT_ID, 0, R.string.add_measurement);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch(item.getItemId()) {
        case INSERT_ID:
            createMeasurement();
            return true;
        }
       
        return super.onMenuItemSelected(featureId, item);
    }
    
    private void createMeasurement() {
        Intent i = new Intent(this, MeasurementEdit.class);
        startActivityForResult(i, ACTIVITY_CREATE);
    }
    
    private void editMeasurement(long id) {
        Intent i = new Intent(this, MeasurementEdit.class);
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
        	Toast.makeText(this, R.string.measurement_edited, Toast.LENGTH_SHORT).show();
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
        menu.add(0, EDIT_ID, 0, R.string.edit_measurement);
        menu.add(0, DELETE_ID, 1, R.string.delete_measurement);
    }
    
    @Override
	public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
    	
        switch(item.getItemId()) {
        case DELETE_ID:
            mDbHelper.deleteMeasurementType(info.id);
            fillData();
            return true;
            
        case EDIT_ID:
        	editMeasurement(info.id);
        	return true;
        }

		return super.onContextItemSelected(item);
	}
    
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        
        Intent i = new Intent(this, MeasLogEntries.class);
        i.putExtra(ExcercisesDbAdapter.KEY_ROWID, id);
        startActivity(i); 
    }
}
