package com.leonti.fitmaestro;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ExcercisesDbAdapter {

	public static final String KEY_ROWID = "_id";
	public static final String KEY_TITLE = "title";
	public static final String KEY_DESC = "desc";
	public static final String KEY_SITEID = "site_id";
	public static final String KEY_TYPE = "ex_type"; // 0 - own weight, 1 - with
														// weight
	public static final String KEY_GROUPID = "group_id"; // for exercises
	public static final String KEY_EXERCISEID = "exercise_id"; // for log
	public static final String KEY_SETID = "set_id"; // for set connector
	public static final String KEY_SESSIONID = "session_id"; // for session
																// connector
	public static final String KEY_WEIGHT = "weight";
	public static final String KEY_TIMES = "reps";
	public static final String KEY_REPS = "reps";
	public static final String KEY_MAX_WEIGHT = "max_weight";
	public static final String KEY_MAX_REPS = "max_reps";
	public static final String KEY_PERCENTAGE = "percentage";
	public static final String KEY_PROGRAMID = "program_id";
	public static final String KEY_DAY_NUMBER = "day_number";
	public static final String KEY_SETS_CONNECTORID = "sets_connector_id";
	public static final String KEY_SESSIONS_CONNECTORID = "sessions_connector_id";
	public static final String KEY_PROGRAMS_CONNECTORID = "programs_connector_id";
	public static final String KEY_SETS_DETAILID = "sets_detail_id";
	public static final String KEY_SESSIONS_DETAILID = "sessions_detail_id";
	public static final String KEY_STATUS = "status";
	public static final String KEY_DAY = "day";
	public static final String KEY_DONE = "done";
	public static final String KEY_MEASUREMENT_TYPEID = "measurement_type_id";
	public static final String KEY_VALUE = "value";
	public static final String KEY_UNITS = "units";
	public static final String KEY_DATE = "date";
	public static final String KEY_UPDATED = "updated";
	public static final String KEY_DELETED = "deleted";
	public static final String KEY_LASTUPDATED = "last_updated";
	public static final String KEY_AUTHKEY = "authkey";

	private static final String TAG = "ExcercisesDbAdapter";
	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;

	/**
	 * Database creation sql statement
	 */
	private static final String GROUPS_CREATE = "create table groups "
			+ "(_id integer primary key autoincrement, "
			+ "title text not null, " + "desc text not null, "
			+ "site_id integer default 0, "
			+ "updated timestamp default current_timestamp, "
			+ "deleted integer default 0); ";

	private static final String EXERCISES_CREATE = "create table exercises "
			+ "(_id integer primary key autoincrement, "
			+ "title text not null, " + "desc text not null, "
			+ "ex_type integer, " + "max_weight decimal(10,2) default 0, "
			+ "max_reps integer default 0, " + "group_id integer, "
			+ "site_id integer default 0, "
			+ "updated timestamp default current_timestamp, "
			+ "deleted integer default 0); ";

	private static final String SETS_CREATE = "create table sets "
			+ "(_id integer primary key autoincrement, "
			+ "title text not null, " + "desc text not null, "
			+ "site_id integer default 0, "
			+ "updated timestamp default current_timestamp, "
			+ "deleted integer default 0); ";

	private static final String SETS_CONNECTOR_CREATE = "create table sets_connector "
			+ "(_id integer primary key autoincrement, "
			+ "set_id integer, "
			+ "exercise_id integer, "
			+ "site_id integer default 0, "
			+ "updated timestamp default current_timestamp, "
			+ "deleted integer default 0); ";

	private static final String SETS_DETAIL_CREATE = "create table sets_detail "
			+ "(_id integer primary key autoincrement, "
			+ "sets_connector_id integer, "
			+ "reps integer default 0, "
			+ "percentage decimal(10,2) default 0, "
			+ "site_id integer default 0, "
			+ "updated timestamp default current_timestamp, "
			+ "deleted integer default 0); ";

	private static final String SESSIONS_CREATE = "create table sessions "
			+ "(_id integer primary key autoincrement, "
			+ "programs_connector_id integer, " + "title text not null, "
			+ "desc text not null, " + "status text not null, "
			+ "site_id integer default 0, "
			+ "updated timestamp default current_timestamp, "
			+ "deleted integer default 0); ";

	private static final String SESSIONS_CONNECTOR_CREATE = "create table sessions_connector "
			+ "(_id integer primary key autoincrement, "
			+ "session_id integer, "
			+ "exercise_id integer, "
			+ "site_id integer default 0, "
			+ "updated timestamp default current_timestamp, "
			+ "deleted integer default 0); ";

	private static final String SESSIONS_DETAIL_CREATE = "create table sessions_detail "
			+ "(_id integer primary key autoincrement, "
			+ "sessions_connector_id integer default 0, "
			+ "reps integer default 0, "
			+ "percentage decimal(10,2), "
			+ "site_id integer default 0, "
			+ "updated timestamp default current_timestamp, "
			+ "deleted integer default 0); ";

	private static final String PROGRAMS_CREATE = "create table programs "
			+ "(_id integer primary key autoincrement, "
			+ "title text not null, " + "desc text not null, "
			+ "site_id integer default 0, "
			+ "updated timestamp default current_timestamp, "
			+ "deleted integer default 0); ";

	private static final String PROGRAMS_CONNECTOR_CREATE = "create table programs_connector "
			+ "(_id integer primary key autoincrement, "
			+ "program_id integer, "
			+ "set_id integer, "
			+ "day_number integer, "
			+ "site_id integer default 0, "
			+ "updated timestamp default current_timestamp, "
			+ "deleted integer default 0); ";

	private static final String LOG_CREATE = "create table log "
			+ "(_id integer primary key autoincrement, "
			+ "exercise_id integer, " + "weight decimal(10,2), "
			+ "reps integer, " + "session_id integer, "
			+ "sessions_detail_id integer, "
			+ "done timestamp default current_timestamp, "
			+ "site_id integer default 0, "
			+ "updated timestamp default current_timestamp, "
			+ "deleted integer default 0); ";

	private static final String MEASUREMENT_TYPES_CREATE = "create table measurement_types "
			+ "(_id integer primary key autoincrement, "
			+ "title text not null, "
			+ "units text not null, "
			+ "desc text not null, "
			+ "site_id integer default 0, "
			+ "updated timestamp default current_timestamp, "
			+ "deleted integer default 0); ";

	private static final String MEASUREMENTS_LOG_CREATE = "create table measurements_log "
			+ "(_id integer primary key autoincrement, "
			+ "value decimal(10,2), "
			+ "measurement_type_id integer, "
			+ "date timestamp default current_timestamp, "
			+ "site_id integer default 0, "
			+ "updated timestamp default current_timestamp, "
			+ "deleted integer default 0); ";

	private static final String SETTINGS_CREATE = "create table settings "
			+ "(_id integer primary key autoincrement, "
			+ "authkey text not null, "
			+ "last_updated timestamp default null); ";

	private static final String SETTINGS_FILL = "insert into settings (authkey) values ('');";

	private static final String DATABASE_NAME = "data41";
	public static final String DATABASE_GROUPS_TABLE = "groups";
	public static final String DATABASE_EXERCISES_TABLE = "exercises";
	public static final String DATABASE_SETS_TABLE = "sets";
	public static final String DATABASE_SETS_CONNECTOR_TABLE = "sets_connector";
	public static final String DATABASE_SETS_DETAIL_TABLE = "sets_detail";
	public static final String DATABASE_SESSIONS_TABLE = "sessions";
	public static final String DATABASE_SESSIONS_CONNECTOR_TABLE = "sessions_connector";
	public static final String DATABASE_SESSIONS_DETAIL_TABLE = "sessions_detail";
	public static final String DATABASE_PROGRAMS_TABLE = "programs";
	public static final String DATABASE_PROGRAMS_CONNECTOR_TABLE = "programs_connector";
	public static final String DATABASE_LOG_TABLE = "log";
	public static final String DATABASE_MEASUREMENT_TYPES_TABLE = "measurement_types";
	public static final String DATABASE_MEASUREMENTS_LOG_TABLE = "measurements_log";
	public static final String DATABASE_SETTINGS_TABLE = "settings";
	private static final int DATABASE_VERSION = 2;

	private final Context mCtx;

	private static class DatabaseHelper extends SQLiteOpenHelper {

		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		public String createTrigger(String table) {
			return "CREATE TRIGGER update_" + table + "_trigger "
					+ "AFTER UPDATE ON " + table + " BEGIN " + "UPDATE "
					+ table + " SET updated = DATETIME('NOW') "
					+ "WHERE rowid = new.rowid; " + "END;";
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(GROUPS_CREATE);
			db.execSQL(EXERCISES_CREATE);
			db.execSQL(SETS_CREATE);
			db.execSQL(SETS_CONNECTOR_CREATE);
			db.execSQL(SETS_DETAIL_CREATE);
			db.execSQL(SESSIONS_CREATE);
			db.execSQL(SESSIONS_CONNECTOR_CREATE);
			db.execSQL(SESSIONS_DETAIL_CREATE);
			db.execSQL(PROGRAMS_CREATE);
			db.execSQL(PROGRAMS_CONNECTOR_CREATE);
			db.execSQL(LOG_CREATE);
			db.execSQL(SETTINGS_CREATE);
			db.execSQL(MEASUREMENT_TYPES_CREATE);
			db.execSQL(MEASUREMENTS_LOG_CREATE);
			db.execSQL(SETTINGS_FILL);

			String[] tables = new String[] { "groups", "exercises", "sets",
					"sets_connector", "sets_detail", "sessions",
					"sessions_connector", "sessions_detail", "programs",
					"programs_connector", "log", "measurement_types",
					"measurements_log" };
			for (String table : tables) {
				db.execSQL(createTrigger(table));
			}
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data");

			// UPDATE !!! - UPDATE LIST OF TABLES

			db.execSQL("DROP TABLE IF EXISTS groups");
			db.execSQL("DROP TABLE IF EXISTS excercises");
			db.execSQL("DROP TABLE IF EXISTS sets");
			db.execSQL("DROP TABLE IF EXISTS sets_connector");
			db.execSQL("DROP TABLE IF EXISTS log");
			onCreate(db);
		}
	}

	/**
	 * Constructor - takes the context to allow the database to be
	 * opened/created
	 * 
	 * @param ctx
	 *            the Context within which to work
	 */
	public ExcercisesDbAdapter(Context ctx) {
		this.mCtx = ctx;
	}

	/**
	 * Open the exercises database. If it cannot be opened, try to create a new
	 * instance of the database. If it cannot be created, throw an exception to
	 * signal the failure
	 * 
	 * @return this (self reference, allowing this to be chained in an
	 *         initialization call)
	 * @throws SQLException
	 *             if the database could be neither opened or created
	 */
	public ExcercisesDbAdapter open() throws SQLException {
		mDbHelper = new DatabaseHelper(mCtx);
		mDb = mDbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		mDbHelper.close();
	}

	// all items
	public long createItem(String table, HashMap<String, String> fields) {
		ContentValues initialValues = new ContentValues();

		Set<Map.Entry<String, String>> set = fields.entrySet();

		for (Map.Entry<String, String> entry : set) {
			initialValues.put(entry.getKey(), entry.getValue());
		}

		return mDb.insert(table, null, initialValues);
	}

	public boolean updateItem(String table, HashMap<String, String> fields,
			long rowId) {
		ContentValues args = new ContentValues();

		Set<Map.Entry<String, String>> set = fields.entrySet();

		for (Map.Entry<String, String> entry : set) {
			args.put(entry.getKey(), entry.getValue());
		}

		return mDb.update(table, args, KEY_ROWID + "=" + rowId, null) > 0;
	}

	public Cursor fetchItemBySiteId(String table, String siteId)
			throws SQLException {

		Cursor mCursor = mDb.query(true, table, new String[] { KEY_ROWID },
				KEY_SITEID + "=" + siteId, null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}

	// GROUPS methods
	public long createGroup(String title, String desc, long site_id) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_TITLE, title);
		initialValues.put(KEY_DESC, desc);
		initialValues.put(KEY_SITEID, site_id);

		return mDb.insert(DATABASE_GROUPS_TABLE, null, initialValues);
	}

	public boolean deleteGroup(long rowId) {

		// delete exercises for this group first
		deleteExercisesForGroup(rowId);

		ContentValues args = new ContentValues();
		args.put(KEY_DELETED, 1);

		return mDb.update(DATABASE_GROUPS_TABLE, args, KEY_ROWID + "=" + rowId,
				null) > 0;
	}

	public Cursor fetchAllGroups() {

		return mDb.query(DATABASE_GROUPS_TABLE, new String[] { KEY_ROWID,
				KEY_TITLE, KEY_DESC, KEY_SITEID }, KEY_DELETED + "=0", null,
				null, null, null);
	}

	public Cursor fetchGroup(long rowId, long siteId) throws SQLException {

		// if site_id is present - fetch group using site_id
		String condition = KEY_ROWID + "=" + rowId;
		if (siteId != 0) {
			condition = KEY_SITEID + "=" + siteId;
		}
		Cursor mCursor = mDb.query(true, DATABASE_GROUPS_TABLE, new String[] {
				KEY_ROWID, KEY_TITLE, KEY_DESC, KEY_SITEID }, condition, null,
				null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;

	}

	public Cursor fetchUpdatedGroups(String updated) {

		return mDb.query(DATABASE_GROUPS_TABLE, new String[] { KEY_ROWID,
				KEY_TITLE, KEY_DESC, KEY_SITEID, KEY_UPDATED, KEY_DELETED },
				KEY_UPDATED + " >  ?", new String[] { updated }, null, null,
				null, null);
	}

	public Cursor fetchUpdatedItems(String table, String updated) {

		return mDb.query(table, null, KEY_UPDATED + " >  ?",
				new String[] { updated }, null, null, null, null);
	}

	public boolean updateGroup(long rowId, String title, String desc,
			long site_id) {
		ContentValues args = new ContentValues();
		args.put(KEY_TITLE, title);
		args.put(KEY_DESC, desc);
		args.put(KEY_SITEID, site_id);

		return mDb.update(DATABASE_GROUPS_TABLE, args, KEY_ROWID + "=" + rowId,
				null) > 0;
	}

	// end of GROUPS

	// EXCERCISES methods
	public long createExercise(String title, String desc, long group_id,
			int type, long max_reps, float max_weight) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_TITLE, title);
		initialValues.put(KEY_DESC, desc);
		initialValues.put(KEY_MAX_REPS, max_reps);
		initialValues.put(KEY_MAX_WEIGHT, max_weight);
		initialValues.put(KEY_GROUPID, group_id);
		initialValues.put(KEY_TYPE, type);

		return mDb.insert(DATABASE_EXERCISES_TABLE, null, initialValues);
	}

	public boolean deleteExcercise(long rowId) {

		ContentValues args = new ContentValues();
		args.put(KEY_DELETED, 1);

		return mDb.update(DATABASE_EXERCISES_TABLE, args, KEY_ROWID + "="
				+ rowId, null) > 0;
	}

	public boolean deleteExercisesForGroup(long groupId) {

		ContentValues args = new ContentValues();
		args.put(KEY_DELETED, 1);

		return mDb.update(DATABASE_EXERCISES_TABLE, args, KEY_GROUPID + "="
				+ groupId, null) > 0;
	}

	public Cursor fetchExcercisesForGroup(long group_id) {

		return mDb.query(DATABASE_EXERCISES_TABLE, new String[] { KEY_ROWID,
				KEY_TITLE, KEY_DESC, KEY_TYPE, KEY_GROUPID, KEY_SITEID },
				KEY_GROUPID + "=" + group_id + " AND " + KEY_DELETED + "=0",
				null, null, null, null, null);
	}

	public Cursor fetchExercise(long rowId) throws SQLException {

		Cursor mCursor =

		mDb.query(true, DATABASE_EXERCISES_TABLE, null,
				KEY_ROWID + "=" + rowId, null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;

	}

	public Cursor fetchUpdatedExercises(String updated) {

		return mDb.query(DATABASE_EXERCISES_TABLE, new String[] { KEY_ROWID,
				KEY_TITLE, KEY_DESC, KEY_TYPE, KEY_GROUPID, KEY_SITEID,
				KEY_UPDATED }, KEY_UPDATED + " >  ?", new String[] { updated },
				null, null, null, null);
	}

	public boolean updateExercise(long rowId, String title, String desc,
			long group_id, int type, long max_reps, float max_weight) {
		ContentValues args = new ContentValues();
		args.put(KEY_TITLE, title);
		args.put(KEY_DESC, desc);
		args.put(KEY_GROUPID, group_id);
		args.put(KEY_MAX_REPS, max_reps);
		args.put(KEY_MAX_WEIGHT, max_weight);
		args.put(KEY_TYPE, type);
		args.put(KEY_UPDATED, "CURRENT_TIMESTAMP");

		return mDb.update(DATABASE_EXERCISES_TABLE, args, KEY_ROWID + "="
				+ rowId, null) > 0;
	}

	// end of EXCERCISES methods

	// SETS methods
	public long createSet(String title, String desc, long site_id) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_TITLE, title);
		initialValues.put(KEY_DESC, desc);
		initialValues.put(KEY_SITEID, site_id);

		return mDb.insert(DATABASE_SETS_TABLE, null, initialValues);
	}

	public long addExerciseToSet(long setId, long exerciseId) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_SETID, setId);
		initialValues.put(KEY_EXERCISEID, exerciseId);
		
		// check if exercise is already there - if it is - do not add
		Cursor mCursor =
			mDb.query(true, DATABASE_SETS_CONNECTOR_TABLE, null, 
					KEY_EXERCISEID + "=? AND " + KEY_SETID + "=? AND " + KEY_DELETED + "=0",
					new String[]{String.valueOf(exerciseId), String.valueOf(setId)}, null, null, null, null);

		if(mCursor.getCount() > 0){
			Log.i("ALREADY ADDED:", "Exercise already added");
			return 1;
		}else{
			return mDb.insert(DATABASE_SETS_CONNECTOR_TABLE, null, initialValues);	
		}

	}

	public boolean deleteSet(long rowId) {

		ContentValues args = new ContentValues();
		args.put(KEY_DELETED, 1);

		return mDb.update(DATABASE_SETS_TABLE, args, KEY_ROWID + "=" + rowId,
				null) > 0;
	}

	public Cursor fetchAllSets() {

		return mDb.query(DATABASE_SETS_TABLE, new String[] { KEY_ROWID,
				KEY_TITLE, KEY_DESC, KEY_SITEID }, KEY_DELETED + "=0", null,
				null, null, null);
	}

	// same as fetchAll - in future implement KEY_ROWID NOT IN (SELECT set_id
	// FROM PROGRAMS_CONNECTOR)
	public Cursor fetchFreeSets() {

		return mDb.query(DATABASE_SETS_TABLE, new String[] { KEY_ROWID,
				KEY_TITLE, KEY_DESC, KEY_SITEID }, KEY_DELETED + "=0", null,
				null, null, null);
	}

	public Cursor fetchSet(long rowId) throws SQLException {

		Cursor mCursor =

		mDb.query(true, DATABASE_SETS_TABLE, new String[] { KEY_ROWID,
				KEY_TITLE, KEY_DESC, KEY_SITEID }, KEY_ROWID + "=" + rowId,
				null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;

	}

	public Cursor fetchUpdatedSets(String updated) {

		return mDb.query(DATABASE_SETS_TABLE, new String[] { KEY_ROWID,
				KEY_TITLE, KEY_DESC, KEY_SITEID, KEY_UPDATED }, KEY_UPDATED
				+ " >  ?", new String[] { updated }, null, null, null, null);
	}

	public Cursor fetchExercisesForSet(long setId) throws SQLException {
		Cursor mCursor = mDb.rawQuery("SELECT " + DATABASE_SETS_CONNECTOR_TABLE
				+ "." + KEY_ROWID + ", " + DATABASE_EXERCISES_TABLE + "."
				+ KEY_ROWID + " AS " + KEY_EXERCISEID + ", "
				+ DATABASE_EXERCISES_TABLE + "." + KEY_TITLE + ", "
				+ DATABASE_EXERCISES_TABLE + "." + KEY_TYPE + ", "
				+ DATABASE_EXERCISES_TABLE + "." + KEY_MAX_WEIGHT + ", "
				+ DATABASE_EXERCISES_TABLE + "." + KEY_MAX_REPS + ", "
				+ DATABASE_EXERCISES_TABLE + "." + KEY_DESC + " FROM "
				+ DATABASE_SETS_CONNECTOR_TABLE + ", "
				+ DATABASE_EXERCISES_TABLE + " WHERE "
				+ DATABASE_EXERCISES_TABLE + "." + KEY_ROWID + " = "
				+ DATABASE_SETS_CONNECTOR_TABLE + "." + KEY_EXERCISEID
				+ " AND " + DATABASE_SETS_CONNECTOR_TABLE + "." + KEY_SETID
				+ " = ?" + " AND " + DATABASE_SETS_CONNECTOR_TABLE + "."
				+ KEY_DELETED + " = 0", new String[] { String.valueOf(setId) });

		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;

	}

	public Cursor fetchExerciseForSetsConnector(long setsConnectorId)
			throws SQLException {
		Cursor mCursor = mDb.rawQuery("SELECT " + DATABASE_EXERCISES_TABLE
				+ "." + KEY_ROWID + ", " + DATABASE_EXERCISES_TABLE + "."
				+ KEY_TITLE + ", " + DATABASE_EXERCISES_TABLE + "." + KEY_TYPE
				+ ", " + DATABASE_EXERCISES_TABLE + "." + KEY_MAX_WEIGHT + ", "
				+ DATABASE_EXERCISES_TABLE + "." + KEY_MAX_REPS + ", "
				+ DATABASE_EXERCISES_TABLE + "." + KEY_DESC + " FROM "
				+ DATABASE_SETS_CONNECTOR_TABLE + ", "
				+ DATABASE_EXERCISES_TABLE + " WHERE "
				+ DATABASE_EXERCISES_TABLE + "." + KEY_ROWID + " = "
				+ DATABASE_SETS_CONNECTOR_TABLE + "." + KEY_EXERCISEID
				+ " AND " + DATABASE_SETS_CONNECTOR_TABLE + "." + KEY_ROWID
				+ " = ?" + " AND " + DATABASE_SETS_CONNECTOR_TABLE + "."
				+ KEY_DELETED + " = 0", new String[] { String
				.valueOf(setsConnectorId) });

		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}

	public boolean deleteExerciseFromSet(long rowId) {

		ContentValues args = new ContentValues();
		args.put(KEY_DELETED, 1);

		return mDb.update(DATABASE_SETS_CONNECTOR_TABLE, args, KEY_ROWID + "="
				+ rowId, null) > 0;
	}

	public boolean updateSet(long rowId, String title, String desc, long site_id) {
		ContentValues args = new ContentValues();
		args.put(KEY_TITLE, title);
		args.put(KEY_DESC, desc);
		args.put(KEY_SITEID, site_id);

		return mDb.update(DATABASE_SETS_TABLE, args, KEY_ROWID + "=" + rowId,
				null) > 0;
	}

	// end of SETS

	// LOG methods
	public long createLogEntry(long exercise_id, float weight, int times,
			int program_id, int day) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_EXERCISEID, exercise_id);
		initialValues.put(KEY_WEIGHT, weight);
		initialValues.put(KEY_TIMES, times);
		initialValues.put(KEY_PROGRAMID, program_id);
		initialValues.put(KEY_DAY, day);
		initialValues.put(KEY_SITEID, 0);

		return mDb.insert(DATABASE_LOG_TABLE, null, initialValues);
	}

	public boolean deleteLogEntry(long rowId) {

		ContentValues args = new ContentValues();
		args.put(KEY_DELETED, 1);

		return mDb.update(DATABASE_LOG_TABLE, args, KEY_ROWID + "=" + rowId,
				null) > 0;
	}

	public Cursor fetchLogEntry(long rowId) throws SQLException {

		Cursor mCursor =

		mDb.query(true, DATABASE_LOG_TABLE, new String[] { KEY_ROWID,
				KEY_EXERCISEID, KEY_WEIGHT, KEY_TIMES, KEY_PROGRAMID, KEY_DAY,
				KEY_DONE }, KEY_ROWID + "=" + rowId, null, null, null, null,
				null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;

	}

	public Cursor fetchLogEntriesForExercise(long exerciseId, String donestart,
			String doneend) throws SQLException {

		Cursor mCursor = mDb.query(true, DATABASE_LOG_TABLE, new String[] {
				KEY_ROWID, KEY_EXERCISEID, KEY_WEIGHT, KEY_TIMES,
				KEY_PROGRAMID, KEY_DAY, KEY_DONE }, KEY_EXERCISEID + "="
				+ exerciseId + " AND '" + donestart + "' < " + KEY_DONE
				+ " AND " + KEY_DONE + " < '" + doneend + "' AND "
				+ KEY_DELETED + "=0", null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;

	}

	public Cursor fetchExercisesForDates(String donestart, String doneend)
			throws SQLException {
		Cursor mCursor = mDb.rawQuery("SELECT " + DATABASE_LOG_TABLE + "."
				+ KEY_ROWID + " AS _id, " + DATABASE_LOG_TABLE + "."
				+ KEY_EXERCISEID + " AS " + KEY_EXERCISEID + ", "
				+ DATABASE_EXERCISES_TABLE + "." + KEY_TITLE + " AS "
				+ KEY_TITLE + " FROM " + DATABASE_LOG_TABLE + ", "
				+ DATABASE_EXERCISES_TABLE + " WHERE "
				+ DATABASE_EXERCISES_TABLE + "." + KEY_ROWID + " = "
				+ DATABASE_LOG_TABLE + "." + KEY_EXERCISEID + " AND '"
				+ donestart + "' < " + KEY_DONE + " AND " + KEY_DONE + " < '"
				+ doneend + "' AND " + DATABASE_LOG_TABLE + "." + KEY_DELETED
				+ "=0 GROUP BY " + KEY_EXERCISEID, null);

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
		
		Cursor mCursor = mDb.rawQuery("SELECT " + DATABASE_LOG_TABLE + "."
				+ KEY_ROWID + " AS _id, SUM(" + sumPart + ") AS sum, MAX(" + maxPart + ") AS max, " 
				+ DATABASE_LOG_TABLE + "." + KEY_SESSIONID + " AS " + KEY_SESSIONID + ", "
				+ DATABASE_SESSIONS_CONNECTOR_TABLE + "." + KEY_ROWID + " AS " + KEY_SESSIONS_CONNECTORID + ", "
				+ KEY_DONE + ", " + KEY_TITLE
				+ " FROM " + DATABASE_LOG_TABLE 
				+ " LEFT JOIN " + DATABASE_SESSIONS_TABLE
				+ " ON " + DATABASE_LOG_TABLE + "." + KEY_SESSIONID + "=" + DATABASE_SESSIONS_TABLE + "." + KEY_ROWID
				+ " LEFT JOIN " + DATABASE_SESSIONS_CONNECTOR_TABLE 
				+ " ON " + DATABASE_SESSIONS_CONNECTOR_TABLE + "." + KEY_EXERCISEID + " = " 
				+ DATABASE_LOG_TABLE + "." + KEY_EXERCISEID + " AND " 
				+ DATABASE_SESSIONS_CONNECTOR_TABLE + "." + KEY_SESSIONID + " = " 
				+ DATABASE_LOG_TABLE + "." + KEY_SESSIONID + " AND " 
				+ DATABASE_SESSIONS_CONNECTOR_TABLE + "." + KEY_DELETED + " =0"
				+ " WHERE " + DATABASE_LOG_TABLE + "." + KEY_EXERCISEID + "=" + String.valueOf(exerciseId) + " AND '"
				+ donestart + "' < " + KEY_DONE + " AND " + KEY_DONE + " < '"
				+ doneend + "' AND " + DATABASE_LOG_TABLE + "." + KEY_DELETED
				+ "=0 AND " + DATABASE_SESSIONS_CONNECTOR_TABLE + "." + KEY_DELETED 
				+ "=0 GROUP BY " + KEY_SESSIONID + " ORDER BY " + KEY_SESSIONID + " DESC", null);
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
		
		Cursor mCursor = mDb.rawQuery("SELECT SUM(" + sumPart + ") AS sum, MAX(" + maxPart + ") AS max FROM " + DATABASE_LOG_TABLE + " WHERE " + KEY_EXERCISEID + " = " 
				+ String.valueOf(exerciseId) + " AND " + KEY_SESSIONID + "=" 
				+ String.valueOf(sessionId) + " AND " 
				+ KEY_DELETED
				+ "=0", null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}

	public boolean updateLogEntry(long rowId, float weight, int times) {
		ContentValues args = new ContentValues();
		args.put(KEY_WEIGHT, weight);
		args.put(KEY_TIMES, times);

		return mDb.update(DATABASE_LOG_TABLE, args, KEY_ROWID + "=" + rowId,
				null) > 0;
	}

	// end of LOG

	public String getLocalTime() {

		Cursor mCursor = mDb.rawQuery(
				"SELECT strftime('%s','now') AS localtime", null);

		if (mCursor != null) {
			mCursor.moveToFirst();
		}

		return mCursor.getString(mCursor.getColumnIndex("localtime"));

	}

	// settings

	public boolean setAuthKey(String authkey) {
		ContentValues args = new ContentValues();
		args.put(KEY_AUTHKEY, authkey);

		return mDb
				.update(DATABASE_SETTINGS_TABLE, args, KEY_ROWID + "=1", null) > 0;
	}

	public String getAuthKey() throws SQLException {

		Cursor mCursor = mDb.query(true, DATABASE_SETTINGS_TABLE,
				new String[] { KEY_AUTHKEY }, KEY_ROWID + "=1", null, null,
				null, null, null);

		if (mCursor != null) {
			mCursor.moveToFirst();
		}

		return mCursor.getString(mCursor.getColumnIndex(KEY_AUTHKEY));
	}

	public String getLastUpdated() throws SQLException {

		Cursor mCursor = mDb.query(true, DATABASE_SETTINGS_TABLE,
				new String[] { KEY_LASTUPDATED }, KEY_ROWID + "=1", null, null,
				null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor.getString(mCursor.getColumnIndex(KEY_LASTUPDATED));
	}

	public void setLastUpdated() {
		mDb.execSQL("update " + DATABASE_SETTINGS_TABLE + " set "
				+ KEY_LASTUPDATED + "= DATETIME('NOW') WHERE _id=1");
	}

	// SETS_DETAIL methods
	public Cursor fetchRepsForConnector(Long setsConnectorId)
			throws SQLException {

		Cursor mCursor = mDb.query(true, DATABASE_SETS_DETAIL_TABLE, null,
				KEY_SETS_CONNECTORID + "=" + setsConnectorId + " AND "
						+ KEY_DELETED + "=0", null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}

	public Cursor fetchRepsEntry(long rowId) throws SQLException {

		Cursor mCursor =

		mDb.query(true, DATABASE_SETS_DETAIL_TABLE, null, KEY_ROWID + "="
				+ rowId, null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;

	}

	public long createRepsEntry(long sets_connector_id, int reps,
			float percentage) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_SETS_CONNECTORID, sets_connector_id);
		initialValues.put(KEY_REPS, reps);
		initialValues.put(KEY_PERCENTAGE, percentage);
		initialValues.put(KEY_SITEID, 0);

		return mDb.insert(DATABASE_SETS_DETAIL_TABLE, null, initialValues);
	}

	public boolean updateRepsEntry(long rowId, int reps, float percentage) {
		ContentValues args = new ContentValues();
		args.put(KEY_REPS, reps);
		args.put(KEY_PERCENTAGE, percentage);

		return mDb.update(DATABASE_SETS_DETAIL_TABLE, args, KEY_ROWID + "="
				+ rowId, null) > 0;
	}

	public boolean deleteRepsEntry(long rowId) {

		ContentValues args = new ContentValues();
		args.put(KEY_DELETED, 1);

		return mDb.update(DATABASE_SETS_DETAIL_TABLE, args, KEY_ROWID + "="
				+ rowId, null) > 0;
	}

	// END OF SETS_DETAIL methods

	// SESSION_DETAIL methods
	// they are read-only for now (maybe forever :))

	public Cursor fetchRepsForSessionConnector(Long sessionsConnectorId)
			throws SQLException {

		Cursor mCursor = mDb.query(true, DATABASE_SESSIONS_DETAIL_TABLE, null,
				KEY_SESSIONS_CONNECTORID + "=" + sessionsConnectorId + " AND "
						+ KEY_DELETED + "=0", null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}

	public long createSessionRepsEntry(long sessions_connector_id, long reps,
			float percentage) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_SESSIONS_CONNECTORID, sessions_connector_id);
		initialValues.put(KEY_REPS, reps);
		initialValues.put(KEY_PERCENTAGE, percentage);

		return mDb.insert(DATABASE_SESSIONS_DETAIL_TABLE, null, initialValues);
	}

	// END OF SESSION DETAIL methods

	// PROGRAMS methods

	public Cursor fetchAllPrograms() {

		return mDb.query(DATABASE_PROGRAMS_TABLE, null, KEY_DELETED + "=0",
				null, null, null, null);
	}

	public Cursor fetchProgram(long rowId) throws SQLException {

		Cursor mCursor = mDb.query(true, DATABASE_PROGRAMS_TABLE, null,
				KEY_ROWID + "=" + rowId, null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;

	}

	public long createProgram(String title, String desc) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_TITLE, title);
		initialValues.put(KEY_DESC, desc);

		return mDb.insert(DATABASE_PROGRAMS_TABLE, null, initialValues);
	}

	public boolean updateProgram(long rowId, String title, String desc) {
		ContentValues args = new ContentValues();
		args.put(KEY_TITLE, title);
		args.put(KEY_DESC, desc);

		return mDb.update(DATABASE_PROGRAMS_TABLE, args, KEY_ROWID + "="
				+ rowId, null) > 0;
	}

	public boolean deleteProgram(long rowId) {

		ContentValues args = new ContentValues();
		args.put(KEY_DELETED, 1);

		return mDb.update(DATABASE_PROGRAMS_TABLE, args, KEY_ROWID + "="
				+ rowId, null) > 0;
	}
	
	public Long getProgramMaxDay(long programId) throws SQLException {
		Cursor mCursor = mDb.rawQuery("SELECT MAX(" + KEY_DAY_NUMBER + ") AS max_day " +

				"FROM " + DATABASE_PROGRAMS_CONNECTOR_TABLE + 
				" WHERE " + KEY_DELETED + " = 0 AND " + KEY_PROGRAMID + " = ? "
				, new String[] { String.valueOf(programId) });

		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		
		return mCursor.getLong(mCursor.getColumnIndex("max_day"));
	}

	public Cursor fetchProgramSets(long programId) {
		return mDb.query(DATABASE_PROGRAMS_CONNECTOR_TABLE, null, KEY_DELETED
				+ "=0 AND " + KEY_PROGRAMID + "=" + programId, null, null,
				null, KEY_DAY_NUMBER + " ASC");
	}

	public long addSetToProgram(long program_id, long set_id, long day_number) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_PROGRAMID, program_id);
		initialValues.put(KEY_SETID, set_id);
		initialValues.put(KEY_DAY_NUMBER, day_number);

		return mDb.insert(DATABASE_PROGRAMS_CONNECTOR_TABLE, null,
				initialValues);
	}
	
	public boolean removeSetFromProgram(long rowId) {
		
		ContentValues args = new ContentValues();
		args.put(KEY_DELETED, 1);

		return mDb.update(DATABASE_PROGRAMS_CONNECTOR_TABLE, args, KEY_ROWID + "="
				+ rowId, null) > 0;
	}

	// END of PROGRAMS methods

	// SESSIONS methods
	public Cursor fetchAllSessions() {

		return mDb.query(DATABASE_SESSIONS_TABLE, null, KEY_DELETED + "=0",
				null, null, null, null);
	}

	public Cursor fetchFilteredSessions(String filter) {

		return mDb.query(DATABASE_SESSIONS_TABLE, null, KEY_DELETED + "=0 AND "
				+ KEY_STATUS + "= ?", new String[] { filter }, null, null,
				KEY_ROWID + " DESC", null);
	}

	public Cursor fetchSession(long rowId) throws SQLException {

		Cursor mCursor =

		mDb.query(true, DATABASE_SESSIONS_TABLE, null, KEY_ROWID + "=" + rowId,
				null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;

	}

	public long createSession(String title, String desc,
			long programs_connector_id) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_TITLE, title);
		initialValues.put(KEY_DESC, desc);
		initialValues.put(KEY_PROGRAMS_CONNECTORID, programs_connector_id);
		initialValues.put(KEY_STATUS, "INPROGRESS");

		return mDb.insert(DATABASE_SESSIONS_TABLE, null, initialValues);
	}

	public boolean updateSession(long rowId, String title, String desc,
			String status) {
		ContentValues args = new ContentValues();
		args.put(KEY_TITLE, title);
		args.put(KEY_DESC, desc);
		args.put(KEY_STATUS, status);

		return mDb.update(DATABASE_SESSIONS_TABLE, args, KEY_ROWID + "="
				+ rowId, null) > 0;
	}

	public boolean updateSessionStatus(long rowId, String status) {
		ContentValues args = new ContentValues();
		args.put(KEY_STATUS, status);

		return mDb.update(DATABASE_SESSIONS_TABLE, args, KEY_ROWID + "="
				+ rowId, null) > 0;
	}

	public boolean deleteSession(long rowId) {

		ContentValues args = new ContentValues();
		args.put(KEY_DELETED, 1);

		return mDb.update(DATABASE_SESSIONS_TABLE, args, KEY_ROWID + "="
				+ rowId, null) > 0;
	}

	public Cursor fetchExercisesForSession(long sessionId) throws SQLException {
		Cursor mCursor = mDb.rawQuery("SELECT "
				+ DATABASE_SESSIONS_CONNECTOR_TABLE + "." + KEY_ROWID + ", "
				+ DATABASE_EXERCISES_TABLE + "." + KEY_ROWID + " AS "
				+ KEY_EXERCISEID + ", " + DATABASE_EXERCISES_TABLE + "."
				+ KEY_TITLE + ", " + DATABASE_EXERCISES_TABLE + "." + KEY_DESC
				+ ", " + DATABASE_EXERCISES_TABLE + "." + KEY_TYPE + ", "
				+ DATABASE_EXERCISES_TABLE + "." + KEY_MAX_REPS + ", "
				+ DATABASE_EXERCISES_TABLE + "." + KEY_MAX_WEIGHT + " " +

				"FROM " + DATABASE_SESSIONS_CONNECTOR_TABLE + ", "
				+ DATABASE_EXERCISES_TABLE + " WHERE "
				+ DATABASE_EXERCISES_TABLE + "." + KEY_ROWID + " = "
				+ DATABASE_SESSIONS_CONNECTOR_TABLE + "." + KEY_EXERCISEID
				+ " AND " + DATABASE_SESSIONS_CONNECTOR_TABLE + "."
				+ KEY_SESSIONID + " = ?" + " AND "
				+ DATABASE_SESSIONS_CONNECTOR_TABLE + "." + KEY_DELETED
				+ " = 0", new String[] { String.valueOf(sessionId) });

		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;

	}

	public long addExerciseToSession(long sessionId, long exerciseId) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_SESSIONID, sessionId);
		initialValues.put(KEY_EXERCISEID, exerciseId);

		// check if exercise is already there - if it is - do not add
		Cursor mCursor =
			mDb.query(true, DATABASE_SESSIONS_CONNECTOR_TABLE, null, 
					KEY_EXERCISEID + "=? AND " + KEY_SESSIONID + "=? AND " + KEY_DELETED + "=0",
					new String[]{String.valueOf(exerciseId), String.valueOf(sessionId)}, null, null, null, null);

		if(mCursor.getCount() > 0){
			Log.i("ALREADY ADDED:", "Exercise already added");
			return 1;
		}else{
			return mDb.insert(DATABASE_SESSIONS_CONNECTOR_TABLE, null,
					initialValues);	
		}
	}

	public boolean deleteExerciseFromSession(long rowId) {

		ContentValues args = new ContentValues();
		args.put(KEY_DELETED, 1);

		return mDb.update(DATABASE_SESSIONS_CONNECTOR_TABLE, args, KEY_ROWID
				+ "=" + rowId, null) > 0;
	}

	public Cursor fetchFreeSessionReps(long session_id, long exercise_id)
			throws SQLException {

		Cursor mCursor = mDb.query(true, DATABASE_LOG_TABLE, null,
				KEY_SESSIONID + "=" + session_id + " AND " + KEY_EXERCISEID
						+ "=" + exercise_id + " AND " + KEY_SESSIONS_DETAILID
						+ "=0" + " AND " + KEY_DELETED + "=0", null, null,
				null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}

	public Cursor fetchSessionRepsEntry(long rowId) throws SQLException {

		Cursor mCursor =

		mDb.query(true, DATABASE_LOG_TABLE, null, KEY_ROWID + "=" + rowId,
				null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;

	}

	public Cursor fetchDoneSessionReps(long session_id, long sessions_detail_id)
			throws SQLException {

		Cursor mCursor =

		mDb.query(true, DATABASE_LOG_TABLE, null, KEY_SESSIONID + "="
				+ session_id + " AND " + KEY_SESSIONS_DETAILID + "="
				+ sessions_detail_id + " AND " + KEY_DELETED + "=0", null,
				null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;

	}

	public long createSessionRepsEntry(long session_id, long exercise_id,
			long session_detail_id, int reps, float weight) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_SESSIONID, session_id);
		initialValues.put(KEY_EXERCISEID, exercise_id);
		initialValues.put(KEY_SESSIONS_DETAILID, session_detail_id);
		initialValues.put(KEY_REPS, reps);
		initialValues.put(KEY_WEIGHT, weight);

		return mDb.insert(DATABASE_LOG_TABLE, null, initialValues);
	}

	public boolean updateSessionRepsEntry(long rowId, int reps, float weight) {
		ContentValues args = new ContentValues();
		args.put(KEY_REPS, reps);
		args.put(KEY_WEIGHT, weight);

		return mDb.update(DATABASE_LOG_TABLE, args, KEY_ROWID + "=" + rowId,
				null) > 0;
	}

	public boolean deleteSessionRepsEntry(long rowId) {

		ContentValues args = new ContentValues();
		args.put(KEY_DELETED, 1);

		return mDb.update(DATABASE_LOG_TABLE, args, KEY_ROWID + "=" + rowId,
				null) > 0;
	}

	// END of SESSIONS methods

	// SESSION_CONNECTOR methods
	public Cursor fetchSessionConnector(long rowId) throws SQLException {

		Cursor mCursor =

		mDb.query(true, DATABASE_SESSIONS_CONNECTOR_TABLE, null, KEY_ROWID
				+ "=" + rowId, null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;

	}

	// END of session_connector methods

	// MEASUREMENTS methods
	public long createMeasurementType(String title, String units, String desc) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_TITLE, title);
		initialValues.put(KEY_UNITS, units);
		initialValues.put(KEY_DESC, desc);

		return mDb
				.insert(DATABASE_MEASUREMENT_TYPES_TABLE, null, initialValues);
	}

	public boolean updateMeasurementType(long rowId, String title,
			String units, String desc) {
		ContentValues args = new ContentValues();
		args.put(KEY_TITLE, title);
		args.put(KEY_UNITS, units);
		args.put(KEY_DESC, desc);

		return mDb.update(DATABASE_MEASUREMENT_TYPES_TABLE, args, KEY_ROWID
				+ "=" + rowId, null) > 0;
	}

	public boolean deleteMeasurementType(long rowId) {

		// delete exercises for this group first
		deleteLogsForMeasurement(rowId);

		ContentValues args = new ContentValues();
		args.put(KEY_DELETED, 1);

		return mDb.update(DATABASE_MEASUREMENT_TYPES_TABLE, args, KEY_ROWID
				+ "=" + rowId, null) > 0;
	}

	public Cursor fetchMeasurementType(long rowId) throws SQLException {

		Cursor mCursor = mDb.query(true, DATABASE_MEASUREMENT_TYPES_TABLE,
				null, KEY_ROWID + "=" + rowId, null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;

	}

	public Cursor fetchAllMeasurementTypes() {

		return mDb.query(DATABASE_MEASUREMENT_TYPES_TABLE, null, KEY_DELETED
				+ "=0", null, null, null, null);
	}

	public boolean deleteLogsForMeasurement(long typeId) {

		ContentValues args = new ContentValues();
		args.put(KEY_DELETED, 1);

		return mDb.update(DATABASE_MEASUREMENTS_LOG_TABLE, args,
				KEY_MEASUREMENT_TYPEID + "=" + typeId, null) > 0;
	}

	public Cursor fetchMeasLogEntries(long typeId) throws SQLException {

		return mDb.query(DATABASE_MEASUREMENTS_LOG_TABLE, null,
				KEY_MEASUREMENT_TYPEID + "=" + typeId + " AND " + KEY_DELETED
						+ "=0", null, null, null, KEY_ROWID + " DESC", null);

	}
	
	public Cursor fetchMeasLogEntriesForDates(long typeId, String begin, String end) throws SQLException {

		return mDb.query(true, DATABASE_MEASUREMENTS_LOG_TABLE, null, 
				KEY_MEASUREMENT_TYPEID + "=" + typeId 
				+ " AND '" + begin + "' < " + KEY_DATE
				+ " AND " + KEY_DATE + " < '" + end + "' AND "
				+ KEY_DELETED + "=0", null, null, null, KEY_ROWID + " DESC", null);

	}
	
	public Cursor fetchMaxMeasForDates(long typeId, String begin, String end) throws SQLException {
	
		Cursor cursor = mDb.rawQuery( "SELECT MAX(" + KEY_VALUE + ") AS max FROM "
				+ DATABASE_MEASUREMENTS_LOG_TABLE + " WHERE " 
				+ KEY_MEASUREMENT_TYPEID + "=" + typeId 
				+ " AND '" + begin + "' < " + KEY_DATE
				+ " AND " + KEY_DATE + " < '" + end + "' AND "
				+ KEY_DELETED + "=0", null);
		if (cursor != null) {
			cursor.moveToFirst();
		}
		return cursor;

	}

	public long createMeasLogEntry(long type_id, float value) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_MEASUREMENT_TYPEID, type_id);
		initialValues.put(KEY_VALUE, value);

		return mDb.insert(DATABASE_MEASUREMENTS_LOG_TABLE, null, initialValues);
	}

	public boolean updateMeasLogEntry(long rowId, float value) {
		ContentValues args = new ContentValues();
		args.put(KEY_VALUE, value);

		return mDb.update(DATABASE_MEASUREMENTS_LOG_TABLE, args, KEY_ROWID
				+ "=" + rowId, null) > 0;
	}

	public Cursor fetchMeasLogEntry(long rowId) throws SQLException {

		Cursor mCursor = mDb.query(true, DATABASE_MEASUREMENTS_LOG_TABLE, null,
				KEY_ROWID + "=" + rowId, null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;

	}

	public boolean deleteMeasLogEntry(long rowId) {

		ContentValues args = new ContentValues();
		args.put(KEY_DELETED, 1);

		return mDb.update(DATABASE_MEASUREMENTS_LOG_TABLE, args, KEY_ROWID
				+ "=" + rowId, null) > 0;
	}
	// END of MEASUREMENTS methods

}
