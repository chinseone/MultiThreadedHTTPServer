package com.chinseone.multithreadedserver;

/**
 * Response object, which generates response headers and body
 * @author Li Cheng
 */

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Response {
	private Request request;
	private OutputStream outputStream;
    private PrintStream printStream;
   
    
    public Response(Request request, OutputStream outputStream) {
    	this.request = request;
    	this.outputStream = outputStream;
    	printStream = new PrintStream(new BufferedOutputStream(outputStream), true);
    }
    
    public void sendHeader(String status, File file) {
        long length = 0;
        String contentType = null;
        SimpleDateFormat httpDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");
        StringBuilder headers = new StringBuilder();
        String CRLF = new String("\r\n");
        if (file != null) {
            if (file.exists()) {
                length = file.length();
                contentType = getContentType(file);
            }
        }
        headers.append("HTTP/1.1 ").append(status).append(CRLF);
        headers.append("Server: Multi-Threaded Server").append(CRLF);
        headers.append("Allow: GET, HEAD").append(CRLF);
        headers.append("Connection: close").append(CRLF);
        headers.append("Content-Length: ").append(length).append(CRLF);
        if (contentType != null) {
            headers.append("Content-Type: ").append(contentType).append(CRLF);
        }
        headers.append("Date: ").append(httpDateFormat.format(new Date())).append(CRLF);
        printStream.println(headers);
    }
    
    public void sendBody(File file) throws IOException {
        InputStream fStream = new FileInputStream(file);
        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = fStream.read(buffer)) != -1) {
        	printStream.write(buffer, 0, bytesRead);
        }
        fStream.close();
    }
    
    public String getContentType(File file) {
        String fileName = file.getName();
        String contentType;
        if (fileName.endsWith(".htm") || fileName.endsWith(".html"))
            contentType = "text/html";
        else if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg"))
            contentType = "image/jpeg";
        else if (fileName.endsWith(".png"))
            contentType = "image/png";
        else if (fileName.endsWith(".gif"))
            contentType = "image/gif";
        else if (fileName.endsWith(".pdf"))
            contentType = "application/pdf";
        else if (fileName.endsWith(".ico"))
            contentType = "image/x-icon";
        else
            contentType = "text/plain";
        return contentType;
    }
}
