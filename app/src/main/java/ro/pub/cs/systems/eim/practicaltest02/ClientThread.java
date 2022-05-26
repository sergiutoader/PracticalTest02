package ro.pub.cs.systems.eim.practicaltest02;


import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;



public class ClientThread extends Thread {
    private String address, port;
    private TextView resultTextView;
    private String key;
    private String value;
    private Operation operation;

    public ClientThread (String address, String port, TextView resultTextView, String key) {
        this.address = address;
        this.port = port;
        this.resultTextView = resultTextView;
        this.key = key;
        this.operation = Operation.GET;

    }

    public ClientThread (String address, String port, TextView resultTextView, String key, String value) {
        this.address = address;
        this.port = port;
        this.resultTextView = resultTextView;
        this.key = key;
        this.value = value;
        this.operation = Operation.PUT;
    }

    @Override
    public void run () {
        Log.v(Utilities.TAG, "=== ClientThread run() method invoked on: " + this.address + ":" + this.port);
        String line;

        try {
            Socket socket = new Socket(this.address, Integer.parseInt(this.port));

            Log.v(Utilities.TAG, "=== ClientThread created new socket on: " + socket.getInetAddress() + ":" + socket.getLocalPort());

            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);

            switch(operation) {
                case GET:
                    Log.v(Utilities.TAG, "=== ClientThread sending: " + this.key);
                    printWriter.println(String.format("get,%s\n", key));
                    break;
                case PUT:



                    Log.v(Utilities.TAG, "=== ClientThread sending: " + this.key + " " + this.value);
                    printWriter.println(String.format("put,%s,%s\n", key, value));
                    break;
            }



            while (true) {
                line = bufferedReader.readLine();
                if (line != null) {
                    Log.d(Utilities.TAG, "=== ClientThread on: "
                            + socket.getInetAddress() + ":" + socket.getLocalPort() + " received from server: " + line);

                    String finalLine = line;
                    resultTextView.post(() -> resultTextView.setText(finalLine));
                }
            }
        } catch (UnknownHostException unknownHostException) {
            Log.d(Utilities.TAG, "=== ClientThread run() exception unknown: " + unknownHostException.getMessage());
        } catch (IOException ioException) {
            Log.d(Utilities.TAG, "=== ClientThread run() ioException: " + ioException.getMessage());
        }
    }
}
