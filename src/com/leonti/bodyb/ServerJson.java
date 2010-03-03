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

public class ServerJson {
	public JSONObject getServerData(JSONObject jsonIn) throws JSONException, ClientProtocolException, IOException {

	    DefaultHttpClient httpClient = new DefaultHttpClient();
		ResponseHandler <String> resonseHandler = new BasicResponseHandler();
		HttpPost postMethod = new HttpPost("http://10.0.2.2/bodyb/api.php");
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
}
