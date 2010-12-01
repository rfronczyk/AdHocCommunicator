package pl.edu.agh.mobile.adhoccom.flooder;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashSet;


public abstract class AdHocFlooder {
	private static final int MAX_PACKET_SIZE = 1500;
	private static final int SOCKET_TIMEOUT = 1000;
	private int historySize;
	private LinkedHashSet<Integer> messagesHistory;
	private DatagramSocket socket;
	private boolean stop = false;
	private MessageDigest messageDigest;
	private MessageListener messageListener;
	
	public AdHocFlooder(int historySize) {
		this(historySize, null);
	}
	
	public AdHocFlooder(int historySize, MessageListener messageListener) {
		this.historySize = historySize;
		messagesHistory = new LinkedHashSet<Integer>(historySize);
		this.messageListener = messageListener;
		try {
			messageDigest = MessageDigest.getInstance("SHA-1");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}
	
	public void start() {
		try {
			socket = getSocket();
			socket.setSoTimeout(SOCKET_TIMEOUT);
			byte buffer[] = new byte[MAX_PACKET_SIZE];
			DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length);
			while(!stop) {
				try {
					socket.receive(datagramPacket);
					ByteArrayOutputStream stream = new ByteArrayOutputStream(datagramPacket.getLength());
					stream.write(datagramPacket.getData());
					byte[] receivedData = stream.toByteArray();
					messageReceived(new DatagramPacket(receivedData, receivedData.length,	datagramPacket.getSocketAddress()));
				} catch(SocketTimeoutException e) {
					
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (socket != null) {
				socket.close();
				socket = null;
			}
		}
		
	}
	
	abstract protected DatagramSocket getSocket() throws IOException;
	
	private byte[] getMessageDigest(DatagramPacket packet) {
		messageDigest.update(packet.getData());
		return messageDigest.digest();
	}

	private void messageReceived(DatagramPacket datagramPacket) {
		boolean messageInHistory = false;
		synchronized(this) {
			messageInHistory = messagesHistory.contains(new String(getMessageDigest(datagramPacket)).hashCode());
		}
		if (!messageInHistory) {
			try {
				send(datagramPacket);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			onMessageReceive(datagramPacket);
		}
	}

	protected void onMessageReceive(DatagramPacket datagramPacket) {
		if (this.messageListener != null) {
			messageListener.onMessageReceive(datagramPacket);
		}
	}

	public void stop() {
		this.stop = true;
	}
	
	public synchronized void send(DatagramPacket packet) throws IOException {
		if (socket != null) {
			socket.send(packet);
			messagesHistory.add(new String(getMessageDigest(packet)).hashCode());
			if (messagesHistory.size() > historySize) {
				Iterator<Integer> it = messagesHistory.iterator();
				it.next();
				it.remove();
			}
		}
	}		
	
	public abstract void send(byte[] data) throws SocketException, IOException;
}
