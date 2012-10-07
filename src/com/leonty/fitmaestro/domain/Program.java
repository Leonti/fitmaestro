package com.leonty.fitmaestro.domain;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;

public class Program {

	FitmaestroDb db;
	
	public Program(FitmaestroDb db) {
		this.db = db;
	}	
	
	// PROGRAMS methods

	public Cursor fetchAllPrograms() {

		return db.getDb().query(FitmaestroDb.DATABASE_PROGRAMS_TABLE, null, FitmaestroDb.KEY_DELETED + "=0",
				null, null, null, null);
	}

	public Cursor fetchProgram(long rowId) throws SQLException {

		Cursor mCursor = db.getDb().query(true, FitmaestroDb.DATABASE_PROGRAMS_TABLE, null,
				FitmaestroDb.KEY_ROWID + "=" + rowId, null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;

	}

	public long createProgram(String title, String desc) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(FitmaestroDb.KEY_TITLE, title);
		initialValues.put(FitmaestroDb.KEY_DESC, desc);

		return db.getDb().insert(FitmaestroDb.DATABASE_PROGRAMS_TABLE, null, initialValues);
	}

	public boolean updateProgram(long rowId, String title, String desc) {
		ContentValues args = new ContentValues();
		args.put(FitmaestroDb.KEY_TITLE, title);
		args.put(FitmaestroDb.KEY_DESC, desc);

		return db.getDb().update(FitmaestroDb.DATABASE_PROGRAMS_TABLE, args, FitmaestroDb.KEY_ROWID + "="
				+ rowId, null) > 0;
	}

	public boolean deleteProgram(long rowId) {

		ContentValues args = new ContentValues();
		args.put(FitmaestroDb.KEY_DELETED, 1);

		return db.getDb().update(FitmaestroDb.DATABASE_PROGRAMS_TABLE, args, FitmaestroDb.KEY_ROWID + "="
				+ rowId, null) > 0;
	}
	
	public Long getProgramMaxDay(long programId) throws SQLException {
		Cursor mCursor = db.getDb().rawQuery("SELECT MAX(" + FitmaestroDb.KEY_DAY_NUMBER + ") AS max_day " +

				"FROM " + FitmaestroDb.DATABASE_PROGRAMS_CONNECTOR_TABLE + 
				" WHERE " + FitmaestroDb.KEY_DELETED + " = 0 AND " + FitmaestroDb.KEY_PROGRAMID + " = ? "
				, new String[] { String.valueOf(programId) });

		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		
		return mCursor.getLong(mCursor.getColumnIndex("max_day"));
	}

	public Cursor fetchProgramSets(long programId) {
		return db.getDb().query(FitmaestroDb.DATABASE_PROGRAMS_CONNECTOR_TABLE, null, FitmaestroDb.KEY_DELETED
				+ "=0 AND " + FitmaestroDb.KEY_PROGRAMID + "=" + programId, null, null,
				null, FitmaestroDb.KEY_DAY_NUMBER + " ASC");
	}

	public long addSetToProgram(long program_id, long set_id, long day_number) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(FitmaestroDb.KEY_PROGRAMID, program_id);
		initialValues.put(FitmaestroDb.KEY_SETID, set_id);
		initialValues.put(FitmaestroDb.KEY_DAY_NUMBER, day_number);

		return db.getDb().insert(FitmaestroDb.DATABASE_PROGRAMS_CONNECTOR_TABLE, null,
				initialValues);
	}
	
	public boolean removeSetFromProgram(long rowId) {
		
		ContentValues args = new ContentValues();
		args.put(FitmaestroDb.KEY_DELETED, 1);

		return db.getDb().update(FitmaestroDb.DATABASE_PROGRAMS_CONNECTOR_TABLE, args, FitmaestroDb.KEY_ROWID + "="
				+ rowId, null) > 0;
	}

	// END of PROGRAMS methods
}
