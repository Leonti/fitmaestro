package com.leonti.bodyb;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;

public class Synchronization extends Activity {

    private ExcercisesDbAdapter mDbHelper;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDbHelper = new ExcercisesDbAdapter(this);
        mDbHelper.open();
        
        setContentView(R.layout.synchronization);
       

        
        try {
			startSynchronization();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
       
     }
    
    public void startSynchronization() throws JSONException{
    	
		 ServerJson Js = new ServerJson();
		 JSONObject jsonUpdateData = new JSONObject();
		 JSONObject sendFirstData = prepareLocalUpdates(""); 
		 jsonUpdateData = Js.getUpdates("prishelec@gmail.com", "proverko", "", sendFirstData);
		 if(jsonUpdateData != null){

			 // start of GROUPS
			 JSONArray groups = jsonUpdateData.getJSONArray("groups");
			 JSONArray groupsReturn = new JSONArray();
			 
			 for (int i = 0; i < groups.length(); i++) {
				 JSONObject group = groups.getJSONObject(i);
				 
				 long siteId = group.getLong("id");
				 String title = group.getString("title");
				 String desc = group.getString("desc");
				 String updated =  group.getString("updated");
				 long rowId = group.getLong("phone_id");
				 
				 // this entry exist only on web server, adding it
				 if(rowId == 0){
					 
					 Cursor groupCursor = mDbHelper.fetchGroup(0, siteId);
					 
					 // we do not want to doubled entries when connection is bad for example
					 if(groupCursor.getCount() == 0){
						 
						// long phoneId = mDbHelper.createGroup(title, desc, siteId);
						 
						 // now put phone id's back to update site database
						 JSONObject groupReturn = new JSONObject();
						 //groupReturn.put("phone_id", phoneId);
						 groupReturn.put("site_id", siteId);
						 groupsReturn.put(groupReturn);
					 }
				 }else{ // it's already on the phone, so we need to update it
					 	
					 //	mDbHelper.updateGroup(rowId, title, desc, siteId);
				 }
                

             }
			 
			 // end of GROUPS
			 
		 }else{
			 Log.i("WTF", "Fuck!");
		 }

    	
    }
    
    public JSONObject prepareLocalUpdates(String updated){
    	
    	JSONObject dataToSend = new JSONObject();

    	try {
			dataToSend.put("groups", prepareGroups(updated));
			dataToSend.put("exercises", prepareExercises(updated));
			dataToSend.put("sets", prepareSets(updated));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	return dataToSend;
    }
    
    public JSONArray prepareGroups(String updated) throws JSONException{
    	
    	JSONArray groupsReturn = new JSONArray();
    	Cursor updatedGroups = mDbHelper.fetchUpdatedGroups(updated);
    	
    	updatedGroups.moveToFirst();
		for (int i=0; i<updatedGroups.getCount(); i++)
		{
			JSONObject jsonRow = new JSONObject();
			
			String rowId = updatedGroups.getString(updatedGroups.getColumnIndex(ExcercisesDbAdapter.KEY_ROWID));
			jsonRow.put("id", rowId);
			String title = updatedGroups.getString(updatedGroups.getColumnIndex(ExcercisesDbAdapter.KEY_TITLE));
			jsonRow.put("title", title);
			String desc = updatedGroups.getString(updatedGroups.getColumnIndex(ExcercisesDbAdapter.KEY_DESC));
			jsonRow.put("desc", desc);
			String siteId = updatedGroups.getString(updatedGroups.getColumnIndex(ExcercisesDbAdapter.KEY_SITEID));
			jsonRow.put("site_id", siteId);
			String updatedSend = updatedGroups.getString(updatedGroups.getColumnIndex(ExcercisesDbAdapter.KEY_UPDATED));
			jsonRow.put("updated", updatedSend);

			groupsReturn.put(jsonRow);
			updatedGroups.moveToNext();
		} 
    	
    	return groupsReturn;
    }
    
    public JSONArray prepareExercises(String updated) throws JSONException{
    	
    	JSONArray exercisesReturn = new JSONArray();
    	Cursor updatedExercises = mDbHelper.fetchUpdatedExercises(updated);
    	
    	updatedExercises.moveToFirst();
		for (int i=0; i<updatedExercises.getCount(); i++)
		{
			JSONObject jsonRow = new JSONObject();
			
			String rowId = updatedExercises.getString(updatedExercises.getColumnIndex(ExcercisesDbAdapter.KEY_ROWID));
			jsonRow.put("id", rowId);
			String title = updatedExercises.getString(updatedExercises.getColumnIndex(ExcercisesDbAdapter.KEY_TITLE));
			jsonRow.put("title", title);
			String desc = updatedExercises.getString(updatedExercises.getColumnIndex(ExcercisesDbAdapter.KEY_DESC));
			jsonRow.put("desc", desc);
			String groupId = updatedExercises.getString(updatedExercises.getColumnIndex(ExcercisesDbAdapter.KEY_GROUPID));
			jsonRow.put("group_id", groupId);
			String type = updatedExercises.getString(updatedExercises.getColumnIndex(ExcercisesDbAdapter.KEY_TYPE));
			jsonRow.put("type", type);
			String siteId = updatedExercises.getString(updatedExercises.getColumnIndex(ExcercisesDbAdapter.KEY_SITEID));
			jsonRow.put("site_id", siteId);
			String updatedSend = updatedExercises.getString(updatedExercises.getColumnIndex(ExcercisesDbAdapter.KEY_UPDATED));
			jsonRow.put("updated", updatedSend);
			
			exercisesReturn.put(jsonRow);
			updatedExercises.moveToNext();
		}
    	
    	return exercisesReturn;
    }
    
    public JSONArray prepareSets(String updated) throws JSONException{
    	
    	JSONArray setsReturn = new JSONArray();
    	Cursor updatedSets = mDbHelper.fetchUpdatedSets(updated);
    	
    	updatedSets.moveToFirst();
		for (int i=0; i<updatedSets.getCount(); i++)
		{
			JSONObject jsonRow = new JSONObject();
			
			String rowId = updatedSets.getString(updatedSets.getColumnIndex(ExcercisesDbAdapter.KEY_ROWID));
			jsonRow.put("id", rowId);
			String title = updatedSets.getString(updatedSets.getColumnIndex(ExcercisesDbAdapter.KEY_TITLE));
			jsonRow.put("title", title);
			String desc = updatedSets.getString(updatedSets.getColumnIndex(ExcercisesDbAdapter.KEY_DESC));
			jsonRow.put("desc", desc);
			String siteId = updatedSets.getString(updatedSets.getColumnIndex(ExcercisesDbAdapter.KEY_SITEID));
			jsonRow.put("site_id", siteId);
			String updatedSend = updatedSets.getString(updatedSets.getColumnIndex(ExcercisesDbAdapter.KEY_UPDATED));
			jsonRow.put("updated", updatedSend);

			setsReturn.put(jsonRow);
			updatedSets.moveToNext();
		}
		
    	return setsReturn;
    }
}
