package uk.ac.rhul.cs.cl1.api.rest;

import com.sun.jersey.api.container.httpserver.HttpServerFactory;
import com.sun.net.httpserver.HttpServer;

/**
 * Simple standalone test web server for the REST API
 * @author tamas
 */
public class StandaloneWebServer {
	/**
	 * Starts a toy web server that provides the application
	 * @throws IOException 
	 * @throws IllegalArgumentException 
	 */
	public static void main(String[] args) throws Exception {
		HttpServer server = HttpServerFactory.create("http://localhost:8080/cl1/api");
		System.out.println("Starting server on port 8080...");
		server.start();
		while (true) {
			try {
				Thread.sleep(1000000);
			} catch (InterruptedException e) {
			}
		}
	}
}
