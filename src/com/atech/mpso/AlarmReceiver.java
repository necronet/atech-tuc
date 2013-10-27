package com.atech.mpso;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

import com.atech.mpso.database.TarjetaManager;
import com.atech.mpso.database.DatabaseContract.Tarjeta;

public class AlarmReceiver extends BroadcastReceiver implements
		ResponseCallback {

	
	private Context context;
	@Override
	public void onReceive(Context context, Intent intent) {
		this.context = context;
		Cursor cursor = context.getContentResolver().query(Tarjeta.CONTENT_URI,
				new String[] { Tarjeta.Columns.NUMERO }, null, null, null);

		MPesoCaller caller = new MPesoCaller(context);

		while (cursor.moveToNext()) {
			String tarjeta = cursor.getString(cursor
					.getColumnIndex(Tarjeta.Columns.NUMERO));
			caller.consultarSaldo(tarjeta, this);
		}
	}


	@Override
	public void response(String tarjetaTUC, String saldo) {
		Matcher matcher = Pattern.compile("C\\$\\s(\\d*\\.\\d*)")
				.matcher(saldo);

		if (matcher.find()) {
			TarjetaManager.instance(context).save(tarjetaTUC, Double.parseDouble(matcher.group(1)));
		}
	}

	@Override
	public void error(String message) {
		// TODO: marcarlo como un error que cool seria
	}

}