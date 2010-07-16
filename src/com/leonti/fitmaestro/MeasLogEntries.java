package com.leonti.fitmaestro;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import com.leonti.fitmaestro.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class MeasLogEntries extends ListActivity {
	private static final int ACTIVITY_EDIT = 1;
	private static final int DIALOG_EDIT_MEAS = 2;

	private static final int INSERT_ID = Menu.FIRST;
	private static final int DELETE_ID = Menu.FIRST + 1;

	private ExcercisesDbAdapter mDbHelper;
	private Cursor mMeasLogCursor;
	private Long mMeasurementId;
	private Long mMeasLogId;
	DateFormat iso8601Format;
	DateFormat iso8601FormatLocal;
	private Dialog mEditValueDialog;
	String TAG = "MeasLogEntries";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.log_entries_list);

		mMeasurementId = savedInstanceState != null ? savedInstanceState
				.getLong(ExcercisesDbAdapter.KEY_ROWID) : null;
		if (mMeasurementId == null) {
			Bundle extras = getIntent().getExtras();
			mMeasurementId = extras != null ? extras
					.getLong(ExcercisesDbAdapter.KEY_ROWID) : null;
		}

		mDbHelper = new ExcercisesDbAdapter(this);
		mDbHelper.open();
		iso8601Format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		iso8601Format.setTimeZone(TimeZone.getTimeZone("UTC"));
		iso8601FormatLocal = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		fillData();
		registerForContextMenu(getListView());
	}

	private void fillData() {

		/*
		 * Calendar beginning = Calendar.getInstance();
		 * beginning.set(Calendar.HOUR_OF_DAY, 0);
		 * beginning.set(Calendar.MINUTE, 0); beginning.set(Calendar.SECOND, 0);
		 * Calendar ending = (Calendar) beginning.clone();
		 * ending.add(Calendar.DAY_OF_YEAR, + 1);
		 * 
		 * String begin = iso8601Format.format(beginning.getTime()); String end
		 * = iso8601Format.format(ending.getTime());
		 */

		mMeasLogCursor = mDbHelper.fetchMeasLogEntries(mMeasurementId);
		startManagingCursor(mMeasLogCursor);
		String[] from = new String[] { ExcercisesDbAdapter.KEY_VALUE,
				ExcercisesDbAdapter.KEY_DATE };
		int[] to = new int[] { R.id.meas_value, R.id.entry_time };
		SimpleCursorAdapter measLog = new SimpleCursorAdapter(this,
				R.layout.meas_log_list_row, mMeasLogCursor, from, to);
		measLog.setViewBinder(new MyViewBinder());
		setListAdapter(measLog);
	}

	public class MyViewBinder implements SimpleCursorAdapter.ViewBinder {
		@Override
		public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
			if (columnIndex == cursor
					.getColumnIndex(ExcercisesDbAdapter.KEY_DATE)) {
				TextView timeText = (TextView) view;

				try {
					Date date = iso8601FormatLocal.parse(cursor
							.getString(columnIndex));
					long when = date.getTime();
					int flags = 0;
					flags |= android.text.format.DateUtils.FORMAT_SHOW_DATE;
					String finalDateTime = android.text.format.DateUtils
							.formatDateTime(getBaseContext(), when
									+ TimeZone.getDefault().getOffset(when),
									flags);
					timeText.setText(finalDateTime);
				} catch (ParseException e) {
					Log.e(TAG, "Parsing ISO8601 datetime failed", e);
				}
				return true;
			}
			return false;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, INSERT_ID, 0, R.string.add_entry);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case INSERT_ID:
			createEntry();
			return true;
		}

		return super.onMenuItemSelected(featureId, item);
	}

	private void createEntry() {
		mMeasLogId = null;
		showDialog(DIALOG_EDIT_MEAS);
		populateEntryDialog();
	}

	private void editEntry(long id) {
		/*
		 * mMeasLogId = id; showDialog(DIALOG_EDIT_MEAS); populateEntryDialog();
		 */
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		fillData();

		switch (requestCode) {
		case ACTIVITY_EDIT:
			Toast.makeText(this, R.string.log_entry_edited, Toast.LENGTH_SHORT)
					.show();
			break;
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View view,
			ContextMenuInfo menuInfo) {
		AdapterView.AdapterContextMenuInfo info;
		info = (AdapterView.AdapterContextMenuInfo) menuInfo;

		TextView measLogEntry = (TextView) view.findViewById(R.id.meas_value);
		String title = measLogEntry.getText().toString();

		menu.setHeaderTitle(title);
		menu.add(0, DELETE_ID, 1, R.string.delete_log_entry);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();

		switch (item.getItemId()) {
		case DELETE_ID:
			mDbHelper.deleteMeasLogEntry(info.id);
			fillData();
			return true;
		}
		return super.onContextItemSelected(item);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		mMeasLogId = id;
		showDialog(DIALOG_EDIT_MEAS);
		populateEntryDialog();

		/*
		 * Intent i = new Intent(this, SetView.class);
		 * i.putExtra(ExcercisesDbAdapter.KEY_ROWID, id);
		 * startActivityForResult(i, 5);
		 */
		// SHOW ADDITIONAL INFO FOR THIS LOG ENTRY
	}

	@Override
	protected Dialog onCreateDialog(int id) {

		switch (id) {
		case DIALOG_EDIT_MEAS:
			LayoutInflater factory = LayoutInflater.from(this);
			final View valueEditView = factory.inflate(
					R.layout.edit_meas_value, null);

			final EditText valueText = (EditText) valueEditView
					.findViewById(R.id.editText_value);

			mEditValueDialog = new AlertDialog.Builder(this).setTitle(
					R.string.edit_measurement_value).setView(valueEditView)
					.setPositiveButton(R.string.done,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {

									/* User clicked OK so do some stuff */

									String value = valueText.getText()
											.toString();

									if (value.length() > 0) {

										// entry is new, so we add it
										if (mMeasLogId == null) {

											mDbHelper.createMeasLogEntry(
													mMeasurementId, Float
															.valueOf(value
																	.trim()));
										} else {

											// entry is old so we update it
											mDbHelper.updateMeasLogEntry(
													mMeasLogId, Float
															.valueOf(value
																	.trim()));
										}

										fillData();
										registerForContextMenu(getListView());
									}

								}
							}).setNegativeButton(R.string.cancel,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {

									/* User clicked cancel so do some stuff */
								}
							}).create();

			return mEditValueDialog;
		}

		return null;
	}

	public void populateEntryDialog() {

		EditText valueText = (EditText) mEditValueDialog
				.findViewById(R.id.editText_value);

		// editing entry - prepopulating
		if (mMeasLogId != null) {
			Cursor MeasLogEntryCursor = mDbHelper.fetchMeasLogEntry(mMeasLogId);
			String value = MeasLogEntryCursor.getString(MeasLogEntryCursor
					.getColumnIndex(ExcercisesDbAdapter.KEY_VALUE));
			valueText.setText(value);

		} else {
			valueText.setText("");
		}
	}
}
