package com.atech.mpso;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class DatabaseHelper extends SQLiteOpenHelper{

	private static final String NAME = "MPeso";
	private static final int VERSION = 1;
	
	public DatabaseHelper(Context context) {
		super(context, NAME, null, VERSION);
	}

	public static final String CREATE_TARJETA_TABLE = 
			String.format ("create table %s (%s int auto_increment primary key, "
			+ "%s text not null, %s text not null, %s text not null, "
			+ "%s text not null, unique(%s) ON CONFLICT REPLACE )",
			Table.TARJETA, TarjetaColumns._ID,
			TarjetaColumns.NUMERO,
			TarjetaColumns.ALIAS,
			TarjetaColumns.ULTIMO_SALDO,
			TarjetaColumns.ULTIMA_REVISION,
			TarjetaColumns.NUMERO);
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_TARJETA_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		//no updates yet to the db		
	}
	
	public interface Table { 
		String TARJETA = "tarjeta";
	}
	
	public interface TarjetaColumns extends BaseColumns {
		String NUMERO = "numero";
		String ALIAS = "alias";
		String ULTIMO_SALDO = "ultimo_saldo";
		String ULTIMA_REVISION = "ultima_revision";
	}
	

}
