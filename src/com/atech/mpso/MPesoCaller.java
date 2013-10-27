package com.atech.mpso;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.ContextWrapper;

import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class MPesoCaller extends ContextWrapper {

	public MPesoCaller(Context base) {
		super(base);
	}

	private String url = "http://190.124.38.36/datos/consulta.php";

	public void consultarSaldo(final String tarjetaTUC, final ResponseCallback callback) {

		RequestQueue queue = Volley.newRequestQueue(this);

		StringRequest request = new StringRequest(Method.POST, url,
				new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						JSONObject object;
						try {
							object = new JSONObject(response);
							callback.response(tarjetaTUC,object.getString("Mensaje"));
						} catch (JSONException e) {
							e.printStackTrace();
						}
						
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						callback.error(error.getMessage());
					}
				}) {

			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> params = new HashMap<String, String>();
				params.put("_funcion", "1");
				// 00442039
				params.put("_terminal", tarjetaTUC);
				return params;
			}
		};

		queue.add(request);
	}
}
