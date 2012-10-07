package com.leonty.fitmaestro.domain;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;

public class Measurement {

	FitmaestroDb db;
	
	public Measurement(FitmaestroDb db) {
		this.db = db;
	}		
	
	// MEASUREMENTS methods
	public long createMeasurementType(String title, String units, String desc) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(FitmaestroDb.KEY_TITLE, title);
		initialValues.put(FitmaestroDb.KEY_UNITS, units);
		initialValues.put(FitmaestroDb.KEY_DESC, desc);

		return db.getDb()
				.insert(FitmaestroDb.DATABASE_MEASUREMENT_TYPES_TABLE, null, initialValues);
	}

	public boolean updateMeasurementType(long rowId, String title,
			String units, String desc) {
		ContentValues args = new ContentValues();
		args.put(FitmaestroDb.KEY_TITLE, title);
		args.put(FitmaestroDb.KEY_UNITS, units);
		args.put(FitmaestroDb.KEY_DESC, desc);

		return db.getDb().update(FitmaestroDb.DATABASE_MEASUREMENT_TYPES_TABLE, args, FitmaestroDb.KEY_ROWID
				+ "=" + rowId, null) > 0;
	}

	public boolean deleteMeasurementType(long rowId) {

		// delete exercises for this group first
		deleteLogsForMeasurement(rowId);

		ContentValues args = new ContentValues();
		args.put(FitmaestroDb.KEY_DELETED, 1);

		return db.getDb().update(FitmaestroDb.DATABASE_MEASUREMENT_TYPES_TABLE, args, FitmaestroDb.KEY_ROWID
				+ "=" + rowId, null) > 0;
	}

	public Cursor fetchMeasurementType(long rowId) throws SQLException {

		Cursor mCursor = db.getDb().query(true, FitmaestroDb.DATABASE_MEASUREMENT_TYPES_TABLE,
				null, FitmaestroDb.KEY_ROWID + "=" + rowId, null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;

	}

	public Cursor fetchAllMeasurementTypes() {

		return db.getDb().query(FitmaestroDb.DATABASE_MEASUREMENT_TYPES_TABLE, null, FitmaestroDb.KEY_DELETED
				+ "=0", null, null, null, null);
	}

	public boolean deleteLogsForMeasurement(long typeId) {

		ContentValues args = new ContentValues();
		args.put(FitmaestroDb.KEY_DELETED, 1);

		return db.getDb().update(FitmaestroDb.DATABASE_MEASUREMENTS_LOG_TABLE, args,
				FitmaestroDb.KEY_MEASUREMENT_TYPEID + "=" + typeId, null) > 0;
	}

	public Cursor fetchMeasLogEntries(long typeId) throws SQLException {

		return db.getDb().query(FitmaestroDb.DATABASE_MEASUREMENTS_LOG_TABLE, null,
				FitmaestroDb.KEY_MEASUREMENT_TYPEID + "=" + typeId + " AND " + FitmaestroDb.KEY_DELETED
						+ "=0", null, null, null, FitmaestroDb.KEY_ROWID + " DESC", null);

	}
	
	public Cursor fetchMeasLogEntriesForDates(long typeId, String begin, String end) throws SQLException {

		return db.getDb().query(true, FitmaestroDb.DATABASE_MEASUREMENTS_LOG_TABLE, null, 
				FitmaestroDb.KEY_MEASUREMENT_TYPEID + "=" + typeId 
				+ " AND '" + begin + "' < " + FitmaestroDb.KEY_DATE
				+ " AND " + FitmaestroDb.KEY_DATE + " < '" + end + "' AND "
				+ FitmaestroDb.KEY_DELETED + "=0", null, null, null, FitmaestroDb.KEY_ROWID + " DESC", null);

	}
	
	public Cursor fetchMaxMeasForDates(long typeId, String begin, String end) throws SQLException {
	
		Cursor cursor = db.getDb().rawQuery( "SELECT MAX(" + FitmaestroDb.KEY_VALUE + ") AS max FROM "
				+ FitmaestroDb.DATABASE_MEASUREMENTS_LOG_TABLE + " WHERE " 
				+ FitmaestroDb.KEY_MEASUREMENT_TYPEID + "=" + typeId 
				+ " AND '" + begin + "' < " + FitmaestroDb.KEY_DATE
				+ " AND " + FitmaestroDb.KEY_DATE + " < '" + end + "' AND "
				+ FitmaestroDb.KEY_DELETED + "=0", null);
		if (cursor != null) {
			cursor.moveToFirst();
		}
		return cursor;

	}

	public long createMeasLogEntry(long type_id, float value) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(FitmaestroDb.KEY_MEASUREMENT_TYPEID, type_id);
		initialValues.put(FitmaestroDb.KEY_VALUE, value);

		return db.getDb().insert(FitmaestroDb.DATABASE_MEASUREMENTS_LOG_TABLE, null, initialValues);
	}

	public boolean updateMeasLogEntry(long rowId, float value) {
		ContentValues args = new ContentValues();
		args.put(FitmaestroDb.KEY_VALUE, value);

		return db.getDb().update(FitmaestroDb.DATABASE_MEASUREMENTS_LOG_TABLE, args, FitmaestroDb.KEY_ROWID
				+ "=" + rowId, null) > 0;
	}

	public Cursor fetchMeasLogEntry(long rowId) throws SQLException {

		Cursor mCursor = db.getDb().query(true, FitmaestroDb.DATABASE_MEASUREMENTS_LOG_TABLE, null,
				FitmaestroDb.KEY_ROWID + "=" + rowId, null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;

	}

	public boolean deleteMeasLogEntry(long rowId) {

		ContentValues args = new ContentValues();
		args.put(FitmaestroDb.KEY_DELETED, 1);

		return db.getDb().update(FitmaestroDb.DATABASE_MEASUREMENTS_LOG_TABLE, args, FitmaestroDb.KEY_ROWID
				+ "=" + rowId, null) > 0;
	}
	// END of MEASUREMENTS methods	
}
