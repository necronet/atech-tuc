package com.atech.mpso.database;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;


/**
 * Clase estara encargada del almacenamiento y consultas relacionadas a saldo
 * sera nuestro puente contra el DatabasHelper
 * */
public class TarjetaManager {
	
	private static TarjetaManager instance;
	private Context context;
	private DatabaseHelper helper;

	private TarjetaManager() {
		
	}

	public static TarjetaManager instance(Context context) {
		if (instance == null) {
			instance = new TarjetaManager();
		}
		
		if (instance.getContext() != context)//if context has change 
			instance.setContext(context);
		
		return instance;
	}
	
	public Context getContext(){
		return context;
	}
	
	public TarjetaManager setContext(Context context) {
		this.context = context;
		helper = new DatabaseHelper(context);
		return this;
	}

	public TarjetaManager save(String tarjeta, double saldo) {
		//Aqui otras validaciones como no consultar dos veces una tarjeta que fue consultada hace 5 min
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy hh:mm aa");
		
		SQLiteDatabase db = helper.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(TarjetaColumns.NUMERO, tarjeta);
		values.put(TarjetaColumns.ALIAS, tarjeta);//por defecto el alias es el # de la tarjeta
		values.put(TarjetaColumns.ULTIMA_REVISION, format.format(calendar.getTime()));
		values.put(TarjetaColumns.ULTIMO_SALDO, saldo);
		
		db.insert(Table.TARJETA, null, values);
		
		return this;
	}

}
