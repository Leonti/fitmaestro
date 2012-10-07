package com.leonty.fitmaestro.domain;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;

public class Synchro {
	
	FitmaestroDb db;
	
	public Synchro(FitmaestroDb db) {
		this.db = db;
	}
	
	public long createItem(String table, HashMap<String, String> fields) {
		ContentValues initialValues = new ContentValues();

		Set<Map.Entry<String, String>> set = fields.entrySet();

		for (Map.Entry<String, String> entry : set) {
			initialValues.put(entry.getKey(), entry.getValue());
		}

		return db.getDb().insert(table, null, initialValues);
	}

	public boolean updateItem(String table, HashMap<String, String> fields,
			long rowId) {
		ContentValues args = new ContentValues();

		Set<Map.Entry<String, String>> set = fields.entrySet();

		for (Map.Entry<String, String> entry : set) {
			args.put(entry.getKey(), entry.getValue());
		}

		return db.getDb().update(table, args, FitmaestroDb.KEY_ROWID + "=" + rowId, null) > 0;
	}

	public Cursor fetchItemBySiteId(String table, String siteId)
			throws SQLException {

		Cursor mCursor = db.getDb().query(true, table, new String[] { FitmaestroDb.KEY_ROWID },
				FitmaestroDb.KEY_SITEID + "=" + siteId, null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}


	public Cursor fetchUpdatedItems(String table, String updated) {

		return db.getDb().query(table, null, FitmaestroDb.KEY_UPDATED + " >  ?",
				new String[] { updated }, null, null, null, null);
	}	

	public String getLocalTime() {

		Cursor mCursor = db.getDb().rawQuery(
				"SELECT strftime('%s','now') AS localtime", null);

		if (mCursor != null) {
			mCursor.moveToFirst();
		}

		return mCursor.getString(mCursor.getColumnIndex("localtime"));

	}

	// settings

	public boolean setAuthKey(String authkey) {
		ContentValues args = new ContentValues();
		args.put(FitmaestroDb.KEY_AUTHKEY, authkey);

		return db.getDb()
				.update(FitmaestroDb.DATABASE_SETTINGS_TABLE, args, FitmaestroDb.KEY_ROWID + "=1", null) > 0;
	}

	public String getAuthKey() throws SQLException {

		Cursor mCursor = db.getDb().query(true, FitmaestroDb.DATABASE_SETTINGS_TABLE,
				new String[] { FitmaestroDb.KEY_AUTHKEY }, FitmaestroDb.KEY_ROWID + "=1", null, null,
				null, null, null);

		if (mCursor != null) {
			mCursor.moveToFirst();
		}

		return mCursor.getString(mCursor.getColumnIndex(FitmaestroDb.KEY_AUTHKEY));
	}

	public String getLastUpdated() throws SQLException {

		Cursor mCursor = db.getDb().query(true, FitmaestroDb.DATABASE_SETTINGS_TABLE,
				new String[] { FitmaestroDb.KEY_LASTUPDATED }, FitmaestroDb.KEY_ROWID + "=1", null, null,
				null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor.getString(mCursor.getColumnIndex(FitmaestroDb.KEY_LASTUPDATED));
	}

	public void setLastUpdated() {
		db.getDb().execSQL("update " + FitmaestroDb.DATABASE_SETTINGS_TABLE + " set "
				+ FitmaestroDb.KEY_LASTUPDATED + "= DATETIME('NOW') WHERE _id=1");
	}
	
	// FILES methods
	public Cursor fetchCurrentFiles() {
		return db.getDb().query(FitmaestroDb.DATABASE_FILES_TABLE, null, FitmaestroDb.KEY_DELETED
				+ "=0", null, null, null, null);
	}
	
	public Cursor fetchDeletedFiles() {
		return db.getDb().query(FitmaestroDb.DATABASE_FILES_TABLE, null, FitmaestroDb.KEY_DELETED
				+ "=1", null, null, null, null);
	}
	
	public Cursor fetchFile(long rowId) throws SQLException {

		Cursor mCursor = db.getDb().query(true, FitmaestroDb.DATABASE_FILES_TABLE, null,
				FitmaestroDb.KEY_ROWID + "=" + rowId, null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}
	// END of FILES methods	
}
