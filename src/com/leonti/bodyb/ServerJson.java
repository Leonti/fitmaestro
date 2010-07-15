package com.leonti.bodyb;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class ServerJson {
	public static final int SUCCESS = 0;
	public static final int NO_CONNECTION = 1;
	public static final int ALREADY_EXISTS = 2;
	public static final int INVALID = 3;
	
	public static final String address = "http://10.0.2.2/koh/remote";
	//public static final String address = "http://fitmaestro.com/remote";
	
	public JSONObject getServerData(JSONObject jsonIn) throws JSONException, ClientProtocolException, IOException {

	    DefaultHttpClient httpClient = new DefaultHttpClient();
		ResponseHandler <String> resonseHandler = new BasicResponseHandler();
		
		HttpPost postMethod = new HttpPost(address);
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);

		nameValuePairs.add(new BasicNameValuePair("jsonString", jsonIn.toString()));
		postMethod.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		String response = httpClient.execute(postMethod,resonseHandler);

		JSONObject jsonResponse = new JSONObject(response);

		/*
		JSONArray serverData1 = jsonResponse.getJSONArray("data1");
		JSONArray serverData2 = jsonResponse.getJSONArray("data2");
		for(int i = 0; i < serverData1.length() && i < serverData2.length(); i++) {
			//Do something with the data
		} */

		return jsonResponse;
   }
	
	public int registerUser(String email, String password, ExcercisesDbAdapter dbbHelper){
		JSONObject jsonSend = new JSONObject();
		JSONObject jsonAnswer = new JSONObject();
		try {
			jsonSend.put("what", "REGISTER");
			jsonSend.put("email", email);
			jsonSend.put("password", password);
			
			jsonAnswer = getServerData(jsonSend);
			String result = jsonAnswer.get("result").toString();
			
			if(result.equals("CREATED")){
		        dbbHelper.setAuthKey(jsonAnswer.get("authkey").toString());
		        
				Log.i("EPTEL: ", "Success!!!");
				return SUCCESS;
			}
			if(result.equals("EXISTS")){
				Log.i("EPTEL: ", "Exists!!!");
				return ALREADY_EXISTS;
			}
			
			Log.i("EPTEL: ", jsonAnswer.get("result").toString());
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return NO_CONNECTION;
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return NO_CONNECTION;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return NO_CONNECTION;
		}
		
		
		return NO_CONNECTION;
	}

	public int loginUser(String email, String password, ExcercisesDbAdapter dbbHelper){
		JSONObject jsonSend = new JSONObject();
		JSONObject jsonAnswer = new JSONObject();
		try {
			jsonSend.put("what", "LOGIN");
			jsonSend.put("email", email);
			jsonSend.put("password", password);
			
			jsonAnswer = getServerData(jsonSend);
			String result = jsonAnswer.get("result").toString();
			
			if(result.equals("LOGGEDIN")){
		        dbbHelper.setAuthKey(jsonAnswer.get("authkey").toString());
		        
				Log.i("EPTEL: ", "Success!!!");
				return SUCCESS;
			}
			if(result.equals("INVALID")){
				Log.i("EPTEL: ", "Invalid credentials!!!");
				return INVALID;
			}
			
			Log.i("EPTEL: ", jsonAnswer.get("result").toString());
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return NO_CONNECTION;
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return NO_CONNECTION;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return NO_CONNECTION;
		}
		
		
		return NO_CONNECTION;
	}
	
	public JSONObject getUpdates(String authKey, JSONObject sendFirstData, long fresh){
		JSONObject jsonSend = new JSONObject();
		JSONObject jsonAnswer = new JSONObject();

		try {
			jsonSend.put("what", "STARTUPDATE");
			jsonSend.put("authkey", authKey);
			jsonSend.put("data", sendFirstData);
			jsonSend.put("fresh", fresh);
			
			jsonAnswer = getServerData(jsonSend);
			String result = jsonAnswer.get("result").toString();
			
			if(result.equals("STARTUPDATED")){
				return jsonAnswer.getJSONObject("data");	
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		return null;
	}
	
	public JSONObject finishUpdates(String authKey, JSONObject sendSecondData){
		JSONObject jsonSend = new JSONObject();
		JSONObject jsonAnswer = new JSONObject();

		try {
			jsonSend.put("what", "FINISHUPDATE");
			jsonSend.put("authkey", authKey);
			jsonSend.put("data", sendSecondData);
			
			jsonAnswer = getServerData(jsonSend);
			String result = jsonAnswer.get("result").toString();
			
			if(result.equals("FINISHUPDATED")){
				return jsonAnswer.getJSONObject("data");	
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		return null;
	}
	
	public JSONObject getPublicExercises(String authKey){
		JSONObject jsonSend = new JSONObject();

		try {
			jsonSend.put("what", "PUBLICEXERCISES");
			jsonSend.put("authkey", authKey);
			
			return getServerData(jsonSend);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		return null;
	}
	
	public JSONObject getPublicPrograms(String authKey){
		JSONObject jsonSend = new JSONObject();

		try {
			jsonSend.put("what", "PUBLICPROGRAMS");
			jsonSend.put("authkey", authKey);
			
			return getServerData(jsonSend);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		return null;
	}

	public JSONObject importExercises(String authKey, JSONArray toImport) {

		JSONObject jsonSend = new JSONObject();
		
		try {
			jsonSend.put("what", "IMPORTEXERCISES");
			jsonSend.put("authkey", authKey);
			jsonSend.put("data", toImport);
			return getServerData(jsonSend);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		return null;
	}
	
	public JSONObject importPrograms(String authKey, JSONArray toImport) {

		JSONObject jsonSend = new JSONObject();
		
		try {
			jsonSend.put("what", "IMPORTPROGRAMS");
			jsonSend.put("authkey", authKey);
			jsonSend.put("data", toImport);
			return getServerData(jsonSend);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		return null;
	}
	
}
