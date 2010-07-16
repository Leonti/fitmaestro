package com.leonti.fitmaestro;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.leonti.fitmaestro.R;

import android.app.ExpandableListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.Toast;

public class ImportExercises extends ExpandableListActivity {

	private int mResult;
	private static final String TITLE = "TITLE";
	private static final String DESC = "DESC";
	private static final int IMPORT_ID = Menu.FIRST;

	private JSONArray exercisesData;
	private ExpandableListAdapter mAdapter;
	private List<Map<String, String>> groupData;
	List<List<Map<String, String>>> childData;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		new FetchExercises().execute();
	}

	public void fillList() throws JSONException {
		groupData = new ArrayList<Map<String, String>>();
		childData = new ArrayList<List<Map<String, String>>>();

		for (int i = 0; i < exercisesData.length(); i++) {
			JSONObject group = exercisesData.getJSONObject(i);

			Map<String, String> curGroupMap = new HashMap<String, String>();
			groupData.add(curGroupMap);
			curGroupMap.put(TITLE, group.getString("title"));
			curGroupMap.put(DESC, group.getString("desc"));

			Log.i("Group name: ", group.getString("title"));
			JSONArray group_exercises = group.getJSONArray("exercises");

			Log.i("Exercises: ", String.valueOf(group_exercises.length()));

			List<Map<String, String>> children = new ArrayList<Map<String, String>>();
			for (int j = 0; j < group_exercises.length(); j++) {
				JSONObject exercise = group_exercises.getJSONObject(j);
				Map<String, String> curChildMap = new HashMap<String, String>();
				children.add(curChildMap);
				curChildMap.put(TITLE, exercise.getString("title"));
				curChildMap.put(DESC, exercise.getString("desc"));
				int imported = exercise.getInt("imported");
				String checked = imported == 1 ? "true" : "false";
				curChildMap.put("checked", checked);

				Log.i("Exercise name: ", exercise.getString("title"));
			}
			childData.add(children);
		}

		// Set up our adapter
		mAdapter = new MyExpandableListAdapter(this, groupData,
				android.R.layout.simple_expandable_list_item_1, new String[] {
						TITLE, DESC }, new int[] { android.R.id.text1,
						android.R.id.text2 }, childData,
				R.layout.import_child_row, new String[] { TITLE, DESC },
				new int[] { R.id.title, R.id.desc });
		setListAdapter(mAdapter);
	}

	public class MyExpandableListAdapter extends SimpleExpandableListAdapter {

		public MyExpandableListAdapter(Context context,
				List<? extends Map<String, ?>> groupData,
				int expandedGroupLayout, String[] groupFrom, int[] groupTo,
				List<? extends List<? extends Map<String, ?>>> childData,
				int childLayout, String[] childFrom, int[] childTo) {
			super(context, groupData, expandedGroupLayout, groupFrom, groupTo,
					childData, childLayout, childFrom, childTo);
			// TODO Auto-generated constructor stub
		}

		public View getChildView(final int groupPosition,
				final int childPosition, boolean isLastChild, View convertView,
				ViewGroup parent) {
			View rowView = super.getChildView(groupPosition, childPosition,
					isLastChild, convertView, parent);
			Log.d("Generating child view: ", "generating");

			Log.d("Checkbox value: ", childData.get(groupPosition).get(
					childPosition).get("checked"));

			CheckBox cb = (CheckBox) rowView.findViewById(R.id.check1);
			cb.setChecked(Boolean.parseBoolean(childData.get(groupPosition)
					.get(childPosition).get("checked")));

			cb.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					CheckBox clickedCheckbox = (CheckBox) v;
					childData.get(groupPosition).get(childPosition).put(
							"checked",
							String.valueOf(clickedCheckbox.isChecked()));
				}
			});

			return rowView;
		}

	}

	public boolean onChildClick(ExpandableListView parent, View v,
			int groupPosition, int childPosition, long id) {
		Log.d("Child click: ", "onChildClick: " + childPosition);
		CheckBox cb = (CheckBox) v.findViewById(R.id.check1);

		if (cb != null)
			cb.toggle();

		childData.get(groupPosition).get(childPosition).put("checked",
				String.valueOf(cb.isChecked()));
		Log.d("Checkbox value: ", childData.get(groupPosition).get(
				childPosition).get("checked"));
		return false;
	}

	public void onGroupExpand(int groupPosition) {
		Log.d("Group expand: ", "onGroupExpand: " + groupPosition);
	}

	private class FetchExercises extends AsyncTask<Void, Integer, Long> {

		private ProgressDialog mProgress = new ProgressDialog(
				ImportExercises.this);

		protected Long doInBackground(Void... arg0) {

			// Synchronization sync = new
			// Synchronization(SynchronizationView.this);
			Imports imports = new Imports(ImportExercises.this);

			try {
				exercisesData = imports.getPublicExercises();

			} catch (JSONException e) {
				Log.i("ERROR: ", e.getMessage());
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return Long.valueOf(1);
		}

		protected void onPreExecute() {

			mProgress.setMessage(getString(R.string.fetching));
			mProgress.show();
		}

		protected void onProgressUpdate(Integer... progress) {
			Log.i("PROGRESS: ", String.valueOf(progress[0]));
			// setProgressPercent(progress[0]);
		}

		protected void onPostExecute(Long result) {
			mProgress.dismiss();
			try {
				fillList();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (mResult == ServerJson.NO_CONNECTION) {
				Toast.makeText(ImportExercises.this, R.string.no_connection,
						Toast.LENGTH_LONG).show();
			}
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, IMPORT_ID, 0, R.string.import_exercises);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case IMPORT_ID:
			try {
				importExercises();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return true;
		}

		return super.onMenuItemSelected(featureId, item);
	}

	public void importExercises() throws JSONException {
		JSONArray toImport = new JSONArray();
		for (int i = 0; i < exercisesData.length(); i++) {
			JSONArray groupExercises = exercisesData.getJSONObject(i)
					.getJSONArray("exercises");
			for (int j = 0; j < groupExercises.length(); j++) {
				JSONObject exercise = groupExercises.getJSONObject(j);
				String checked = childData.get(i).get(j).get("checked");
				int imported = exercise.getInt("imported");

				// exercise is not currently imported and checkbox is checked
				if (imported == 0 && Boolean.parseBoolean(checked)) {
					toImport.put(exercise.getInt("id"));
				}
			}
		}

		if (toImport.length() > 0) {
			new doImport(toImport).execute();
		} else {
			finish();
		}
	}

	private class doImport extends AsyncTask<Void, Integer, Long> {

		private ProgressDialog mProgress = new ProgressDialog(
				ImportExercises.this);
		private JSONArray toImport;

		public doImport(JSONArray ids) {
			toImport = ids;
		}

		protected Long doInBackground(Void... arg0) {

			// Synchronization sync = new
			// Synchronization(SynchronizationView.this);
			Imports imports = new Imports(ImportExercises.this);
			Synchronization sync = new Synchronization(ImportExercises.this);

			imports.importExercises(toImport);
			try {
				mResult = sync.startSynchronization();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return Long.valueOf(1);
		}

		protected void onPreExecute() {

			mProgress.setMessage(getString(R.string.importing_exercises));
			mProgress.show();
		}

		protected void onProgressUpdate(Integer... progress) {
			Log.i("PROGRESS: ", String.valueOf(progress[0]));
			// setProgressPercent(progress[0]);
		}

		protected void onPostExecute(Long result) {
			mProgress.dismiss();
			Toast.makeText(ImportExercises.this, R.string.exercises_imported,
					Toast.LENGTH_LONG).show();
			finish();
			if (mResult == ServerJson.NO_CONNECTION) {
				Toast.makeText(ImportExercises.this, R.string.no_connection,
						Toast.LENGTH_LONG).show();
			}
		}

	}

}
