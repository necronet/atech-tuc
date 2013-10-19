package com.atech.mpso;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.atech.mpso.database.TarjetaManager;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MPsoActivity extends Activity implements ResponseCallback {

	private EditText editText;
	private String tarjetaTUC;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(R.string.title);
		setContentView(R.layout.activity_mpso);

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

	private void search() {
		tarjetaTUC = editText.getText().toString();
		if (valid(tarjetaTUC))
			new MPesoCaller(getBaseContext()).consultarSaldo(tarjetaTUC,
					MPsoActivity.this);
		else
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
		Matcher matcher = Pattern.compile("C\\$\\s(\\d*\\.\\d*)").matcher(saldo);
		
		if(matcher.find()) {
			TarjetaManager.instance(getBaseContext()).save(tarjetaTUC, Double.parseDouble(matcher.group(1)));
			
		}
	}

	@Override
	public void error(String message) {
		Toast.makeText(this, "saldo es " + message, Toast.LENGTH_LONG).show();
	}

}
