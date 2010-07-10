package com.leonti.bodyb;


import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TabHost;

public class BodyB extends TabActivity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        TabHost tabHost = getTabHost();        
        LayoutInflater.from(this).inflate(R.layout.main, tabHost.getTabContentView(), true);
        
        tabHost.addTab(tabHost.newTabSpec("tab1")
                .setIndicator(getString(R.string.exercising))
                .setContent(R.id.tab_exercising));
        tabHost.addTab(tabHost.newTabSpec("tab2")
                .setIndicator(getString(R.string.fitmaestro))
                .setContent(R.id.tab_fitmaestro));

        Button btnExercises = (Button) findViewById(R.id.button_exercises);
        btnExercises.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(BodyB.this, ExercisesList.class);
                startActivity(i); 
            }
        });
        
        Button btnPrograms = (Button) findViewById(R.id.button_programs);
        btnPrograms.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(BodyB.this, ProgramsList.class);
                startActivity(i); 
            }
        });
        
        Button btnSets = (Button) findViewById(R.id.button_sets);
        btnSets.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(BodyB.this, SetsList.class);
                startActivity(i); 
            }
        });
        
        Button btnSessions = (Button) findViewById(R.id.button_sessions);
        btnSessions.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(BodyB.this, SessionTabs.class);
                startActivity(i); 
            }
        });
        
        Button btnMeasurements = (Button) findViewById(R.id.button_measurements);
        btnMeasurements.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(BodyB.this, MeasurementsList.class);
                startActivity(i); 
            }
        });
        
        Button btnDownloadPrograms = (Button) findViewById(R.id.button_download_programs);
        btnDownloadPrograms.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
             //   Intent i = new Intent(BodyB.this, Expandable2.class);
             //   startActivityForResult(i, 231); 
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
                Intent i = new Intent(BodyB.this, SynchronizationView.class);
                startActivity(i); 
            }
        });
        
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, 
                                    Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        Bundle extras = intent.getExtras();
    //	Long ExerciseId = extras != null ? extras.getLong(ExcercisesDbAdapter.KEY_EXERCISEID) 
    //			: null;
   // 	Log.i("EXERCISE ID FROM ACTIVITY: ", String.valueOf(ExerciseId));
    }
}