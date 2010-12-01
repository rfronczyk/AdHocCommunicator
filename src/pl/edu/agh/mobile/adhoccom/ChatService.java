package pl.edu.agh.mobile.adhoccom;

import java.util.Random;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class ChatService extends Service {

	public static final String MESSAGE_RECEIVED = "MESSAGE_RECEIVED";
	private final IBinder mBinder = new ChatServiceBinder();
	private ListeningThread mListeningThread;
	private ChatDbAdapter mDbAdapter;
	
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
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		mDbAdapter.close();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		startService();
		return Service.START_STICKY;
	}
	
	
	public void sendMessage(Message msg) {
		// TODO send message
		announceMessage(msg);
	}

	private void announceMessage(Message msg) {
		long newMsgId = mDbAdapter.addNewMessage(msg);
		Intent intent = new Intent(MESSAGE_RECEIVED);
		intent.putExtra("newMsgId", newMsgId);
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
			while (true) {
				try {
					sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
