package com.leonti.bodyb;

import android.app.Activity;
import android.content.Intent;
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
                Intent i = new Intent(BodyB.this, Group.class);
                startActivity(i); 
            }
        });
        
        Button btnStartProgram = (Button) findViewById(R.id.button_start_program);
        btnStartProgram.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(BodyB.this, ExcercisesList.class);
                startActivity(i); 
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