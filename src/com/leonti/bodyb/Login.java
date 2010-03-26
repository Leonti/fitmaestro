package com.leonti.bodyb;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Login extends Activity {
	private EditText mEmailText;
	private EditText mPasswordText;
    private ExcercisesDbAdapter mDbHelper;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        
        mEmailText = (EditText) findViewById(R.id.edit_email);
        mPasswordText = (EditText) findViewById(R.id.edit_password);
        
        mDbHelper = new ExcercisesDbAdapter(this);
        mDbHelper.open();
        
        Button btnLogin = (Button) findViewById(R.id.button_login);
        btnLogin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	
                loginUser(mEmailText.getText().toString(),
                			mPasswordText.getText().toString());
            }
        });
    }
    
    public int loginUser(String email, String password){
  	 
    		 ServerJson Js = new ServerJson();
    		 switch(Js.loginUser(email, password, mDbHelper)){
    		 case ServerJson.SUCCESS:
    	     		Toast.makeText(this, R.string.login_success, Toast.LENGTH_SHORT).show();
    			 break;
    		 case ServerJson.INVALID:
    	     		Toast.makeText(this, R.string.invalid_credentials, Toast.LENGTH_SHORT).show();
    			 break;
    		 case ServerJson.NO_CONNECTION:
 	     		Toast.makeText(this, R.string.no_connection, Toast.LENGTH_SHORT).show();
 			 break;
    		 }
    	
    	return 0;
    }

}
