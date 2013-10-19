package com.atech.mpso.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.atech.mpso.database.DatabaseContract.Historico;
import com.atech.mpso.database.DatabaseContract.Tarjeta;

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
			Tarjeta.NAME, Tarjeta.Columns._ID,
			Tarjeta.Columns.NUMERO,
			Tarjeta.Columns.ALIAS,
			Tarjeta.Columns.ULTIMO_SALDO,
			Tarjeta.Columns.ULTIMA_REVISION,
			Tarjeta.Columns.NUMERO);
	
	public static final String CREATE_HISTORICO_TABLE = 
			String.format ("create table %s (%s int auto_increment primary key, "
			+ "%s text not null, %s text not null, %s text not null)",
			Historico.NAME, Historico.Columns._ID,
			Historico.Columns.NUMERO,
			Historico.Columns.SALDO,
			Historico.Columns.REVISION);
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_TARJETA_TABLE);
		db.execSQL(CREATE_HISTORICO_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		//no updates yet to the db		
	}
	
	

}
