package pl.edu.agh.mobile.adhoccom;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pl.edu.agh.mobile.adhoccom.chatprotocol.ChatMessageException;
import pl.edu.agh.mobile.adhoccom.chatprotocol.Message;
import pl.edu.agh.mobile.adhoccom.flooder.AdHocFlooder;
import pl.edu.agh.mobile.adhoccom.flooder.BroadcastAdHocFlooder;
import pl.edu.agh.mobile.adhoccom.flooder.MessageListener;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;


public class ChatService extends Service implements MessageListener {

	public static final String MESSAGE_RECEIVED = "MESSAGE_RECEIVED";
	public static final String NEW_MSG_ID_EXTRA = "pl.edu.agh.mobile.adhoccom.newMsgId";
	public static final String NEW_MSG_GROUP_EXTRA = "pl.edu.agh.mobile.adhoccom.newMsgGroup";
	public static final String DEFAULT_GROUP = "default";
	private static final String LOGGER_TAG = "ChatService";
	private final IBinder mBinder = new ChatServiceBinder();
	private ListeningThread mListeningThread;
	private ChatDbAdapter mDbAdapter;
	private AdHocFlooder adHocFlooder;
	private Map<String, String> chatGroups = new HashMap<String, String>();
	private AppConfig config;
	private BroadcastReceiver mBroadcastReceiver;
	private boolean reload;
	private boolean connected = true;
	private InputStream inputStream;
	private OutputStream outputStream;
	
	private Thread messageReceivingThread = new Thread(new Runnable(){
		@Override
		public void run() {
			while (connected) {
				try {
					Message msg = Message.parseFrom(inputStream);
					sendMessage(new Message(msg.getBody().getBytes(), msg.getSender(), msg.getDate(), msg.getGroupName()), false);
				} catch (IOException e) {
					e.printStackTrace();
				} catch(ChatMessageException e) {
					e.printStackTrace();
				}
			}
		}
	});
	
	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	private void startService() {
		mListeningThread = new ListeningThread();
		mListeningThread.start();
		messageReceivingThread.start();
	}

	@Override
	public void onCreate() {
		super.onCreate();
		config = AppConfig.getInstance();
		mDbAdapter = (new ChatDbAdapter(this)).open();
		adHocFlooder = new BroadcastAdHocFlooder(config.getPort(), config.getAddress(),
												 config.getFlooderHistorySize(), this);	
		IntentFilter filter = new IntentFilter(AppConfig.CONFIG_CHANGED);
		registerReceiver(new ConfigChangedReceiver(), filter);
		try {
			Socket s = new Socket("10.0.0.2", 1234);
			inputStream = s.getInputStream();
			outputStream = s.getOutputStream();
		} catch(Exception e) {
			
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mDbAdapter.close();
		adHocFlooder.stop();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (startId == 1) { // start listening threads only once
			startService();
		}
		return Service.START_STICKY;
	}
	
	public void sendMessage(Message msg, boolean ble) throws IOException {
		announceMessage(msg);
		if (!msg.getGroupName().equals(DEFAULT_GROUP)) {
			msg.encrypt(chatGroups.get(msg.getGroupName()));
		}
		adHocFlooder.send(msg.toByteArray());
	}

	public void sendMessage(Message msg) throws IOException {
		announceMessage(msg);
		if (!msg.getGroupName().equals(DEFAULT_GROUP)) {
			msg.encrypt(chatGroups.get(msg.getGroupName()));
		}
		try {
			msg.writeTo(outputStream);
		} catch (IOException ex) {
			
		}
		adHocFlooder.send(msg.toByteArray());
	}

	private void announceMessage(Message msg) {
		long newMsgId = mDbAdapter.addNewMessage(msg);
		Intent intent = new Intent(MESSAGE_RECEIVED);
		intent.putExtra("newMsgId", newMsgId);
		intent.putExtra(NEW_MSG_ID_EXTRA, newMsgId);
		intent.putExtra(NEW_MSG_GROUP_EXTRA, msg.getGroupName());
		sendBroadcast(intent);
	}

	public List<String> getChatGroups() {
		return new ArrayList<String>(chatGroups.keySet());
	}
	
	/**
	 * Join given group. Starts receiving massages for given group
	 * @param groupName
	 * @param groupCode
	 */
	public void joinGroup(String groupName, String groupCode) {
		if (!chatGroups.containsKey(groupName)) {
			chatGroups.put(groupName, groupCode);
		}
	}

	/**
	 * Leave given group. Stops receiving messages for given group
	 * @param groupName
	 */
	public void leaveGroup(String groupName) throws ChatServiceException {
		if (ChatService.DEFAULT_GROUP.equals(groupName)) {
			throw new ChatServiceException(getString(R.string.leave_default_group_error));
		}
		chatGroups.remove(groupName);
	}
	
	public boolean isAttachedToGroup(String groupName) {
		return chatGroups.containsKey(groupName);
	}

	public class ChatServiceBinder extends Binder {
		public ChatService getService() {
			return ChatService.this;
		}
	}

	private class ListeningThread extends Thread {
		@Override
		public void run() {
			ChatService.this.reload = true;
			while(ChatService.this.reload) {
				ChatService.this.reload = false;
				ChatService.this.adHocFlooder.start();
			}
		}
	}

	@Override
	public void onMessageReceive(DatagramPacket packet) {
		try {
			Message msg = Message.parseFrom(packet);
			if (!msg.getGroupName().equals(DEFAULT_GROUP)) {
				String pass = chatGroups.get(msg.getGroupName());
				if (chatGroups.containsKey(msg.getGroupName())
					&& msg.cenBeDecrypted(pass)) {
					msg.decrypt(pass);
					announceMessage(msg);
				}
			} else {
				announceMessage(msg);
			}
		} catch (ChatMessageException e) {
			Log.i(LOGGER_TAG, "Exception while parsing message: " + e.getMessage());
		}

	}
	
	private class ConfigChangedReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			BroadcastAdHocFlooder flooder = (BroadcastAdHocFlooder)ChatService.this.adHocFlooder;
			AppConfig config = AppConfig.getInstance();

			if (intent.hasExtra(AppConfig.PORT_ID) || intent.hasExtra(AppConfig.ADDRESS_ID)) {
				flooder.setPort(config.getPort());
				flooder.setAddress(new InetSocketAddress(config.getAddress(), config.getPort()));
				
				ChatService.this.reload = true;
				ChatService.this.adHocFlooder.stop();
			}
		}
		
	}
}
