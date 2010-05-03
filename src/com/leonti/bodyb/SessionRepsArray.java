package com.leonti.bodyb;

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
    private Long mSetsConnectorId;
    
    ArrayList<HashMap<String, String>>  mSessionRepsList = new ArrayList<HashMap<String, String>>();
    
    public SessionRepsArray(Context ctx, Long sessionId, Long exerciseId, Long setsConnectorId){
		this.mCtx = ctx;
		mSessionId = sessionId;
		mExerciseId = exerciseId;
		mSetsConnectorId = setsConnectorId;
		
        mDbHelper = new ExcercisesDbAdapter(mCtx);
        mDbHelper.open();
    }
    
    public ArrayList<HashMap<String, String>> getRepsArray(){

    	// converting cursor(s) data to array list    	
    	mSessionRepsList.clear();
    	Log.i("SETS CONNECTOR ID: ", mSetsConnectorId.toString());
    	Log.i("EXERCISE ID: ", mExerciseId.toString());
    	Log.i("SESSION ID: ", mSessionId.toString());
    	// if it's not 0 - session was created from set so we can get planned reps for this exercise
    	if(mSetsConnectorId != Long.valueOf(0)){
        	Cursor setReps = mDbHelper.fetchRepsForConnector(mSetsConnectorId);  
        	Log.i("DYG: ", "MYG");
        	setReps.moveToFirst();
        	for (int i=0; i < setReps.getCount(); i++) {
        		
        		Long setDetailId = setReps.getLong(
        				setReps.getColumnIndexOrThrow(ExcercisesDbAdapter.KEY_ROWID));
        		
        		Long planned_reps = setReps.getLong(
        				setReps.getColumnIndexOrThrow(ExcercisesDbAdapter.KEY_REPS));
        		
        		Float planned_percentage = setReps.getFloat(
        				setReps.getColumnIndexOrThrow(ExcercisesDbAdapter.KEY_PERCENTAGE));
        		
        		HashMap<String, String> item = new HashMap<String, String>(); 
        		
        		// fetch done from log table and fill it
        		
        		Cursor sessionReps = mDbHelper.fetchSessionRepsEntryBySet(mSessionId, setDetailId);
        		if(sessionReps.getCount() > 0){
        			String id = sessionReps.getString(
        					sessionReps.getColumnIndexOrThrow(ExcercisesDbAdapter.KEY_ROWID));
        			
        			String reps = sessionReps.getString(
        					sessionReps.getColumnIndexOrThrow(ExcercisesDbAdapter.KEY_REPS));
        			
        			String weight = sessionReps.getString(
        					sessionReps.getColumnIndexOrThrow(ExcercisesDbAdapter.KEY_WEIGHT));
        			
            		item.put("id", id);
            		item.put("reps", reps);
            		item.put("weight", weight);	
            		
        		}else{
            		item.put("id", null);
            		item.put("reps", "not_done");
            		item.put("weight", "not_done");	
        		}
        		
        		item.put("set_detail_id", setDetailId.toString());
        		item.put("planned_reps", planned_reps.toString());
        		item.put("planned_weight", planned_percentage.toString());
        		mSessionRepsList.add(item);
        		
        		setReps.moveToNext(); 
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
