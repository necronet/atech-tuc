package com.atech.mpso.database;

import android.content.ContentResolver;
import android.content.UriMatcher;
import android.net.Uri;
import android.provider.BaseColumns;

public class DatabaseContract {
	
	public static final String AUTHORITY = "com.atech.mpeso";
	private static final Uri BASE_URI = Uri.parse("content://" + AUTHORITY);
	public static final UriMatcher URI_MATCHER = buildUriMatcher();

	private static UriMatcher buildUriMatcher() {
		final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
		final String authority = AUTHORITY;
		
		matcher.addURI(authority, Tarjeta.PATH, Tarjeta.PATH_TOKEN);
		matcher.addURI(authority, Tarjeta.PATH_FOR_ID,
				Tarjeta.PATH_FOR_ID_TOKEN);

		matcher.addURI(authority, Historico.PATH, Historico.PATH_TOKEN);
		matcher.addURI(authority, Historico.PATH_FOR_ID,
				Historico.PATH_FOR_ID_TOKEN);

		return matcher;
	}

	public static class Tarjeta {
		public static final int PATH_TOKEN = 100;
		public static final int PATH_FOR_ID_TOKEN = 101;
		public static final String NAME = "tarjetas";
		public static final String PATH = "tarjetas";

		public static final String PATH_FOR_ID = "tarjetas/*";
		
		public static final Uri CONTENT_URI = BASE_URI.buildUpon().appendPath(PATH).build();

		public static final String CONTENT_TYPE_DIR = ContentResolver.CURSOR_DIR_BASE_TYPE
				+ "/vnd.tarjetas.mpso";
		public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
				+ "/vnd.tarjetas.mpso";

		public interface Columns extends BaseColumns {
			String NUMERO = "numero";
			String ALIAS = "alias";
			String ULTIMO_SALDO = "ultimo_saldo";
			String ULTIMA_REVISION = "ultima_revision";
		}
	}
	
	public static class Historico {
		public static final int PATH_TOKEN = 200;
		public static final int PATH_FOR_ID_TOKEN = 201;
		public static final String NAME = "historicos";
		public static final String PATH = "historicos";

		public static final String PATH_FOR_ID = "historicos/*";

		public static final Uri CONTENT_URI = BASE_URI.buildUpon()
				.appendPath(PATH).build();

		public static final String CONTENT_TYPE_DIR = ContentResolver.CURSOR_DIR_BASE_TYPE
				+ "/vnd.historico.mpso";
		public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
				+ "/vnd.historico.mpso";

		public interface Columns extends BaseColumns {
			String NUMERO = "numero";
			String SALDO = "saldo";
			String REVISION = "revision";
		}
	}


}
