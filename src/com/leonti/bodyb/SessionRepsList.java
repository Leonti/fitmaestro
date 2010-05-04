package com.leonti.bodyb;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class SessionRepsList extends ListActivity {

    private ExcercisesDbAdapter mDbHelper;

    private Long mSessionConnectorId;
    private Long mSessionId;
    private Long mExerciseId;
    private Long mExType;
    private Long mSetsConnectorId;
    private Long mListPosition;
    private Dialog mEditRepsDialog;
    private Long mSessionRepsId;
    
    ArrayList<HashMap<String, String>>  mSessionRepsList = new ArrayList<HashMap<String, String>>();  
//    private SimpleAdapter mRepsAdapter;
    
    private static final int ACTIVITY_CREATE=0;
    private static final int ACTIVITY_EDIT=1;
    
    private static final int DIALOG_EDIT_REPS = 2;
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
    	
    	Cursor exerciseCursor = (Cursor) mDbHelper.fetchExcercise(mExerciseId);
    	mExType = exerciseCursor.getLong(exerciseCursor.getColumnIndexOrThrow(ExcercisesDbAdapter.KEY_TYPE));
    	
    	SessionRepsArray repsArray = new SessionRepsArray(this, mSessionId, mExerciseId, mSetsConnectorId); 
    	mSessionRepsList = repsArray.getRepsArray();
    	
    	
        String[] from = new String[]{"reps", "weight", "planned_reps", "planned_weight"};
        int[] to = new int[]{R.id.reps_value, R.id.weight_value, R.id.planned_reps_value, R.id.planned_weight_value};
        
        SpecialAdapter SessionRepsAdapter = new SpecialAdapter(this, mSessionRepsList, R.layout.session_reps_list_row, from, to); 
        
        setListAdapter(SessionRepsAdapter); 
        
        Log.i("EX TYPE: ", String.valueOf(mExType));
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
    	/*
        Intent i = new Intent(this, EditSessionRepsEntry.class);
        i.putExtra("session_id", mSessionId);
        i.putExtra("exercise_id", mExerciseId);
        i.putExtra("set_detail_id", 0);    	
        i.putExtra(ExcercisesDbAdapter.KEY_TYPE, mExType);
        startActivityForResult(i, ACTIVITY_CREATE);
        */
    	mListPosition = null;
    	showDialog(DIALOG_EDIT_REPS);
    	populateRepsDialog();
    }
    
    private void editEntry(long position) {
/*
    	Intent i = new Intent(this, EditSessionRepsEntry.class);
        i.putExtra(ExcercisesDbAdapter.KEY_ROWID, id);
        i.putExtra("set_detail_id", 0);
        i.putExtra(ExcercisesDbAdapter.KEY_TYPE, mExType);
        startActivityForResult(i, ACTIVITY_EDIT);
        
*/
    	/*
    	mListPosition = position;
    	showDialog(DIALOG_EDIT_REPS);
    	*/
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
            
            if(mExType == 0){
            	menu.setHeaderTitle(reps);
            }else{
            	menu.setHeaderTitle(reps + "x" + weight);
            }
	        
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
        	//editEntry(info.position);
        	mListPosition = Long.valueOf(info.position);
        	showDialog(DIALOG_EDIT_REPS);
        	populateRepsDialog();
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
 
        	mListPosition = Long.valueOf(position);
        	showDialog(DIALOG_EDIT_REPS);
        	populateRepsDialog();
        	/*
            Intent i = new Intent(this, EditSessionRepsEntry.class);
            i.putExtra("session_id", mSessionId);
            i.putExtra("exercise_id", mExerciseId);
            i.putExtra("set_detail_id", Long.valueOf(mSessionRepsList.get(position).get("set_detail_id")));
            i.putExtra(ExcercisesDbAdapter.KEY_TYPE, mExType);
            startActivityForResult(i, ACTIVITY_CREATE);
            */
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
    
    @Override
    protected Dialog onCreateDialog(int id){
    	
    	switch (id) {
	        case DIALOG_EDIT_REPS:
	        LayoutInflater factory = LayoutInflater.from(this);
	        final View repsEditView = factory.inflate(R.layout.edit_session_reps_entry, null);

        	final EditText repsText = (EditText) repsEditView.findViewById(R.id.editText_reps);
        	final EditText weightText = (EditText) repsEditView.findViewById(R.id.editText_weight);
        	
	        if(mExType == Long.valueOf(0)){
	       	 	
	        	repsEditView.findViewById(R.id.text_weight).setVisibility(View.GONE);
	       	 	weightText.setText("0");
	       	 	weightText.setVisibility(View.GONE);
	        }
	        
	        mEditRepsDialog = new AlertDialog.Builder(this)
	            .setTitle(R.string.edit_session_reps_entry)
	            .setView(repsEditView)
	            .setPositiveButton(R.string.done, new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int whichButton) {
	
	                    /* User clicked OK so do some stuff */
        
	                	 
	                    String reps = repsText.getText().toString();
	                    String weight = weightText.getText().toString();
	                    
	                    if(reps.length() > 0 && weight.length() > 0){
	                    	
	                    	// entry is new, so we add it
	            	        if (mSessionRepsId == null) {
	            	        	
	            	        	// if it has an entry than it's planned, so we get corresponding setDetailId, if not - it's 0
	            	        	Long setDetailId = mListPosition != null ?
	            	        			Long.valueOf(mSessionRepsList.get(mListPosition.intValue()).get("set_detail_id")) :
	            	        			0;
	            	        	
	            	        	
	            	        	mDbHelper.createSessionRepsEntry(mSessionId, mExerciseId, setDetailId, Integer.parseInt(reps.trim()), Float.valueOf(weight.trim()));
	            	        } else {
	            	        	
	            	        	// entry is old so we update it
	            	        	mDbHelper.updateSessionRepsEntry(mSessionRepsId, Integer.parseInt(reps.trim()), Float.valueOf(weight.trim()));
	            	        }
	            	        
	            	        fillData();
	            	        registerForContextMenu(getListView());
	                	}
	                    
	                }
	            })
	            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int whichButton) {
	
	                    /* User clicked cancel so do some stuff */
	                }
	            })
	            .create();
	        
	        return mEditRepsDialog;
    	}
    	
        return null;
    }
    
    public void populateRepsDialog(){
    	
    	EditText repsText = (EditText) mEditRepsDialog.findViewById(R.id.editText_reps);
    	EditText weightText = (EditText) mEditRepsDialog.findViewById(R.id.editText_weight);
    	
    	// we have an entry in the list
        if (mListPosition != null) {

	        mSessionRepsId = mSessionRepsList.get(mListPosition.intValue()).get("id") != null ? 
	        		Long.valueOf(mSessionRepsList.get(mListPosition.intValue()).get("id")) :
	        		null;
	        
	        // entry has corresponding entry in session - so prepopulate it		
	        if (mSessionRepsId != null) {
		        repsText.setText(mSessionRepsList.get(mListPosition.intValue()).get("reps"));
		        weightText.setText(mSessionRepsList.get(mListPosition.intValue()).get("weight"));
	        }else{
	        	
	        	// it's not done yet - prepopulate it with planned values for easier entry
	        	//  empty values for now
	        	
	        	repsText.setText("");
	        	
		        if(mExType == Long.valueOf(0)){
		       	 	
		        	weightText.setText("0");
		        }
	        	
	        }
	        

	    // empty the values for new extra entry
        }else{
        	mSessionRepsId = null;
        	repsText.setText("");
        	
	        if(mExType == Long.valueOf(0)){
	       	 	
	        	weightText.setText("0");
	        }
        }
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
    	  
    	  if(mExType == 0){
    		  view.findViewById(R.id.x_col).setVisibility(View.GONE);
    		  view.findViewById(R.id.planned_weight_col).setVisibility(View.GONE);
    		  view.findViewById(R.id.x_done_col).setVisibility(View.GONE);
    		  view.findViewById(R.id.weight_col).setVisibility(View.GONE);
    		  
    		  
    		  view.findViewById(R.id.planned_weight_value).setVisibility(View.GONE);
    		  view.findViewById(R.id.weight_value).setVisibility(View.GONE);
    		  view.findViewById(R.id.x_col_value).setVisibility(View.GONE);
    		  view.findViewById(R.id.x_col_value2).setVisibility(View.GONE);
    	  }

    	  return view;
    	}
    }

	    
	
}
