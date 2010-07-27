package com.leonti.fitmaestro;

import java.util.ArrayList;
import java.util.HashMap;

import com.leonti.fitmaestro.R;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class SessionView extends ListActivity {

	private ExcercisesDbAdapter mDbHelper;
	private Cursor mExercisesForSessionCursor;
	private Long mRowId;
	private String mStatus;

	private static final int ADD_ID = Menu.FIRST;
	private static final int DELETE_ID = Menu.FIRST + 1;
	private static final int DONE_ID = Menu.FIRST + 2;
	private static final int INPROGRESS_ID = Menu.FIRST + 3;

	private static final int ACTIVITY_ADD = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sessionview_list);

		Bundle extras = getIntent().getExtras();
		mRowId = savedInstanceState != null ? savedInstanceState
				.getLong(ExcercisesDbAdapter.KEY_ROWID) : null;

				if (mRowId == null && extras != null) {
					mRowId = extras.getLong(ExcercisesDbAdapter.KEY_ROWID);
				}

				mDbHelper = new ExcercisesDbAdapter(this);
				mDbHelper.open();
				fillData();
				registerForContextMenu(getListView());
	}
	
	@Override
	protected void onDestroy() {
		mDbHelper.close();
		super.onDestroy();
	}

	private void fillData() {

		Cursor session = mDbHelper.fetchSession(mRowId);
		startManagingCursor(session);
		mStatus = session.getString(session
				.getColumnIndexOrThrow(ExcercisesDbAdapter.KEY_STATUS));

		mExercisesForSessionCursor = mDbHelper.fetchExercisesForSession(mRowId);
		startManagingCursor(mExercisesForSessionCursor);
		String[] from = new String[] { ExcercisesDbAdapter.KEY_TITLE };
		int[] to = new int[] { R.id.exercise_name };
		SessionViewCursorAdapter excercises = new SessionViewCursorAdapter(
				this, R.layout.sessionview_list_row,
				mExercisesForSessionCursor, from, to);
		setListAdapter(excercises);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		Bundle extras = intent.getExtras();
		Long ExerciseId = extras != null ? extras
				.getLong(ExcercisesDbAdapter.KEY_EXERCISEID) : null;
				mDbHelper.addExerciseToSession(mRowId, ExerciseId);
				fillData();
				Log.i("EXERCISE ID FROM ACTIVITY: ", String.valueOf(ExerciseId));
				Log.i("SESSION ID FROM ACTIVITY: ", String.valueOf(mRowId));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuItem insert = menu.add(0, ADD_ID, 0, R.string.add_exercise_to_session);
		insert.setIcon(android.R.drawable.ic_menu_add);

		if (mStatus.equals("DONE")) {
			menu.add(0, INPROGRESS_ID, 0, R.string.set_inprogress);
		} else {
			menu.add(0, DONE_ID, 0, R.string.set_done);
		}
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case ADD_ID:
			addExercise();
			return true;
		case INPROGRESS_ID:
			markInProgress();
			return true;
		case DONE_ID:
			markDone();
			return true;
		}

		return super.onMenuItemSelected(featureId, item);
	}

	private void addExercise() {
		Intent i = new Intent(this, ExercisesList.class);
		startActivityForResult(i, ACTIVITY_ADD);
	}

	private void markInProgress() {
		mDbHelper.updateSessionStatus(mRowId, "INPROGRESS");
		finish();
	}

	private void markDone() {
		Log.i("Updating", "Updatingg");
		mDbHelper.updateSessionStatus(mRowId, "DONE");
		finish();
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View view,
			ContextMenuInfo menuInfo) {
		AdapterView.AdapterContextMenuInfo info;
		info = (AdapterView.AdapterContextMenuInfo) menuInfo;

		TextView exerciseName = (TextView) info.targetView
		.findViewById(R.id.exercise_name);
		String title = exerciseName.getText().toString();
		menu.setHeaderTitle(title);
		menu.add(0, DELETE_ID, 0, R.string.remove_from_session);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case DELETE_ID:
			AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
			.getMenuInfo();
			mDbHelper.deleteExerciseFromSession(info.id);
			fillData();
			Toast.makeText(this, R.string.exercise_removed_from_session,
					Toast.LENGTH_SHORT).show();
			return true;
		}
		return super.onContextItemSelected(item);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {

		super.onListItemClick(l, v, position, id);
		Intent i = new Intent(this, SessionRepsList.class);
		i.putExtra(ExcercisesDbAdapter.KEY_ROWID, id);
		startActivity(i);

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putLong(ExcercisesDbAdapter.KEY_ROWID, mRowId);
	}

	protected class SessionViewCursorAdapter extends SimpleCursorAdapter {

		Activity mActivity;

		public SessionViewCursorAdapter(Activity activity, int layout,
				Cursor c, String[] from, int[] to) {
			super(activity, layout, c, from, to);

			mActivity = activity;

		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			super.bindView(view, context, cursor);

			Log.i("BIND", "bind view called");

			Long sessionId = mRowId;
			Long exerciseId = cursor.getLong(cursor
					.getColumnIndexOrThrow(ExcercisesDbAdapter.KEY_EXERCISEID));

			Long exType = cursor.getLong(cursor
					.getColumnIndexOrThrow(ExcercisesDbAdapter.KEY_TYPE));

			Long sessionsConnectorId = cursor.getLong(cursor
					.getColumnIndexOrThrow(ExcercisesDbAdapter.KEY_ROWID));

			SessionRepsArray repsArray = new SessionRepsArray(SessionView.this,
					sessionId, exerciseId, sessionsConnectorId);
			ArrayList<HashMap<String, String>> sessionRepsList = repsArray
			.getRepsArray();

			repsArray.drawTable(SessionView.this, view, sessionRepsList, exType);

		}

	}
}
