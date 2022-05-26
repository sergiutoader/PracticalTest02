package ro.pub.cs.systems.eim.practicaltest02;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.util.EntityUtils;

public class ServerCommunicationThread extends Thread {
    private Socket socket;
    private ServerThread server;

    public ServerCommunicationThread(ServerThread server, Socket socket) {
        this.socket = socket;
        this.server = server;
    }

    @Override
    public void run() {
        Log.v(Utilities.TAG, "=== ServerCommunicationThread started running on: " + socket.getInetAddress() + ":" + socket.getLocalPort());
        String line;

        try {
            PrintWriter printWriter = Utilities.getWriter(socket);
            BufferedReader bufferedReader = Utilities.getReader(socket);

            line = bufferedReader.readLine();

            String[] split = line.split(",");
            String op = split[0];
            String key = split[1];

            if ("get".equals(op)) {
                String result;
                if(server.hashTable.containsKey(key)) {
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpGet httpGet = new HttpGet("http://worldtimeapi.org/api/ip");
                    HttpResponse httpGetResponse = httpClient.execute(httpGet);
                    HttpEntity httpGetEntity = httpGetResponse.getEntity();
                    String json;
                    Long currentTimestamp = -1L;
                    if (httpGetEntity != null) {
                        json = EntityUtils.toString(httpGetEntity);
                        String[] splitJson = json.split(",");
                        for (String s : splitJson) {
                            if (s.startsWith("\"unixtime\":")) {
                                currentTimestamp = Long.parseLong(s.split(":")[1]);
                                Long oldTimestamp = server.timestamps.get(key);
                                if (currentTimestamp - oldTimestamp > 10) {
                                    server.hashTable.remove(key);
                                }
                            }
                        }
                    }
                }
                if (server.hashTable.containsKey(key)) {
                    result = server.hashTable.get(key);
                } else {
                    result = "";
                }

                printWriter.println(result);
            } else if ("put".equals(op)) {

                // ====================== HTTP REQUEST ===========================
                Log.i(Utilities.TAG, "[COMMUNICATION THREAD] Getting the information from the webservice...");
                HttpClient httpClient = new DefaultHttpClient();

                String json;

                Long currentTimestamp = -1L;
                
                HttpGet httpGet = new HttpGet("http://worldtimeapi.org/api/ip");
                HttpResponse httpGetResponse = httpClient.execute(httpGet);
                HttpEntity httpGetEntity = httpGetResponse.getEntity();
                if (httpGetEntity != null) {
                    json = EntityUtils.toString(httpGetEntity);
                    String[] splitJson = json.split(",");
                    for (String s : splitJson) {
                        if (s.startsWith("\"unixtime\":")) {
                            currentTimestamp = Long.parseLong(s.split(":")[1]);
                            server.timestamps.put(key, currentTimestamp);
                            break;
                        }
                    }
                }


                // ====================== HTTP REQUEST ===========================

                String value = split[2];
                String result = "New entry in hashtable: " + key + ", " + value;

                printWriter.println(result);

                server.hashTable.put(key, value);
            }

            socket.close();
            Log.v(Utilities.TAG, "=== ServerCommunicationThread with " + socket.getInetAddress() + ":" + socket.getLocalPort() + " closed connection");
        } catch (IOException ioException) {
            Log.e(Utilities.TAG, "=== ServerCommunicationThread run() exception has occurred: "+ ioException.getMessage());
        }
    }
}
