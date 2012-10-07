package com.leonty.fitmaestro.domain;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.util.Log;

public class Session {

	FitmaestroDb db;
	
	public Session(FitmaestroDb db) {
		this.db = db;
	}	
	
	// SESSIONS methods
	public Cursor AllSessions() {

		return db.getDb().query(FitmaestroDb.DATABASE_SESSIONS_TABLE, null, FitmaestroDb.KEY_DELETED + "=0",
				null, null, null, null);
	}

	public Cursor fetchFilteredSessions(String filter) {

		return db.getDb().query(FitmaestroDb.DATABASE_SESSIONS_TABLE, null, FitmaestroDb.KEY_DELETED + "=0 AND "
				+ FitmaestroDb.KEY_STATUS + "= ?", new String[] { filter }, null, null,
				FitmaestroDb.KEY_ROWID + " DESC", null);
	}

	public Cursor fetchSession(long rowId) throws SQLException {

		Cursor mCursor = db.getDb().query(true, FitmaestroDb.DATABASE_SESSIONS_TABLE, null, FitmaestroDb.KEY_ROWID + "=" + rowId,
				null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		
		return mCursor;
	}

	public long createSession(String title, String desc,
			long programs_connector_id) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(FitmaestroDb.KEY_TITLE, title);
		initialValues.put(FitmaestroDb.KEY_DESC, desc);
		initialValues.put(FitmaestroDb.KEY_PROGRAMS_CONNECTORID, programs_connector_id);
		initialValues.put(FitmaestroDb.KEY_STATUS, "INPROGRESS");

		return db.getDb().insert(FitmaestroDb.DATABASE_SESSIONS_TABLE, null, initialValues);
	}

	public boolean updateSession(long rowId, String title, String desc,
			String status) {
		ContentValues args = new ContentValues();
		args.put(FitmaestroDb.KEY_TITLE, title);
		args.put(FitmaestroDb.KEY_DESC, desc);
		args.put(FitmaestroDb.KEY_STATUS, status);

		return db.getDb().update(FitmaestroDb.DATABASE_SESSIONS_TABLE, args, FitmaestroDb.KEY_ROWID + "="
				+ rowId, null) > 0;
	}

	public boolean updateSessionStatus(long rowId, String status) {
		ContentValues args = new ContentValues();
		args.put(FitmaestroDb.KEY_STATUS, status);

		return db.getDb().update(FitmaestroDb.DATABASE_SESSIONS_TABLE, args, FitmaestroDb.KEY_ROWID + "="
				+ rowId, null) > 0;
	}

	public boolean deleteSession(long rowId) {

		ContentValues args = new ContentValues();
		args.put(FitmaestroDb.KEY_DELETED, 1);

		return db.getDb().update(FitmaestroDb.DATABASE_SESSIONS_TABLE, args, FitmaestroDb.KEY_ROWID + "="
				+ rowId, null) > 0;
	}

	public Cursor fetchExercisesForSession(long sessionId) throws SQLException {
		Cursor mCursor = db.getDb().rawQuery("SELECT "
				+ FitmaestroDb.DATABASE_SESSIONS_CONNECTOR_TABLE + "." + FitmaestroDb.KEY_ROWID + ", "
				+ FitmaestroDb.DATABASE_EXERCISES_TABLE + "." + FitmaestroDb.KEY_ROWID + " AS "
				+ FitmaestroDb.KEY_EXERCISEID + ", " + FitmaestroDb.DATABASE_EXERCISES_TABLE + "."
				+ FitmaestroDb.KEY_TITLE + ", " + FitmaestroDb.DATABASE_EXERCISES_TABLE + "." + FitmaestroDb.KEY_DESC
				+ ", " + FitmaestroDb.DATABASE_EXERCISES_TABLE + "." + FitmaestroDb.KEY_TYPE + ", "
				+ FitmaestroDb.DATABASE_EXERCISES_TABLE + "." + FitmaestroDb.KEY_MAX_REPS + ", "
				+ FitmaestroDb.DATABASE_EXERCISES_TABLE + "." + FitmaestroDb.KEY_MAX_WEIGHT + " " +

				"FROM " + FitmaestroDb.DATABASE_SESSIONS_CONNECTOR_TABLE + ", "
				+ FitmaestroDb.DATABASE_EXERCISES_TABLE + " WHERE "
				+ FitmaestroDb.DATABASE_EXERCISES_TABLE + "." + FitmaestroDb.KEY_ROWID + " = "
				+ FitmaestroDb.DATABASE_SESSIONS_CONNECTOR_TABLE + "." + FitmaestroDb.KEY_EXERCISEID
				+ " AND " + FitmaestroDb.DATABASE_SESSIONS_CONNECTOR_TABLE + "."
				+ FitmaestroDb.KEY_SESSIONID + " = ?" + " AND "
				+ FitmaestroDb.DATABASE_SESSIONS_CONNECTOR_TABLE + "." + FitmaestroDb.KEY_DELETED
				+ " = 0", new String[] { String.valueOf(sessionId) });

		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;

	}

	public long addExerciseToSession(long sessionId, long exerciseId) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(FitmaestroDb.KEY_SESSIONID, sessionId);
		initialValues.put(FitmaestroDb.KEY_EXERCISEID, exerciseId);

		// check if exercise is already there - if it is - do not add
		Cursor mCursor =
			db.getDb().query(true, FitmaestroDb.DATABASE_SESSIONS_CONNECTOR_TABLE, null, 
					FitmaestroDb.KEY_EXERCISEID + "=? AND " + FitmaestroDb.KEY_SESSIONID + "=? AND " + FitmaestroDb.KEY_DELETED + "=0",
					new String[]{String.valueOf(exerciseId), String.valueOf(sessionId)}, null, null, null, null);

		return db.getDb().insert(FitmaestroDb.DATABASE_SESSIONS_CONNECTOR_TABLE, null,
				initialValues);	
	}

	public boolean deleteExerciseFromSession(long rowId) {

		ContentValues args = new ContentValues();
		args.put(FitmaestroDb.KEY_DELETED, 1);

		return db.getDb().update(FitmaestroDb.DATABASE_SESSIONS_CONNECTOR_TABLE, args, FitmaestroDb.KEY_ROWID
				+ "=" + rowId, null) > 0;
	}

	public Cursor fetchFreeSessionReps(long session_id, long exercise_id)
			throws SQLException {

		Cursor mCursor = db.getDb().query(true, FitmaestroDb.DATABASE_LOG_TABLE, null,
				FitmaestroDb.KEY_SESSIONID + "=" + session_id + " AND " + FitmaestroDb.KEY_EXERCISEID
						+ "=" + exercise_id + " AND " + FitmaestroDb.KEY_SESSIONS_DETAILID
						+ "=0" + " AND " + FitmaestroDb.KEY_DELETED + "=0", null, null,
				null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}

	public Cursor fetchSessionRepsEntry(long rowId) throws SQLException {

		Cursor mCursor =

		db.getDb().query(true, FitmaestroDb.DATABASE_LOG_TABLE, null, FitmaestroDb.KEY_ROWID + "=" + rowId,
				null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;

	}

	public Cursor fetchDoneSessionReps(long session_id, long sessions_detail_id)
			throws SQLException {

		Cursor mCursor =

		db.getDb().query(true, FitmaestroDb.DATABASE_LOG_TABLE, null, FitmaestroDb.KEY_SESSIONID + "="
				+ session_id + " AND " + FitmaestroDb.KEY_SESSIONS_DETAILID + "="
				+ sessions_detail_id + " AND " + FitmaestroDb.KEY_DELETED + "=0", null,
				null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;

	}

	public long createSessionRepsEntry(long session_id, long exercise_id,
			long session_detail_id, int reps, float weight) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(FitmaestroDb.KEY_SESSIONID, session_id);
		initialValues.put(FitmaestroDb.KEY_EXERCISEID, exercise_id);
		initialValues.put(FitmaestroDb.KEY_SESSIONS_DETAILID, session_detail_id);
		initialValues.put(FitmaestroDb.KEY_REPS, reps);
		initialValues.put(FitmaestroDb.KEY_WEIGHT, weight);

		return db.getDb().insert(FitmaestroDb.DATABASE_LOG_TABLE, null, initialValues);
	}

	public boolean updateSessionRepsEntry(long rowId, int reps, float weight) {
		ContentValues args = new ContentValues();
		args.put(FitmaestroDb.KEY_REPS, reps);
		args.put(FitmaestroDb.KEY_WEIGHT, weight);

		return db.getDb().update(FitmaestroDb.DATABASE_LOG_TABLE, args, FitmaestroDb.KEY_ROWID + "=" + rowId,
				null) > 0;
	}

	public boolean deleteSessionRepsEntry(long rowId) {

		ContentValues args = new ContentValues();
		args.put(FitmaestroDb.KEY_DELETED, 1);

		return db.getDb().update(FitmaestroDb.DATABASE_LOG_TABLE, args, FitmaestroDb.KEY_ROWID + "=" + rowId,
				null) > 0;
	}

	// END of SESSIONS methods	

	// SESSION_CONNECTOR methods
	public Cursor fetchSessionConnector(long rowId) throws SQLException {

		Cursor mCursor =

		db.getDb().query(true, FitmaestroDb.DATABASE_SESSIONS_CONNECTOR_TABLE, null, FitmaestroDb.KEY_ROWID
				+ "=" + rowId, null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;

	}

	// END of session_connector methods	

	// SESSION_DETAIL methods
	// they are read-only for now (maybe forever :))

	public Cursor fetchRepsForSessionConnector(Long sessionsConnectorId)
			throws SQLException {

		Cursor mCursor = db.getDb().query(true, FitmaestroDb.DATABASE_SESSIONS_DETAIL_TABLE, null,
				FitmaestroDb.KEY_SESSIONS_CONNECTORID + "=" + sessionsConnectorId + " AND "
						+ FitmaestroDb.KEY_DELETED + "=0", null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}

	public long createSessionRepsEntry(long sessions_connector_id, long reps,
			double weight) {
		ContentValues initialValues = new ContentValues();
		
		// percentage in this context means calculated weight
		initialValues.put(FitmaestroDb.KEY_SESSIONS_CONNECTORID, sessions_connector_id);
		initialValues.put(FitmaestroDb.KEY_REPS, reps);
		initialValues.put(FitmaestroDb.KEY_PERCENTAGE, weight);

		return db.getDb().insert(FitmaestroDb.DATABASE_SESSIONS_DETAIL_TABLE, null, initialValues);
	}

	// END OF SESSION DETAIL methods	
	
}
