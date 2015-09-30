package io.macu.demoappy.dbms;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import io.macu.demoappy.R;
import io.macu.demoappy.model.User;
import io.macu.demoappy.util.DatabaseUtil;
import io.macu.demoappy.util.ResUtil;
import timber.log.Timber;

public class DemoDB extends SQLiteOpenHelper {

	private static final String DBName = null; // null for in-memory
	private static final int DBSchemaVersion = 1;

	// Declare table and column names used in the current database schema.
	// Build queries using these constants.
	private static final String TBL_User = "users";
	private static final String COL_UserId = "id";
	private static final String COL_UserUsername = "username";

	private Context context;

	public DemoDB(@NonNull Context context) {
		super(context, DBName, null, DBSchemaVersion);
		this.context = context;
	}

	/**
	 * Called when the database is created for the first time.
	 *
	 * @param db The database.
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		Timber.d("Creating " + DBName + " database...");

		// Here you should execute a script containing CREATE statements to set up the database
		// at the CURRENT VERSION specified by DBSchemaVersion.
		// The schema version is stored automatically in the SQLite user_version pragma.
		String schemaScript = ResUtil.loadRawResourceAsString(context, R.raw.demo_schema_1);
		DatabaseUtil.executeSQL(db, schemaScript);
	}

	/**
	 * Called when the database needs to be upgraded. The implementation
	 * should use this method to drop tables, add tables, or do anything else it
	 * needs to upgrade to the new schema version.
	 * <p/><p>
	 * This method executes within a transaction.  If an exception is thrown, all changes
	 * will automatically be rolled back.
	 * </p>
	 *
	 * @param db         The database.
	 * @param oldVersion The old database version.
	 * @param newVersion The new database version.
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Timber.d("Upgrading " + DBName + " database from v" + oldVersion + " to v" + newVersion);
		for (int i = oldVersion; i < newVersion; i++) {
			switch (i) {
				case 1:
					// Database currently at schema 1.
					// Migrate from schema version 1 to schema version 2.
					// The schema version is stored in the SQLite user_version pragma,
					// and updated automatically after this method runs.
					String migrationScript = ResUtil.loadRawResourceAsString(context, R.raw.demo_migrate_1_to_2);
					DatabaseUtil.executeSQL(db, migrationScript);
				default:
					Timber.e("Unsupported " + DBSchemaVersion + " schema version: " + (i + 1));
			}
		}
	}

	@Override
	public synchronized void close() {
		Timber.d("close()");
		super.close();
		context = null;
	}

	/**
	 * @param user The user to persist in this database
	 * @return Whether the persist action was successful
	 */
	public boolean persist(User user) {
		SQLiteDatabase db = getWritableDatabase();

		// Prepare all initial values.
		ContentValues args = new ContentValues();
		args.put(COL_UserId, user.id);
		args.put(COL_UserUsername, user.username);

		// Update or insert row.
		if (hasUserForId(user.id)) {
			// db.update returns the number of rows affected.
			return db.update(TBL_User, args, COL_UserId + "=?", new String[]{user.id}) == 1;
		} else {
			// db.insert returns the new row ID or -1 on failure.
			return db.insert(TBL_User, null, args) >= 0;
		}
	}

	/**
	 * @param user The user to discard from this database
	 * @return Whether a record was actually discarded
	 */
	public boolean discard(User user) {
		SQLiteDatabase db = getWritableDatabase();

		// db.delete returns the number of rows affected.
		return db.delete(TBL_User, COL_UserId + "=?", new String[]{user.id}) == 1;
	}

	public void discardAllUsers() {
		getWritableDatabase().delete(TBL_User, null, null);
	}

	public int countUsers() {
		return DatabaseUtil.count(getReadableDatabase(), TBL_User);
	}

	public boolean hasUserForId(String id) {
		return DatabaseUtil.count(getReadableDatabase(), TBL_User, COL_UserId + "=?", id) > 0;
	}

	public User getUserById(String id) {
		List<User> users = getUsers(COL_UserId + "=?", id);
		if (users.isEmpty()) {
			return null;
		}
		return users.get(0);
	}

	public List<User> getUsers() {
		return getUsers(null);
	}

	public List<User> getUsers(String selection, String... selectionArgs) {
		Cursor c = getReadableDatabase().query(
				/*TABLE*/ TBL_User,
				/*COLUMNS*/ new String[]{COL_UserId, COL_UserUsername},
				/*SELECTION*/ selection,
				/*ARGS*/ selectionArgs,
				/*GROUP BY*/ null,
				/*HAVING*/ null,
				/*ORDER BY*/ null
		);
		c.moveToFirst();
		List<User> users = new ArrayList<>();
		while (!c.isAfterLast()) {
			users.add(new User(c.getString(0), c.getString(1)));
			c.moveToNext();
		}
		c.close();
		return users;
	}

}
