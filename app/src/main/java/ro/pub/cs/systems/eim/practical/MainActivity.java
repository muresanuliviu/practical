package ro.pub.cs.systems.eim.practical;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {


    private EditText serverPort;
    private EditText clientPort;
    private EditText clientAddress;
    private EditText site;
    private Button startServer;
    private Button connect;
    private TextView result;
    private ServerThread serverThread;
    private ClientThread clientThread;

    private ConnectButtonClickListener buttonClickListener = new ConnectButtonClickListener();
    private class ConnectButtonClickListener implements Button.OnClickListener {

        @Override
        public void onClick(View view) {
            Log.d(Constants.TAG, serverPort.getText().toString());
            String serverPortString = serverPort.getText().toString();
            if (serverPortString == null || serverPortString.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Server port should be filled!", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                serverThread = new ServerThread(Integer.parseInt(serverPortString));
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (serverThread.getServerSocket() == null) {
                Log.e(Constants.TAG, "[MAIN ACTIVITY] Could not create server thread!");
                return;
            }
            serverThread.start();
        }
    }



    private ClientButtonClickListener connectClickListener = new ClientButtonClickListener();
    private class ClientButtonClickListener implements Button.OnClickListener {

        @Override
        public void onClick(View view) {
            String clientAddressString = clientAddress.getText().toString();
            String clientPortString = clientPort.getText().toString();
            if (clientAddressString == null || clientAddressString.isEmpty()
                    || clientPortString == null || clientPortString.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Client connection parameters should be filled!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (serverThread == null || !serverThread.isAlive()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] There is no server to connect to!", Toast.LENGTH_SHORT).show();
                return;
            }
            String siteString = site.getText().toString();
            if (siteString == null || siteString.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Parameters from client (site ) should be filled", Toast.LENGTH_SHORT).show();
                return;
            }

            result.setText(Constants.EMPTY_STRING);

            clientThread = new ClientThread(
                    clientAddressString, Integer.parseInt(clientPortString), siteString, result
            );
            clientThread.start();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        serverPort = findViewById(R.id.server_port);
        clientPort = findViewById(R.id.client_port);
        clientAddress = findViewById(R.id.address);
        site = findViewById(R.id.site);
        clientPort = findViewById(R.id.client_port);
        startServer = findViewById(R.id.start_server);
        connect = findViewById(R.id.connect);
        result = findViewById(R.id.result);

        startServer.setOnClickListener(buttonClickListener);
        connect.setOnClickListener(connectClickListener);
    }

    @Override
    protected void onDestroy() {
        Log.d(Constants.TAG, "[MAIN ACTIVITY] onDestroy() callback method has been invoked");
        if (serverThread != null) {
            serverThread.stopThread();
        }
        super.onDestroy();
    }
}
