package ro.pub.cs.systems.eim.practicaltest02;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    EditText serverPortEditText,
            clientPortEditText,
            clientAddressEditText,
            clientKeyEditText,
            clientValueEditText;
    Button serverConnectButton, clientGetButton, clientPutButton;
    TextView clientResultTextView;

    ServerThread serverThread = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        serverPortEditText = findViewById(R.id.serverPortEditText);
        clientPortEditText = findViewById(R.id.clientPortEditText);
        clientAddressEditText = findViewById(R.id.clientAddressEditText);
        clientKeyEditText = findViewById(R.id.clientKey);
        clientValueEditText = findViewById(R.id.clientValue);

        serverConnectButton = findViewById(R.id.serverPortConnectButton);
        clientGetButton = findViewById(R.id.clientGetButton);
        clientPutButton = findViewById(R.id.clientPutButton);

        clientResultTextView = findViewById(R.id.clientResultTextView);

        serverConnectButton.setOnClickListener(onServerConnectButtonClick);
        clientGetButton.setOnClickListener(onClientGetButtonClick);
        clientPutButton.setOnClickListener(onClientPutButtonClick);
    }

    private final ServerConnectButtonListener onServerConnectButtonClick = new ServerConnectButtonListener();

    public class ServerConnectButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            String serverPort = serverPortEditText.getText().toString();
            if (serverPort == null || serverPort.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Server port should be filled!", Toast.LENGTH_SHORT).show();
                return;
            }
            int serverPortNumber = -1;
            try {
                serverPortNumber = Integer.parseInt(serverPort);
            } catch (IllegalArgumentException e) {
                Toast.makeText(getApplicationContext(), "Server port should be filled with a number!", Toast.LENGTH_SHORT).show();
                return;
            }
            serverThread = new ServerThread(serverPortNumber);
            serverThread.startServer();
        }
    }

    private final ClientGetButtonListener onClientGetButtonClick = new ClientGetButtonListener();
    public class ClientGetButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            String clientAddress = clientAddressEditText.getText().toString();
            String clientPort = clientPortEditText.getText().toString();
            if (clientAddress == null || clientAddress.isEmpty()
                    || clientPort == null || clientPort.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Client connection parameters should be filled!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (serverThread == null || !serverThread.isAlive()) {
                Toast.makeText(getApplicationContext(), "There is no server to connect to!", Toast.LENGTH_SHORT).show();
                return;
            }

            String key = clientKeyEditText.getText().toString();

            ClientThread clientThread = new ClientThread(clientAddress, clientPort, clientResultTextView, key);
            clientThread.start();
        }
    }

    private final ClientPutButtonListener onClientPutButtonClick = new ClientPutButtonListener();
    public class ClientPutButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            String clientAddress = clientAddressEditText.getText().toString();
            String clientPort = clientPortEditText.getText().toString();
            if (clientAddress == null || clientAddress.isEmpty()
                    || clientPort == null || clientPort.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Client connection parameters should be filled!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (serverThread == null || !serverThread.isAlive()) {
                Toast.makeText(getApplicationContext(), "There is no server to connect to!", Toast.LENGTH_SHORT).show();
                return;
            }

            String key = clientKeyEditText.getText().toString();
            String value = clientValueEditText.getText().toString();

            ClientThread clientThread = new ClientThread(clientAddress, clientPort, clientResultTextView, key, value);
            clientThread.start();
        }
    }
}