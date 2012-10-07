package com.leonty.fitmaestro.domain;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;

public class LogEntry {
	
	FitmaestroDb db;
	
	public LogEntry(FitmaestroDb db) {
		this.db = db;
	}
	
	// LOG methods
	public long createLogEntry(long exercise_id, float weight, int times,
			int program_id, int day) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(FitmaestroDb.KEY_EXERCISEID, exercise_id);
		initialValues.put(FitmaestroDb.KEY_WEIGHT, weight);
		initialValues.put(FitmaestroDb.KEY_TIMES, times);
		initialValues.put(FitmaestroDb.KEY_PROGRAMID, program_id);
		initialValues.put(FitmaestroDb.KEY_DAY, day);
		initialValues.put(FitmaestroDb.KEY_SITEID, 0);

		return db.getDb().insert(FitmaestroDb.DATABASE_LOG_TABLE, null, initialValues);
	}

	public boolean deleteLogEntry(long rowId) {

		ContentValues args = new ContentValues();
		args.put(FitmaestroDb.KEY_DELETED, 1);

		return db.getDb().update(FitmaestroDb.DATABASE_LOG_TABLE, args, FitmaestroDb.KEY_ROWID + "=" + rowId,
				null) > 0;
	}

	public Cursor fetchLogEntry(long rowId) throws SQLException {

		Cursor mCursor =

		db.getDb().query(true, FitmaestroDb.DATABASE_LOG_TABLE, new String[] { FitmaestroDb.KEY_ROWID,
				FitmaestroDb.KEY_EXERCISEID, FitmaestroDb.KEY_WEIGHT, FitmaestroDb.KEY_TIMES, FitmaestroDb.KEY_PROGRAMID, FitmaestroDb.KEY_DAY,
				FitmaestroDb.KEY_DONE }, FitmaestroDb.KEY_ROWID + "=" + rowId, null, null, null, null,
				null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;

	}

	public Cursor fetchLogEntriesForExercise(long exerciseId, String donestart,
			String doneend) throws SQLException {

		Cursor mCursor = db.getDb().query(true, FitmaestroDb.DATABASE_LOG_TABLE, new String[] {
				FitmaestroDb.KEY_ROWID, FitmaestroDb.KEY_EXERCISEID, FitmaestroDb.KEY_WEIGHT, FitmaestroDb.KEY_TIMES,
				FitmaestroDb.KEY_PROGRAMID, FitmaestroDb.KEY_DAY, FitmaestroDb.KEY_DONE }, FitmaestroDb.KEY_EXERCISEID + "="
				+ exerciseId + " AND '" + donestart + "' < " + FitmaestroDb.KEY_DONE
				+ " AND " + FitmaestroDb.KEY_DONE + " < '" + doneend + "' AND "
				+ FitmaestroDb.KEY_DELETED + "=0", null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;

	}

	public Cursor fetchExercisesForDates(String donestart, String doneend)
			throws SQLException {
		Cursor mCursor = db.getDb().rawQuery("SELECT " + FitmaestroDb.DATABASE_LOG_TABLE + "."
				+ FitmaestroDb.KEY_ROWID + " AS _id, " + FitmaestroDb.DATABASE_LOG_TABLE + "."
				+ FitmaestroDb.KEY_EXERCISEID + " AS " + FitmaestroDb.KEY_EXERCISEID + ", "
				+ FitmaestroDb.DATABASE_EXERCISES_TABLE + "." + FitmaestroDb.KEY_TITLE + " AS "
				+ FitmaestroDb.KEY_TITLE + " FROM " + FitmaestroDb.DATABASE_LOG_TABLE + ", "
				+ FitmaestroDb.DATABASE_EXERCISES_TABLE + " WHERE "
				+ FitmaestroDb.DATABASE_EXERCISES_TABLE + "." + FitmaestroDb.KEY_ROWID + " = "
				+ FitmaestroDb.DATABASE_LOG_TABLE + "." + FitmaestroDb.KEY_EXERCISEID + " AND '"
				+ donestart + "' < " + FitmaestroDb.KEY_DONE + " AND " + FitmaestroDb.KEY_DONE + " < '"
				+ doneend + "' AND " + FitmaestroDb.DATABASE_LOG_TABLE + "." + FitmaestroDb.KEY_DELETED
				+ "=0 GROUP BY " + FitmaestroDb.KEY_EXERCISEID, null);

		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;

	}
	
	public Cursor fetchStatsForExercise(long exerciseId, String donestart,
			String doneend, int exType) throws SQLException {

		//"SELECT SUM(`reps` * `weight`), MAX(`weight`), `session_id`, `done`, `title` FROM `log` LEFT JOIN `sessions` ON `session_id` = `sessions`.`id` WHERE `exercise_id` = 147 AND `done` BETWEEN '2010-04-04' AND '2010-06-09' AND `log`.deleted='0' GROUP BY `session_id` ORDER BY `session_id` DESC"

		String sumPart = "reps * weight";
		String maxPart = "weight";
		
		// for own weight - we can get stats only for repetitions
		if(exType == 0){
			sumPart = "reps";
			maxPart = "reps";		
		}
		
		Cursor mCursor = db.getDb().rawQuery("SELECT " + FitmaestroDb.DATABASE_LOG_TABLE + "."
				+ FitmaestroDb.KEY_ROWID + " AS _id, SUM(" + sumPart + ") AS sum, MAX(" + maxPart + ") AS max, " 
				+ FitmaestroDb.DATABASE_LOG_TABLE + "." + FitmaestroDb.KEY_SESSIONID + " AS " + FitmaestroDb.KEY_SESSIONID + ", "
				+ FitmaestroDb.DATABASE_SESSIONS_CONNECTOR_TABLE + "." + FitmaestroDb.KEY_ROWID + " AS " + FitmaestroDb.KEY_SESSIONS_CONNECTORID + ", "
				+ FitmaestroDb.KEY_DONE + ", " + FitmaestroDb.KEY_TITLE
				+ " FROM " + FitmaestroDb.DATABASE_LOG_TABLE 
				+ " LEFT JOIN " + FitmaestroDb.DATABASE_SESSIONS_TABLE
				+ " ON " + FitmaestroDb.DATABASE_LOG_TABLE + "." + FitmaestroDb.KEY_SESSIONID + "=" + FitmaestroDb.DATABASE_SESSIONS_TABLE + "." + FitmaestroDb.KEY_ROWID
				+ " LEFT JOIN " + FitmaestroDb.DATABASE_SESSIONS_CONNECTOR_TABLE 
				+ " ON " + FitmaestroDb.DATABASE_SESSIONS_CONNECTOR_TABLE + "." + FitmaestroDb.KEY_EXERCISEID + " = " 
				+ FitmaestroDb.DATABASE_LOG_TABLE + "." + FitmaestroDb.KEY_EXERCISEID + " AND " 
				+ FitmaestroDb.DATABASE_SESSIONS_CONNECTOR_TABLE + "." + FitmaestroDb.KEY_SESSIONID + " = " 
				+ FitmaestroDb.DATABASE_LOG_TABLE + "." + FitmaestroDb.KEY_SESSIONID + " AND " 
				+ FitmaestroDb.DATABASE_SESSIONS_CONNECTOR_TABLE + "." + FitmaestroDb.KEY_DELETED + " =0"
				+ " WHERE " + FitmaestroDb.DATABASE_LOG_TABLE + "." + FitmaestroDb.KEY_EXERCISEID + "=" + String.valueOf(exerciseId) + " AND '"
				+ donestart + "' < " + FitmaestroDb.KEY_DONE + " AND " + FitmaestroDb.KEY_DONE + " < '"
				+ doneend + "' AND " + FitmaestroDb.DATABASE_LOG_TABLE + "." + FitmaestroDb.KEY_DELETED
				+ "=0 AND " + FitmaestroDb.DATABASE_SESSIONS_CONNECTOR_TABLE + "." + FitmaestroDb.KEY_DELETED 
				+ "=0 GROUP BY " + FitmaestroDb.KEY_SESSIONID + " ORDER BY " + FitmaestroDb.KEY_SESSIONID + " DESC", null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;

	}
	
	public Cursor getTotalsForExercise(long exerciseId, long sessionId, int exType){
		
		String sumPart = "reps * weight";
		String maxPart = "weight";
		
		// for own weight - we can get stats only for repetitions
		if(exType == 0){
			sumPart = "reps";
			maxPart = "reps";		
		}
		
		Cursor mCursor = db.getDb().rawQuery("SELECT SUM(" + sumPart + ") AS sum, MAX(" + maxPart + ") AS max FROM " + FitmaestroDb.DATABASE_LOG_TABLE + " WHERE " + FitmaestroDb.KEY_EXERCISEID + " = " 
				+ String.valueOf(exerciseId) + " AND " + FitmaestroDb.KEY_SESSIONID + "=" 
				+ String.valueOf(sessionId) + " AND " 
				+ FitmaestroDb.KEY_DELETED
				+ "=0", null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}

	public boolean updateLogEntry(long rowId, float weight, int times) {
		ContentValues args = new ContentValues();
		args.put(FitmaestroDb.KEY_WEIGHT, weight);
		args.put(FitmaestroDb.KEY_TIMES, times);

		return db.getDb().update(FitmaestroDb.DATABASE_LOG_TABLE, args, FitmaestroDb.KEY_ROWID + "=" + rowId,
				null) > 0;
	}

	// end of LOG
}
