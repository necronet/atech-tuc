package com.atech.mpso;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.atech.mpso.database.DatabaseContract.Historico;

public class HistoricoActivity extends FragmentActivity implements
		LoaderCallbacks<Cursor> {

	private SimpleCursorAdapter adapter;
	private String tarjetaTUC, tarjetaSaldo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		adapter = new SimpleCursorAdapter(this, R.layout.historico_item, null,
				new String[] { Historico.Columns.SALDO },
				new int[] { R.id.textTarjetaSaldo }, 0);

		Intent intent = getIntent();
		if (intent != null) {
			tarjetaTUC = intent.getStringExtra("tarjetaTUC");
			tarjetaSaldo = intent.getStringExtra("tarjetaSaldo");	
		}

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
		Toast.makeText(this, "count "+ c.getCount(), Toast.LENGTH_LONG).show();
		adapter.swapCursor(c);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> c) {
		adapter.swapCursor(null);

	}

}
