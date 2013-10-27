package com.atech.mpso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter.ViewBinder;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.atech.mpso.database.DatabaseContract.Historico;

public class HistoricoActivity extends FragmentActivity implements
		LoaderCallbacks<Cursor> {

	private SimpleCursorAdapter adapter;
	private String tarjetaTUC, tarjetaSaldo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		adapter = new SimpleCursorAdapter(this, R.layout.historico_item, null,
				new String[] { Historico.Columns.SALDO, Historico.Columns.REVISION },
				new int[] { R.id.textTarjetaSaldo, R.id.textHour}, 0);
		
		adapter.setViewBinder(new ViewBinder() {

			@Override
			public boolean setViewValue(View v, Cursor c, int pos) {
				
				
				if (v.getId() == R.id.textTarjetaSaldo) {
					String tarjetaSaldo = c.getString(c.getColumnIndex(Historico.Columns.SALDO));
					TextView textTarjetaSaldo = (TextView)v;
					textTarjetaSaldo.setText(String.format("C$ %s", tarjetaSaldo));
					
					return true;
				} 
				
				if (v.getId() == R.id.textHour) {
					String hora = c.getString(c.getColumnIndex(Historico.Columns.REVISION));
					
					try {
						SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy hh:mm aa");
						Date dateRevision = format.parse(hora);
						
						format = new SimpleDateFormat("hh:mm aa");
						
						TextView textHora = (TextView)v;
						textHora.setText(format.format(dateRevision));
					} catch (ParseException e) {
						e.printStackTrace();
					}
					
					
					
					return true;
				}
				
				return false;
			}
			
		});

		Intent intent = getIntent();
		if (intent != null) {
			tarjetaTUC = intent.getStringExtra("tarjetaTUC");
			tarjetaSaldo = intent.getStringExtra("tarjetaSaldo");	
		}
		
		setTitle(getString(R.string.historico_title, tarjetaTUC));

		setContentView(R.layout.historico_activity);
		
		((TextView)findViewById(R.id.textTarjetaNumber)).setText(tarjetaTUC);
		((TextView)findViewById(R.id.textTarjetaAlias)).setText(tarjetaTUC);
		((TextView)findViewById(R.id.textTarjetaSaldo)).setText(tarjetaSaldo);
		
		ListView list = (ListView)findViewById(R.id.list);
		list.setAdapter(adapter);
	}

	@Override
	protected void onResume() {
		super.onResume();
		getSupportLoaderManager().restartLoader(1, null, this);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle extras) {
		return new CursorLoader(this, Historico.CONTENT_URI, null,
				Historico.Columns.NUMERO + "=?", new String[] { tarjetaTUC },
				null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor c) {
		adapter.swapCursor(c);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> c) {
		adapter.swapCursor(null);

	}

}
