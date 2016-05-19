package com.db.edu.chat.server;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.net.Socket;

import static com.db.edu.chat.server.TestUtils.sleep;
import static org.junit.Assert.assertEquals;


public class ChatServerTest {
	private Server testServer;
	private Socket socket1, socket2, socket3;
	private BufferedWriter socketWriter1, socketWriter2, socketWriter3;
	private BufferedReader socketReader1, socketReader2, socketReader3;

	@Before
	public void setUp() throws ServerError, IOException {
		testServer = new Server();
		testServer.start();
		sleep(500);
		
		socket1 = new Socket(Server.HOST, Server.PORT);
		socketWriter1 = new BufferedWriter(new OutputStreamWriter(socket1.getOutputStream()));
		socketReader1 = new BufferedReader(new InputStreamReader(socket1.getInputStream()));
		
		socket2 = new Socket(Server.HOST, Server.PORT);
		socketWriter2 = new BufferedWriter(new OutputStreamWriter(socket2.getOutputStream()));
		socketReader2 = new BufferedReader(new InputStreamReader(socket2.getInputStream()));
		
		socket3 = new Socket(Server.HOST, Server.PORT);
		socketWriter3 = new BufferedWriter(new OutputStreamWriter(socket3.getOutputStream()));
		socketReader3 = new BufferedReader(new InputStreamReader(socket3.getInputStream()));
		
		sleep(300);
	}

	@After
	public void tearDown() throws ServerError, IOException {
		socketReader1.close();
		socketWriter1.close();
		socket1.close();
		
		socketReader2.close();
		socketWriter2.close();
		socket2.close();
		
		socketReader3.close();
		socketWriter3.close();
		socket3.close();

		testServer.stop();
	}
	
	
	@Test(timeout=3000)
	public void shouldEchoMessageToSecondClient() throws IOException, ServerError {
		socketWrite(socketWriter1, "test");
		assertEquals("test", socketReader2.readLine());
	}
	
	@Test(timeout=3000) 
	public void shouldEchoMessageToSecondAndThirdClient() throws IOException, ServerError {
		socketWrite(socketWriter1, "test");
		assertEquals("test", socketReader2.readLine());
		assertEquals("test", socketReader3.readLine());
	}
	
	
	private void socketWrite(BufferedWriter socketWriter, String text) throws IOException {
		socketWriter.write(text);
		socketWriter.newLine();
		socketWriter.flush();
	}
}
