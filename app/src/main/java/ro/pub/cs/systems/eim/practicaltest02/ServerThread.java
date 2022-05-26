package ro.pub.cs.systems.eim.practicaltest02;

import android.util.Log;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ServerThread extends Thread {
    private boolean isRunning;

    private ServerSocket serverSocket;
    private int serverPort;

    Map<String, String> hashTable = new HashMap<>();
    Map<String, Long> timestamps = new HashMap<>();

    public ServerThread(int serverPort) {
        this.serverPort = serverPort;
    }

    public void startServer() {
        this.isRunning = true;
        this.start();
        Log.d(Utilities.TAG, "=== serverThread startServer() on port: " + this.serverPort);
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(serverPort);
            while (isRunning) {
                Socket socket = serverSocket.accept();
                new ServerCommunicationThread(this, socket).start();
                Log.d(Utilities.TAG, "=== New CommunicationThread created with " + socket.getInetAddress() + ":" + socket.getLocalPort());
            }
        } catch (IOException ioException) {
            Log.e(Utilities.TAG, "=== serverThread run() exception has occurred: "+ioException.getMessage());
        }
    }

    public void stopServer () {
        this.isRunning = false;

        new Thread(() -> {
            try {
                if (serverSocket != null) {
                    serverSocket.close();
                }
                Log.v(Utilities.TAG, "=== stopServer() method invoked with serverSocket: " + serverSocket);
            } catch (IOException ioException) {
                Log.e(Utilities.TAG, "=== stopServer() exception has occurred: " + ioException.getMessage());
            }
        }).start();
    }
}
