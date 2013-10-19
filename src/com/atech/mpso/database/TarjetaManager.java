package com.atech.mpso.database;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.content.ContentValues;
import android.content.Context;

import com.atech.mpso.database.DatabaseContract.Historico;
import com.atech.mpso.database.DatabaseContract.Tarjeta;


/**
 * Clase estara encargada del almacenamiento y consultas relacionadas a saldo
 * sera nuestro puente contra el DatabasHelper
 * */
public class TarjetaManager {
	
	private static TarjetaManager instance;
	private Context context;
	
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
		return this;
	}

	public TarjetaManager save(String tarjeta, double saldo) {
		//Aqui otras validaciones como no consultar dos veces una tarjeta que fue consultada hace 5 min
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy hh:mm aa");
		
		ContentValues values = new ContentValues();
		values.put(Tarjeta.Columns.NUMERO, tarjeta);
		values.put(Tarjeta.Columns.ALIAS, tarjeta);//por defecto el alias es el # de la tarjeta
		values.put(Tarjeta.Columns.ULTIMA_REVISION, format.format(calendar.getTime()));
		values.put(Tarjeta.Columns.ULTIMO_SALDO, saldo);
		
		context.getContentResolver().insert(Tarjeta.CONTENT_URI, values);
		
		values = new ContentValues();
		values.put(Historico.Columns.NUMERO, tarjeta);
		values.put(Historico.Columns.REVISION, format.format(calendar.getTime()));
		values.put(Historico.Columns.SALDO, saldo);
		context.getContentResolver().insert(Historico.CONTENT_URI, values);
		
		return this;
	}

}
