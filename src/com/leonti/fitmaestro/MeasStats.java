package com.leonti.fitmaestro;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class MeasStats extends Activity {

	private ExcercisesDbAdapter mDbHelper;
	private Cursor mMeasLogCursor;
	private Long mMeasurementId;
	
	private Button mBtnGetChart;
	
    
    private Calendar mStartDate;
    private Calendar mEndDate;
	DateFormat iso8601Format;
    private measParamsAdapter mMeasParamsAdapter;

    static final int START_DATE_DIALOG_ID = 0;
    static final int END_DATE_DIALOG_ID = 1;
	private static final int START_POS = 0;
	private static final int END_POS = 1;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.meas_stats_list);

        ListView statsParameters = (ListView) findViewById(R.id.stats_selector);	
		String paramTitles[] = getResources().getStringArray(R.array.stats_params);
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		for(int i=1; i<paramTitles.length; i++){
			Map<String, String> map = new HashMap<String, String>();
			map.put("desc", paramTitles[i]);
			list.add(map);	
		}
		String[] from = {"desc"};
		int[] to = {R.id.desc_txt};

		mMeasParamsAdapter = new measParamsAdapter(this, list, R.layout.stats_selector_list_row, from, to);
		statsParameters.setAdapter(mMeasParamsAdapter);
		
		statsParameters
		.setOnItemClickListener(new ListView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View v,
					int position, long id) {

				Log.v("ITEM POSITION", String.valueOf(position));
				switch (position) {
				case START_POS:
					showDialog(START_DATE_DIALOG_ID);
					break;
				case END_POS:
					showDialog(END_DATE_DIALOG_ID);
					break;
				}
			}
		});
		
		
		mDbHelper = new ExcercisesDbAdapter(this);
		mDbHelper.open();
		
		iso8601Format = new SimpleDateFormat("yyyy-MM-dd");
		//iso8601Format.setTimeZone(TimeZone.getTimeZone("UTC"));
		
        mBtnGetChart = (Button) findViewById(R.id.get_chart);
        mBtnGetChart.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
               getChart();
            }
        });
        
        initValues(savedInstanceState);
    }
    
	@Override
	protected void onDestroy() {
		mDbHelper.close();
		super.onDestroy();
	}
	
	private class measParamsAdapter extends SimpleAdapter{

		public measParamsAdapter(Context context,
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
			case START_POS:
				currentTitle.setText(
		                new StringBuilder()
		                        // Month is 0 based so add 1
		                        .append(mStartDate.get(Calendar.MONTH) + 1).append("-")
		                        .append(mStartDate.get(Calendar.DAY_OF_MONTH)).append("-")
		                        .append(mStartDate.get(Calendar.YEAR)).append(" "));
				break;
			case END_POS:
				currentTitle.setText(
		                new StringBuilder()
		                        // Month is 0 based so add 1
		                        .append(mEndDate.get(Calendar.MONTH) + 1).append("-")
		                        .append(mEndDate.get(Calendar.DAY_OF_MONTH)).append("-")
		                        .append(mEndDate.get(Calendar.YEAR)).append(" "));
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
		
		mMeasurementId = savedInstanceState != null ? savedInstanceState
				.getLong(ExcercisesDbAdapter.KEY_ROWID) : null;
		if (mMeasurementId == null) {

			mMeasurementId = extras != null ? extras
					.getLong(ExcercisesDbAdapter.KEY_ROWID) : null;
		}       
    }
    
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		
		outState.putLong(ExcercisesDbAdapter.KEY_ROWID, mMeasurementId);
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
    
    private DatePickerDialog.OnDateSetListener mStartDateSetListener =
        new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear,
                    int dayOfMonth) {

                mStartDate.set(Calendar.YEAR, year);
                mStartDate.set(Calendar.MONTH, monthOfYear);
                mStartDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                mMeasParamsAdapter.notifyDataSetChanged();
            }
	};

	private DatePickerDialog.OnDateSetListener mEndDateSetListener =
	    new DatePickerDialog.OnDateSetListener() {
	
	        public void onDateSet(DatePicker view, int year, int monthOfYear,
	                int dayOfMonth) {
	
	                mEndDate.set(Calendar.YEAR, year);
	                mEndDate.set(Calendar.MONTH, monthOfYear);
	                mEndDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
	                mMeasParamsAdapter.notifyDataSetChanged();
	        }
	};

	private void getChart(){
		Intent i = new Intent(this, Chart.class);
		
		View screen = findViewById(R.id.meas_stats_layout); 
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
		
		Log.i("DRAWING CHART", "Drawing chart");
		String begin = iso8601Format.format(mStartDate.getTime());
		String end = iso8601Format.format(mEndDate.getTime());
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
		
		String monthNames = String.valueOf(currentMonth) + "|";
		String monthPositions = String.valueOf(0) + ",";
		String dayLabels = "";
		String dataValues = "";
		Double maxValue;

		Cursor maxCursor = mDbHelper.fetchMaxMeasForDates(mMeasurementId, begin, end);
		startManagingCursor(maxCursor);
		if(maxCursor.getCount() > 0){			
			maxValue = maxCursor.getDouble(maxCursor.getColumnIndex("max"));
			Log.i("HAVING MAX:", String.valueOf(maxValue));
		}else{
			maxValue = 0d;
		}
		
		mMeasLogCursor = mDbHelper.fetchMeasLogEntriesForDates(mMeasurementId, begin, end);
		startManagingCursor(mMeasLogCursor);
		
		if(mMeasLogCursor.getCount() > 0){
			mMeasLogCursor.moveToLast();
		}
		
		do{
			
			Log.i("Day to check:", iso8601Format.format(currentDay.getTime()));
			String dayToCheck = iso8601Format.format(currentDay.getTime());

			
			dayLabels += String.valueOf(currentDay.get(Calendar.DAY_OF_MONTH)) + "|";
			
			if(currentDay.get(Calendar.MONTH) != currentMonth){
				Log.i("MONTH CHANGE: ", "Month changed!");
				currentMonth = currentDay.get(Calendar.MONTH);
				monthNames += String.valueOf(currentMonth) + "|";
				Log.i("DAYS FUCKUP: ", "Day number: " + String.valueOf(dayNumber) + "Days difference: " + String.valueOf(diffDays));
				monthPositions += String.valueOf(Math.ceil(Double.valueOf(dayNumber)/diffDays*100)) + ",";
			}
			
			
			if(mMeasLogCursor.getCount() > 0){
				
				Log.i("CURRENT VALUE:", String.valueOf(mMeasLogCursor.getDouble(mMeasLogCursor.getColumnIndex(ExcercisesDbAdapter.KEY_VALUE))));
				try {
					String dbDateUnparsed = mMeasLogCursor.getString(mMeasLogCursor.getColumnIndex(ExcercisesDbAdapter.KEY_DATE));
					String dbDate = iso8601Format.format(iso8601Format.parse(dbDateUnparsed));
					
					Log.i("PARSED DATE: ", dbDate);
					
					if(dayToCheck.equals(dbDate)){

						Log.i("DATE MATCH: ", "Dates match!!!");
						Double value = mMeasLogCursor.getDouble(mMeasLogCursor.getColumnIndex(ExcercisesDbAdapter.KEY_VALUE));
						dataValues += String.valueOf(Math.ceil(value/maxValue*100)) + ",";
						
						// we have to go backwards because our sort order is by date descending
						if (!mMeasLogCursor.isFirst()) {
							mMeasLogCursor.moveToPrevious();
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
		
		if(maxValue == 0d){
			maxValue = 100d;
		}
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
