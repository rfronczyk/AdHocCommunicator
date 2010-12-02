package pl.edu.agh.mobile.adhoccom;

import java.io.IOException;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class ChatActivity extends Activity implements OnClickListener {
	private ListView mMessagesView;
	private Button mSendButton;
	private EditText mEditMessage;
	
	private BroadcastReceiver mBroadcastReceiver;
	private ChatService mChatService;
	private ChatDbAdapter mDbAdapter;
	
	// Handles the connection between the service and activity
	private ServiceConnection mConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			ChatActivity.this.mChatService = ((ChatService.ChatServiceBinder) service).getService();
		}

		public void onServiceDisconnected(ComponentName className) {
			mChatService = null;
		}
	};

	private void initializeWidgets() {
		mMessagesView = (ListView) findViewById(R.id.msg_view);
		mEditMessage = (EditText) findViewById(R.id.msg_edit);
		mSendButton = (Button) findViewById(R.id.send);
		mSendButton.setOnClickListener(this);
	}
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chat_layout);
		initializeWidgets();
		
		Intent svc = new Intent(this, ChatService.class);
		startService(svc);
		Intent bindIntent = new Intent(ChatActivity.this, ChatService.class);
		bindService(bindIntent, mConnection, Context.BIND_AUTO_CREATE);

		IntentFilter filter = new IntentFilter(ChatService.MESSAGE_RECEIVED);
		if (mBroadcastReceiver == null)
			mBroadcastReceiver = new MessageReceiver();
		registerReceiver(new MessageReceiver(), filter);
		mDbAdapter = (new ChatDbAdapter(this)).open();
		fillData();
	}
	
	@Override
	public void onStart() {
		super.onStart();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		mDbAdapter.close();
	}
	
	@Override
	public void onResume() {
		super.onResume();

	}
	
	public void onPause() {
		super.onPause();
	}
	
	private void showToast(String text) {
		Toast.makeText(this, text, Toast.LENGTH_LONG).show();
	}

	@Override
	public void onClick(View v) {
		if (v == mSendButton) {
			if (mEditMessage.getText().length() > 0) {
				try {
					mChatService.sendMessage(new Message(mEditMessage.getText()
							.toString(), "Me", ""));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
    private void fillData() {
    	Cursor c = mDbAdapter.fetchAllMessages();
    	startManagingCursor(c);
    	String[] from = new String[] {ChatDbAdapter.SENDER_COLLUMN, ChatDbAdapter.BODY_COLLUMN};
    	int[] to = new int[] {R.id.message_sender, R.id.message_body};
    	SimpleCursorAdapter msgAdapter = new SimpleCursorAdapter(this, R.layout.message_layout, c, from, to);
    	mMessagesView.setAdapter(msgAdapter);
    }
	
	public class MessageReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			fillData();
		}
		
	}
}