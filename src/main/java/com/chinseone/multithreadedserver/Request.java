package com.chinseone.multithreadedserver;

/**
 * Request object, which detects methods and request path
 * @author Li Cheng
 */

import java.util.HashMap;

public class Request {
	
	private String[] tokens;
	private HashMap<String, String> headers;
	
	public Request(String[] requestTokens, HashMap<String, String> headers) {
		this.tokens = requestTokens;
		this.headers = headers;
	}
	
	public String getMethod() {
		return tokens[0];
	}
	
	public String getPath() {
        return tokens[1];
    }

}
