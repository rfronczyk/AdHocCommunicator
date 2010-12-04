package pl.edu.agh.mobile.adhoccom;

import java.io.IOException;
import java.net.DatagramPacket;
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
	private final IBinder mBinder = new ChatServiceBinder();
	private ListeningThread mListeningThread;
	private ChatDbAdapter mDbAdapter;
	private MessageParser messageParser = new MessageParser();
	private AdHocFlooder adHocFlooder;
	
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
		startService();
		return Service.START_STICKY;
	}
	
	
	public void sendMessage(Message msg) throws IOException {
		// TODO send message
		adHocFlooder.send(messageParser.getBytes(msg));
		announceMessage(msg);
	}

	private void announceMessage(Message msg) {
		long newMsgId = mDbAdapter.addNewMessage(msg);
		Intent intent = new Intent(MESSAGE_RECEIVED);
		intent.putExtra("newMsgId", newMsgId);
		intent.putExtra(NEW_MSG_ID_EXTRA, newMsgId);
		sendBroadcast(intent);
	}
	
	/**
	 * Ta metoda ma umożliwiać dołączenie do grupy - 
	 * to znaczy, że serwis gdy odbierze wiadomość z tej grupy to
	 * wysle notyfikację do activities
	 */
	public void joinGroup(/*TODO choose parameter*/) {
		// TODO implement
		throw new UnsupportedOperationException("not implemented yet");
	}
	
	/** 
	 * podobnie jak wyżej
	 */
	public void leaveGroup(/* TODO choose parameter */) {
		// TODO implement
		throw new UnsupportedOperationException("not implemented yet");
	}
	
	public class ChatServiceBinder extends Binder {
		public ChatService getService() {
			return ChatService.this;
		}
	}
	
	/**
	 * 
	 * @author piotrek
	 * W tym wątku będzie nasluchiwanie na wiadomości.
	 */
	private class ListeningThread extends Thread {
		@Override
		public void run() {
			ChatService.this.adHocFlooder.start();
		}
	}

	@Override
	public void onMessageReceive(byte[] receivedData) {
		announceMessage(messageParser.parse(receivedData));
	}
}
