package com.leonty.fitmaestro;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.leonty.fitmaesto.remote.ServerJson;
import com.leonty.fitmaestro.domain.FitmaestroDb;
import com.leonty.fitmaestro.domain.Synchro;

public class Login extends Activity {
	private EditText mEmailText;
	private EditText mPasswordText;

	private FitmaestroDb db;
	private Synchro synchro;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);

		db = new FitmaestroDb(this).open();
		synchro = new Synchro(db);	
		
		mEmailText = (EditText) findViewById(R.id.edit_email);
		mPasswordText = (EditText) findViewById(R.id.edit_password);

		Button btnLogin = (Button) findViewById(R.id.button_login);
		btnLogin.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				new PerformLogin().execute(mEmailText.getText().toString(),
						mPasswordText.getText().toString());
			}
		});
		
		((Button) findViewById(R.id.button_register)).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				startActivity (new Intent(getApplicationContext(), Register.class));
			}
		});	
	}
	
	@Override
	protected void onDestroy() {
		db.close();
		super.onDestroy();
	}

	private class PerformLogin extends AsyncTask<String, Integer, Long> {
		protected Long doInBackground(String... userData) {

			ServerJson Js = new ServerJson();

			return Long.valueOf(Js.loginUser(userData[0], userData[1],
					synchro));
		}

		protected void onPreExecute() {
			// onStartSync();
		}

		protected void onPostExecute(Long result) {
			onEndLogin(result);
		}

	}

	private void onEndLogin(Long result) {

		Log.i("Login DONE: ", String.valueOf(result));

		switch (result.intValue()) {
		case ServerJson.SUCCESS:
			Toast.makeText(this, R.string.login_success, Toast.LENGTH_SHORT)
					.show();
			finish();
			break;
		case ServerJson.INVALID:
			Toast.makeText(this, R.string.invalid_credentials,
					Toast.LENGTH_SHORT).show();
			break;
		case ServerJson.NO_CONNECTION:
			Toast.makeText(this, R.string.no_connection, Toast.LENGTH_SHORT)
					.show();
			break;
		}
	}

}
