package com.leonti.fitmaestro;

import org.json.JSONException;

import com.leonti.fitmaestro.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class SynchronizationView extends Activity {

	private ExcercisesDbAdapter mDbHelper;
	private TextView mMessageText;
	private int mResult;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.synchronization);

		mDbHelper = new ExcercisesDbAdapter(this);
		mDbHelper.open();

		// for now
		mMessageText = (TextView) findViewById(R.id.TextResult);
		
		Button btnStart = (Button) findViewById(R.id.btnStart);
		btnStart.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				
				// check if user is authenticated, if not - go to login screen
				if (mDbHelper.getAuthKey().equals("")) {
					Intent i = new Intent(SynchronizationView.this, Login.class);
					startActivity(i);
				} else {
					new PerformSync().execute();
				}
			}

		});

	}
	
	@Override
	protected void onDestroy() {
		mDbHelper.close();
		super.onDestroy();
	}

	private class PerformSync extends AsyncTask<Void, Integer, Long> {

		private ProgressDialog mProgress = new ProgressDialog(
				SynchronizationView.this);

		protected Long doInBackground(Void... arg0) {

			Synchronization sync = new Synchronization(SynchronizationView.this);
			try {
				mResult = sync.startSynchronization();
			} catch (JSONException e) {
				Log.i("ERROR: ", e.getMessage());
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return Long.valueOf(1);
		}

		protected void onPreExecute() {

			mProgress.setMessage(getString(R.string.synchronizing));
			mProgress.show();

			onStartSync();
		}

		protected void onProgressUpdate(Integer... progress) {
			Log.i("PROGRESS: ", String.valueOf(progress[0]));
			// setProgressPercent(progress[0]);
		}

		protected void onPostExecute(Long result) {
			mProgress.dismiss();
			onEndSync(result);
			if (mResult == ServerJson.NO_CONNECTION) {
				Toast.makeText(SynchronizationView.this,
						R.string.no_connection, Toast.LENGTH_LONG).show();
			}
			// showDialog("Downloaded " + result + " bytes");
		}

	}

	private void onStartSync() {
		Log.i("PREEXECUTE: ", "PREEXECUTE");
		// mMessageText.setText("Sychronization started. Please wait...(Nice wait animation is going on :)");
	}

	private void onEndSync(Long result) {
		Log.i("SYNCRONIZATION DONE: ", String.valueOf(result));
		if (mResult == ServerJson.SUCCESS) {
			mMessageText.setText(R.string.synchronization_finished);
		} else {
			mMessageText.setText(R.string.synchronization_failed);
		}

	}

}
