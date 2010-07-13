package com.leonti.bodyb;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ExpandableListActivity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;


public class ImportExercises extends ExpandableListActivity {

    private int mResult;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new FetchExercises().execute();
    }
    
    private class FetchExercises extends AsyncTask<Void, Integer, Long> {
    	
    	private ProgressDialog mProgress = new ProgressDialog(ImportExercises.this);
    	
    	
        protected Long doInBackground(Void... arg0) {

        	//Synchronization sync = new Synchronization(SynchronizationView.this);
        	Imports imports = new Imports(ImportExercises.this);
        	
			try {
				JSONArray exercises = imports.getPublicExercises();
				
				for (int i = 0; i < exercises.length(); i++) {
					JSONObject group = exercises.getJSONObject(i);
					Log.i("Group name: ", group.getString("title"));
				}
				
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

        	if(mResult == ServerJson.NO_CONNECTION){
        		Toast.makeText(ImportExercises.this, R.string.no_connection, Toast.LENGTH_LONG).show(); 
        	}
        }

    }
    
}
