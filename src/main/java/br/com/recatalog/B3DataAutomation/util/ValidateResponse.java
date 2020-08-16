package br.com.recatalog.B3DataAutomation.util;

public class ValidateResponse {
	
	Object[] response;
	
	public ValidateResponse(String expected, String actual) {
		response = new Object[3];
		response[0] = false;
		response[1] = expected;
		response[2] = actual;
	}
	
	public boolean validate() {
		return (Boolean) response[0];
	}
	
	public String getExpected() {
		return (String) response[1];
	}
	
	public String getActual() {
		return (String) response[2];
	}
}
