package com.leonti.bodyb;


import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TabHost;

public class BodyB extends TabActivity {
	
    private static final int EXERCISES_POS=0;
    private static final int WORKOUTS_POS=1;
    private static final int PROGRAMS_POS=2;
    private static final int SESSIONS_POS=3;
    private static final int MEASUREMENTS_POS=4;
    
    private static final int ACCOUNT_POS=0;
    private static final int SYNCHRONIZE_POS=1;
    private static final int DOWNLOADS_POS=2;
	
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

        
        ListView listViewExercising= (ListView) findViewById(R.id.ListView_exercising);
        listViewExercising.setOnItemClickListener(new ListView.OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {

				Log.v("ITEM POSITION", String.valueOf(position)); 
				switch(position){
				case EXERCISES_POS:
	                Intent i = new Intent(BodyB.this, ExercisesList.class);
	                startActivity(i);
	                break;
				case WORKOUTS_POS:
	                Intent i1 = new Intent(BodyB.this, SetsList.class);
	                startActivity(i1); 
	                break;
				case PROGRAMS_POS:
	                Intent i2 = new Intent(BodyB.this, ProgramsList.class);
	                startActivity(i2); 
	                break;
				case SESSIONS_POS:
	                Intent i3 = new Intent(BodyB.this, SessionTabs.class);
	                startActivity(i3);
	                break;
				case MEASUREMENTS_POS:
	                Intent i4 = new Intent(BodyB.this, MeasurementsList.class);
	                startActivity(i4); 
	                break;
				}
			}
        }); 

        ListView listViewFitmaestro = (ListView) findViewById(R.id.ListView_fitmaestro);
        listViewFitmaestro.setOnItemClickListener(new ListView.OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {

				Log.v("ITEM POSITION", String.valueOf(position)); 
				switch(position){
				case ACCOUNT_POS:
	                Intent i = new Intent(BodyB.this, Account.class);
	                startActivity(i);
	                break;
				case SYNCHRONIZE_POS:
	                Intent i1 = new Intent(BodyB.this, SynchronizationView.class);
	                startActivity(i1); 
	                break;
				case DOWNLOADS_POS:
	                Intent i2 = new Intent(BodyB.this, Downloads.class);
	                startActivity(i2); 
	                break;
				}
			}
        });
                
    }
}