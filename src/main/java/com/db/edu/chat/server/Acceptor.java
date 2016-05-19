package com.db.edu.chat.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.Collection;

/**
 * Created by Student on 19.05.2016.
 */
public class Acceptor {

    private static final Logger logger = LoggerFactory.getLogger(Acceptor.class);
    private final Collection<Socket> clientsSockets = new java.util.concurrent.CopyOnWriteArrayList<>();
    private Thread connectionEventLoop = new Thread() {
        @Override
        public void run() {
            while(!isInterrupted()) {
                try {
                    Socket clientSocket = Server.serverSocket.accept();
                    logger.info("Client connected: " + clientSocket.getInetAddress() + ":" + clientSocket.getPort());

                    clientsSockets.add(clientSocket);

                    Thread clientConnectionHandler = new Thread(new ClientConnectionHandler(clientSocket, clientsSockets));
                    clientConnectionHandler.setDaemon(true);
                    clientConnectionHandler.start();
                } catch (SocketException e) {
                    logger.debug("Intentionally closed socket: time to stop");
                    break;
                } catch (IOException e) {
                    logger.error("Network error", e);
                    break;
                }
            }
        }
    };

    public Thread getConnectionEventLoop() {
        return connectionEventLoop;
    }
}
