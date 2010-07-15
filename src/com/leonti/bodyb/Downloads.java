package com.leonti.bodyb;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class Downloads extends Activity {

    private static final int EXERCISES_POS=0;
    private static final int PROGRAMS_POS=1;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.imports);
        
        ListView listViewDownloads = (ListView) findViewById(R.id.ListView_imports);
        listViewDownloads.setOnItemClickListener(new ListView.OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {

				Log.v("ITEM POSITION", String.valueOf(position)); 
				switch(position){
				case EXERCISES_POS:
	                Intent i = new Intent(Downloads.this, ImportExercises.class);
	                startActivity(i);
	                break;
				case PROGRAMS_POS:
					Intent i1 = new Intent(Downloads.this, ImportPrograms.class);
	                startActivity(i1); 
	                break;
				}
			}
        });
    }
}
