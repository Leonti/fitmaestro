package com.leonty.fitmaestro;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class Account extends Activity {

	private static final int LOGIN_POS = 0;
	private static final int REGISTER_POS = 1;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.account);

		ListView listViewAccount = (ListView) findViewById(R.id.ListView_account);
		listViewAccount
				.setOnItemClickListener(new ListView.OnItemClickListener() {

					public void onItemClick(AdapterView<?> arg0, View v,
							int position, long id) {

						Log.v("ITEM POSITION", String.valueOf(position));
						switch (position) {
						case REGISTER_POS:
							Intent i = new Intent(Account.this, Register.class);
							startActivity(i);
							break;
						case LOGIN_POS:
							Intent i1 = new Intent(Account.this, Login.class);
							startActivity(i1);
							break;
						}
					}
				});

	}
}
