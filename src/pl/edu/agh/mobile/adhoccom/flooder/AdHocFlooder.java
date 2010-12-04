package pl.edu.agh.mobile.adhoccom.flooder;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.DatagramChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.LinkedHashSet;


public abstract class AdHocFlooder {
	private static final int MAX_PACKET_SIZE = 1500;
	private int historySize;
	private LinkedHashSet<Integer> messagesHistory;
	private DatagramChannel channel;
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
			channel = getChannel();
			ByteBuffer byteBuffer = ByteBuffer.allocate(MAX_PACKET_SIZE);
			while(!stop) {
				try {
					channel.receive(byteBuffer);
					byteBuffer.flip();
					byte[] receivedData = new byte[byteBuffer.remaining()];
					byteBuffer.get(receivedData);
					messageReceived(receivedData);
					byteBuffer.clear();
				} catch(AsynchronousCloseException e) {
					
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (channel != null) {
				try {
					channel.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				channel = null;
			}
		}
		
	}
	
	/* the channel returned by this function must implement ByteChannel InterruptibleChannel
	 * interfaces and must by connected to the proper port
	 */
	abstract protected DatagramChannel getChannel() throws IOException;
	
	private byte[] getMessageDigest(byte[] receivedData) {
		messageDigest.update(receivedData);
		return messageDigest.digest();
	}

	private void messageReceived(byte[] receivedData) {
		boolean messageInHistory = false;
		synchronized(this) {
			messageInHistory = messagesHistory.contains(new String(getMessageDigest(receivedData)).hashCode());
		}
		if (!messageInHistory) {
			try {
				send(receivedData);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			onMessageReceive(receivedData);
		}
	}

	protected void onMessageReceive(byte[] receivedData) {
		if (this.messageListener != null) {
			messageListener.onMessageReceive(receivedData);
		}
	}

	public void stop() {
		this.stop = true;
		try {
			this.channel.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public synchronized void send(byte[] data) throws IOException {
		if (channel != null && channel.isOpen()) {
			ByteBuffer sendBuffer = ByteBuffer.wrap(data);
			channel.send(sendBuffer, new InetSocketAddress("255.255.255.255", 8888));
			messagesHistory.add(new String(getMessageDigest(data)).hashCode());
			if (messagesHistory.size() > historySize) {
				Iterator<Integer> it = messagesHistory.iterator();
				it.next();
				it.remove();
			}
		}
	}		
}
