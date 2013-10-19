package com.atech.mpso;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.atech.mpso.database.DatabaseContract.Tarjeta;

public class TarjetaAdapter extends SimpleCursorAdapter {

	public TarjetaAdapter(Context context, Cursor c) {
		super(context, R.layout.tarjeta_item, c, new String[] {
				Tarjeta.Columns.ALIAS, Tarjeta.Columns.ULTIMO_SALDO },
				new int[] { R.id.textTarjetaAlias, R.id.textTarjetaSaldo }, 0);

		setViewBinder(new ViewBinder() {

			@Override
			public boolean setViewValue(View v, Cursor c, int columnIndex) {
				if (v.getId() == R.id.textTarjetaSaldo) {
					String saldo = c.getString(c
							.getColumnIndex(Tarjeta.Columns.ULTIMO_SALDO));
					saldo = String.format("C$ %s", saldo);
					((TextView) v).setText(saldo);
					return true;
				}
				return false;
			}
		});
	}
	
	@Override
	public View getView(int position, View v, ViewGroup container) {
		View view = super.getView(position, v, container);
		
		Cursor cursor = getCursor();
		cursor.moveToPosition(position);
		String numero = cursor.getString(cursor.getColumnIndex(Tarjeta.Columns.NUMERO));
		String alias = cursor.getString(cursor.getColumnIndex(Tarjeta.Columns.ALIAS));
		
		TextView textTarjetaNumber = (TextView)view.findViewById(R.id.textTarjetaNumber);
		if (!numero.equals(alias)) {
			textTarjetaNumber.setText(numero);
			textTarjetaNumber.setVisibility(View.VISIBLE);
		} else {
			textTarjetaNumber.setVisibility(View.GONE);
		}
			
		
		
		return view;
	}

}
