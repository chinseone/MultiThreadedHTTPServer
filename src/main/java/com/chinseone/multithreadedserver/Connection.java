package com.chinseone.multithreadedserver;

/**
 * Connection Handler for Server
 * 
 * @author Li Cheng
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;

import org.apache.log4j.Logger;


public class Connection implements Runnable{
	
	private BufferedReader input;
    private OutputStream output;
	
	private Socket clientSocket;
	
	private String root;
    private String indexPage;
    private String notFoundPage;
	
	private final static Logger log = Logger.getLogger(Connection.class);
	
	/**
	 * Constructor
	 * @param clientSocket
	 * @param root
	 * @param index
	 * @param notFound
	 */
	public Connection(Socket clientSocket, String root, String index, String notFound) {
		this.clientSocket = clientSocket;
		this.root = root;
        this.indexPage = index;
        this.notFoundPage = notFound;
	}

	/**
	 * Process request and send response
	 */
	public void run() {
		
		try {
			input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            output = clientSocket.getOutputStream();
		} catch(IOException e) {
			log.error("Exception occured when establishing input and output stream.");
		}
		
		try {
			sendResponse(processRequest());
		} catch (Exception e) {
			log.error("Exception occured when processing request.");
		}
		
		try {
			input.close();
			output.close();
			clientSocket.close();
		} catch (Exception e) {
			log.error("Exception occured when closing input/output stream and socket.");
		}
		
	}
	
	/**
	 * Parse request and generate a Request object
	 * @return
	 * @throws IOException
	 */
	private Request processRequest() throws IOException {
		String[] requestToken = input.readLine().split("\\s+");
		String headerLine = null;
		HashMap<String, String> headers = new HashMap<String, String>();
		while((headerLine = input.readLine())!=null) {
			
			if(!headerLine.isEmpty()){
				String key = headerLine.split(":\\s+")[0];
				String value = headerLine.split(":\\s+")[1];
				headers.put(key, value);
			} else break;
			
		}
		
		Request request = new Request(requestToken, headers);
		
		return request;
		
	}
	
	/**
	 * GET and HEAD are supported
	 * @param request
	 * @throws IOException
	 */
	private void sendResponse(Request request) throws IOException {
		Response response = new Response(request, output);
		String filePath = initializePath(request.getPath());
		File file = new File(root + File.separator + filePath);
		String method = request.getMethod();
		if(method.equalsIgnoreCase("GET")) {
			if(file.exists()) {
				response.sendHeader(ResponseCode.OK.getMessage(), file);
				response.sendBody(file);
			}
			else {
				file = new File(root + File.separator + notFoundPage);
				response.sendHeader(ResponseCode.NOT_FOUND.getMessage(), file);
				response.sendBody(file);
			}
		}
		else if(method.equalsIgnoreCase("HEAD")) {
			if (file.exists()) {
                response.sendHeader(ResponseCode.OK.getMessage(), file);
                log.debug("HEAD Request Processed: " + ResponseCode.OK.getMessage());
            } else {
                file = new File(root + File.separator + notFoundPage);
                response.sendHeader(ResponseCode.NOT_FOUND.getMessage(), file);
                log.debug("HEAD Request Processed: " + ResponseCode.NOT_FOUND.getMessage());
            }
		}
		else {
			response.sendHeader(ResponseCode.NOT_IMPLEMENTED.getMessage(), null);
            log.debug("Request Processed: " + ResponseCode.NOT_IMPLEMENTED.getMessage());
		}
		
	}
	
	/**
	 * Initialize request page to prepared files, take .. into account to prevent traversing through local machine
	 * @param path
	 * @return processed path
	 */
	private String initializePath(String path) {
        log.debug("Original Path: " + path);
        if (path.equals("/") || path == null || path.contains("..")) {
            path = indexPage;
        }
        path.replace('/', File.separator.charAt(0));
        log.debug("Prepared Path: " + path);
        return path;
    }

}
