package com.leonti.bodyb;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

public class Imports {

	private final Context mCtx;
    private ExcercisesDbAdapter mDbHelper;
    
	public Imports(Context ctx){
		
		this.mCtx = ctx;

        mDbHelper = new ExcercisesDbAdapter(mCtx);
        mDbHelper.open();
        
	}
	
	public JSONArray getPublicExercises() throws JSONException{
		
		String authKey = mDbHelper.getAuthKey();
		ServerJson Js = new ServerJson();
		JSONArray jsonExercises = Js.getPublicExercises(authKey).getJSONArray("data");
		
		
		return jsonExercises;
	}

}
