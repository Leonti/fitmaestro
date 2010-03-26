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

public class Register extends Activity {
	private EditText mEmailText;
	private EditText mPasswordText;
	private EditText mRepeatPasswordText;
    private ExcercisesDbAdapter mDbHelper;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        
        mEmailText = (EditText) findViewById(R.id.edit_email);
        mPasswordText = (EditText) findViewById(R.id.edit_password);
        mRepeatPasswordText = (EditText) findViewById(R.id.edit_repeatpassword);
        
        mDbHelper = new ExcercisesDbAdapter(this);
        mDbHelper.open();
        
        Button btnRegister = (Button) findViewById(R.id.button_register);
        btnRegister.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	
                registerUser(mEmailText.getText().toString(),
                			mPasswordText.getText().toString(),
                			mRepeatPasswordText.getText().toString());
            }
        });
    }
    
    public int registerUser(String email, String password, String repeatPassword){
    	Log.i("DYG", email + " " + password + " " + repeatPassword);
    	int errors = 0;
    	if(!password.equals(repeatPassword)){
    		Toast.makeText(this, R.string.password_missmatch, Toast.LENGTH_SHORT).show();
    		errors = 1;
    	}
    	
    	 //Initialize reg ex for email.  
    	 String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";   
    	 //Make the comparison case-insensitive.  
    	 Pattern pattern = Pattern.compile(expression,Pattern.CASE_INSENSITIVE);  
    	 Matcher matcher = pattern.matcher(email);  
    	 if(!matcher.matches()){  
     		Toast.makeText(this, R.string.email_invalid, Toast.LENGTH_SHORT).show();
    		errors = 1;  
    	 }
    	 
    	 if(errors == 0){
    		 ServerJson Js = new ServerJson();
    		 switch(Js.registerUser(email, password, mDbHelper)){
    		 case ServerJson.SUCCESS:
    	     		Toast.makeText(this, R.string.account_created, Toast.LENGTH_SHORT).show();
    			 break;
    		 case ServerJson.ALREADY_EXISTS:
    	     		Toast.makeText(this, R.string.email_registered, Toast.LENGTH_SHORT).show();
    			 break;
    		 case ServerJson.NO_CONNECTION:
    	     		Toast.makeText(this, R.string.no_connection, Toast.LENGTH_SHORT).show();
    			 break;
    		 }
    	 }
    	
    	return 0;
    }
}
