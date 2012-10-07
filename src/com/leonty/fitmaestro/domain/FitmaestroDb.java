package com.leonty.fitmaestro.domain;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class FitmaestroDb {
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
	public static final String KEY_FILENAME = "filename";
	public static final String KEY_UPDATED = "updated";
	public static final String KEY_DELETED = "deleted";
	public static final String KEY_LASTUPDATED = "last_updated";
	public static final String KEY_AUTHKEY = "authkey";
	
	private static final String DATABASE_NAME = "data45";
	public static final String DATABASE_GROUPS_TABLE = "groups";
	public static final String DATABASE_FILES_TABLE = "files";
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

	/**
	 * Database creation sql statement
	 */
	private static final String GROUPS_CREATE = "create table groups "
			+ "(_id integer primary key autoincrement, "
			+ "title text not null, " + "desc text not null, "
			+ "site_id integer default 0, "
			+ "updated timestamp default current_timestamp, "
			+ "deleted integer default 0); ";

	private static final String FILES_CREATE = "create table files "
			+ "(_id integer primary key autoincrement, "
			+ "filename text not null, " 
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
	
	private static final String TAG = "FitmaestroDb";
	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;
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
			db.execSQL(FILES_CREATE);
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

			String[] tables = new String[] { "groups", "files", "exercises", "sets",
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
	public FitmaestroDb(Context ctx) {
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
	public FitmaestroDb open() throws SQLException {
		mDbHelper = new DatabaseHelper(mCtx);
		mDb = mDbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		mDbHelper.close();
	}
	
	public SQLiteDatabase getDb() {
		return mDb;
	}	
}
