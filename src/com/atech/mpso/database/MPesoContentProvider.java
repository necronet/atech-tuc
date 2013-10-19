package com.atech.mpso.database;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;
import android.util.SparseArray;

import com.atech.mpso.database.DatabaseContract.Historico;
import com.atech.mpso.database.DatabaseContract.Tarjeta;

public class MPesoContentProvider extends ContentProvider {
	
	
	private DatabaseHelper databaseHelper;
	private SQLiteDatabase database;
	
	private static SparseArray<String> tableMap = new SparseArray<String>();
    static { 
            tableMap.put(Tarjeta.PATH_FOR_ID_TOKEN, Tarjeta.NAME);
            tableMap.put(Tarjeta.PATH_TOKEN, Tarjeta.NAME);
            tableMap.put(Historico.PATH_FOR_ID_TOKEN, Historico.NAME);
            tableMap.put(Historico.PATH_TOKEN, Historico.NAME);       
    }
    

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int match = match(uri);
		String table = tableMap.get(match);

		int rows = 0;

		if (TextUtils.isEmpty(selection))
			selection = "";

		selection = selection + BaseColumns._ID + " = "
				+ uri.getLastPathSegment();

		rows = database.delete(table, selection, selectionArgs);
		getContext().getContentResolver().notifyChange(uri, null);
		
		return rows;
	}

	@Override
	public String getType(Uri uri) {
		
		switch (match(uri)) {
		case Tarjeta.PATH_TOKEN:
			return Tarjeta.CONTENT_TYPE_DIR;
		case Tarjeta.PATH_FOR_ID_TOKEN:
			return Tarjeta.CONTENT_ITEM_TYPE;
		case Historico.PATH_TOKEN:
			return Historico.CONTENT_TYPE_DIR;
		case Historico.PATH_FOR_ID_TOKEN:
			return Historico.CONTENT_ITEM_TYPE;
		
		default:
			throw new UnsupportedOperationException("URI " + uri
					+ " is not supported.");
		}

	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		int match = match(uri);
		
		database = databaseHelper.getWritableDatabase();

		String tableName = tableMap.get(match);

		long id = database.insert(tableName, null, values);

		if (id > 0) {
			getContext().getContentResolver().notifyChange(uri, null);
			return uri.buildUpon().appendPath(String.valueOf(id)).build();
		}

		return null;
	}

	@Override
	public boolean onCreate() {
		databaseHelper = new DatabaseHelper(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		database = databaseHelper.getWritableDatabase();
		int match = match(uri);
		
		String table = tableMap.get(match);


		if (table == null) // no match
			throw new UnsupportedOperationException(String.format(
					"Uri %s is not supported", uri));

		boolean item_type = true;
		try {
			Integer.parseInt(uri.getLastPathSegment());
		} catch (NumberFormatException ex) {
			item_type = false;
		}

		selection = TextUtils.isEmpty(selection) ? "" : selection;

		if (item_type) {
			selection = selection + BaseColumns._ID + " = "
					+ uri.getLastPathSegment();
		}
		

		if (!TextUtils.isEmpty(table) && database != null) {
			Cursor cursor = database.query(table, projection, selection,
					selectionArgs, null, null, sortOrder, null);
			
			cursor.setNotificationUri(getContext().getContentResolver(), uri);
			return cursor;
		}

		return null;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {

		int match = match(uri);
		String table = tableMap.get(match);

		int rows = 0;

		if (table == null)
			throw new UnsupportedOperationException(String.format(
					"Uri: %s is not supported", uri));

		
		if (TextUtils.isEmpty(selection))
			selection = "";

		selection = selection + BaseColumns._ID + " = "
				+ uri.getLastPathSegment();

		rows = database.update(table, values, selection, selectionArgs);
		getContext().getContentResolver().notifyChange(uri, null);

		return rows;
	}

	private int match(Uri uri) {
		return DatabaseContract.URI_MATCHER.match(uri);
	}

}
