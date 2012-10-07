package com.leonty.fitmaestro;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.leonty.fitmaesto.remote.ServerJson;
import com.leonty.fitmaestro.domain.FitmaestroDb;
import com.leonty.fitmaestro.domain.Synchro;

public class Imports {

	private FitmaestroDb db;
	private Synchro synchro;	
	
	public Imports(Context ctx) {

		db = new FitmaestroDb(ctx).open();
		synchro = new Synchro(db);			
	}

	public JSONArray getPublicExercises() throws JSONException {

		String authKey = synchro.getAuthKey();
		ServerJson Js = new ServerJson();
		JSONArray jsonExercises = Js.getPublicExercises(authKey).getJSONArray(
				"data");

		return jsonExercises;
	}

	public JSONArray getPublicPrograms() throws JSONException {

		String authKey = synchro.getAuthKey();
		ServerJson Js = new ServerJson();
		return Js.getPublicPrograms(authKey).getJSONArray("data");
	}

	public JSONObject importExercises(JSONArray toImport) {
		String authKey = synchro.getAuthKey();
		ServerJson Js = new ServerJson();
		return Js.importExercises(authKey, toImport);
	}

	public JSONObject importPrograms(JSONArray toImport) {
		String authKey = synchro.getAuthKey();
		ServerJson Js = new ServerJson();
		return Js.importPrograms(authKey, toImport);
	}

}
