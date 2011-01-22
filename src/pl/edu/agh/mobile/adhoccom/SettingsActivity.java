package pl.edu.agh.mobile.adhoccom;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class SettingsActivity extends Activity implements OnClickListener {
	private Button applyButton;
	private Button cancelButton;
	private EditText ipInput;
	private EditText portInput;
	private EditText nameInput;
	private AppConfig mConfig;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_layout);
		mConfig = AppConfig.getInstance();
		initializeWidgets();
	}

	private void initializeWidgets() {
		ipInput = (EditText) findViewById(R.id.ip_input);
		ipInput.setText(mConfig.getAddress());
		portInput = (EditText) findViewById(R.id.port_input);
		portInput.setText(String.valueOf(mConfig.getPort()));
		nameInput = (EditText) findViewById(R.id.name_input);
		nameInput.setText(mConfig.getUserNickname());
		applyButton = (Button) findViewById(R.id.apply_button);
		applyButton.setOnClickListener(this);
		cancelButton = (Button) findViewById(R.id.cancel_button);
		cancelButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (v == applyButton) {
			mConfig.setPort(Integer.valueOf(portInput.getText().toString()));
			mConfig.setAddress(ipInput.getText().toString());
			mConfig.setUserNickname(nameInput.getText().toString());
			mConfig.saveConfig();
		}
		finish();
		return;
	}
}
