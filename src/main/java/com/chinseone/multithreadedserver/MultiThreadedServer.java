package com.chinseone.multithreadedserver;

import java.awt.Desktop;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

/**
 * Implements a multi-threaded Http 1.1 server
 * @author Li Cheng
 *
 */
public class MultiThreadedServer implements Runnable {
	
	protected int port;
	private final static String webroot = "webroot";
    private final static String indexPage = "index.html";
    private final static String notFoundPage = "404.html";
	private final static Logger log = Logger.getLogger(MultiThreadedServer.class);
	
	/**
	 * Constructor
	 * @param port
	 */
	public MultiThreadedServer(int port) {
		this.port = port;
	}
	
	/**
	 * args[0] accepts initial port number, where server starts
	 * Opens "http://localhost:<port>" in browser
	 * @param args
	 */
    public static void main( String[] args ) {
    	if(portIsValid(args[0])) {
    		
    		MultiThreadedServer server = new MultiThreadedServer(Integer.parseInt(args[0]));
    		initializeDefaultContent();
        	new Thread(server).start();
        	System.out.println("Server starts at port: " + args[0]);
        	
        	try {
        		if(Desktop.isDesktopSupported())
        			Desktop.getDesktop().browse(new URI("http://localhost:" + args[0]));
        		else
        			log.warn("Browser cannot be opened at this desktop!");
        	} catch (URISyntaxException e) {
        		log.error("Illegal uri:", e);
        	} catch (IOException e) {
                log.error("Exception occured while launching default browser: ", e);
            }
        	
    	} else {
    		System.out.println("Please use a valid port! Port ranges [1, 65535]");
    		System.exit(0);
    	}
    	
    	
    }

	public void run() {
		
		ExecutorService pool = Executors.newCachedThreadPool();
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            log.info("HTTPdLight is accepting requests on port: " + port);
            log.info("webroot: " + System.getProperty("user.dir") + File.separator + webroot);
            while (true) {
                Socket socket = serverSocket.accept();
                pool.submit(new Connection(socket, webroot, indexPage, notFoundPage));
            }
        } catch (IOException ex) {
            log.fatal("Exception occured while establishing server socket on port " + port + ", please select an available port: ", ex);
        }
        pool.shutdown();
	}
	
	/**
	 * Verifies if input is a valid port number
	 * @param port
	 * @return true if port is valid, otherwise returns false
	 */
	private static boolean portIsValid(String port) {
		
		try {
			int pt = Integer.parseInt(port);
			if(pt >=1 && pt <= 65535)
				return true;
			return false;
		} catch (NumberFormatException e) {
			return false;
		}
		
	}
	
	public static void initializeDefaultContent() {
        File root = new File(webroot);
        if (!root.exists()) {
            root.mkdir();
            log.info("Initialized default webroot directory: " + root.getAbsolutePath());
        }
        initializeFile(webroot + File.separator + indexPage, "<!DOCTYPE html><html><head><title>MultiThreaded Server</title></head><body>Hello There!</body></html>");
        initializeFile(webroot + File.separator + notFoundPage, "<!DOCTYPE html><html><head><title>404</title></head><body>404 - Page not found</body></html>");
    }

    /**
     * Initializes file if it doesn't exist.
     *
     * @param path    the file path.
     * @param content the file content.
     */
    private static void initializeFile(String path, String content) {
        File file = new File(path);
        if (!file.exists()) {
            try {
                if (content != null) {
                    FileWriter writer = new FileWriter(file);
                    writer.write(content);
                    writer.close();
                } else {
                    file.createNewFile();
                }
                log.info("Initialized default content: " + path);
            } catch (IOException ex) {
                log.error("Exception occured while initializing default file: " + path, ex);
            }
        }
    }
	
	
}
