package com.leonti.bodyb;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class SessionRepsList extends ListActivity {

    private ExcercisesDbAdapter mDbHelper;

    private Long mSessionConnectorId;
    private Long mSessionId;
    private Long mExerciseId;
    private Long mSetsConnectorId;
    
    ArrayList<HashMap<String, String>>  mSessionRepsList = new ArrayList<HashMap<String, String>>();  
//    private SimpleAdapter mRepsAdapter;
    
    private static final int ACTIVITY_CREATE=0;
    private static final int ACTIVITY_EDIT=1;
    private static final int INSERT_ID = Menu.FIRST;
    private static final int DELETE_ID = Menu.FIRST + 1;
    private static final int EDIT_ID = Menu.FIRST + 2;
	    
    private static final String TAG = "SessionRepsList";
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.session_reps_list);
        
        mSessionConnectorId = savedInstanceState != null ? savedInstanceState.getLong(ExcercisesDbAdapter.KEY_ROWID)
        : null;
        if (mSessionConnectorId == null) {
        	Bundle extras = getIntent().getExtras();            
        	mSessionConnectorId = extras != null ? extras.getLong(ExcercisesDbAdapter.KEY_ROWID) 
        			: null;
        }
         
        mDbHelper = new ExcercisesDbAdapter(this);
        mDbHelper.open();
        fillData(); 
        registerForContextMenu(getListView());
    }
    
    private void fillData() {
    	
    	Cursor SessionConnectorCursor = mDbHelper.fetchSessionConnector(mSessionConnectorId);
    	mSessionId = SessionConnectorCursor.getLong(
    			SessionConnectorCursor.getColumnIndexOrThrow(ExcercisesDbAdapter.KEY_SESSIONID));
    	mExerciseId = SessionConnectorCursor.getLong(
    			SessionConnectorCursor.getColumnIndexOrThrow(ExcercisesDbAdapter.KEY_EXERCISEID));
    	mSetsConnectorId = SessionConnectorCursor.getLong(
    			SessionConnectorCursor.getColumnIndexOrThrow(ExcercisesDbAdapter.KEY_SETS_CONNECTORID));
    	
    	SessionRepsArray repsArray = new SessionRepsArray(this, mSessionId, mExerciseId, mSetsConnectorId); 
    	mSessionRepsList = repsArray.getRepsArray();
    	
    	
        String[] from = new String[]{"reps", "weight", "planned_reps", "planned_weight"};
        int[] to = new int[]{R.id.reps_value, R.id.weight_value, R.id.planned_reps_value, R.id.planned_weight_value};
        
        SpecialAdapter SessionRepsAdapter = new SpecialAdapter(this, mSessionRepsList, R.layout.session_reps_list_row, from, to); 
        
        setListAdapter(SessionRepsAdapter); 
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
        Intent i = new Intent(this, EditSessionRepsEntry.class);
        i.putExtra("session_id", mSessionId);
        i.putExtra("exercise_id", mExerciseId);
        i.putExtra("set_detail_id", 0);
        startActivityForResult(i, ACTIVITY_CREATE);
    }
    
    private void editEntry(long id) {
        Intent i = new Intent(this, EditSessionRepsEntry.class);
        i.putExtra(ExcercisesDbAdapter.KEY_ROWID, id);
        i.putExtra("set_detail_id", 0);
        startActivityForResult(i, ACTIVITY_EDIT);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, 
                                    Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        fillData();
        
        switch(requestCode) {
        case ACTIVITY_EDIT:
        	Toast.makeText(this, R.string.session_reps_entry_edited, Toast.LENGTH_SHORT).show();
        	break;
        }
    }
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, 
    		ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info;
        info = (AdapterView.AdapterContextMenuInfo) menuInfo;

       // String title = ((TextView) info.targetView).getText().toString();
       
        
        if(mSessionRepsList.get(info.position).get("id") != null){
        	
            String reps = mSessionRepsList.get(info.position).get("reps");
            String weight = mSessionRepsList.get(info.position).get("weight");
            
	        menu.setHeaderTitle(reps + "x" + weight);
	        menu.add(0, EDIT_ID, 0, R.string.edit_session_reps_entry);
	        menu.add(0, DELETE_ID, 1, R.string.delete_session_reps_entry);
        }
    }
    
    @Override
	public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        
        Long id = Long.valueOf(mSessionRepsList.get(info.position).get("id"));
    	switch(item.getItemId()) {
        case DELETE_ID:
            mDbHelper.deleteSessionRepsEntry(id);
            fillData();
            return true;
        
        case EDIT_ID:
        	editEntry(id);
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
        
        if(mSessionRepsList.get(position).get("id") == null){
        	Log.i("ID: ", "Null!!!");
        	Log.i("SET detail id: ", mSessionRepsList.get(position).get("set_detail_id"));
 
            Intent i = new Intent(this, EditSessionRepsEntry.class);
            i.putExtra("session_id", mSessionId);
            i.putExtra("exercise_id", mExerciseId);
            i.putExtra("set_detail_id", Long.valueOf(mSessionRepsList.get(position).get("set_detail_id")));
            startActivityForResult(i, ACTIVITY_CREATE);
            
        }else{
        	Log.i("ID: ", mSessionRepsList.get(position).get("id"));
        }
        
        /*
        Intent i = new Intent(this, EditSessionRepsEntry.class);
        i.putExtra("session_id", mSessionId);
        i.putExtra("exercise_id", mExerciseId);
        i.putExtra("set_detail_id", 0);
        startActivityForResult(i, ACTIVITY_CREATE);
        */
    }
    
    public class SpecialAdapter extends SimpleAdapter {
    	private int[] colors = new int[] { 0x30FF0000, 0x300000FF };

    	public SpecialAdapter(Context context, ArrayList<HashMap<String, String>> items, int resource, String[] from, int[] to) {
    		super(context, items, resource, from, to);
    	}


    	public View getView(int position, View convertView, ViewGroup parent) {
    	  View view = super.getView(position, convertView, parent);
    	  int colorPos = position % colors.length;
    	  view.setBackgroundColor(colors[colorPos]);
    	  
    	 // TextView plannedReps = (TextView) view.findViewById(R.id.planned_reps_value);
    	 // plannedReps.setText("Dyg: " + String.valueOf(position) );

    	  return view;
    	}
    }

	    
	
}
