package com.atech.mpso;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter.ViewBinder;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.atech.mpso.database.DatabaseContract.Tarjeta;
import com.atech.mpso.database.TarjetaManager;

public class MPsoActivity extends FragmentActivity implements ResponseCallback,
		LoaderCallbacks<Cursor> {

	private EditText editText;
	private String tarjetaTUC;
	private SimpleCursorAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(R.string.title);
		setContentView(R.layout.activity_mpso);

		ListView list = (ListView)findViewById(R.id.list);
		adapter = new SimpleCursorAdapter(this, R.layout.tarjeta_item, null,
				new String[] {Tarjeta.Columns.ALIAS, Tarjeta.Columns.ULTIMO_SALDO}, 
				new int[] {R.id.textTarjetaAlias,R.id.textTarjetaSaldo}, 0);
		
		adapter.setViewBinder(new ViewBinder() {
			
			@Override
			public boolean setViewValue(View v, Cursor c, int columnIndex) {
				if (v.getId() == R.id.textTarjetaSaldo) {
					String saldo = c.getString(c.getColumnIndex(Tarjeta.Columns.ULTIMO_SALDO));
					saldo = String.format("C$ %s", saldo);
					((TextView)v).setText(saldo);
					return true;
				}
				return false;
			}
		});
		
		list.setAdapter(adapter);

		editText = (EditText) findViewById(R.id.editTUC);

		editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					search();
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
					return true;
				}
				return false;
			}
		});

		findViewById(R.id.search).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				search();
			}
		});

		if (BuildConfig.DEBUG)
			editText.setText("00442039");
		
		
	}
	
	public void onResume() {
		super.onResume();
		getSupportLoaderManager().restartLoader(0, null, this);
	}

	private void search() {
		tarjetaTUC = editText.getText().toString();
		if (valid(tarjetaTUC)) {
			new MPesoCaller(getBaseContext()).consultarSaldo(tarjetaTUC,
					MPsoActivity.this);
			editText.setText("");
		}else
			editText.setError(getString(R.string.validation_error));
	}

	private boolean valid(String tarjetaTUC) {

		try {
			Integer.parseInt(tarjetaTUC);
		} catch (NumberFormatException ex) {
			return false;
		}

		return tarjetaTUC.length() == 8;
	}

	@Override
	public void response(String saldo) {
		Matcher matcher = Pattern.compile("C\\$\\s(\\d*\\.\\d*)")
				.matcher(saldo);

		if (matcher.find()) {
			TarjetaManager.instance(getBaseContext()).save(tarjetaTUC,
					Double.parseDouble(matcher.group(1)));

		}
	}

	@Override
	public void error(String message) {
		Toast.makeText(this, "saldo es " + message, Toast.LENGTH_LONG).show();
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle extras) {
		return new CursorLoader(this, Tarjeta.CONTENT_URI, null, null, null,
				null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		adapter.swapCursor(cursor);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> cursor) {
		adapter.swapCursor(null);
	}

}
