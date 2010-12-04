package pl.edu.agh.mobile.adhoccom.flooder;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.channels.ByteChannel;
import java.nio.channels.DatagramChannel;


public class BroadcastAdHocFlooder extends AdHocFlooder {
	
	private int port;

	public BroadcastAdHocFlooder(int port, int historySize) {
		this(port, historySize, null);	}
	
	public BroadcastAdHocFlooder(int port, int historySize, MessageListener listener) {
		super(historySize, listener);
		this.port = port;
	}
	
	public static void main(String[] args) {
		BroadcastAdHocFlooder flooder = new BroadcastAdHocFlooder(8888, 10);
		Thread worker = new Thread(flooder.new Sender(flooder, 1));
		worker.start();
		flooder.start();
	}
	
	public class Sender implements Runnable {
		private AdHocFlooder flooder;
		private int messagePrefix;
		
		public Sender(AdHocFlooder flooder, int messagePrefix) {
			this.flooder = flooder;
			this.messagePrefix = messagePrefix;
		}
		
		public void run() {
			for(int i = 0; i < 10; i++ ) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				byte[] messageData = ("Message " + String.valueOf(messagePrefix * 100 + i)).getBytes();
				try {
					this.flooder.send(messageData);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			flooder.stop();
		}
	}

	protected void onMessageReceive(byte[] message) {
		System.out.println(new String(message));
	}

	@Override
	protected DatagramChannel getChannel() throws IOException {
		DatagramChannel channel = DatagramChannel.open();
		channel.configureBlocking(true);
		DatagramSocket socket = channel.socket();
		socket.setBroadcast(true);
		socket.bind(new InetSocketAddress(port));
		return channel;
	}

}
