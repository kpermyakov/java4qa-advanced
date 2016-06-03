package com.db.edu.chat.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;

public class Server {
	private static final Logger logger = LoggerFactory.getLogger(Server.class);
	
	public static final String HOST = "127.0.0.1";
	public static final int PORT = 4458;
	
	public static volatile ServerSocket serverSocket;
	private Acceptor acceptor = new Acceptor();
	
	public void start() throws ServerError {
		try {
			serverSocket = new ServerSocket(PORT);
		} catch (IOException e) {
			throw new ServerError(e);
		}
		acceptor.getConnectionEventLoop().start();
	}
	
	public void stop() throws ServerError {
		acceptor.getConnectionEventLoop().interrupt();
		
		try { Thread.sleep(1000); } catch (InterruptedException ignored) { }
		
		try {
			serverSocket.close();
		} catch (IOException e) {
			throw new ServerError(e);
		}
	}
	
	public static void main(String... args) throws ServerError {
		new Server().start();
	}
}
