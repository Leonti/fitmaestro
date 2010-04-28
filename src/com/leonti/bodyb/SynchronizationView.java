package com.leonti.bodyb;

import org.json.JSONException;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class SynchronizationView extends Activity {

    private ExcercisesDbAdapter mDbHelper;
    private TextView mMessageText;
    
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        
        setContentView(R.layout.synchronization);
       
        mDbHelper = new ExcercisesDbAdapter(this);
        mDbHelper.open();
        
        // for now
        mMessageText = (TextView) findViewById(R.id.TextMessage);
        
        // check if user is authenticated, if not - go to login screen
        if(mDbHelper.getAuthKey().equals("")){
            Intent i = new Intent(this, Login.class);
            startActivity(i);
        }else{
            new PerformSync().execute();	
        }
       
     }
    
    private class PerformSync extends AsyncTask<Void, Integer, Long> {
        protected Long doInBackground(Void... arg0) {

        	Synchronization sync = new Synchronization(SynchronizationView.this);
			try {
				sync.startSynchronization();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
            return Long.valueOf(1);
        }
        
        protected void onPreExecute(){
        	onStartSync();        	
        }

        protected void onProgressUpdate(Integer... progress) {
        	Log.i("PROGRESS: ", String.valueOf(progress[0]));
           // setProgressPercent(progress[0]);
        }

        protected void onPostExecute(Long result) {
        	onEndSync(result);
           // showDialog("Downloaded " + result + " bytes");
        }

    }
    
    private void onStartSync(){
    	Log.i("PREEXECUTE: ", "PREEXECUTE");
    	mMessageText.setText("Sychronization started. Please wait...(Nice wait animation is going on :)");
    }
    
    private void onEndSync(Long result){
    	Log.i("SYNCRONIZATION DONE: ", String.valueOf(result));
    	mMessageText.setText("Synchronization finished");
    }

}
