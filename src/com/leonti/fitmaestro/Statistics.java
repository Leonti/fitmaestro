package com.leonti.fitmaestro;

import java.util.Calendar;

import com.leonti.fitmaestro.R;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

public class Statistics extends Activity {

    private TextView mExerciseDisplay;
    private TextView mStartDateDisplay;
    private TextView mEndDateDisplay; 
    private Long mExerciseId;
	private ExcercisesDbAdapter mDbHelper;
	private Button mBtnGetStats;
    
    private Calendar mStartDate;
    private Calendar mEndDate;
    
    static final int START_DATE_DIALOG_ID = 0;
    static final int END_DATE_DIALOG_ID = 1;
    static final int ACTIVITY_CHANGE_EX = 2;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.statistics);

        mExerciseDisplay = (TextView) findViewById(R.id.txt_exercise);
        mStartDateDisplay = (TextView) findViewById(R.id.txt_start_date);
        mEndDateDisplay = (TextView) findViewById(R.id.txt_end_date);
        
		mDbHelper = new ExcercisesDbAdapter(this);
		mDbHelper.open();
 
        Button changeExercise = (Button) findViewById(R.id.exercise_change);
        changeExercise.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
            	changeExercise();
            }
        });
        
        Button changeStartDate = (Button) findViewById(R.id.start_date_change);
        changeStartDate.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                showDialog(START_DATE_DIALOG_ID);
            }
        });
        
        Button changeEndDate = (Button) findViewById(R.id.end_date_change);
        changeEndDate.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                showDialog(END_DATE_DIALOG_ID);
            }
        });

        
        mBtnGetStats = (Button) findViewById(R.id.get_stats);
        mBtnGetStats.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
               getStats();
            }
        });

        initValues(savedInstanceState);
        updateDates();
        updateExercise();
    }
    
	@Override
	protected void onDestroy() {
		mDbHelper.close();
		super.onDestroy();
	}
    
    private void initValues(Bundle savedInstanceState){
    
		Bundle extras = getIntent().getExtras();
    	
    	mStartDate = savedInstanceState != null ? (Calendar) savedInstanceState
				.getSerializable("start_date") : null;
				
		// if we don't have it saved - get one for 2 months before
		if (mStartDate == null) {
			mStartDate = Calendar.getInstance();
			mStartDate.add(Calendar.MONTH, -2);
		}
		
    	mEndDate = savedInstanceState != null ? (Calendar) savedInstanceState
				.getSerializable("end_date") : null;
				
		// if we don't have it saved - get one for now
		if (mEndDate == null) {
			mEndDate = Calendar.getInstance();
		}
		
		mExerciseId = savedInstanceState != null ? savedInstanceState
				.getLong(ExcercisesDbAdapter.KEY_ROWID) : null;
		if (mExerciseId == null) {

			mExerciseId = extras != null ? extras
					.getLong(ExcercisesDbAdapter.KEY_ROWID) : null;
		}       
    }
    
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		
		if(mExerciseId != null){
			outState.putLong(ExcercisesDbAdapter.KEY_ROWID, mExerciseId);
		}
		outState.putSerializable("start_date", mStartDate);
		outState.putSerializable("end_date", mEndDate);
	}
    
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case START_DATE_DIALOG_ID:
                return new DatePickerDialog(this,
                        mStartDateSetListener,
                        mStartDate.get(Calendar.YEAR), mStartDate.get(Calendar.MONTH), mStartDate.get(Calendar.DAY_OF_MONTH));
            case END_DATE_DIALOG_ID:
                return new DatePickerDialog(this,
                        mEndDateSetListener,
                        mEndDate.get(Calendar.YEAR), mEndDate.get(Calendar.MONTH), mEndDate.get(Calendar.DAY_OF_MONTH));
        }
        return null;
    }

    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
        switch (id) {
        	case START_DATE_DIALOG_ID:
                ((DatePickerDialog) dialog).updateDate(mStartDate.get(Calendar.YEAR), mStartDate.get(Calendar.MONTH), mStartDate.get(Calendar.DAY_OF_MONTH));
                break;
        	case END_DATE_DIALOG_ID:
                ((DatePickerDialog) dialog).updateDate(mEndDate.get(Calendar.YEAR), mEndDate.get(Calendar.MONTH), mEndDate.get(Calendar.DAY_OF_MONTH));
                break;
        }
    }    

    private void updateDates() {
    	
        mStartDateDisplay.setText(
            new StringBuilder()
                    // Month is 0 based so add 1
                    .append(mStartDate.get(Calendar.MONTH) + 1).append("-")
                    .append(mStartDate.get(Calendar.DAY_OF_MONTH)).append("-")
                    .append(mStartDate.get(Calendar.YEAR)).append(" "));
        
        mEndDateDisplay.setText(
                new StringBuilder()
                        // Month is 0 based so add 1
                        .append(mEndDate.get(Calendar.MONTH) + 1).append("-")
                        .append(mEndDate.get(Calendar.DAY_OF_MONTH)).append("-")
                        .append(mEndDate.get(Calendar.YEAR)).append(" "));
        
    }
    
    public void updateExercise(){
    	
    	if(mExerciseId != null){
    		Cursor exercise = mDbHelper.fetchExercise(mExerciseId);
    		startManagingCursor(exercise);
    		mExerciseDisplay.setText(exercise.getString(exercise
					.getColumnIndexOrThrow(ExcercisesDbAdapter.KEY_TITLE)));
    		mBtnGetStats.setEnabled(true);
    	}
    	
    }

    private DatePickerDialog.OnDateSetListener mStartDateSetListener =
            new DatePickerDialog.OnDateSetListener() {

                public void onDateSet(DatePicker view, int year, int monthOfYear,
                        int dayOfMonth) {

                    mStartDate.set(Calendar.YEAR, year);
                    mStartDate.set(Calendar.MONTH, monthOfYear);
                    mStartDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    updateDates();
                }
    };
    
    private DatePickerDialog.OnDateSetListener mEndDateSetListener =
        new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear,
                    int dayOfMonth) {

	                mEndDate.set(Calendar.YEAR, year);
	                mEndDate.set(Calendar.MONTH, monthOfYear);
	                mEndDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
	                updateDates();
            }
    };
    
	private void changeExercise() {
		Intent i = new Intent(this, ExercisesList.class);
		startActivityForResult(i, ACTIVITY_CHANGE_EX);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		
		switch (requestCode) {
		case ACTIVITY_CHANGE_EX:
			Bundle extras = intent.getExtras();
			mExerciseId = extras != null ? extras
					.getLong(ExcercisesDbAdapter.KEY_EXERCISEID) : null;
			updateExercise();
			Log.i("EXERCISE ID FROM ACTIVITY: ", String.valueOf(mExerciseId));
			break;
		}
		

	}
	
	private void getStats(){
		Log.i("STATS: ", "getting some");

		Intent i = new Intent(this, StatsList.class);
		i.putExtra(ExcercisesDbAdapter.KEY_ROWID, mExerciseId);
		i.putExtra("start_date", mStartDate);
		i.putExtra("end_date", mEndDate);
		startActivity(i);
	}

    
}
