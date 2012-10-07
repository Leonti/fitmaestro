package com.leonty.fitmaestro;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.leonty.fitmaestro.domain.FitmaestroDb;
import com.leonty.fitmaestro.domain.Session;

public class SessionView extends ListActivity {

	private Cursor mExercisesForSessionCursor;
	private Long mRowId;
	private String mStatus;

	
	private static final int ADD_ID = Menu.FIRST;
	private static final int DELETE_ID = Menu.FIRST + 1;
	private static final int DONE_ID = Menu.FIRST + 2;
	private static final int INPROGRESS_ID = Menu.FIRST + 3;

	private static final int ACTIVITY_ADD = 0;

	private FitmaestroDb db;
	private Session session;		
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sessionview_list);

		db = new FitmaestroDb(this).open();
		session = new Session(db);				
		
		Bundle extras = getIntent().getExtras();
		mRowId = savedInstanceState != null ? savedInstanceState
				.getLong(FitmaestroDb.KEY_ROWID) : null;

		if (mRowId == null && extras != null) {
			mRowId = extras.getLong(FitmaestroDb.KEY_ROWID);
		}


		fillData();
		registerForContextMenu(getListView());
	}
	
	@Override
	protected void onDestroy() {
		db.close();
		super.onDestroy();
	}

	private void fillData() {

		Cursor sessionCursor = session.fetchSession(mRowId);
		startManagingCursor(sessionCursor);
		mStatus = sessionCursor.getString(sessionCursor
				.getColumnIndexOrThrow(FitmaestroDb.KEY_STATUS));

		mExercisesForSessionCursor = session.fetchExercisesForSession(mRowId);
		startManagingCursor(mExercisesForSessionCursor);
		String[] from = new String[] { FitmaestroDb.KEY_TITLE };
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
		if(resultCode == RESULT_OK){
			Bundle extras = intent.getExtras();
			Long ExerciseId = extras != null ? extras
					.getLong(FitmaestroDb.KEY_EXERCISEID) : null;
			session.addExerciseToSession(mRowId, ExerciseId);
			fillData();
			Log.i("EXERCISE ID FROM ACTIVITY: ", String.valueOf(ExerciseId));
			Log.i("SESSION ID FROM ACTIVITY: ", String.valueOf(mRowId));
		}
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
		session.updateSessionStatus(mRowId, "INPROGRESS");
		finish();
	}

	private void markDone() {
		Log.i("Updating", "Updatingg");
		session.updateSessionStatus(mRowId, "DONE");
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
			session.deleteExerciseFromSession(info.id);
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
		i.putExtra(FitmaestroDb.KEY_ROWID, id);
		startActivity(i);

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putLong(FitmaestroDb.KEY_ROWID, mRowId);
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
					.getColumnIndexOrThrow(FitmaestroDb.KEY_EXERCISEID));

			Long exType = cursor.getLong(cursor
					.getColumnIndexOrThrow(FitmaestroDb.KEY_TYPE));

			Long sessionsConnectorId = cursor.getLong(cursor
					.getColumnIndexOrThrow(FitmaestroDb.KEY_ROWID));

			SessionRepsArray repsArray = new SessionRepsArray(SessionView.this,
					sessionId, exerciseId, sessionsConnectorId);
			ArrayList<HashMap<String, String>> sessionRepsList = repsArray
			.getRepsArray();

			repsArray.drawTable(SessionView.this, view, sessionRepsList, exType);

		}

	}
}
