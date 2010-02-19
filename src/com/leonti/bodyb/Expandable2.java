package com.leonti.bodyb;


import android.app.ExpandableListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.SimpleCursorTreeAdapter;

public class Expandable2 extends ExpandableListActivity {
	
    private static final int INSERT_GROUP_ID = Menu.FIRST;
    private static final int DELETE_GROUP_ID = Menu.FIRST + 1;
    private static final int INSERT_EXERCISE_ID = Menu.FIRST + 2;
    private static final int DELETE_EXERCISE_ID = Menu.FIRST + 3;
    
    private ExcercisesDbAdapter mDbHelper;
    private Cursor mGroupsCursor;
    private int mGroupIdColumnIndex; 
    private ExpandableListAdapter mAdapter;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDbHelper = new ExcercisesDbAdapter(this);
        mDbHelper.open();
        fillData();
    }
    
    private void fillData(){
    	mGroupsCursor = mDbHelper.fetchAllGroups();
    	startManagingCursor(mGroupsCursor);

        // Cache the ID column index
        mGroupIdColumnIndex = mGroupsCursor.getColumnIndexOrThrow(ExcercisesDbAdapter.KEY_ROWID);

        // Set up our adapter
        mAdapter = new MyExpandableListAdapter(mGroupsCursor,
                this,
                android.R.layout.simple_expandable_list_item_1,
                android.R.layout.simple_expandable_list_item_1,
                new String[] {ExcercisesDbAdapter.KEY_TITLE}, // group title for group layouts
                new int[] {android.R.id.text1},
                new String[] {ExcercisesDbAdapter.KEY_TITLE}, // exercise title for child layouts
                new int[] {android.R.id.text1});
        setListAdapter(mAdapter);   	
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, INSERT_GROUP_ID, 0, R.string.add_group);
        menu.add(0, INSERT_EXERCISE_ID, 1, R.string.add_excercise);
        return true;
    }
    
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch(item.getItemId()) {
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
        startActivityForResult(i, 0);
    }
    
    private void createExercise() {
        Intent i = new Intent(this, ExcerciseEdit.class);
        startActivityForResult(i, 0);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, 
                                    Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        fillData();
    }
    
    public class MyExpandableListAdapter extends SimpleCursorTreeAdapter {

        public MyExpandableListAdapter(Cursor cursor, Context context, int groupLayout,
                int childLayout, String[] groupFrom, int[] groupTo, String[] childrenFrom,
                int[] childrenTo) {
            super(context, cursor, groupLayout, groupFrom, groupTo, childLayout, childrenFrom,
                    childrenTo);
        }

        @Override
        protected Cursor getChildrenCursor(Cursor groupCursor) {
        	Cursor exercisesCursor = mDbHelper.fetchExcercisesForGroup(groupCursor.getLong(mGroupIdColumnIndex));
        	startManagingCursor(exercisesCursor);
            return exercisesCursor;
        }

    }
    
    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int
    groupPosition, int childPosition, long id)
    {
    	Log.i("EX ID: ", String.valueOf(id));
    	/*
    CheckedTextView tempView = (CheckedTextView)v.findViewById
    (android.R.id.text1);
    tempView.setChecked(!tempView.isChecked()); */
    return super.onChildClick(parent, v, groupPosition,
    childPosition, id);
    }
}
