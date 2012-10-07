package com.leonty.fitmaestro.domain;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.util.Log;

public class Workout {
	
	FitmaestroDb db;
	
	public Workout(FitmaestroDb db) {
		this.db = db;
	}
	
	// SETS methods
	public long createSet(String title, String desc, long site_id) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(FitmaestroDb.KEY_TITLE, title);
		initialValues.put(FitmaestroDb.KEY_DESC, desc);
		initialValues.put(FitmaestroDb.KEY_SITEID, site_id);

		return db.getDb().insert(FitmaestroDb.DATABASE_SETS_TABLE, null, initialValues);
	}

	public long addExerciseToSet(long setId, long exerciseId) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(FitmaestroDb.KEY_SETID, setId);
		initialValues.put(FitmaestroDb.KEY_EXERCISEID, exerciseId);
		
		// check if exercise is already there - if it is - do not add
		/*
		Cursor mCursor =
			db.getDb().query(true, FitmaestroDb.DATABASE_SETS_CONNECTOR_TABLE, null, 
					FitmaestroDb.KEY_EXERCISEID + "=? AND " + FitmaestroDb.KEY_SETID + "=? AND " + FitmaestroDb.KEY_DELETED + "=0",
					new String[]{String.valueOf(exerciseId), String.valueOf(setId)}, null, null, null, null);

		if(mCursor.getCount() > 0){
			Log.i("ALREADY ADDED:", "Exercise already added");
			return 1;
		}else{
			*/
			return db.getDb().insert(FitmaestroDb.DATABASE_SETS_CONNECTOR_TABLE, null, initialValues);	
	//	}
	}

	public boolean deleteSet(long rowId) {

		ContentValues args = new ContentValues();
		args.put(FitmaestroDb.KEY_DELETED, 1);

		return db.getDb().update(FitmaestroDb.DATABASE_SETS_TABLE, args, FitmaestroDb.KEY_ROWID + "=" + rowId,
				null) > 0;
	}

	public Cursor fetchAllSets() {

		return db.getDb().query(FitmaestroDb.DATABASE_SETS_TABLE, new String[] { FitmaestroDb.KEY_ROWID,
				FitmaestroDb.KEY_TITLE, FitmaestroDb.KEY_DESC, FitmaestroDb.KEY_SITEID }, FitmaestroDb.KEY_DELETED + "=0", null,
				null, null, null);
	}

	// same as fetchAll - in future implement FitmaestroDb.KEY_ROWID NOT IN (SELECT set_id
	// FROM PROGRAMS_CONNECTOR)
	public Cursor fetchFreeSets() {

		return db.getDb().query(FitmaestroDb.DATABASE_SETS_TABLE, new String[] { FitmaestroDb.KEY_ROWID,
				FitmaestroDb.KEY_TITLE, FitmaestroDb.KEY_DESC, FitmaestroDb.KEY_SITEID }, FitmaestroDb.KEY_DELETED + "=0 AND " + FitmaestroDb.KEY_ROWID 
				+ " NOT IN (SELECT " + FitmaestroDb.KEY_SETID + " FROM " + FitmaestroDb.DATABASE_PROGRAMS_CONNECTOR_TABLE + ")", null,
				null, null, null);
	}

	public Cursor fetchSet(long rowId) throws SQLException {

		Cursor mCursor =

		db.getDb().query(true, FitmaestroDb.DATABASE_SETS_TABLE, new String[] { FitmaestroDb.KEY_ROWID,
				FitmaestroDb.KEY_TITLE, FitmaestroDb.KEY_DESC, FitmaestroDb.KEY_SITEID }, FitmaestroDb.KEY_ROWID + "=" + rowId,
				null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;

	}

	public Cursor fetchUpdatedSets(String updated) {

		return db.getDb().query(FitmaestroDb.DATABASE_SETS_TABLE, new String[] { FitmaestroDb.KEY_ROWID,
				FitmaestroDb.KEY_TITLE, FitmaestroDb.KEY_DESC, FitmaestroDb.KEY_SITEID, FitmaestroDb.KEY_UPDATED }, FitmaestroDb.KEY_UPDATED
				+ " >  ?", new String[] { updated }, null, null, null, null);
	}

	public Cursor fetchExercisesForSet(long setId) throws SQLException {
		Cursor mCursor = db.getDb().rawQuery("SELECT " + FitmaestroDb.DATABASE_SETS_CONNECTOR_TABLE
				+ "." + FitmaestroDb.KEY_ROWID + ", " + FitmaestroDb.DATABASE_EXERCISES_TABLE + "."
				+ FitmaestroDb.KEY_ROWID + " AS " + FitmaestroDb.KEY_EXERCISEID + ", "
				+ FitmaestroDb.DATABASE_EXERCISES_TABLE + "." + FitmaestroDb.KEY_TITLE + ", "
				+ FitmaestroDb.DATABASE_EXERCISES_TABLE + "." + FitmaestroDb.KEY_TYPE + ", "
				+ FitmaestroDb.DATABASE_EXERCISES_TABLE + "." + FitmaestroDb.KEY_MAX_WEIGHT + ", "
				+ FitmaestroDb.DATABASE_EXERCISES_TABLE + "." + FitmaestroDb.KEY_MAX_REPS + ", "
				+ FitmaestroDb.DATABASE_EXERCISES_TABLE + "." + FitmaestroDb.KEY_DESC + " FROM "
				+ FitmaestroDb.DATABASE_SETS_CONNECTOR_TABLE + ", "
				+ FitmaestroDb.DATABASE_EXERCISES_TABLE + " WHERE "
				+ FitmaestroDb.DATABASE_EXERCISES_TABLE + "." + FitmaestroDb.KEY_ROWID + " = "
				+ FitmaestroDb.DATABASE_SETS_CONNECTOR_TABLE + "." + FitmaestroDb.KEY_EXERCISEID
				+ " AND " + FitmaestroDb.DATABASE_SETS_CONNECTOR_TABLE + "." + FitmaestroDb.KEY_SETID
				+ " = ?" + " AND " + FitmaestroDb.DATABASE_SETS_CONNECTOR_TABLE + "."
				+ FitmaestroDb.KEY_DELETED + " = 0", new String[] { String.valueOf(setId) });

		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;

	}

	public Cursor fetchExerciseForSetsConnector(long setsConnectorId)
			throws SQLException {
		Cursor mCursor = db.getDb().rawQuery("SELECT " + FitmaestroDb.DATABASE_EXERCISES_TABLE
				+ "." + FitmaestroDb.KEY_ROWID + ", " + FitmaestroDb.DATABASE_EXERCISES_TABLE + "."
				+ FitmaestroDb.KEY_TITLE + ", " + FitmaestroDb.DATABASE_EXERCISES_TABLE + "." + FitmaestroDb.KEY_TYPE
				+ ", " + FitmaestroDb.DATABASE_EXERCISES_TABLE + "." + FitmaestroDb.KEY_MAX_WEIGHT + ", "
				+ FitmaestroDb.DATABASE_EXERCISES_TABLE + "." + FitmaestroDb.KEY_MAX_REPS + ", "
				+ FitmaestroDb.DATABASE_EXERCISES_TABLE + "." + FitmaestroDb.KEY_DESC + " FROM "
				+ FitmaestroDb.DATABASE_SETS_CONNECTOR_TABLE + ", "
				+ FitmaestroDb.DATABASE_EXERCISES_TABLE + " WHERE "
				+ FitmaestroDb.DATABASE_EXERCISES_TABLE + "." + FitmaestroDb.KEY_ROWID + " = "
				+ FitmaestroDb.DATABASE_SETS_CONNECTOR_TABLE + "." + FitmaestroDb.KEY_EXERCISEID
				+ " AND " + FitmaestroDb.DATABASE_SETS_CONNECTOR_TABLE + "." + FitmaestroDb.KEY_ROWID
				+ " = ?" + " AND " + FitmaestroDb.DATABASE_SETS_CONNECTOR_TABLE + "."
				+ FitmaestroDb.KEY_DELETED + " = 0", new String[] { String
				.valueOf(setsConnectorId) });

		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}

	public boolean deleteExerciseFromSet(long rowId) {

		ContentValues args = new ContentValues();
		args.put(FitmaestroDb.KEY_DELETED, 1);

		return db.getDb().update(FitmaestroDb.DATABASE_SETS_CONNECTOR_TABLE, args, FitmaestroDb.KEY_ROWID + "="
				+ rowId, null) > 0;
	}

	public boolean updateSet(long rowId, String title, String desc, long site_id) {
		ContentValues args = new ContentValues();
		args.put(FitmaestroDb.KEY_TITLE, title);
		args.put(FitmaestroDb.KEY_DESC, desc);
		args.put(FitmaestroDb.KEY_SITEID, site_id);

		return db.getDb().update(FitmaestroDb.DATABASE_SETS_TABLE, args, FitmaestroDb.KEY_ROWID + "=" + rowId,
				null) > 0;
	}

	// end of SETS
	
	// SETS_DETAIL methods
	public Cursor fetchRepsForConnector(Long setsConnectorId)
			throws SQLException {

		Cursor mCursor = db.getDb().query(true, FitmaestroDb.DATABASE_SETS_DETAIL_TABLE, null,
				FitmaestroDb.KEY_SETS_CONNECTORID + "=" + setsConnectorId + " AND "
						+ FitmaestroDb.KEY_DELETED + "=0", null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}

	public Cursor fetchRepsEntry(long rowId) throws SQLException {

		Cursor mCursor =

		db.getDb().query(true, FitmaestroDb.DATABASE_SETS_DETAIL_TABLE, null, FitmaestroDb.KEY_ROWID + "="
				+ rowId, null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;

	}

	public long createRepsEntry(long sets_connector_id, int reps,
			float percentage) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(FitmaestroDb.KEY_SETS_CONNECTORID, sets_connector_id);
		initialValues.put(FitmaestroDb.KEY_REPS, reps);
		initialValues.put(FitmaestroDb.KEY_PERCENTAGE, percentage);
		initialValues.put(FitmaestroDb.KEY_SITEID, 0);

		return db.getDb().insert(FitmaestroDb.DATABASE_SETS_DETAIL_TABLE, null, initialValues);
	}

	public boolean updateRepsEntry(long rowId, int reps, float percentage) {
		ContentValues args = new ContentValues();
		args.put(FitmaestroDb.KEY_REPS, reps);
		args.put(FitmaestroDb.KEY_PERCENTAGE, percentage);

		return db.getDb().update(FitmaestroDb.DATABASE_SETS_DETAIL_TABLE, args, FitmaestroDb.KEY_ROWID + "="
				+ rowId, null) > 0;
	}

	public boolean deleteRepsEntry(long rowId) {

		ContentValues args = new ContentValues();
		args.put(FitmaestroDb.KEY_DELETED, 1);

		return db.getDb().update(FitmaestroDb.DATABASE_SETS_DETAIL_TABLE, args, FitmaestroDb.KEY_ROWID + "="
				+ rowId, null) > 0;
	}

	// END OF SETS_DETAIL methods	
	
}
