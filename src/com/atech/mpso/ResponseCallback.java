package com.atech.mpso;

public interface ResponseCallback {

	public void response(String tarjetaTUC, String saldo);
	public void error(String message);
	
}
