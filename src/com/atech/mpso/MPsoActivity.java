package com.atech.mpso;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

public class MPsoActivity extends Activity implements ResponseCallback{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mpso);
		
		final EditText editText = (EditText)findViewById(R.id.editTUC);
		
		editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
	        @Override
	        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
	            if (actionId == EditorInfo.IME_ACTION_DONE) {
	            		
	            		String tarjetaTUC = editText.getText().toString();
	            		if (valid(tarjetaTUC))
	            			new MPesoCaller(getBaseContext()).consultarSaldo(tarjetaTUC, MPsoActivity.this);
	            	
	                return true;
	            }
	            return false;
	        }
	    });
		
		if (BuildConfig.DEBUG) 
			editText.setText("00442039");
	}
	
	private boolean valid(String tarjetaTUC) {
		
		try {
			Integer.parseInt(tarjetaTUC);
		} catch(NumberFormatException ex) {
			return false;
		}
		
		return tarjetaTUC.length() == 8;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.mpso, menu);
		return true;
	}

	@Override
	public void response(String saldo) {
		// TODO Auto-generated method stub
		
	}

}
