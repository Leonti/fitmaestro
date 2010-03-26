package com.leonti.bodyb;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class BodyB extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        Button btnExcerciseLog = (Button) findViewById(R.id.button_excercise_log);
        btnExcerciseLog.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(BodyB.this, LogChooser.class);
                startActivity(i); 
            }
        });
        
        Button btnStartProgram = (Button) findViewById(R.id.button_start_program);
        btnStartProgram.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ServerJson Js = new ServerJson();
                try {
                	
            		JSONObject jsonObject = new JSONObject();
            		jsonObject.put("name", "Leonti"); 
            		
            		Map<String,String> mp=new HashMap<String, String>();
            		mp.put("preved", "medved");
            		
            	    ExcercisesDbAdapter mDbHelper = new ExcercisesDbAdapter(BodyB.this);
                    mDbHelper.open();
            	    Cursor mSetsCursor = mDbHelper.fetchAllSets();

            	    JSONObject jsonSets = new JSONObject();
            		mSetsCursor.moveToFirst();
            		for (int i=0; i<mSetsCursor.getCount(); i++)
            		{
        				JSONObject jsonRow = new JSONObject();
         			   String TitleRaw = mSetsCursor.getString(mSetsCursor.getColumnIndex(ExcercisesDbAdapter.KEY_TITLE));
         			   jsonRow.put("title", TitleRaw);
         			   String DescRaw = mSetsCursor.getString(mSetsCursor.getColumnIndex(ExcercisesDbAdapter.KEY_DESC));
         			   jsonRow.put("desc", DescRaw);
         			  String IdRaw = mSetsCursor.getString(mSetsCursor.getColumnIndex(ExcercisesDbAdapter.KEY_ROWID));
         			 jsonRow.put("id", IdRaw);
         			 jsonSets.put(IdRaw, jsonRow.toString());
            		    mSetsCursor.moveToNext();
            		}
      			   jsonObject.put("sets", jsonSets.toString());
            		
            		
            		
            		
            		
					Js.getServerData(jsonObject);
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                
                /*
                Intent i = new Intent(BodyB.this, ExcercisesList.class);
                startActivity(i); */ 
            }
        });
        
        Button btnContinueProgram = (Button) findViewById(R.id.button_continue_program);
        btnContinueProgram.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(BodyB.this, SetsList.class);
                startActivity(i); 
            }
        });
        
        Button btnDownloadPrograms = (Button) findViewById(R.id.button_download_programs);
        btnDownloadPrograms.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(BodyB.this, Expandable2.class);
                startActivityForResult(i, 231); 
            }
        });
        
        Button btnRegister = (Button) findViewById(R.id.button_register);
        btnRegister.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(BodyB.this, Register.class);
                startActivity(i); 
            }
        });
        
        Button btnLogin = (Button) findViewById(R.id.button_login);
        btnLogin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(BodyB.this, Login.class);
                startActivity(i); 
            }
        });
        
        Button btnSync = (Button) findViewById(R.id.button_synchronize);
        btnSync.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(BodyB.this, Synchronization.class);
                startActivity(i); 
            }
        });
        
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, 
                                    Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        Bundle extras = intent.getExtras();
    	Long ExerciseId = extras != null ? extras.getLong(ExcercisesDbAdapter.KEY_EXERCISEID) 
    			: null;
    	Log.i("EXERCISE ID FROM ACTIVITY: ", String.valueOf(ExerciseId));
    }
}