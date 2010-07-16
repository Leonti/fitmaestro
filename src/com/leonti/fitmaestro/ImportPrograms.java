package com.leonti.fitmaestro;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.leonti.fitmaestro.R;
import com.leonti.fitmaestro.ImportExercises.MyExpandableListAdapter;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class ImportPrograms extends ListActivity {

	private int mResult;
	private static final String TITLE = "TITLE";
	private static final String DESC = "DESC";
	private static final int IMPORT_ID = Menu.FIRST;

	private JSONArray programsJson;
	private List<Map<String, String>> programsData;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.import_programs_list);

		new FetchPrograms().execute();
	}

	public void fillList() throws JSONException {

		programsData = new ArrayList<Map<String, String>>();
		for (int i = 0; i < programsJson.length(); i++) {
			JSONObject program = programsJson.getJSONObject(i);
			Map<String, String> curProgramMap = new HashMap<String, String>();
			curProgramMap.put(TITLE, program.getString("title"));
			curProgramMap.put(DESC, program.getString("desc"));
			int imported = program.getInt("imported");
			String checked = imported == 1 ? "true" : "false";
			curProgramMap.put("checked", checked);

			programsData.add(curProgramMap);
			Log.i("Program title:", program.getString("title"));
		}

		String[] from = new String[] { TITLE, DESC };
		int[] to = new int[] { R.id.title, R.id.desc };

		SimpleAdapter programs = new MySimpleAdapter(this, programsData,
				R.layout.import_programs_list_row, from, to);
		setListAdapter(programs);
	}

	private class MySimpleAdapter extends SimpleAdapter {

		public MySimpleAdapter(Context context,
				List<? extends Map<String, ?>> data, int resource,
				String[] from, int[] to) {
			super(context, data, resource, from, to);
			// TODO Auto-generated constructor stub
		}

		public View getView(final int position, View convertView,
				ViewGroup parent) {
			View rowView = super.getView(position, convertView, parent);

			CheckBox cb = (CheckBox) rowView.findViewById(R.id.check1);
			cb.setChecked(Boolean.parseBoolean(programsData.get(position).get(
					"checked")));

			cb.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					CheckBox clickedCheckbox = (CheckBox) v;
					programsData.get(position).put("checked",
							String.valueOf(clickedCheckbox.isChecked()));
				}
			});

			return rowView;
		}
	}

	private class FetchPrograms extends AsyncTask<Void, Integer, Long> {

		private ProgressDialog mProgress = new ProgressDialog(
				ImportPrograms.this);

		protected Long doInBackground(Void... arg0) {

			// Synchronization sync = new
			// Synchronization(SynchronizationView.this);
			Imports imports = new Imports(ImportPrograms.this);

			try {
				programsJson = imports.getPublicPrograms();

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
				Toast.makeText(ImportPrograms.this, R.string.no_connection,
						Toast.LENGTH_LONG).show();
			}
		}

	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		CheckBox cb = (CheckBox) v.findViewById(R.id.check1);

		if (cb != null)
			cb.toggle();

		programsData.get(position).put("checked",
				String.valueOf(cb.isChecked()));
		Log.d("Checkbox value: ", programsData.get(position).get("checked"));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, IMPORT_ID, 0, R.string.import_programs);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case IMPORT_ID:
			try {
				importPrograms();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return true;
		}

		return super.onMenuItemSelected(featureId, item);
	}

	public void importPrograms() throws JSONException {
		JSONArray toImport = new JSONArray();
		for (int i = 0; i < programsJson.length(); i++) {
			JSONObject program = programsJson.getJSONObject(i);
			String checked = programsData.get(i).get("checked");
			int imported = program.getInt("imported");
			if (imported == 0 && Boolean.parseBoolean(checked)) {
				toImport.put(program.getInt("id"));
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
				ImportPrograms.this);
		private JSONArray toImport;

		public doImport(JSONArray ids) {
			toImport = ids;
		}

		protected Long doInBackground(Void... arg0) {

			// Synchronization sync = new
			// Synchronization(SynchronizationView.this);
			Imports imports = new Imports(ImportPrograms.this);
			Synchronization sync = new Synchronization(ImportPrograms.this);

			imports.importPrograms(toImport);
			try {
				mResult = sync.startSynchronization();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return Long.valueOf(1);
		}

		protected void onPreExecute() {

			mProgress.setMessage(getString(R.string.importing_programs));
			mProgress.show();
		}

		protected void onProgressUpdate(Integer... progress) {
			Log.i("PROGRESS: ", String.valueOf(progress[0]));
			// setProgressPercent(progress[0]);
		}

		protected void onPostExecute(Long result) {
			mProgress.dismiss();
			Toast.makeText(ImportPrograms.this, R.string.programs_imported,
					Toast.LENGTH_LONG).show();
			finish();
			if (mResult == ServerJson.NO_CONNECTION) {
				Toast.makeText(ImportPrograms.this, R.string.no_connection,
						Toast.LENGTH_LONG).show();
			}
		}

	}
}
