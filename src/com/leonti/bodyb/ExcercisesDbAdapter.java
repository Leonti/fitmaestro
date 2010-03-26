
package com.leonti.bodyb;

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
    public static final String KEY_TYPE = "ex_type"; //0 - own weight, 1 - with weight
    public static final String KEY_GROUPID = "group_id"; //for exercises
    public static final String KEY_EXERCISEID = "exercise_id"; //for log
    public static final String KEY_SETID = "set_id"; //for set connector
    public static final String KEY_WEIGHT = "weight";
    public static final String KEY_TIMES = "times";
    public static final String KEY_PROGRAMID = "program_id";
    public static final String KEY_DAY = "day";
    public static final String KEY_DONE = "done";
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
    private static final String GROUPS_CREATE =
            "create table groups (_id integer primary key autoincrement, " //ON UPDATE CURRENT_TIMESTAMP
                    + "title text not null, desc text not null, site_id integer, updated timestamp default current_timestamp, deleted integer default 0); ";
    private static final String EXERCISES_CREATE =
    		"create table exercises (_id integer primary key autoincrement, "
                    + "title text not null, desc text not null, ex_type integer, group_id integer, site_id integer, updated timestamp default current_timestamp, deleted integer default 0); ";
    private static final String SETS_CREATE =
    		"create table sets (_id integer primary key autoincrement, "
                    + "title text not null, desc text not null, site_id integer, updated timestamp default current_timestamp, deleted integer default 0); ";
    
    private static final String SETS_CONNECTOR_CREATE =
		"create table sets_connector (_id integer primary key autoincrement, "
                + "set_id integer, exercise_id integer, site_id integer, updated timestamp default current_timestamp, deleted integer default 0); ";
    
    private static final String LOG_CREATE =
		"create table log (_id integer primary key autoincrement, "
                + "exercise_id integer, weight decimal(10,2), times integer, "
                + "program_id integer, day integer, done timestamp default current_timestamp, site_id integer, updated timestamp default current_timestamp, deleted integer default 0); ";

    private static final String SETTINGS_CREATE =
		"create table settings (_id integer primary key autoincrement, "
                + "authkey text not null, last_updated timestamp default current_timestamp); ";
    
    private static final String SETTINGS_FILL = 
    	"insert into settings (authkey) values ('');";
    
    private static final String GROUPS_TRIGGER =
    	"CREATE TRIGGER update_group_trigger AFTER UPDATE ON groups "
    	+ "BEGIN " 
    	+ "UPDATE groups SET updated = DATETIME('NOW') "
  + "WHERE rowid = new.rowid; "
  + "END;";
    
    private static final String EXERCISES_TRIGGER =
    	"CREATE TRIGGER update_exercise_trigger AFTER UPDATE ON exercises "
    	+ "BEGIN " 
    	+ "UPDATE exercises SET updated = DATETIME('NOW') "
  + "WHERE rowid = new.rowid; "
  + "END;";
    
    private static final String SETS_TRIGGER =
    	"CREATE TRIGGER update_set_trigger AFTER UPDATE ON sets "
    	+ "BEGIN " 
    	+ "UPDATE sets SET updated = DATETIME('NOW') "
  + "WHERE rowid = new.rowid; "
  + "END;";
    
    private static final String SETS_CONNECTOR_TRIGGER =
    	"CREATE TRIGGER update_sets_connector_trigger AFTER UPDATE ON sets_connector "
    	+ "BEGIN " 
    	+ "UPDATE sets_connector SET updated = DATETIME('NOW') "
  + "WHERE rowid = new.rowid; "
  + "END;";
    
    private static final String LOG_TRIGGER =
    	"CREATE TRIGGER update_log_trigger AFTER UPDATE ON log "
    	+ "BEGIN " 
    	+ "UPDATE log SET updated = DATETIME('NOW') "
  + "WHERE rowid = new.rowid; "
  + "END;";
    
    
    private static final String DATABASE_NAME = "data17";
    public static final String DATABASE_GROUPS_TABLE = "groups";
    public static final String DATABASE_EXERCISES_TABLE = "exercises";
    public static final String DATABASE_SETS_TABLE = "sets";
    public static final String DATABASE_SETS_CONNECTOR_TABLE = "sets_connector";
    public static final String DATABASE_LOG_TABLE = "log";
    public static final String DATABASE_SETTINGS_TABLE = "settings";
    private static final int DATABASE_VERSION = 2;

    private final Context mCtx;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(GROUPS_CREATE);
            db.execSQL(EXERCISES_CREATE);
            db.execSQL(SETS_CREATE);
            db.execSQL(SETS_CONNECTOR_CREATE);
            db.execSQL(LOG_CREATE);
            db.execSQL(SETTINGS_CREATE);
            db.execSQL(SETTINGS_FILL);
            
            db.execSQL(GROUPS_TRIGGER);
            db.execSQL(EXERCISES_TRIGGER);            
            db.execSQL(SETS_TRIGGER);
            db.execSQL(SETS_CONNECTOR_TRIGGER);
            db.execSQL(LOG_TRIGGER);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
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
     * @param ctx the Context within which to work
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
     * @throws SQLException if the database could be neither opened or created
     */
    public ExcercisesDbAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }
    
    public void close() {
        mDbHelper.close();
    }

    //all items
    public long createItem(String table, HashMap<String, String> fields){
        ContentValues initialValues = new ContentValues();
        
		Set<Map.Entry<String, String>> set = fields.entrySet();

		for (Map.Entry<String, String> entry : set) {
			initialValues.put(entry.getKey(), entry.getValue());
		}
		
    	return mDb.insert(table, null, initialValues);
    }
    
    public boolean updateItem(String table, HashMap<String, String> fields, long rowId){
        ContentValues args = new ContentValues();
        
		Set<Map.Entry<String, String>> set = fields.entrySet();

		for (Map.Entry<String, String> entry : set) {
			args.put(entry.getKey(), entry.getValue());
		}
		
		return mDb.update(table, args, KEY_ROWID + "=" + rowId, null) > 0;
    }
    
    public Cursor fetchItemBySiteId(String table, String siteId) throws SQLException {

        Cursor mCursor =
                mDb.query(true, table, new String[] {KEY_ROWID}, KEY_SITEID + "=" + siteId, null,
                        null, null, null, null);
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
    	
        return mDb.update(DATABASE_GROUPS_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
    }

    public Cursor fetchAllGroups() {

        return mDb.query(DATABASE_GROUPS_TABLE, new String[] {KEY_ROWID, KEY_TITLE,
                KEY_DESC, KEY_SITEID}, KEY_DELETED + "=0", null, null, null, null);
    }

    public Cursor fetchGroup(long rowId, long siteId) throws SQLException {

    	// if site_id is present - fetch group using site_id 
    	String condition = KEY_ROWID + "=" + rowId;
    	if(siteId != 0){
    		condition = KEY_SITEID + "=" + siteId;
    	}
        Cursor mCursor =
                mDb.query(true, DATABASE_GROUPS_TABLE, new String[] {KEY_ROWID,
                        KEY_TITLE, KEY_DESC, KEY_SITEID}, condition, null,
                        null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }
    
    public Cursor fetchUpdatedGroups(String updated) {

        return mDb.query(DATABASE_GROUPS_TABLE, new String[] {KEY_ROWID, KEY_TITLE,
                KEY_DESC, KEY_SITEID, KEY_UPDATED, KEY_DELETED}, KEY_UPDATED + " >  ?", new String[]{updated}, null, null, null, null);
    }
    
    public Cursor fetchUpdatedItems(String table, String updated) {

        return mDb.query(table, null, KEY_UPDATED + " >  ?", new String[]{updated}, null, null, null, null);
    }

    public boolean updateGroup(long rowId, String title, String desc, long site_id) {
        ContentValues args = new ContentValues();
        args.put(KEY_TITLE, title);
        args.put(KEY_DESC, desc);
        args.put(KEY_SITEID, site_id);

        return mDb.update(DATABASE_GROUPS_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
    }
    // end of GROUPS
    
    // EXCERCISES methods
    public long createExcercise(String title, String desc, int type, long group_id, long site_id) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_TITLE, title);
        initialValues.put(KEY_DESC, desc);
        initialValues.put(KEY_GROUPID, group_id);
        initialValues.put(KEY_SITEID, site_id);
        initialValues.put(KEY_TYPE, type);

        return mDb.insert(DATABASE_EXERCISES_TABLE, null, initialValues);
    }

    public boolean deleteExcercise(long rowId) {

    	ContentValues args = new ContentValues();
    	args.put(KEY_DELETED, 1);
    	
        return mDb.update(DATABASE_EXERCISES_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
    }
    
    public boolean deleteExercisesForGroup(long groupId) {

    	ContentValues args = new ContentValues();
    	args.put(KEY_DELETED, 1);
    	
        return mDb.update(DATABASE_EXERCISES_TABLE, args, KEY_GROUPID + "=" + groupId, null) > 0;
    }

    public Cursor fetchExcercisesForGroup(long group_id) {

        return mDb.query(DATABASE_EXERCISES_TABLE, new String[] {KEY_ROWID, KEY_TITLE,
                KEY_DESC, KEY_TYPE, KEY_GROUPID, KEY_SITEID}, KEY_GROUPID + "=" + group_id + " AND " + KEY_DELETED + "=0", null, null, null, null, null);
    }

    public Cursor fetchExcercise(long rowId) throws SQLException {

        Cursor mCursor =

                mDb.query(true, DATABASE_EXERCISES_TABLE, new String[] {KEY_ROWID,
                        KEY_TITLE, KEY_DESC, KEY_TYPE, KEY_GROUPID, KEY_SITEID}, KEY_ROWID + "=" + rowId, null,
                        null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }
    
    public Cursor fetchUpdatedExercises(String updated) {

        return mDb.query(DATABASE_EXERCISES_TABLE, new String[] {KEY_ROWID, KEY_TITLE,
                KEY_DESC, KEY_TYPE, KEY_GROUPID, KEY_SITEID, KEY_UPDATED}, KEY_UPDATED + " >  ?", new String[]{updated}, null, null, null, null);
    }

    public boolean updateExcercise(long rowId, String title, String desc, int type, long group_id, long site_id) {
        ContentValues args = new ContentValues();
        args.put(KEY_TITLE, title);
        args.put(KEY_DESC, desc);
        args.put(KEY_GROUPID, group_id);
        args.put(KEY_SITEID, site_id);
        args.put(KEY_TYPE, type);
        args.put(KEY_UPDATED, "CURRENT_TIMESTAMP");

        return mDb.update(DATABASE_EXERCISES_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
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

    public long addExerciseToSet(long set_id, long exercise_id, long site_id) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_SETID, set_id);
        initialValues.put(KEY_EXERCISEID, exercise_id);
        initialValues.put(KEY_SITEID, site_id);

        return mDb.insert(DATABASE_SETS_CONNECTOR_TABLE, null, initialValues);
    }
    
    public boolean deleteSet(long rowId) {

    	ContentValues args = new ContentValues();
    	args.put(KEY_DELETED, 1);
    	
        return mDb.update(DATABASE_SETS_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
    }

    public Cursor fetchAllSets() {

        return mDb.query(DATABASE_SETS_TABLE, new String[] {KEY_ROWID, KEY_TITLE,
                KEY_DESC, KEY_SITEID},  KEY_DELETED + "=0", null, null, null, null);
    }

    public Cursor fetchSet(long rowId) throws SQLException {

        Cursor mCursor =

                mDb.query(true, DATABASE_SETS_TABLE, new String[] {KEY_ROWID,
                        KEY_TITLE, KEY_DESC, KEY_SITEID}, KEY_ROWID + "=" + rowId, null,
                        null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }
    
    public Cursor fetchUpdatedSets(String updated) {

        return mDb.query(DATABASE_SETS_TABLE, new String[] {KEY_ROWID, KEY_TITLE,
                KEY_DESC, KEY_SITEID, KEY_UPDATED}, KEY_UPDATED + " >  ?", new String[]{updated}, null, null, null, null);
    }

    public Cursor fetchExercisesForSet(long setId) throws SQLException {
        Cursor mCursor = mDb.rawQuery(
        	"SELECT " + DATABASE_SETS_CONNECTOR_TABLE + "." + KEY_ROWID + ", "+ DATABASE_EXERCISES_TABLE +
        	"." + KEY_TITLE + ", " + DATABASE_EXERCISES_TABLE + "." + KEY_DESC + " FROM "+ DATABASE_SETS_CONNECTOR_TABLE + 
        	", " + DATABASE_EXERCISES_TABLE + 
        	" WHERE " + DATABASE_EXERCISES_TABLE + 
        	"." + KEY_ROWID + " = "+DATABASE_SETS_CONNECTOR_TABLE+
        	"." + KEY_EXERCISEID + " AND " + DATABASE_SETS_CONNECTOR_TABLE +
        	"." + KEY_SETID + " = ?" + " AND " + DATABASE_SETS_CONNECTOR_TABLE +
        	"." + KEY_DELETED + " = 0", new String[]{String.valueOf(setId)});

    	if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }
    
    public boolean deleteExerciseFromSet(long rowId) {
    	
    	ContentValues args = new ContentValues();
    	args.put(KEY_DELETED, 1);
    	
        return mDb.update(DATABASE_SETS_CONNECTOR_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
    }
    
    public boolean updateSet(long rowId, String title, String desc, long site_id) {
        ContentValues args = new ContentValues();
        args.put(KEY_TITLE, title);
        args.put(KEY_DESC, desc);
        args.put(KEY_SITEID, site_id);

        return mDb.update(DATABASE_SETS_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
    }
    // end of SETS
    
    // LOG methods    
    public long createLogEntry(long exercise_id, float weight, int times, int program_id, int day) {
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
    	
        return mDb.update(DATABASE_LOG_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
    }

    public Cursor fetchLogEntry(long rowId) throws SQLException {

        Cursor mCursor =

                mDb.query(true, DATABASE_LOG_TABLE, new String[] {KEY_ROWID, KEY_EXERCISEID,
                		KEY_WEIGHT, KEY_TIMES, KEY_PROGRAMID,
                		KEY_DAY, KEY_DONE}, KEY_ROWID + "=" + rowId, null,
                        null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }
    
    public Cursor fetchLogEntriesForExercise(long exerciseId, String donestart, String doneend) throws SQLException {

        Cursor mCursor =
                mDb.query(true, DATABASE_LOG_TABLE, new String[] {KEY_ROWID, KEY_EXERCISEID,
                		KEY_WEIGHT, KEY_TIMES, KEY_PROGRAMID,
                		KEY_DAY, KEY_DONE}, KEY_EXERCISEID + "=" + exerciseId + " AND '" + donestart + "' < " + KEY_DONE + " AND " + KEY_DONE + " < '" + doneend + "' AND "
                		+ KEY_DELETED + "=0", null,
                        null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }
    
    public Cursor fetchExercisesForDates(String donestart, String doneend) throws SQLException {
        Cursor mCursor = mDb.rawQuery(
        	"SELECT " + DATABASE_LOG_TABLE + "." + KEY_ROWID + " AS _id, " + DATABASE_LOG_TABLE+
        	"." + KEY_EXERCISEID + " AS " + KEY_EXERCISEID + ", "+ DATABASE_EXERCISES_TABLE +
        	"." + KEY_TITLE + " AS "+ KEY_TITLE + " FROM "+ DATABASE_LOG_TABLE + 
        	", " + DATABASE_EXERCISES_TABLE + 
        	" WHERE " + DATABASE_EXERCISES_TABLE + 
        	"." + KEY_ROWID + " = "+DATABASE_LOG_TABLE+
        	"." + KEY_EXERCISEID + " AND '" + donestart + "' < " + KEY_DONE + " AND " + KEY_DONE + " < '" + doneend + "' AND " +
        	DATABASE_LOG_TABLE + "." + KEY_DELETED + "=0 GROUP BY " + KEY_EXERCISEID, null);

    	if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }

    public boolean updateLogEntry(long rowId, float weight, int times) {
        ContentValues args = new ContentValues();
        args.put(KEY_WEIGHT, weight);
        args.put(KEY_TIMES, times);
        
        return mDb.update(DATABASE_LOG_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
    }
    // end of LOG
    
    public String getLocalTime(){
    	
    	Cursor mCursor = mDb.rawQuery("SELECT strftime('%s','now') AS localtime", null);
   
    	if (mCursor != null) {
            mCursor.moveToFirst();
        }
    	
    	return mCursor.getString(mCursor.getColumnIndex("localtime"));
    
    }
    
    // settings
    
    public boolean setAuthKey(String authkey) {
        ContentValues args = new ContentValues();
        args.put(KEY_AUTHKEY, authkey);

        return mDb.update(DATABASE_SETTINGS_TABLE, args, KEY_ROWID + "=1", null) > 0;
    }
    
    public String getAuthKey() throws SQLException {

        Cursor mCursor =
                mDb.query(true, DATABASE_SETTINGS_TABLE, new String[] {KEY_AUTHKEY}, KEY_ROWID + "=1", null,
                        null, null, null, null);

        if (mCursor != null) {
            mCursor.moveToFirst();
        }
    	
        return mCursor.getString(mCursor.getColumnIndex(KEY_AUTHKEY));
    }
    
    public String getLastUpdated() throws SQLException {

        Cursor mCursor =
                mDb.query(true, DATABASE_SETTINGS_TABLE, new String[] {KEY_LASTUPDATED}, 
                		KEY_ROWID + "=1", null,
                        null, null, null, null);
    	if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor.getString(mCursor.getColumnIndex(KEY_LASTUPDATED));
    }
    
    public void setLastUpdated(){
    	mDb.execSQL("update " + DATABASE_SETTINGS_TABLE + " set " +
    					KEY_LASTUPDATED + "= DATETIME('NOW') WHERE _id=1"); 
    }
    

}
