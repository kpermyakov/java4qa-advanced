package com.db.edu.chat.server;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;

import static org.junit.Assume.assumeNotNull;

public class JUnitLoadTest {
    private static final Logger logger = LoggerFactory.getLogger(JUnitLoadTest.class);

    private IOException gotException = null;

    @Ignore
    @Test(timeout = 1000)
    public void shouldGetMessageBackWhenSendMessage() throws IOException, InterruptedException {
        final String sentMessage = Thread.currentThread().getName() + ";seed:" + Math.random();
        logger.debug("Sending message: " + sentMessage);

        Socket readerClientSocket = null;
        try {
            readerClientSocket = new Socket(Server.HOST, Server.PORT);
        } catch (IOException e) {
            logger.error("Can't connect to server: ", e);
        }
        assumeNotNull(readerClientSocket);

        final BufferedReader readerClientSocketReader
                = new BufferedReader(new InputStreamReader(readerClientSocket.getInputStream()));

        Thread readerClient = new Thread(() -> {
            try {
                String gotMessage;

                do {
                    gotMessage = readerClientSocketReader.readLine();
                    logger.debug("Got msg: " + gotMessage);
                } while (!sentMessage.equals(gotMessage));

            } catch (IOException e) {
                gotException = e;
            }
        });
        readerClient.start();

        final Socket writerClientSocket = new Socket(Server.HOST, Server.PORT);
        final BufferedWriter writerClientSocketWriter = new BufferedWriter(new OutputStreamWriter(writerClientSocket.getOutputStream()));
        socketWrite(writerClientSocketWriter, sentMessage);

        readerClient.join();
        if(gotException != null) throw gotException;
    }


    private static void sleep(int seconds) {
        try { Thread.sleep(1000*seconds); } catch (InterruptedException e) { }
    }

    private static void socketWrite(BufferedWriter socketWriter, String text) throws IOException {
        socketWriter.write(text);
        socketWriter.newLine();
        socketWriter.flush();
    }
}
