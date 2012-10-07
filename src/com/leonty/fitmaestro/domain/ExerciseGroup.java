package com.leonty.fitmaestro.domain;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;

public class ExerciseGroup {

	FitmaestroDb db;
	
	public ExerciseGroup(FitmaestroDb db) {
		this.db = db;
	}	
	
	// GROUPS methods
	public long createGroup(String title, String desc, long site_id) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(FitmaestroDb.KEY_TITLE, title);
		initialValues.put(FitmaestroDb.KEY_DESC, desc);
		initialValues.put(FitmaestroDb.KEY_SITEID, site_id);

		return db.getDb().insert(FitmaestroDb.DATABASE_GROUPS_TABLE, null, initialValues);
	}

	public boolean deleteGroup(long rowId) {

		// delete exercises for this group first
		deleteExercisesForGroup(rowId);

		ContentValues args = new ContentValues();
		args.put(FitmaestroDb.KEY_DELETED, 1);

		return db.getDb().update(FitmaestroDb.DATABASE_GROUPS_TABLE, args, FitmaestroDb.KEY_ROWID + "=" + rowId,
				null) > 0;
	}

	public boolean deleteExercisesForGroup(long groupId) {

		ContentValues args = new ContentValues();
		args.put(FitmaestroDb.KEY_DELETED, 1);

		return db.getDb().update(FitmaestroDb.DATABASE_EXERCISES_TABLE, args, FitmaestroDb.KEY_GROUPID + "="
				+ groupId, null) > 0;
	}	
	
	public Cursor fetchAllGroups() {

		return db.getDb().query(FitmaestroDb.DATABASE_GROUPS_TABLE, new String[] { FitmaestroDb.KEY_ROWID,
				FitmaestroDb.KEY_TITLE, FitmaestroDb.KEY_DESC, FitmaestroDb.KEY_SITEID }, FitmaestroDb.KEY_DELETED + "=0", null,
				null, null, null);
	}

	public Cursor fetchGroup(long rowId, long siteId) throws SQLException {

		// if site_id is present - fetch group using site_id
		String condition = FitmaestroDb.KEY_ROWID + "=" + rowId;
		if (siteId != 0) {
			condition = FitmaestroDb.KEY_SITEID + "=" + siteId;
		}
		Cursor mCursor = db.getDb().query(true, FitmaestroDb.DATABASE_GROUPS_TABLE, new String[] {
				FitmaestroDb.KEY_ROWID, FitmaestroDb.KEY_TITLE, FitmaestroDb.KEY_DESC, FitmaestroDb.KEY_SITEID }, condition, null,
				null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;

	}

	public Cursor fetchUpdatedGroups(String updated) {

		return db.getDb().query(FitmaestroDb.DATABASE_GROUPS_TABLE, new String[] { FitmaestroDb.KEY_ROWID,
				FitmaestroDb.KEY_TITLE, FitmaestroDb.KEY_DESC, FitmaestroDb.KEY_SITEID, FitmaestroDb.KEY_UPDATED, FitmaestroDb.KEY_DELETED },
				FitmaestroDb.KEY_UPDATED + " >  ?", new String[] { updated }, null, null,
				null, null);
	}

	public boolean updateGroup(long rowId, String title, String desc,
			long site_id) {
		ContentValues args = new ContentValues();
		args.put(FitmaestroDb.KEY_TITLE, title);
		args.put(FitmaestroDb.KEY_DESC, desc);
		args.put(FitmaestroDb.KEY_SITEID, site_id);

		return db.getDb().update(FitmaestroDb.DATABASE_GROUPS_TABLE, args, FitmaestroDb.KEY_ROWID + "=" + rowId,
				null) > 0;
	}

	// end of GROUPS
}
