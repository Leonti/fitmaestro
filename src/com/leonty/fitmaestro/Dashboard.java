package com.leonty.fitmaestro;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Dashboard extends SherlockActivity {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        menu.add("WWW").setOnMenuItemClickListener(new OnMenuItemClickListener() {

			public boolean onMenuItemClick(MenuItem item) {
				startActivity (new Intent(getApplicationContext(), SynchronizationView.class));
				return false;
			}
        	
        }).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

        
        return true;
    }	
	
	protected void onCreate(Bundle savedInstanceState) 
	{
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.dashboard);
	}	
	
	public void onItemClick(View v)
	{
	    int id = v.getId ();
	    switch (id) {
	      case R.id.home_btn_sessions :
	           startActivity (new Intent(getApplicationContext(), SessionTabs.class));
	           break;
	      case R.id.home_btn_workouts :
	           startActivity (new Intent(getApplicationContext(), WorkoutList.class));
	           break;
	      case R.id.home_btn_exercises :
	           startActivity (new Intent(getApplicationContext(), ExercisesList.class));
	           break;
	      case R.id.home_btn_programs :
	           startActivity (new Intent(getApplicationContext(), ProgramList.class));
	           break;
	      case R.id.home_btn_measurements :
	           startActivity (new Intent(getApplicationContext(), MeasurementList.class));
	           break;
	      case R.id.home_btn_statistics :
	           startActivity (new Intent(getApplicationContext(), Statistics.class));
	           break;
	      default: 
	    	   break;
	    }
	}
}
