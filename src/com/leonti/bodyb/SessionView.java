package com.leonti.bodyb;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
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
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.TableRow.LayoutParams;

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
        SessionViewCursorAdapter excercises = 
        	    new SessionViewCursorAdapter(this, R.layout.sessionview_list_row, mExercisesForSessionCursor, from, to);
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

    	super.onListItemClick(l, v, position, id);
        Intent i = new Intent(this, SessionRepsList.class);
        i.putExtra(ExcercisesDbAdapter.KEY_ROWID, id);
        startActivity(i);    

    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(ExcercisesDbAdapter.KEY_ROWID, mRowId);
        outState.putLong("sets_connector_id", mSetsConnectorId);
    }
    
    protected class SessionViewCursorAdapter extends SimpleCursorAdapter {

    	Activity mActivity;

    	public SessionViewCursorAdapter(Activity activity, int layout, Cursor c, String[] from, int[] to) {
    		super(activity, layout, c, from, to);

    		mActivity = activity;

    	}
    	
    	
    	

    	@Override
    	public void bindView(View view, Context context, Cursor cursor) {
    		super.bindView(view, context, cursor);
    		
    		Log.i("BIND", "bind view called");

    		Long sessionId = mRowId;
    		Long exerciseId = cursor.getLong(
        			cursor.getColumnIndexOrThrow(ExcercisesDbAdapter.KEY_EXERCISEID));
    		
    		Long exType = cursor.getLong(
    				cursor.getColumnIndexOrThrow(ExcercisesDbAdapter.KEY_TYPE));
    		
    		Long setsConnectorId = cursor.getLong(
        			cursor.getColumnIndexOrThrow(ExcercisesDbAdapter.KEY_SETS_CONNECTORID));
    		
        	SessionRepsArray repsArray = new SessionRepsArray(SessionView.this, sessionId, exerciseId, setsConnectorId); 
        	ArrayList<HashMap<String, String>> sessionRepsList = repsArray.getRepsArray();
    		
            TableLayout repsTable = (TableLayout) view.findViewById(R.id.reps_table);
            
            repsTable.removeViews(1, repsTable.getChildCount() - 1);

            // if 0 - own weight - don't show percentage values
            if(exType == Long.valueOf(0)){
           	 repsTable.findViewById(R.id.x_col).setVisibility(View.GONE);
           	 repsTable.findViewById(R.id.planned_weight_col).setVisibility(View.GONE);
           	 repsTable.findViewById(R.id.x_done_col).setVisibility(View.GONE);
           	 repsTable.findViewById(R.id.weight_col).setVisibility(View.GONE);
            }else{
             repsTable.findViewById(R.id.x_col).setVisibility(View.VISIBLE);
             repsTable.findViewById(R.id.planned_weight_col).setVisibility(View.VISIBLE);
           	 repsTable.findViewById(R.id.x_done_col).setVisibility(View.VISIBLE);
           	 repsTable.findViewById(R.id.weight_col).setVisibility(View.VISIBLE);	            	 
            }
            
            
            for (int i = 0; i < sessionRepsList.size(); i++) {
            	
        	    // Create a new row to be added. 
        	    TableRow tr = new TableRow(SessionView.this);
        	    tr.setLayoutParams(new LayoutParams(
        	                   LayoutParams.FILL_PARENT,
        	                   LayoutParams.WRAP_CONTENT));  
        	    
	   	         // planned reps 
        	    String plannedReps = sessionRepsList.get(i).get("planned_reps");
	   	        TextView plannedRepsTxt = new TextView(SessionView.this);
	   	        plannedRepsTxt.setText(plannedReps);
	   	        plannedRepsTxt.setLayoutParams(new LayoutParams(
	   	                   LayoutParams.FILL_PARENT,
	   	                   LayoutParams.WRAP_CONTENT));
	   	        tr.addView(plannedRepsTxt);
	   	        
	   	        TextView xTxt = new TextView(SessionView.this);
	   	        xTxt.setText("x");
	   	        xTxt.setLayoutParams(new LayoutParams(
	   	                   LayoutParams.FILL_PARENT,
	   	                   LayoutParams.WRAP_CONTENT));
	   	        tr.addView(xTxt);
	   	        
        	    String plannedWeight = sessionRepsList.get(i).get("planned_weight");
	   	        TextView plannedWeightTxt = new TextView(SessionView.this);
	   	        plannedWeightTxt.setText(plannedWeight);
	   	        plannedWeightTxt.setLayoutParams(new LayoutParams(
	   	                   LayoutParams.FILL_PARENT,
	   	                   LayoutParams.WRAP_CONTENT));
	   	        tr.addView(plannedWeightTxt);
	   	        
	   	        // done reps
        	    String reps = sessionRepsList.get(i).get("reps");
	   	        TextView repsTxt = new TextView(SessionView.this);
	   	        repsTxt.setText(reps);
	   	        repsTxt.setLayoutParams(new LayoutParams(
	   	                   LayoutParams.FILL_PARENT,
	   	                   LayoutParams.WRAP_CONTENT));
	   	        tr.addView(repsTxt);
	   	        
	   	        TextView xTxt2 = new TextView(SessionView.this);
	   	        xTxt2.setText("x");
	   	        xTxt2.setLayoutParams(new LayoutParams(
	   	                   LayoutParams.FILL_PARENT,
	   	                   LayoutParams.WRAP_CONTENT));
	   	        tr.addView(xTxt2);
	   	        
        	    String weight = sessionRepsList.get(i).get("weight");
	   	        TextView weightTxt = new TextView(SessionView.this);
	   	        weightTxt.setText(weight);
	   	        weightTxt.setLayoutParams(new LayoutParams(
	   	                   LayoutParams.FILL_PARENT,
	   	                   LayoutParams.WRAP_CONTENT));
	   	        tr.addView(weightTxt);
	   	         
	   	         // Add row to TableLayout. 

	             repsTable.addView(tr, new TableLayout.LayoutParams(
	      	             LayoutParams.FILL_PARENT,
	      	             LayoutParams.WRAP_CONTENT)); 
	             
	             // if 0 - own weight - don't show percentage values
	             if(exType == Long.valueOf(0)){
	            	 xTxt.setVisibility(View.GONE);
	            	 plannedWeightTxt.setVisibility(View.GONE);
	            	 xTxt2.setVisibility(View.GONE);
	            	 weightTxt.setVisibility(View.GONE);
	             }

            } 
            

    	}

    	}
}
