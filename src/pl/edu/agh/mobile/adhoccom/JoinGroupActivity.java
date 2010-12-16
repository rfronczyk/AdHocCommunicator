package pl.edu.agh.mobile.adhoccom;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class JoinGroupActivity extends Activity implements OnClickListener {
	public static final String GROUP_NAME = "group_name";
	public static final String GROUP_PASSWORD = "group_password";

	private EditText mGroupName;
	private EditText mGroupPassword;
	private Button mJoinButton;

	private void initializeWidgets() {
		mGroupName = (EditText) findViewById(R.id.group_name);
		mGroupPassword = (EditText) findViewById(R.id.group_code);
		mJoinButton = (Button) findViewById(R.id.join_group_btn);
		mJoinButton.setOnClickListener(this);
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
		}
	}
}
