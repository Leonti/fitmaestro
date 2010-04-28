package com.leonti.bodyb;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.util.Log;

public class Synchronization {
	
	private final Context mCtx;
    private ExcercisesDbAdapter mDbHelper;
    DateFormat iso8601Format;
    public HashMap<String, String> exerciseFields = new HashMap<String, String>();
    public HashMap<String, String> groupFields = new HashMap<String, String>();
    public HashMap<String, String> logFields = new HashMap<String, String>();
    public HashMap<String, String> programFields = new HashMap<String, String>();
    public HashMap<String, String> programs_connectorFields = new HashMap<String, String>();
    public HashMap<String, String> sessionFields = new HashMap<String, String>();
    public HashMap<String, String> sessions_connectorFields = new HashMap<String, String>();
    
    public HashMap<String, String> setFields = new HashMap<String, String>();
    public HashMap<String, String> sets_connectorFields = new HashMap<String, String>();
    public HashMap<String, String> sets_detailFields = new HashMap<String, String>();
	
	public Synchronization(Context ctx){
	
		this.mCtx = ctx;
;
        mDbHelper = new ExcercisesDbAdapter(mCtx);
        mDbHelper.open();
        
        iso8601Format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        iso8601Format.setTimeZone(TimeZone.getTimeZone("UTC"));
        
        fillHashes();
	}
	
    public void fillHashes(){
    
    	
    	
    	exerciseFields.put("title", ExcercisesDbAdapter.KEY_TITLE);
    	exerciseFields.put("desc", ExcercisesDbAdapter.KEY_DESC);
    	exerciseFields.put("group_id", ExcercisesDbAdapter.KEY_GROUPID);
    	exerciseFields.put("ex_type", ExcercisesDbAdapter.KEY_TYPE);
    	exerciseFields.put("max_weight", ExcercisesDbAdapter.KEY_MAX_WEIGHT);
    	exerciseFields.put("max_reps", ExcercisesDbAdapter.KEY_MAX_REPS);
 
    	groupFields.put("title", ExcercisesDbAdapter.KEY_TITLE);
    	groupFields.put("desc", ExcercisesDbAdapter.KEY_DESC);
    	
    	logFields.put("exercise_id", ExcercisesDbAdapter.KEY_EXERCISEID);
    	logFields.put("weight", ExcercisesDbAdapter.KEY_WEIGHT);
    	logFields.put("reps", ExcercisesDbAdapter.KEY_REPS);
    	logFields.put("done", ExcercisesDbAdapter.KEY_DONE);
    	logFields.put("session_id", ExcercisesDbAdapter.KEY_SESSIONID);
    	logFields.put("sets_detail_id", ExcercisesDbAdapter.KEY_SETS_DETAILID);
    	
    	programFields.put("title", ExcercisesDbAdapter.KEY_TITLE);
    	programFields.put("desc", ExcercisesDbAdapter.KEY_DESC); 
    	
    	programs_connectorFields.put("program_id", ExcercisesDbAdapter.KEY_PROGRAMID);
    	programs_connectorFields.put("set_id", ExcercisesDbAdapter.KEY_SETID);
    	programs_connectorFields.put("day_number", ExcercisesDbAdapter.KEY_DAY_NUMBER);
  
    	sessionFields.put("programs_connector_id", ExcercisesDbAdapter.KEY_PROGRAMS_CONNECTORID);
    	sessionFields.put("title", ExcercisesDbAdapter.KEY_TITLE);
    	sessionFields.put("desc", ExcercisesDbAdapter.KEY_DESC); 
    	sessionFields.put("status", ExcercisesDbAdapter.KEY_STATUS); 
    	
    	sessions_connectorFields.put("session_id", ExcercisesDbAdapter.KEY_SESSIONID);
    	sessions_connectorFields.put("sets_connector_id", ExcercisesDbAdapter.KEY_SETS_CONNECTORID);
    	sessions_connectorFields.put("exercise_id", ExcercisesDbAdapter.KEY_EXERCISEID);
 
    	
    	setFields.put("title", ExcercisesDbAdapter.KEY_TITLE);
    	setFields.put("desc", ExcercisesDbAdapter.KEY_DESC);
    	
    	
    	sets_connectorFields.put("set_id", ExcercisesDbAdapter.KEY_SETID);
    	sets_connectorFields.put("exercise_id", ExcercisesDbAdapter.KEY_EXERCISEID);
    	
    	sets_detailFields.put("sets_connector_id", ExcercisesDbAdapter.KEY_SETS_CONNECTORID);
    	sets_detailFields.put("reps", ExcercisesDbAdapter.KEY_REPS);
    	sets_detailFields.put("percentage", ExcercisesDbAdapter.KEY_PERCENTAGE);
 
    
    
    	
    }
    
    public void startSynchronization() throws JSONException{
    	/*
    	 * 1. Send to site new/updated items
    	 * 2. Site performs updates and gives back his items but with relations not complete
    	 * 3. Perform updates on the phone and give back id's
    	 * 4. Site receives id's - repairs relations and gives back items with updated relations
    	 * 5. we perform updates from p.3 and give nothing back    
    	*/
    	 String lastUpdated = mDbHelper.getLastUpdated();
		 Log.i("Last updated", lastUpdated);
    	 String authKey = mDbHelper.getAuthKey();
		 ServerJson Js = new ServerJson();
		 JSONObject jsonUpdateData = new JSONObject();
		 JSONObject sendFirstData = prepareLocalUpdates(lastUpdated); 
		 jsonUpdateData = Js.getUpdates(authKey, sendFirstData);
		 if(jsonUpdateData != null){
			 
			 JSONObject jsonRelationsData = new JSONObject();
			 jsonRelationsData = Js.finishUpdates(authKey, updateItems(jsonUpdateData));
			 
			 if(jsonRelationsData != null){
				 
				 updateItems(jsonRelationsData);
				 Log.i("Last updated", "Saving last updated!");
				 mDbHelper.setLastUpdated();
			 }else{
				 Log.i("WTF2", "Second Fuck!");
			 }
		 }else{
			 Log.i("WTF", "Fuck!");
		 }    	
    }
    
    public JSONObject updateItems(JSONObject jsonUpdateData) throws JSONException{
    	
		 //sending data back for phone_id updates
		 JSONObject backData =  new JSONObject();

		 JSONArray groups = jsonUpdateData.getJSONArray("groups");			 
		 JSONArray groupsReturn = performItemsUpdate(ExcercisesDbAdapter.DATABASE_GROUPS_TABLE, groups, groupFields);
		 backData.put("groups", groupsReturn);
		 
		 JSONArray exercises = jsonUpdateData.getJSONArray("exercises");			 
		 JSONArray exercisesReturn = performItemsUpdate(ExcercisesDbAdapter.DATABASE_EXERCISES_TABLE, exercises, exerciseFields);
		 backData.put("exercises", exercisesReturn);
		 
		 JSONArray sets = jsonUpdateData.getJSONArray("sets");			 
		 JSONArray setsReturn = performItemsUpdate(ExcercisesDbAdapter.DATABASE_SETS_TABLE, sets, setFields);
		 backData.put("sets", setsReturn);

		 JSONArray sets_connector = jsonUpdateData.getJSONArray("sets_connector");			 
		 JSONArray sets_connectorReturn = performItemsUpdate(ExcercisesDbAdapter.DATABASE_SETS_CONNECTOR_TABLE, sets_connector, sets_connectorFields);
		 backData.put("sets_connector", sets_connectorReturn);
		 
		 JSONArray sets_detail = jsonUpdateData.getJSONArray("sets_detail");			 
		 JSONArray sets_detailReturn = performItemsUpdate(ExcercisesDbAdapter.DATABASE_SETS_DETAIL_TABLE, sets_detail, sets_detailFields);
		 backData.put("sets_detail", sets_detailReturn);

		 JSONArray programs = jsonUpdateData.getJSONArray("programs");			 
		 JSONArray programsReturn = performItemsUpdate(ExcercisesDbAdapter.DATABASE_PROGRAMS_TABLE, programs, programFields);
		 backData.put("programs", programsReturn); 
		 
		 JSONArray programs_connector = jsonUpdateData.getJSONArray("programs_connector");			 
		 JSONArray programs_connectorReturn = performItemsUpdate(ExcercisesDbAdapter.DATABASE_PROGRAMS_CONNECTOR_TABLE, programs_connector, programs_connectorFields);
		 backData.put("programs_connector", programs_connectorReturn);

		 JSONArray sessions = jsonUpdateData.getJSONArray("sessions");			 
		 JSONArray sessionsReturn = performItemsUpdate(ExcercisesDbAdapter.DATABASE_SESSIONS_TABLE, sessions, sessionFields);
		 backData.put("sessions", sessionsReturn); 
		 
		 JSONArray sessions_connector = jsonUpdateData.getJSONArray("sessions_connector");			 
		 JSONArray sessions_connectorReturn = performItemsUpdate(ExcercisesDbAdapter.DATABASE_SESSIONS_CONNECTOR_TABLE, sessions_connector, sessions_connectorFields);
		 backData.put("sessions_connector", sessions_connectorReturn);
		 
		 JSONArray log = jsonUpdateData.getJSONArray("log");			 
		 JSONArray logReturn = performItemsUpdate(ExcercisesDbAdapter.DATABASE_LOG_TABLE, log, logFields);
		 backData.put("log", logReturn);
		 
		
		 return backData;
   	   	
   }
    
    public JSONArray performItemsUpdate(String table, JSONArray items, HashMap<String, String> fields) throws JSONException{
    	
    	JSONArray itemsReturn = new JSONArray();
    	
		 for (int i = 0; i < items.length(); i++) {
			 JSONObject item = items.getJSONObject(i);
			 
			 String siteId = item.getString("id");
			 long rowId = item.getLong("phone_id");	
			 String deleted = item.getString("deleted");

			 HashMap<String, String> updateFields = new HashMap<String, String>();
				
			 updateFields.put(ExcercisesDbAdapter.KEY_DELETED, deleted);
			 updateFields.put(ExcercisesDbAdapter.KEY_SITEID, siteId);
				
				Set<Map.Entry<String, String>> set = fields.entrySet();
				
				for (Map.Entry<String, String> entry : set) {
					
					updateFields.put(entry.getValue(), item.getString(entry.getKey()));
				}

			 
			 // this entry exist only on web server, adding it
			 if(rowId == 0){
				 
				 // we do not want to doubled entries when connection is bad for example
				 // so we try to fetch it first
				 Cursor itemCursor = mDbHelper.fetchItemBySiteId(table, siteId);
				 			 
				 if(itemCursor.getCount() == 0){
					 
					 long phoneId = mDbHelper.createItem(table, updateFields);
					 
					 // now put phone id's back to update site database
					 JSONObject itemReturn = new JSONObject();
					 itemReturn.put("phone_id", phoneId);
					 itemReturn.put("site_id", siteId);
					 itemsReturn.put(itemReturn);
				 }
			 }else{ // it's already on the phone, so we need to update it
				 	
				 	mDbHelper.updateItem(table, updateFields, rowId);
			 }
         } 
		 
		 return itemsReturn;
    }
    
    public JSONObject prepareLocalUpdates(String updated){
    	
    	JSONObject dataToSend = new JSONObject();
  	
    	try {
    		
    		dataToSend.put("groups", prepareItems(ExcercisesDbAdapter.DATABASE_GROUPS_TABLE, groupFields, updated));
    		dataToSend.put("exercises", prepareItems(ExcercisesDbAdapter.DATABASE_EXERCISES_TABLE, exerciseFields, updated));
    		dataToSend.put("sets", prepareItems(ExcercisesDbAdapter.DATABASE_SETS_TABLE, setFields, updated));
    		dataToSend.put("sets_connector", prepareItems(ExcercisesDbAdapter.DATABASE_SETS_CONNECTOR_TABLE, sets_connectorFields, updated));
			dataToSend.put("sets_detail", prepareItems(ExcercisesDbAdapter.DATABASE_SETS_DETAIL_TABLE, sets_detailFields, updated));
			dataToSend.put("programs", prepareItems(ExcercisesDbAdapter.DATABASE_PROGRAMS_TABLE, programFields, updated));
			dataToSend.put("programs_connector", prepareItems(ExcercisesDbAdapter.DATABASE_PROGRAMS_CONNECTOR_TABLE, programs_connectorFields, updated));
			dataToSend.put("sessions", prepareItems(ExcercisesDbAdapter.DATABASE_SESSIONS_TABLE, sessionFields, updated));
			dataToSend.put("sessions_connector", prepareItems(ExcercisesDbAdapter.DATABASE_SESSIONS_CONNECTOR_TABLE, sessions_connectorFields, updated));						
			dataToSend.put("log", prepareItems(ExcercisesDbAdapter.DATABASE_LOG_TABLE, logFields, updated));
			
            
			dataToSend.put("localtime", mDbHelper.getLocalTime());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	return dataToSend;
    }
    
    public JSONArray prepareItems(String table, HashMap<String, String> fields, String updated) throws JSONException{
    	
    	JSONArray itemsReturn = new JSONArray();
    	Cursor updatedItems = mDbHelper.fetchUpdatedItems(table,updated);
    	
    	updatedItems.moveToFirst();
		for (int i=0; i<updatedItems.getCount(); i++)
		{
			JSONObject jsonRow = new JSONObject();
			
			// every table has those columns:
			String rowId = updatedItems.getString(updatedItems.getColumnIndex(ExcercisesDbAdapter.KEY_ROWID));
			jsonRow.put("id", rowId);
			String siteId = updatedItems.getString(updatedItems.getColumnIndex(ExcercisesDbAdapter.KEY_SITEID));
			jsonRow.put("site_id", siteId);
			String deleted = updatedItems.getString(updatedItems.getColumnIndex(ExcercisesDbAdapter.KEY_DELETED));
			jsonRow.put("deleted", deleted);
			String updatedSend = updatedItems.getString(updatedItems.getColumnIndex(ExcercisesDbAdapter.KEY_UPDATED));			
			try {
				Date stamp = iso8601Format.parse(updatedSend);
				jsonRow.put("stamp", stamp.getTime()/1000);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			// custom columns			
			Set<Map.Entry<String, String>> set = fields.entrySet();

			for (Map.Entry<String, String> entry : set) {
				Log.i("GETTING", entry.getKey());
			      jsonRow.put(entry.getKey(), updatedItems.getString(updatedItems.getColumnIndex(entry.getValue())));
			    }

			itemsReturn.put(jsonRow);
			updatedItems.moveToNext();
		} 
    	
		updatedItems.close();
    	return itemsReturn;
    }
    
    

}
