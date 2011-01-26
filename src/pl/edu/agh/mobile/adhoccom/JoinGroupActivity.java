package pl.edu.agh.mobile.adhoccom;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class JoinGroupActivity extends Activity implements OnClickListener {
	public static final String GROUP_NAME = "group_name";
	public static final String GROUP_PASSWORD = "group_password";

	private EditText mGroupName;
	private EditText mGroupPassword;
	private Button mJoinButton;
	private Button mScanButton;

	private void initializeWidgets() {
		mGroupName = (EditText) findViewById(R.id.group_name);
		mGroupPassword = (EditText) findViewById(R.id.group_code);
		mJoinButton = (Button) findViewById(R.id.join_group_btn);
		mJoinButton.setOnClickListener(this);
		mScanButton = (Button) findViewById(R.id.scan_barcode);
		mScanButton.setOnClickListener(this);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.join_group_layout);
		initializeWidgets();
	}

	@Override
	public void onClick(View v) {
		if (v == mJoinButton) {
			if (mGroupName.getText().length() > 0) {
				Intent retVal = new Intent();
				String groupName = mGroupName.getText().toString();
				retVal.putExtra(GROUP_NAME, groupName);
				String groupPassword = mGroupPassword.getText().toString();
				retVal.putExtra(GROUP_PASSWORD, groupPassword);
				setResult(RESULT_OK, retVal);
				finish();
			}
		} else if ( v == mScanButton) {
			IntentIntegrator.initiateScan(this);
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
		if (scanResult != null && scanResult.getContents() != null 
				&& scanResult.getContents().indexOf("|") != -1) {
			String[] results = scanResult.getContents().split("\\|");
			if (results.length > 0) {
				mGroupName.setText(results[0]);
				if (results.length > 1) {
					mGroupPassword.setText(results[1]);
				}
				return;
			}
		}
		Toast t = Toast.makeText(getApplicationContext(), "Wrong QR Code format", 5);
		t.show();
	}
}
