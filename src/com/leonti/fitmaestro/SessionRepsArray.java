package com.leonti.fitmaestro;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

public class SessionRepsArray {

	private final Context mCtx;
    private ExcercisesDbAdapter mDbHelper;
    
    private Cursor mRepsForSessionCursor;
    
    // free - without sets detail predefined
    private Cursor mFreeRepsForSessionCursor;
    
    private Long mSessionId;
    private Long mExerciseId;
    private Long mSessionsConnectorId;
    
    ArrayList<HashMap<String, String>>  mSessionRepsList = new ArrayList<HashMap<String, String>>();
    
    public SessionRepsArray(Context ctx, Long sessionId, Long exerciseId, Long sessionsConnectorId){
		this.mCtx = ctx;
		mSessionId = sessionId;
		mExerciseId = exerciseId;
		mSessionsConnectorId = sessionsConnectorId;
		
        mDbHelper = new ExcercisesDbAdapter(mCtx);
        mDbHelper.open();
    }
    
    public ArrayList<HashMap<String, String>> getRepsArray(){

    	// converting cursor(s) data to array list    	
    	mSessionRepsList.clear();
    	Log.i("SESSIONS CONNECTOR ID: ", mSessionsConnectorId.toString());
    	Log.i("EXERCISE ID: ", mExerciseId.toString());
    	Log.i("SESSION ID: ", mSessionId.toString());
    	// if it's not 0 - session was created from set so we can get planned reps for this exercise
    	if(mSessionsConnectorId != Long.valueOf(0)){
        	Cursor sessionReps = mDbHelper.fetchRepsForSessionConnector(mSessionsConnectorId);  
        	Log.i("DYG: ", "MYG");
        	sessionReps.moveToFirst();
        	for (int i=0; i < sessionReps.getCount(); i++) {
        		
        		Long sessionDetailId = sessionReps.getLong(
        				sessionReps.getColumnIndexOrThrow(ExcercisesDbAdapter.KEY_ROWID));
        		
        		Long planned_reps = sessionReps.getLong(
        				sessionReps.getColumnIndexOrThrow(ExcercisesDbAdapter.KEY_REPS));
        		
        		Float planned_percentage = sessionReps.getFloat(
        				sessionReps.getColumnIndexOrThrow(ExcercisesDbAdapter.KEY_PERCENTAGE));
        		
        		HashMap<String, String> item = new HashMap<String, String>(); 
        		
        		// fetch done from log table and fill it
        		
        		Cursor sessionDoneReps = mDbHelper.fetchDoneSessionReps(mSessionId, sessionDetailId);
        		if(sessionDoneReps.getCount() > 0){
        			String id = sessionDoneReps.getString(
        					sessionDoneReps.getColumnIndexOrThrow(ExcercisesDbAdapter.KEY_ROWID));
        			
        			String reps = sessionDoneReps.getString(
        					sessionDoneReps.getColumnIndexOrThrow(ExcercisesDbAdapter.KEY_REPS));
        			
        			String weight = sessionDoneReps.getString(
        					sessionDoneReps.getColumnIndexOrThrow(ExcercisesDbAdapter.KEY_WEIGHT));
        			
            		item.put("id", id);
            		item.put("reps", reps);
            		item.put("weight", weight);	
            		
        		}else{
            		item.put("id", null);
            		item.put("reps", "not_done");
            		item.put("weight", "not_done");	
        		}
        		
        		item.put("session_detail_id", sessionDetailId.toString());
        		item.put("planned_reps", planned_reps.toString());
        		item.put("planned_weight", planned_percentage.toString());
        		mSessionRepsList.add(item);
        		
        		sessionReps.moveToNext(); 
        	} 
    	}

    	

    	mFreeRepsForSessionCursor = mDbHelper.fetchFreeSessionReps(mSessionId, mExerciseId);    	
    	mFreeRepsForSessionCursor.moveToFirst(); 
    	for (int i=0; i < mFreeRepsForSessionCursor.getCount(); i++) {

    		Long repsId = mFreeRepsForSessionCursor.getLong(
    				mFreeRepsForSessionCursor.getColumnIndexOrThrow(ExcercisesDbAdapter.KEY_ROWID));
    		
    		Long reps = mFreeRepsForSessionCursor.getLong(
    				mFreeRepsForSessionCursor.getColumnIndexOrThrow(ExcercisesDbAdapter.KEY_REPS));
    		
    		Float weight = mFreeRepsForSessionCursor.getFloat(
    				mFreeRepsForSessionCursor.getColumnIndexOrThrow(ExcercisesDbAdapter.KEY_WEIGHT));
    		
    		HashMap<String, String> item = new HashMap<String, String>(); 
    		
    		item.put("id", repsId.toString());
    		item.put("reps", reps.toString());
    		item.put("weight", weight.toString());
    		item.put("planned_reps", "extra");
    		item.put("planned_weight", "extra");
    		mSessionRepsList.add(item);
    		
    		mFreeRepsForSessionCursor.moveToNext(); 
    	}
    	
    	return mSessionRepsList;
    }
	
}
