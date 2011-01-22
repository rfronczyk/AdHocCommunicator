package pl.edu.agh.mobile.adhoccom;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ServerConnectionActivity extends Activity implements OnClickListener {
	private Button connectButton;
	private Button cancelButton;
	private EditText ipInput;
	private EditText portInput;
	private AppConfig mConfig;
	private ServerConnection serverConnection;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.server_connection_layout);
		mConfig = AppConfig.getInstance();
		serverConnection = ServerConnection.getInstance();
		initializeWidgets();
	}

	private void initializeWidgets() {
		ipInput = (EditText) findViewById(R.id.ip_input);
		ipInput.setText(mConfig.getServerAddress());
		portInput = (EditText) findViewById(R.id.port_input);
		portInput.setText(String.valueOf(mConfig.getServerPort()));
		connectButton = (Button) findViewById(R.id.connect_button);
		connectButton.setOnClickListener(this);
		cancelButton = (Button) findViewById(R.id.cancel_button);
		cancelButton.setOnClickListener(this);
		if (serverConnection.isConnected()) {
			ipInput.setFocusable(false);
			portInput.setFocusable(false);
			connectButton.setText(R.string.disconnect_label);
		}
	}

	@Override
	public void onClick(View v) {
		if (v == connectButton) {
			if (serverConnection.isConnected()) {
				serverConnection.disconnect();
				finish();
			} else {
				String ipAddress = ipInput.getText().toString().trim();
				int port = Integer.valueOf(portInput.getText().toString());
				mConfig.setServerPort(port);
				mConfig.setServerAddress(ipAddress);
				mConfig.saveConfig();
				if (serverConnection.connect(ipAddress, port)) {
					Toast toast = Toast.makeText(this.getBaseContext(), "Connection Established", 2);
					toast.show();
					finish();
				} else {
					Toast toast = Toast.makeText(this.getBaseContext(), "Connection Failed", 2);
					toast.show();
				}
			}
		} else {
			finish();
		}
	}
}
