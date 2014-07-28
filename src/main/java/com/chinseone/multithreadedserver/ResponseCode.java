package com.chinseone.multithreadedserver;

/**
 * Defines three response code: OK, Not Found, Not Implemented
 * @author Li Cheng
 *
 */

public enum ResponseCode {
	OK(200, "OK"),
	NOT_FOUND(404, "Not Found"),
	NOT_IMPLEMENTED(501, "Not Implemented");
	
	private final int code;
	private final String message;
	
	/**
	 * Constructor
	 */
	private ResponseCode(int code, String message) {
		this.code = code;
		this.message = message;
	}
	
	public String getMessage() {
		return message;
	}
}
