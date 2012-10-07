package com.leonty.fitmaestro;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.webkit.WebView;

public class Chart extends Activity {

    private String mChartUrl;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.chart);
        

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        initValues(savedInstanceState);
        getChart();
    }
    
    private void initValues(Bundle savedInstanceState){
        
		Bundle extras = getIntent().getExtras();
    	
	
		mChartUrl = savedInstanceState != null ? savedInstanceState
				.getString("chart_url") : null;
				
		if (mChartUrl == null) {

			mChartUrl = extras != null ? extras
					.getString("chart_url") : null;
		} 
		
    }
    
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		
		outState.putString("chart_url", mChartUrl);
	}
    
    public void getChart(){
    	WebView chartView = (WebView) findViewById(R.id.chart_view);
    	chartView.loadUrl(mChartUrl);
    }
    
    
}
