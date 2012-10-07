package com.leonty.fitmaestro;

import android.app.ExpandableListActivity;
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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.ExpandableListContextMenuInfo;
import android.widget.SimpleCursorTreeAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.leonty.fitmaestro.domain.Exercise;
import com.leonty.fitmaestro.domain.ExerciseGroup;
import com.leonty.fitmaestro.domain.FitmaestroDb;

public class ExercisesList extends ExpandableListActivity {

	private static final int INSERT_GROUP_ID = Menu.FIRST;
	private static final int INSERT_EXERCISE_ID = Menu.FIRST + 1;
	private static final int DELETE_ID = Menu.FIRST + 2;
	private static final int EDIT_ID = Menu.FIRST + 3;

	private static final int ACTIVITY_GROUP_CREATE = 0;
	private static final int ACTIVITY_GROUP_EDIT = 1;
	private static final int ACTIVITY_EXERCISE_EDIT = 3;

	private Cursor mGroupsCursor;
	private int mGroupIdColumnIndex;

	private FitmaestroDb db;
	private Exercise exercise;	
	private ExerciseGroup exerciseGroup;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		db = new FitmaestroDb(this).open();
		exercise = new Exercise(db);
		exerciseGroup = new ExerciseGroup(db);
		
		fillData();
		registerForContextMenu(getExpandableListView());
	}
	
	@Override
	protected void onDestroy() {
		db.close();
		super.onDestroy();
	}

	private void fillData() {
		mGroupsCursor = exerciseGroup.fetchAllGroups();
		startManagingCursor(mGroupsCursor);

		// Cache the ID column index
		mGroupIdColumnIndex = mGroupsCursor
				.getColumnIndexOrThrow(FitmaestroDb.KEY_ROWID);

		// Set up our adapter
		MyExpandableListAdapter adapter = new MyExpandableListAdapter(mGroupsCursor,this,
				
				android.R.layout.simple_expandable_list_item_1,
				R.layout.exercise_list_row,

				new String[] { FitmaestroDb.KEY_TITLE }, // group title for group layouts
				new int[] { android.R.id.text1 },
				
				new String[] { FitmaestroDb.KEY_TITLE }, // exercise title for child layouts
				new int[] { R.id.exercise_title });
		
		setListAdapter(adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuItem insertGroup = menu.add(0, INSERT_GROUP_ID, 0, R.string.add_group);
		insertGroup.setIcon(android.R.drawable.ic_menu_add);
		MenuItem insertExercise = menu.add(0, INSERT_EXERCISE_ID, 1, R.string.add_excercise);
		insertExercise.setIcon(android.R.drawable.ic_menu_add);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case INSERT_GROUP_ID:
			createGroup();
			return true;

		case INSERT_EXERCISE_ID:
			createExercise();
			return true;
		}

		return super.onMenuItemSelected(featureId, item);
	}

	private void createGroup() {
		Intent i = new Intent(this, GroupEdit.class);
		startActivityForResult(i, ACTIVITY_GROUP_CREATE);
	}

	private void createExercise() {
		Intent i = new Intent(this, ExerciseEdit.class);
		startActivityForResult(i, ACTIVITY_GROUP_CREATE);
	}

	public void editGroup(long id) {
		Intent i = new Intent(this, GroupEdit.class);
		i.putExtra(FitmaestroDb.KEY_ROWID, id);
		startActivityForResult(i, ACTIVITY_GROUP_EDIT);
	}

	private void editExercise(long id) {
		Intent i = new Intent(this, ExerciseEdit.class);
		i.putExtra(FitmaestroDb.KEY_ROWID, id);
		startActivityForResult(i, ACTIVITY_EXERCISE_EDIT);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		fillData();

		switch (requestCode) {
		case ACTIVITY_GROUP_EDIT:
			Toast.makeText(this, R.string.group_edited, Toast.LENGTH_SHORT)
					.show();
			break;
		case ACTIVITY_EXERCISE_EDIT:
			Toast.makeText(this, R.string.exercise_edited, Toast.LENGTH_SHORT)
					.show();
			break;
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View view,
			ContextMenuInfo menuInfo) {
		ExpandableListContextMenuInfo info;

		info = (ExpandableListContextMenuInfo) menuInfo;
		

		int type = ExpandableListView
				.getPackedPositionType(info.packedPosition);
		if (type == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
			TextView titleText = (TextView) info.targetView.findViewById(R.id.exercise_title);
			String title = titleText.getText().toString();
			menu.setHeaderTitle(title);
			
			menu.add(0, EDIT_ID, 0, R.string.edit_exercise);
			menu.add(0, DELETE_ID, 1, R.string.delete_exercise);
		} else if (type == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
			String title = ((TextView) info.targetView).getText().toString();
			menu.setHeaderTitle(title);
			menu.add(0, EDIT_ID, 0, R.string.edit_group);
			menu.add(0, DELETE_ID, 1, R.string.delete_group);
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		ExpandableListContextMenuInfo info = (ExpandableListContextMenuInfo) item
				.getMenuInfo();

		int type = ExpandableListView
				.getPackedPositionType(info.packedPosition);

		if (type == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
			switch (item.getItemId()) {
			case DELETE_ID:
				exercise.deleteExcercise(info.id);
				fillData();
				Toast.makeText(this, R.string.exercise_deleted,
						Toast.LENGTH_SHORT).show();
				return true;
			case EDIT_ID:
				editExercise(info.id);
				return true;
			}
		} else if (type == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
			switch (item.getItemId()) {
			case DELETE_ID:
				exerciseGroup.deleteGroup(info.id);
				fillData();
				Toast
						.makeText(this, R.string.group_deleted,
								Toast.LENGTH_SHORT).show();
				return true;
			case EDIT_ID:
				editGroup(info.id);
				return true;
			}
		}

		return false;
	}

	public class MyExpandableListAdapter extends SimpleCursorTreeAdapter {

		public MyExpandableListAdapter(Cursor cursor, Context context,
				int groupLayout, int childLayout, String[] groupFrom,
				int[] groupTo, String[] childrenFrom, int[] childrenTo) {
			super(context, cursor, groupLayout, groupFrom, groupTo,
					childLayout, childrenFrom, childrenTo);
		}

		@Override
		protected Cursor getChildrenCursor(Cursor groupCursor) {
			Cursor exercisesCursor = exercise.fetchExcercisesForGroup(groupCursor
							.getLong(mGroupIdColumnIndex));
			startManagingCursor(exercisesCursor);
			return exercisesCursor;
		}
		
		public View getChildView(final int groupPosition,
				final int childPosition, boolean isLastChild, View convertView,
				ViewGroup parent) {
			View rowView = super.getChildView(groupPosition, childPosition,
					isLastChild, convertView, parent);
			Log.d("Generating child view: ", "generating");

			
			Button details = (Button) rowView.findViewById(R.id.view_button);
	
			details.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					
					Cursor exerciseCursor = getChild(groupPosition, childPosition);
					
					Long exerciseId = exerciseCursor.getLong(exerciseCursor.getColumnIndex(FitmaestroDb.KEY_ROWID));
					Log.i("Exercise id is: ", String.valueOf(exerciseId));
					
					Intent i = new Intent(ExercisesList.this, ExerciseView.class);
					i.putExtra(FitmaestroDb.KEY_ROWID, exerciseId);
					startActivity(i);
					
				}
			});

			return rowView;
		}
		
		
	}

	@Override
	public boolean onChildClick(ExpandableListView parent, View v,
			int groupPosition, int childPosition, long id) {
		Log.i("EX ID: ", String.valueOf(id));
		Intent resultIntent = new Intent();
		resultIntent.putExtra(FitmaestroDb.KEY_EXERCISEID, id);
		setResult(RESULT_OK, resultIntent);
		finish();

		return super.onChildClick(parent, v, groupPosition, childPosition, id);
	}
}
