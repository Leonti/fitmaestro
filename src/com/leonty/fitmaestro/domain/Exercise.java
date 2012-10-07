package com.leonty.fitmaestro.domain;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;

public class Exercise {

	FitmaestroDb db;
	
	public Exercise(FitmaestroDb db) {
		this.db = db;
	}
	
	// EXCERCISES methods
	public long createExercise(String title, String desc, long group_id,
			int type, long max_reps, float max_weight) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(FitmaestroDb.KEY_TITLE, title);
		initialValues.put(FitmaestroDb.KEY_DESC, desc);
		initialValues.put(FitmaestroDb.KEY_MAX_REPS, max_reps);
		initialValues.put(FitmaestroDb.KEY_MAX_WEIGHT, max_weight);
		initialValues.put(FitmaestroDb.KEY_GROUPID, group_id);
		initialValues.put(FitmaestroDb.KEY_TYPE, type);

		return db.getDb().insert(FitmaestroDb.DATABASE_EXERCISES_TABLE, null, initialValues);
	}

	public boolean deleteExcercise(long rowId) {

		ContentValues args = new ContentValues();
		args.put(FitmaestroDb.KEY_DELETED, 1);

		return db.getDb().update(FitmaestroDb.DATABASE_EXERCISES_TABLE, args, FitmaestroDb.KEY_ROWID + "="
				+ rowId, null) > 0;
	}

	public Cursor fetchExcercisesForGroup(long group_id) {

		return db.getDb().query(FitmaestroDb.DATABASE_EXERCISES_TABLE, new String[] { FitmaestroDb.KEY_ROWID,
				FitmaestroDb.KEY_TITLE, FitmaestroDb.KEY_DESC, FitmaestroDb.KEY_TYPE, FitmaestroDb.KEY_GROUPID, FitmaestroDb.KEY_SITEID },
				FitmaestroDb.KEY_GROUPID + "=" + group_id + " AND " + FitmaestroDb.KEY_DELETED + "=0",
				null, null, null, null, null);
	}

	public Cursor fetchExercise(long rowId) throws SQLException {

		Cursor mCursor =

		db.getDb().query(true, FitmaestroDb.DATABASE_EXERCISES_TABLE, null,
				FitmaestroDb.KEY_ROWID + "=" + rowId, null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;

	}

	public Cursor fetchUpdatedExercises(String updated) {

		return db.getDb().query(FitmaestroDb.DATABASE_EXERCISES_TABLE, new String[] { FitmaestroDb.KEY_ROWID,
				FitmaestroDb.KEY_TITLE, FitmaestroDb.KEY_DESC, FitmaestroDb.KEY_TYPE, FitmaestroDb.KEY_GROUPID, FitmaestroDb.KEY_SITEID,
				FitmaestroDb.KEY_UPDATED }, FitmaestroDb.KEY_UPDATED + " >  ?", new String[] { updated },
				null, null, null, null);
	}

	public boolean updateExercise(long rowId, String title, String desc,
			long group_id, int type, long max_reps, float max_weight) {
		ContentValues args = new ContentValues();
		args.put(FitmaestroDb.KEY_TITLE, title);
		args.put(FitmaestroDb.KEY_DESC, desc);
		args.put(FitmaestroDb.KEY_GROUPID, group_id);
		args.put(FitmaestroDb.KEY_MAX_REPS, max_reps);
		args.put(FitmaestroDb.KEY_MAX_WEIGHT, max_weight);
		args.put(FitmaestroDb.KEY_TYPE, type);
		args.put(FitmaestroDb.KEY_UPDATED, "CURRENT_TIMESTAMP");

		return db.getDb().update(FitmaestroDb.DATABASE_EXERCISES_TABLE, args, FitmaestroDb.KEY_ROWID + "="
				+ rowId, null) > 0;
	}

	// end of EXCERCISES methods	
}
