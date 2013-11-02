package com.atech.mpso;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.atech.mpso.database.DatabaseContract.Tarjeta;
import com.atech.mpso.database.TarjetaManager;

public class MPsoActivity extends FragmentActivity implements ResponseCallback, LoaderCallbacks<Cursor> {

	private EditText editText;
	private String tarjetaTUC;
	private SimpleCursorAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(R.string.title);
		setContentView(R.layout.activity_mpso);

		ListView list = (ListView) findViewById(R.id.list);

		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View v, int position, long id) {
				Intent intent = new Intent(MPsoActivity.this, HistoricoActivity.class);

				Cursor c = adapter.getCursor();
				c.moveToPosition(position);
				String tarjetaTUC = c.getString(c.getColumnIndex(Tarjeta.Columns.NUMERO));
				String tarjetaSaldo = c.getString(c.getColumnIndex(Tarjeta.Columns.ULTIMO_SALDO));

				intent.putExtra("tarjetaTUC", tarjetaTUC);
				intent.putExtra("tarjetaSaldo", tarjetaSaldo);
				startActivity(intent);
			}
		});
		adapter = new TarjetaAdapter(this, null);

		list.setAdapter(adapter);

		editText = (EditText) findViewById(R.id.editTUC);

		editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
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

		createAlarmReceiver();

	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.mpso, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch(item.getItemId()) {
		case R.id.action_settings:
			Intent intent = new Intent(this, PrefsActivity.class);
			
			startActivity(intent);
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}

	public static final int HOURLY = 0, TWO_HOURS = 1, FOUR_HOURS = 2, SIX_HOURS = 3, DAILY = 4, DEVEL = 5;

	private void createAlarmReceiver() {

		SharedPreferences preferences = getSharedPreferences("utuc", MODE_PRIVATE);

		int refreshRate = preferences.getInt("refresh_rate", BuildConfig.DEBUG ? DEVEL : HOURLY);

		long refreshTime = -1;
		
		Log.d("Debug", "Refresh rate " + refreshRate);

		long hour = 1000 * 60 * 60;

		switch (refreshRate) {
		case DEVEL:
			refreshTime = 1000 * 60 * 15;
		case HOURLY:
			refreshTime = hour;
			break;
		case TWO_HOURS:
			refreshTime = 2 * hour;
			break;
		case FOUR_HOURS:
			refreshTime = 4 * hour;
			break;
		case SIX_HOURS:
			refreshTime = 6 * hour;
			break;
		case DAILY:
			refreshTime = 24 * hour;
			break;

		}

		Intent intent = new Intent(this, AlarmReceiver.class);
		PendingIntent alarmIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

		AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		// Hopefully your alarm will have a lower frequency than this!
		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis(), refreshTime, alarmIntent);
	}

	public void onResume() {
		super.onResume();
		getSupportLoaderManager().restartLoader(0, null, this);
	}

	private void search() {
		tarjetaTUC = editText.getText().toString();
		if (valid(tarjetaTUC)) {
			new MPesoCaller(getBaseContext()).consultarSaldo(tarjetaTUC, this);
			editText.setText("");
		} else
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
	public void response(String tarjetaTUC, String saldo) {
		Matcher matcher = Pattern.compile("C\\$\\s(\\d*\\.\\d*)").matcher(saldo);

		if (matcher.find()) {
			TarjetaManager.instance(getBaseContext()).save(tarjetaTUC, Double.parseDouble(matcher.group(1)));

		}
	}

	@Override
	public void error(String message) {
		Toast.makeText(this, "saldo es " + message, Toast.LENGTH_LONG).show();
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle extras) {
		return new CursorLoader(this, Tarjeta.CONTENT_URI, null, null, null, Tarjeta.Columns._ID + " DESC");
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
