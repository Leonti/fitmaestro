package com.leonti.fitmaestro;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class StatsList extends ListActivity {


    private Long mExerciseId;   
    private Calendar mStartDate;
    private Calendar mEndDate;
    private int mExType;
	private ExcercisesDbAdapter mDbHelper;
	private Cursor mStatsCursor;
	private Long mMaxMaxValue = Long.valueOf(0);
	private Long mMaxSumValue = Long.valueOf(0);
	private Dialog mChooserDialog;
	private DateFormats mDateFormats;
	
	DateFormat iso8601Format;
	private static final int CHART_ID = Menu.FIRST;
	private static final int DIALOG_STATS_TYPE_CHOOSER = 0;
	private static final int MAX_CHART = 1;
	private static final int SUMS_CHART = 2;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.stats_list);
        
		mDbHelper = new ExcercisesDbAdapter(this);
		mDbHelper.open();
		
		iso8601Format = new SimpleDateFormat("yyyy-MM-dd");
		//iso8601Format.setTimeZone(TimeZone.getTimeZone("UTC"));
		mDateFormats = new DateFormats(this);
		
        initValues(savedInstanceState);
        fillData();
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
		if (mStartDate == null) {	
			mStartDate = extras != null ? (Calendar) extras
					.getSerializable("start_date") : null;
		}  
				
    	mEndDate = savedInstanceState != null ? (Calendar) savedInstanceState
				.getSerializable("end_date") : null;
		if (mEndDate == null) {	
			mEndDate = extras != null ? (Calendar) extras
					.getSerializable("end_date") : null;
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
		
		outState.putLong(ExcercisesDbAdapter.KEY_ROWID, mExerciseId);
		outState.putSerializable("start_date", mStartDate);
		outState.putSerializable("end_date", mEndDate);
	}
	
	public void fillData(){
		
		Cursor exerciseCursor = (Cursor) mDbHelper.fetchExercise(mExerciseId);
		startManagingCursor(exerciseCursor);
		mExType = exerciseCursor.getInt(exerciseCursor
				.getColumnIndexOrThrow(ExcercisesDbAdapter.KEY_TYPE));
		
		String begin = iso8601Format.format(mStartDate.getTime());
		String end = iso8601Format.format(mEndDate.getTime());
		
		mStatsCursor = mDbHelper.fetchStatsForExercise(mExerciseId, begin,
				end, mExType);		
		startManagingCursor(mStatsCursor);
		String[] from = new String[] { ExcercisesDbAdapter.KEY_TITLE, ExcercisesDbAdapter.KEY_DONE, "sum", "max"};
		int[] to = new int[] { R.id.session_name, R.id.session_date, R.id.sum_txt, R.id.max_txt };
		StatsListCursorAdapter excercises = new StatsListCursorAdapter(
				this, R.layout.stats_list_row,
				mStatsCursor, from, to);
		setListAdapter(excercises);
		
		Log.i("STATS LIST: ", String.valueOf(mExType) + " " + begin + " " + end + " " + mExerciseId);
		Log.i("STATS RESULT COUNT: ", String.valueOf(mStatsCursor.getCount()));
		
	}
	
	protected class StatsListCursorAdapter extends SimpleCursorAdapter {

		Activity mActivity;

		public StatsListCursorAdapter(Activity activity, int layout,
				Cursor c, String[] from, int[] to) {
			super(activity, layout, c, from, to);

			mActivity = activity;

		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			super.bindView(view, context, cursor);

			Log.i("BIND", "bind view called");
			
			Long sessionsConnectorId = cursor.getLong(cursor
					.getColumnIndexOrThrow(ExcercisesDbAdapter.KEY_SESSIONS_CONNECTORID));
			Long sessionId = cursor.getLong(cursor
					.getColumnIndexOrThrow(ExcercisesDbAdapter.KEY_SESSIONID));
			
			// determining max values for chart
			Long sum = cursor.getLong(cursor
					.getColumnIndexOrThrow("sum"));
			Long max = cursor.getLong(cursor
					.getColumnIndexOrThrow("max"));
			
			if(sum > mMaxSumValue){
				mMaxSumValue = sum;
			}
			if(max > mMaxMaxValue){
				mMaxMaxValue = max;
			}
					
			SessionRepsArray repsArray = new SessionRepsArray(StatsList.this,
					sessionId, mExerciseId, sessionsConnectorId);
			ArrayList<HashMap<String, String>> sessionRepsList = repsArray
			.getRepsArray();
			repsArray.drawTable(StatsList.this, view, sessionRepsList, Long.valueOf(mExType));
			
			String finalDateTime;
			try {
				finalDateTime = mDateFormats.getWithYear(cursor.getString(cursor
						.getColumnIndexOrThrow(ExcercisesDbAdapter.KEY_DONE)));
				TextView sessionDate = (TextView) view.findViewById(R.id.session_date);
				sessionDate.setText(finalDateTime);
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}

	}
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, CHART_ID, 0, R.string.get_chart);
		return true;
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case CHART_ID:
			showDialog(DIALOG_STATS_TYPE_CHOOSER);
			return true;
		}

		return super.onMenuItemSelected(featureId, item);
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {

		switch (id) {

		case DIALOG_STATS_TYPE_CHOOSER:
			LayoutInflater chooserInflater = LayoutInflater.from(this);
			final View chooserView = chooserInflater.inflate(R.layout.stats_type_chooser,
					null);

			mChooserDialog = new AlertDialog.Builder(this).setTitle(
					R.string.choose_stats_title).setView(chooserView)
					.setNegativeButton(R.string.cancel,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {

									/* User clicked cancel so do some stuff */
								}
							}).create();
			
	        Button btnMax = (Button) chooserView.findViewById(R.id.btn_max);
	        btnMax.setOnClickListener(new View.OnClickListener() {

	            public void onClick(View v) {
	            	drawChart(MAX_CHART);
	            	mChooserDialog.dismiss();
	            }
	        });
	        Button btnSum = (Button) chooserView.findViewById(R.id.btn_sums);
	        btnSum.setOnClickListener(new View.OnClickListener() {

	            public void onClick(View v) {
	            	drawChart(SUMS_CHART);
	            	mChooserDialog.dismiss();
	            }
	        });

			return mChooserDialog;
		}

		return null;
	}
	
	private void drawChart(int chartType){
		Intent i = new Intent(this, Chart.class);
		
		View screen = findViewById(R.id.LinearLayoutStats); 
		WindowManager mWinMgr = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
		int screenHeight = mWinMgr.getDefaultDisplay().getHeight();
		int statusBarHeight = screenHeight - screen.getHeight();
		
		// we use it later to determine chart height in landscape position
		// we need to take into account that we already can be in landscape position
		
		Long displayWidth;	
		if(mWinMgr.getDefaultDisplay().getOrientation() == 1){
			displayWidth = Long.valueOf(screen.getHeight());
		}else{
			displayWidth = Long.valueOf(screen.getWidth() - statusBarHeight);
		}
		
		Calendar currentDay = (Calendar) mStartDate.clone();
		int currentMonth = currentDay.get(Calendar.MONTH);
		
		// to calculate month caption position
		int dayNumber = 0;
		
		long milis1 = mStartDate.getTimeInMillis();
        long milis2 = mEndDate.getTimeInMillis();
        long diff = milis2 - milis1;
 
        // Calculate difference in days
        long diffDays = diff / (24 * 60 * 60 * 1000);
        Log.i("DAYS DIFFERENCE: ", String.valueOf(diffDays));
		
		String monthNames = mDateFormats.getMonthName(currentDay.getTime()) + "|";
		String monthPositions = String.valueOf(0) + ",";
		String dayLabels = "";
		String dataValues = "";
		
		
		Long maxValue;		
		if(chartType == MAX_CHART){
			maxValue = mMaxMaxValue > Long.valueOf(0) ? mMaxMaxValue : 0;
		}else{
			maxValue = mMaxSumValue > Long.valueOf(0) ? mMaxSumValue : 0;
		}
		
		
		if(mStatsCursor.getCount() > 0){
			mStatsCursor.moveToLast();
		}
		int dataColumnIndex = chartType == MAX_CHART ? mStatsCursor.getColumnIndex("max") : mStatsCursor.getColumnIndex("sum");
		Log.i("CURRENT VALUE: ", String.valueOf(mStatsCursor.getDouble(dataColumnIndex)));
		
		do{
			Log.i("Day to check:", iso8601Format.format(currentDay.getTime()));
			String dayToCheck = iso8601Format.format(currentDay.getTime());

			dayLabels += String.valueOf(currentDay.get(Calendar.DAY_OF_MONTH)) + "|";
			if(currentDay.get(Calendar.MONTH) != currentMonth){
				Log.i("MONTH CHANGE: ", "Month changed!");
				currentMonth = currentDay.get(Calendar.MONTH);
				monthNames += mDateFormats.getMonthName(currentDay.getTime()) + "|";
				Log.i("DAYS FUCKUP: ", "Day number: " + String.valueOf(dayNumber) + "Days difference: " + String.valueOf(diffDays));
				monthPositions += String.valueOf(Math.ceil(Double.valueOf(dayNumber)/diffDays*100)) + ",";
			}
			
			if(mStatsCursor.getCount() > 0){
				
				try {
					String dbDateUnparsed = mStatsCursor.getString(mStatsCursor.getColumnIndex(ExcercisesDbAdapter.KEY_DONE));
					String dbDate = iso8601Format.format(iso8601Format.parse(dbDateUnparsed));
					
					Log.i("PARSED DATE: ", dbDate);
					
					if(dayToCheck.equals(dbDate)){

						Log.i("DATE MATCH: ", "Dates match!!!");
						Double value = mStatsCursor.getDouble(dataColumnIndex);
						dataValues += String.valueOf(Math.ceil(value/maxValue*100)) + ",";
						
						// we have to go backwards because our sort order is by date descending
						if (!mStatsCursor.isFirst()) {
							mStatsCursor.moveToPrevious();
						}
					}else{
						dataValues += "0,";
					}	
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
			}else{
				dataValues += "0,";
			}

			currentDay.add(Calendar.DATE, 1);
			dayNumber++;
		}while(currentDay.before(mEndDate));
		
		Log.i("DAYS:", dayLabels);
		Log.i("MONTHS:", monthNames);
		Log.i("MONTHS POSITIONS:", monthPositions);
		Log.i("DATA STRING: ", dataValues);
		Log.i("MAX VALUE: ", String.valueOf(maxValue));
		
		Long chartHeight = displayWidth;
		Long chartWidth = displayWidth*3;
		
        String chartUrl = "http://chart.apis.google.com/chart?cht=bvs&chco=4d89f9&chbh=a&chs=" 
        					+ String.valueOf(chartWidth) + "x" + String.valueOf(chartHeight);
        
        chartUrl  += "&chd=t:" + dataValues.substring(0, dataValues.length() - 1)
                  + "&chxt=x,x,y&chxl=0:|" + dayLabels.substring(0, dayLabels.length() - 1)
                  + "|1:|" + monthNames.substring(0, monthNames.length() - 1)
                  + "&chxp=1," + monthPositions.substring(0, monthPositions.length() - 1)
                  + "&chxr=2,0," + String.valueOf(maxValue);
        Log.i("CHART URL: ", chartUrl);
		
		Log.i("SCREEN WIDTH: ", String.valueOf(displayWidth));
		
		i.putExtra("chart_url", chartUrl);
		startActivity(i);
	}
}
