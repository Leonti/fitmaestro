package com.leonti.fitmaestro;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.leonti.fitmaestro.R;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TabHost;

public class FitMaestro extends TabActivity {

	private static final int EXERCISES_POS = 0;
	private static final int WORKOUTS_POS = 1;
	private static final int PROGRAMS_POS = 2;
	private static final int SESSIONS_POS = 3;
	private static final int MEASUREMENTS_POS = 4;
	private static final int STATISTICS_POS = 5;

	private static final int ACCOUNT_POS = 0;
	private static final int SYNCHRONIZE_POS = 1;
	private static final int DOWNLOADS_POS = 2;
	
	private static final int SETTINGS_ID = Menu.FIRST;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		TabHost tabHost = getTabHost();
		LayoutInflater.from(this).inflate(R.layout.main,
				tabHost.getTabContentView(), true);
		Resources res = getResources(); // Resource object to get Drawables

		tabHost
				.addTab(tabHost.newTabSpec("tab1").setIndicator(
						getString(R.string.exercising), res.getDrawable(R.drawable.tab_exercising)).setContent(
						R.id.tab_exercising));
		tabHost
				.addTab(tabHost.newTabSpec("tab2").setIndicator(
						getString(R.string.fitmaestro), res.getDrawable(R.drawable.tab_fitmaestro)).setContent(
						R.id.tab_fitmaestro));

		ListView listViewExercising = (ListView) findViewById(R.id.ListView_exercising);
		
		// initialize the List of Maps
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		String exercisingTitles[] = getResources().getStringArray(R.array.exercising_tab);
		String exercisingDescs[] = getResources().getStringArray(R.array.exercising_tab_descs);
		
		for(int i=0; i<exercisingTitles.length; i++){
			Map<String, String> map = new HashMap<String, String>();
			map.put("title", exercisingTitles[i]);
			map.put("desc", exercisingDescs[i]);
			list.add(map);	
		}
		String[] from = {"title", "desc"};
		int[] to = {R.id.title, R.id.desc};

		SimpleAdapter exercisingAdapter = new SimpleAdapter(this, list, R.layout.row_with_description, from, to);
		listViewExercising.setAdapter(exercisingAdapter);
		
		ListView listViewFitmaestro = (ListView) findViewById(R.id.ListView_fitmaestro);
		
		// initialize the List of Maps
		List<Map<String, String>> listFitmaestro = new ArrayList<Map<String, String>>();
		String fitmaestroTitles[] = getResources().getStringArray(R.array.fitmaestro_tab);
		String fitmaestroDescs[] = getResources().getStringArray(R.array.fitmaestro_tab_descs);

		for(int i=0; i<fitmaestroTitles.length; i++){
			Map<String, String> map = new HashMap<String, String>();
			map.put("title", fitmaestroTitles[i]);
			map.put("desc", fitmaestroDescs[i]);
			listFitmaestro.add(map);	
		}

		SimpleAdapter fitmaestroAdapter = new SimpleAdapter(this, listFitmaestro, R.layout.row_with_description, from, to);
		listViewFitmaestro.setAdapter(fitmaestroAdapter);
		
		listViewExercising
				.setOnItemClickListener(new ListView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View v,
							int position, long id) {

						Log.v("ITEM POSITION", String.valueOf(position));
						switch (position) {
						case EXERCISES_POS:
							Intent i = new Intent(FitMaestro.this,
									ExercisesList.class);
							startActivity(i);
							break;
						case WORKOUTS_POS:
							Intent i1 = new Intent(FitMaestro.this,
									WorkoutsList.class);
							startActivity(i1);
							break;
						case PROGRAMS_POS:
							Intent i2 = new Intent(FitMaestro.this,
									ProgramsList.class);
							startActivity(i2);
							break;
						case SESSIONS_POS:
							Intent i3 = new Intent(FitMaestro.this,
									SessionTabs.class);
							startActivity(i3);
							break;
						case MEASUREMENTS_POS:
							Intent i4 = new Intent(FitMaestro.this,
									MeasurementsList.class);
							startActivity(i4);
							break;
						case STATISTICS_POS:
							Intent i5 = new Intent(FitMaestro.this,
									Statistics.class);
							startActivity(i5);
							break;
						}
					}
				});

		listViewFitmaestro
				.setOnItemClickListener(new ListView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View v,
							int position, long id) {

						Log.v("ITEM POSITION", String.valueOf(position));
						switch (position) {
						case ACCOUNT_POS:
							Intent i = new Intent(FitMaestro.this,
									Account.class);
							startActivity(i);
							break;
						case SYNCHRONIZE_POS:
							Intent i1 = new Intent(FitMaestro.this,
									SynchronizationView.class);
							startActivity(i1);
							break;
						case DOWNLOADS_POS:
							Intent i2 = new Intent(FitMaestro.this,
									Downloads.class);
							startActivity(i2);
							break;
						}
					}
				});

	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuItem insert = menu.add(0, SETTINGS_ID, 0, R.string.settings);
		insert.setIcon(android.R.drawable.ic_menu_preferences);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case SETTINGS_ID:
			Intent i = new Intent(this, Settings.class);
			startActivity(i);
			return true;
		}

		return super.onMenuItemSelected(featureId, item);
	}
}