package com.leonty.fitmaestro;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener;
import com.leonty.fitmaestro.domain.FitmaestroDb;
import com.leonty.fitmaestro.domain.Session;

public class SessionTabs extends SherlockActivity {

	private static final int ACTIVITY_CREATE = 0;
	private static final int ACTIVITY_EDIT = 1;
	private static final int INSERT_ID = Menu.FIRST;
	private static final int DELETE_ID = Menu.FIRST + 1;
	private static final int EDIT_ID = Menu.FIRST + 2;	
	
	private FitmaestroDb db;
	private Session session;		

	private Cursor mSessionsCursor;	
	private String mFilter;
	
	ListView lv;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		db = new FitmaestroDb(this).open();
		session = new Session(db);	
		
		Bundle extras = getIntent().getExtras();
		mFilter = savedInstanceState != null ? savedInstanceState
				.getString("filter") : null;
		if (mFilter == null && extras != null) {
			mFilter = extras.getString("filter");
		} else {
			mFilter = "INPROGRESS";
		} 

        setContentView(R.layout.session_tabs);	
        
        lv = (ListView) findViewById(R.id.session_list);		
		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> adapterView, View v, int position,
					long id) {
				Intent i = new Intent(getApplicationContext(), SessionView.class);
				i.putExtra(FitmaestroDb.KEY_ROWID, id);
				startActivityForResult(i, 5);				
			}			
		});
		
		lv.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {

			public void onCreateContextMenu(ContextMenu menu, View view,
					ContextMenuInfo menuInfo) {
				AdapterView.AdapterContextMenuInfo info;
				info = (AdapterView.AdapterContextMenuInfo) menuInfo;

				String title = ((TextView) info.targetView).getText().toString();

				menu.setHeaderTitle(title);
				menu.add(0, EDIT_ID, 0, R.string.edit_session);
				menu.add(0, DELETE_ID, 1, R.string.delete_session);				
			}
		});
		
		fillData();		

		getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        
        ActionBar.Tab tab_inprogress = getSupportActionBar().newTab();
        tab_inprogress.setText(R.string.tab_inprogress);
        tab_inprogress.setTabListener(new InProgressTabListener());
        getSupportActionBar().addTab(tab_inprogress);
        
        ActionBar.Tab tab_done = getSupportActionBar().newTab();
        tab_done.setText(R.string.tab_done);
        tab_done.setTabListener(new DoneTabListener());
        getSupportActionBar().addTab(tab_done);        		
	}

	@Override
	protected void onDestroy() {
		db.close();
		super.onDestroy();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString("filter", mFilter);
	}
	
	private void fillData() {

		mSessionsCursor = session.fetchFilteredSessions(mFilter);
		startManagingCursor(mSessionsCursor);
		String[] from = new String[] { FitmaestroDb.KEY_TITLE };
		int[] to = new int[] { R.id.session_name };
		SimpleCursorAdapter sessions = new SimpleCursorAdapter(this,
				R.layout.session_list_row, mSessionsCursor, from, to);
		lv.setAdapter(sessions);
	}
	
	private class DoneTabListener implements ActionBar.TabListener {
		
		public void onTabSelected(Tab tab, FragmentTransaction ft) {
			mFilter = "DONE";
			fillData();	
		}

		public void onTabUnselected(Tab tab, FragmentTransaction ft) {}
		public void onTabReselected(Tab tab, FragmentTransaction ft) {}		
	}

	private class InProgressTabListener implements ActionBar.TabListener {
		public void onTabSelected(Tab tab, FragmentTransaction ft) {
			mFilter = "INPROGRESS";
			fillData();	
		}

		public void onTabUnselected(Tab tab, FragmentTransaction ft) {}
		public void onTabReselected(Tab tab, FragmentTransaction ft) {}				
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        menu.add(R.string.add).setOnMenuItemClickListener(new OnMenuItemClickListener() {

			public boolean onMenuItemClick(MenuItem item) {
				createSession();
				return false;
			}
        	
        }).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        
        return true;
    }	
	
	@Override
	public boolean onContextItemSelected(android.view.MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();

		switch (item.getItemId()) {
		case DELETE_ID:
			session.deleteSession(info.id);
			fillData();
			return true;

		case EDIT_ID:
			editSession(info.id);
			return true;
		}
		return super.onContextItemSelected(item);
	}
	
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
	    int itemId = item.getItemId();
	    switch (itemId) {
	    case android.R.id.home:
	    	startActivity (new Intent(getApplicationContext(), Dashboard.class));
	        break;
	    }

	    return true;		
	}
	
	private void createSession() {
		Intent i = new Intent(getApplicationContext(), SessionEdit.class);
		startActivityForResult(i, ACTIVITY_CREATE);
	}

	private void editSession(long id) {
		Intent i = new Intent(this, SessionEdit.class);
		i.putExtra(FitmaestroDb.KEY_ROWID, id);
		startActivityForResult(i, ACTIVITY_EDIT);
	}	
}
