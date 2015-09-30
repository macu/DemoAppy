package io.macu.demoappy.util;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DatabaseUtil {

	/**
	 * Executes all statements successively from the given script.
	 */
	public static void executeSQL(SQLiteDatabase db, String script) {
		if (db != null && script != null) {
			// execSQL can only handle one statement at a time.
			String[] statements = script.split(";\n");
			for (String statement : statements) {
				if (!statement.trim().isEmpty()) {
					db.execSQL(statement);
				}
			}
		}
	}

	public static int count(SQLiteDatabase db, String tableName) {
		Cursor result = db.query(tableName, new String[]{"COUNT(*)"}, null, null, null, null, null);
		result.moveToFirst();
		int count = result.getInt(0);
		result.close();
		return count;
	}

	public static int count(SQLiteDatabase db, String tableName, String selection, String... selectionArgs) {
		Cursor result = db.query(tableName, new String[]{"COUNT(*)"}, selection, selectionArgs, null, null, null);
		result.moveToFirst();
		int count = result.getInt(0);
		result.close();
		return count;
	}

	public static int countDistinct(SQLiteDatabase db, String tableName, String columnName) {
		Cursor result = db.query(tableName, new String[]{"COUNT(DISTINCT " + columnName + ")"}, null, null, null, null, null);
		result.moveToFirst();
		int count = result.getInt(0);
		result.close();
		return count;
	}

	public static int countDistinct(SQLiteDatabase db, String tableName, String columnName, String selection, String... selectionArgs) {
		Cursor result = db.query(tableName, new String[]{"COUNT(DISTINCT " + columnName + ")"}, selection, selectionArgs, null, null, null);
		result.moveToFirst();
		int count = result.getInt(0);
		result.close();
		return count;
	}

}
