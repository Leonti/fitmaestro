package com.leonti.fitmaestro;

import com.leonti.fitmaestro.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class LogChooser extends Activity {

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log_chooser);
        
    Button btnBySet = (Button) findViewById(R.id.button_by_set);
    btnBySet.setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent i = new Intent(LogChooser.this, SetsList.class);
            startActivity(i); 
        }
    });
    
    Button btnByExercise = (Button) findViewById(R.id.button_by_exercise);
    btnByExercise.setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent i = new Intent(LogChooser.this, ByExerciseList.class);
            startActivity(i); 
        }
    });
    }
}
