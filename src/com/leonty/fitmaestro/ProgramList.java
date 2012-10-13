package com.leonty.fitmaestro;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
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

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener;
import com.leonty.fitmaestro.domain.FitmaestroDb;
import com.leonty.fitmaestro.domain.Program;

public class ProgramList extends SherlockActivity {

	private static final int ACTIVITY_CREATE = 0;
	private static final int ACTIVITY_EDIT = 1;
	private static final int INSERT_ID = Menu.FIRST;
	private static final int DELETE_ID = Menu.FIRST + 1;
	private static final int EDIT_ID = Menu.FIRST + 2;

	private Cursor mProgramsCursor;

	private FitmaestroDb db;
	private Program program;	

	ListView lv;		
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		db = new FitmaestroDb(this).open();
		program = new Program(db);		
	
		setContentView(R.layout.program_list);	

        lv = (ListView) findViewById(R.id.program_list);		
		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> adapterView, View v, int position,
					long id) {

				Intent i = new Intent(getApplicationContext(), ProgramView.class);
				i.putExtra(FitmaestroDb.KEY_ROWID, id);
				startActivity(i);			
			}			
		});		

		lv.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {

			public void onCreateContextMenu(ContextMenu menu, View view,
					ContextMenuInfo menuInfo) {
				AdapterView.AdapterContextMenuInfo info;
				info = (AdapterView.AdapterContextMenuInfo) menuInfo;

				String title = ((TextView) info.targetView).getText().toString();

				menu.setHeaderTitle(title);
				menu.add(0, EDIT_ID, 0, R.string.edit_program);
				menu.add(0, DELETE_ID, 1, R.string.delete_program);		
			}
		});			
		
		fillData();		
		
	}
	
	@Override
	protected void onDestroy() {
		db.close();
		super.onDestroy();
	}

	private void fillData() {
		mProgramsCursor = program.fetchAllPrograms();
		startManagingCursor(mProgramsCursor);
		String[] from = new String[] { FitmaestroDb.KEY_TITLE };
		int[] to = new int[] { R.id.program_name };
		SimpleCursorAdapter programs = new SimpleCursorAdapter(this,
				R.layout.program_list_row, mProgramsCursor, from, to);
		lv.setAdapter(programs);
	}
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        menu.add(R.string.add).setOnMenuItemClickListener(new OnMenuItemClickListener() {

			public boolean onMenuItemClick(MenuItem item) {
				createProgram();
				return false;
			}
        	
        }).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        
        return true;
    }	

	private void createProgram() {
		Intent i = new Intent(this, ProgramEdit.class);
		startActivityForResult(i, ACTIVITY_CREATE);
	}

	private void editProgram(long id) {
		Intent i = new Intent(this, ProgramEdit.class);
		i.putExtra(FitmaestroDb.KEY_ROWID, id);
		startActivityForResult(i, ACTIVITY_EDIT);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		fillData();

		switch (requestCode) {
		case ACTIVITY_EDIT:
			Toast.makeText(this, R.string.program_edited, Toast.LENGTH_SHORT)
					.show();
			break;
		}
	}

	@Override
	public boolean onContextItemSelected(android.view.MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();

		switch (item.getItemId()) {
		case DELETE_ID:
			program.deleteProgram(info.id);
			fillData();
			return true;

		case EDIT_ID:
			editProgram(info.id);
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
}
