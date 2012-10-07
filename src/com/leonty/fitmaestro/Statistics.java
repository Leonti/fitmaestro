package com.leonty.fitmaestro;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.leonty.fitmaestro.domain.Exercise;
import com.leonty.fitmaestro.domain.FitmaestroDb;

public class Statistics extends Activity {

    private TextView mExerciseDisplay;
    private TextView mStartDateDisplay;
    private TextView mEndDateDisplay; 
    private Long mExerciseId;
	private Button mBtnGetStats;
    
    private Calendar mStartDate;
    private Calendar mEndDate;
    private paramsAdapter mParamsAdapter;
    
    static final int START_DATE_DIALOG_ID = 0;
    static final int END_DATE_DIALOG_ID = 1;
    static final int ACTIVITY_CHANGE_EX = 2;
    
	private static final int EXERCISE_POS = 0;
	private static final int START_POS = 1;
	private static final int END_POS = 2;
  
	private FitmaestroDb db;
	private Exercise exercise;	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stats_selector_list);

		db = new FitmaestroDb(this).open();
		exercise = new Exercise(db);        
        
        ListView statsParameters = (ListView) findViewById(R.id.stats_selector);	
		String paramTitles[] = getResources().getStringArray(R.array.stats_params);
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		for(int i=0; i<paramTitles.length; i++){
			Map<String, String> map = new HashMap<String, String>();
			map.put("desc", paramTitles[i]);
			list.add(map);	
		}
		String[] from = {"desc"};
		int[] to = {R.id.desc_txt};

		mParamsAdapter = new paramsAdapter(this, list, R.layout.stats_selector_list_row, from, to);
		statsParameters.setAdapter(mParamsAdapter);
		
		statsParameters
		.setOnItemClickListener(new ListView.OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View v,
					int position, long id) {

				Log.v("ITEM POSITION", String.valueOf(position));
				switch (position) {
				case EXERCISE_POS:
					changeExercise();
					break;
				case START_POS:
					showDialog(START_DATE_DIALOG_ID);
					break;
				case END_POS:
					showDialog(END_DATE_DIALOG_ID);
					break;
				}
			}
		});
		
        mBtnGetStats = (Button) findViewById(R.id.get_stats);
        mBtnGetStats.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
               getStats();
            }
        });
        
        initValues(savedInstanceState);
    }
    
    
	@Override
	protected void onDestroy() {
		db.close();
		super.onDestroy();
	}
	
	private class paramsAdapter extends SimpleAdapter{

		public paramsAdapter(Context context,
				List<? extends Map<String, ?>> data, int resource,
				String[] from, int[] to) {
			super(context, data, resource, from, to);
			// TODO Auto-generated constructor stub
		}
		
		@Override
		public View getView(int position, View  convertView, ViewGroup  parent){
			View view = super.getView(position, convertView, parent);
			
			TextView currentTitle = (TextView) view.findViewById(R.id.title);
			switch(position){
			case EXERCISE_POS:
		        mExerciseDisplay = currentTitle;
		        updateExercise();
				break;
			case START_POS:
		        mStartDateDisplay = currentTitle;
		        updateStartDate();
				break;
			case END_POS:
		        mEndDateDisplay = currentTitle;
		        updateEndDate();
				break;
			}
			
			return view;
		}
		
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
				.getLong(FitmaestroDb.KEY_ROWID) : null;
		if (mExerciseId == null) {

			mExerciseId = extras != null ? extras
					.getLong(FitmaestroDb.KEY_ROWID) : null;
		}       
    }
    
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		
		if(mExerciseId != null){
			outState.putLong(FitmaestroDb.KEY_ROWID, mExerciseId);
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

    private void updateStartDate() {
    	
        mStartDateDisplay.setText(
            new StringBuilder()
                    // Month is 0 based so add 1
                    .append(mStartDate.get(Calendar.MONTH) + 1).append("-")
                    .append(mStartDate.get(Calendar.DAY_OF_MONTH)).append("-")
                    .append(mStartDate.get(Calendar.YEAR)).append(" "));
        
    }
    
    private void updateEndDate() {
        
        mEndDateDisplay.setText(
                new StringBuilder()
                        // Month is 0 based so add 1
                        .append(mEndDate.get(Calendar.MONTH) + 1).append("-")
                        .append(mEndDate.get(Calendar.DAY_OF_MONTH)).append("-")
                        .append(mEndDate.get(Calendar.YEAR)).append(" "));
        
    }
    
    public void updateExercise(){
    	
    	if(mExerciseId != null && mExerciseId != 0){
    		Cursor exerciseCursor = exercise.fetchExercise(mExerciseId);
    		startManagingCursor(exerciseCursor);
    		mExerciseDisplay.setText(exerciseCursor.getString(exerciseCursor
					.getColumnIndexOrThrow(FitmaestroDb.KEY_TITLE)));
    		mBtnGetStats.setEnabled(true);
    	}else{
    		mExerciseDisplay.setText(R.string.none_selected);
    	}
    	
    }

    private DatePickerDialog.OnDateSetListener mStartDateSetListener =
            new DatePickerDialog.OnDateSetListener() {

                public void onDateSet(DatePicker view, int year, int monthOfYear,
                        int dayOfMonth) {

                    mStartDate.set(Calendar.YEAR, year);
                    mStartDate.set(Calendar.MONTH, monthOfYear);
                    mStartDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    mParamsAdapter.notifyDataSetChanged();
                }
    };
    
    private DatePickerDialog.OnDateSetListener mEndDateSetListener =
        new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear,
                    int dayOfMonth) {

	                mEndDate.set(Calendar.YEAR, year);
	                mEndDate.set(Calendar.MONTH, monthOfYear);
	                mEndDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
	                mParamsAdapter.notifyDataSetChanged();
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
			if(resultCode == RESULT_OK){
				Bundle extras = intent.getExtras();
				mExerciseId = extras != null ? extras
						.getLong(FitmaestroDb.KEY_EXERCISEID) : null;
				mParamsAdapter.notifyDataSetChanged();
				Log.i("EXERCISE ID FROM ACTIVITY: ", String.valueOf(mExerciseId));
			}
			break;
		}
		

	}
	
	private void getStats(){
		Log.i("STATS: ", "getting some");

		Intent i = new Intent(this, StatsList.class);
		i.putExtra(FitmaestroDb.KEY_ROWID, mExerciseId);
		i.putExtra("start_date", mStartDate);
		i.putExtra("end_date", mEndDate);
		startActivity(i);
	}

    
}
