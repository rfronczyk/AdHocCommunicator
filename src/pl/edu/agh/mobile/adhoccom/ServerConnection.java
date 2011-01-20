package pl.edu.agh.mobile.adhoccom;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import pl.edu.agh.mobile.adhoccom.chatprotocol.ChatMessageException;
import pl.edu.agh.mobile.adhoccom.chatprotocol.Message;
import pl.edu.agh.mobile.adhoccom.flooder.AdHocFlooder;
import android.util.Log;
import android.widget.Toast;

public class ServerConnection {
	private static final String SERVER_CONNECTION_TAG = "SERVER CONNECTION";
	private static ServerConnection instance;
	private ChatService chatService;
	private AdHocFlooder flooder;

	private boolean connected = false;
	private InputStream inputStream;
	private OutputStream outputStream;
	private Socket socket;

	private Thread messageReceivingThread = new Thread(new Runnable(){
		@Override
		public void run() {
			while (connected) {
				try {
					Message msg = Message.parseFrom(inputStream);
					chatService.onMessageReceive(msg);
					flooder.send(msg.toByteArray());
				} catch (IOException e) {
					Log.e(SERVER_CONNECTION_TAG, e.getMessage());
					disconnect();
				} catch(ChatMessageException e) {
					Log.e(SERVER_CONNECTION_TAG, e.getMessage());
				}
			}
		}
	});

	private ServerConnection(ChatService service, AdHocFlooder flooder) {
		this.chatService = service;
		this.flooder = flooder;
	}

	public static ServerConnection getInstance(ChatService service, AdHocFlooder flooder) {
		if ( instance == null ) {
			instance = new ServerConnection(service, flooder);
		}
		return instance;
	}

	public static ServerConnection getInstance() {
		return instance;
	}

	public boolean connect(String ipAddres, int port){
		if (!connected) {
			try {
				socket = new Socket();
				socket.connect(new InetSocketAddress(ipAddres, port), 2000);
				if (socket.isConnected()) {
					inputStream = socket.getInputStream();
					outputStream = socket.getOutputStream();
					connected = true;
					messageReceivingThread.start();
				}
			}catch(Exception e) {
				Log.e(SERVER_CONNECTION_TAG, e.getMessage());
			}
		}
		return connected;
	}

	public boolean isConnected() {
		return connected;
	}

	public void disconnect() {
		if (connected) {
			connected = false;
			try {
				socket.close();
			} catch (IOException e) {
				Log.w(SERVER_CONNECTION_TAG, e.getMessage());
			}
			socket = null;
			try {
				inputStream.close();
			} catch (IOException e) {
				Log.w(SERVER_CONNECTION_TAG, e.getMessage());
			}
			inputStream = null;
			try {
				outputStream.close();
			} catch (IOException e) {
				Log.w(SERVER_CONNECTION_TAG, e.getMessage());
			}
			outputStream = null;
		}

	}

	public void send(Message msg) {
		if (connected) {
			try {
				msg.writeTo(outputStream);
			} catch (IOException ex) {
				Log.e(SERVER_CONNECTION_TAG, ex.getMessage());
			}
		}
	}


}
