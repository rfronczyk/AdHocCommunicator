package pl.edu.agh.mobile.adhoccom;

import java.io.IOException;
import java.net.DatagramPacket;
import java.util.LinkedList;
import java.util.List;

import pl.edu.agh.mobile.adhoccom.flooder.AdHocFlooder;
import pl.edu.agh.mobile.adhoccom.flooder.BroadcastAdHocFlooder;
import pl.edu.agh.mobile.adhoccom.flooder.MessageListener;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;


public class ChatService extends Service implements MessageListener {

	public static final String MESSAGE_RECEIVED = "MESSAGE_RECEIVED";
	public static final String NEW_MSG_ID_EXTRA = "pl.edu.agh.mobile.adhoccom.newMsgId";
	public static final String NEW_MSG_GROUP_EXTRA = "pl.edu.agh.mobile.adhoccom.newMsgGroup";
	private final IBinder mBinder = new ChatServiceBinder();
	private ListeningThread mListeningThread;
	private ChatDbAdapter mDbAdapter;
	private MessageParser messageParser = new MessageParser();
	private AdHocFlooder adHocFlooder;
	private List<String> chatGroups = new LinkedList<String>();

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	private void startService() {
		mListeningThread = new ListeningThread();
		mListeningThread.start();
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mDbAdapter = (new ChatDbAdapter(this)).open();
		adHocFlooder = new BroadcastAdHocFlooder(8888, 10, this);
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

	public void sendMessage(Message msg) throws IOException {
		adHocFlooder.send(messageParser.getBytes(msg));
		announceMessage(msg);
	}

	private void announceMessage(Message msg) {
		long newMsgId = mDbAdapter.addNewMessage(msg);
		Intent intent = new Intent(MESSAGE_RECEIVED);
		intent.putExtra("newMsgId", newMsgId);
		intent.putExtra(NEW_MSG_ID_EXTRA, newMsgId);
		intent.putExtra(NEW_MSG_GROUP_EXTRA, msg.getGroup());
		sendBroadcast(intent);
	}

	public List<String> getChatGroups() {
		return chatGroups;
	}
	
	/**
	 * Join given group. Starts receiving massages for given group
	 * @param groupName
	 * @param groupCode
	 */
	public void joinGroup(String groupName, String groupCode) {
		if (!chatGroups.contains(groupName)) {
			chatGroups.add(groupName);
		}
		// TODO save password
	}

	/**
	 * Leave given group. Stops receiving messages for given group
	 * @param groupName
	 */
	public void leaveGroup(String groupName) throws ChatServiceException {
		if (Message.DEFAULT_GROUP.equals(groupName)) {
			throw new ChatServiceException(getString(R.string.leave_default_group_error));
		}
		chatGroups.remove(groupName);
	}
	
	public boolean isAttachedToGroup(String groupName) {
		return chatGroups.contains(groupName);
	}

	public class ChatServiceBinder extends Binder {
		public ChatService getService() {
			return ChatService.this;
		}
	}

	private class ListeningThread extends Thread {
		@Override
		public void run() {
			ChatService.this.adHocFlooder.start();
		}
	}

	@Override
	public void onMessageReceive(DatagramPacket packet) {
		announceMessage(messageParser.parse(packet));
	}
}
