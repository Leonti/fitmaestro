package com.leonti.bodyb;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ExpandableListActivity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;
import android.widget.Toast;


public class ImportExercises extends ExpandableListActivity {

    private int mResult;
    private static final String TITLE = "TITLE";
    private static final String DESC = "DESC";
    
    private JSONArray exercisesData;
    private ExpandableListAdapter mAdapter;  
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new FetchExercises().execute();
    }
    
    public void fillList() throws JSONException{
        List<Map<String, String>> groupData = new ArrayList<Map<String, String>>();
        List<List<Map<String, String>>> childData = new ArrayList<List<Map<String, String>>>();
        
		for (int i = 0; i < exercisesData.length(); i++) {
			JSONObject group = exercisesData.getJSONObject(i);
			
            Map<String, String> curGroupMap = new HashMap<String, String>();
            groupData.add(curGroupMap);
            curGroupMap.put(TITLE, group.getString("title"));
            curGroupMap.put(DESC, group.getString("desc"));
 
			Log.i("Group name: ", group.getString("title"));
            JSONArray group_exercises = group.getJSONArray("exercises");
            
            Log.i("Exercises: ", String.valueOf(group_exercises.length()));
            
            List<Map<String, String>> children = new ArrayList<Map<String, String>>();
            for (int j = 0; j < group_exercises.length(); j++) {
            	JSONObject exercise = group_exercises.getJSONObject(j);
            	Map<String, String> curChildMap = new HashMap<String, String>();
                children.add(curChildMap);
                curChildMap.put(TITLE, exercise.getString("title"));
                curChildMap.put(DESC, exercise.getString("desc"));
                
    			Log.i("Exercise name: ", exercise.getString("title"));
            }
            childData.add(children);	
		}
		
        // Set up our adapter
        mAdapter = new SimpleExpandableListAdapter(
                this,
                groupData,
                android.R.layout.simple_expandable_list_item_1,
                new String[] { TITLE, DESC },
                new int[] { android.R.id.text1, android.R.id.text2 },
                childData,
                R.layout.import_child_row,
                new String[] { TITLE, DESC },
                new int[] { R.id.title, R.id.desc }
                );
        setListAdapter(mAdapter);
    }
    
    public boolean onChildClick(
            ExpandableListView parent, 
            View v, 
            int groupPosition,
            int childPosition,
            long id) {
        Log.d( "Child click: ", "onChildClick: "+childPosition );
        CheckBox cb = (CheckBox)v.findViewById( R.id.check1 );
        if( cb != null )
            cb.toggle();
        return false;
    }

    public void  onGroupExpand  (int groupPosition) {
        Log.d( "Group expand: ","onGroupExpand: "+groupPosition );
    }
    
    private class FetchExercises extends AsyncTask<Void, Integer, Long> {
    	
    	private ProgressDialog mProgress = new ProgressDialog(ImportExercises.this);
    	
    	
        protected Long doInBackground(Void... arg0) {

        	//Synchronization sync = new Synchronization(SynchronizationView.this);
        	Imports imports = new Imports(ImportExercises.this);
        	
			try {
				exercisesData = imports.getPublicExercises();
				
			} catch (JSONException e) {
				Log.i("ERROR: ", e.getMessage());
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
            return Long.valueOf(1);
        }
        
        protected void onPreExecute(){

        	mProgress.setMessage(getString(R.string.fetching));  
        	mProgress.show();        	
        }

        protected void onProgressUpdate(Integer... progress) {
        	Log.i("PROGRESS: ", String.valueOf(progress[0]));
           // setProgressPercent(progress[0]);
        }

        protected void onPostExecute(Long result) {
        	mProgress.dismiss();
			try {
				fillList();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	if(mResult == ServerJson.NO_CONNECTION){
        		Toast.makeText(ImportExercises.this, R.string.no_connection, Toast.LENGTH_LONG).show(); 
        	}
        }

    }
    
}
