package pl.edu.agh.mobile.adhoccom.flooder;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;


public class BroadcastAdHocFlooder extends AdHocFlooder {
	
	private int port;

	public BroadcastAdHocFlooder(int port, int historySize) {
		this(port, historySize, null);	}
	
	public BroadcastAdHocFlooder(int port, int historySize, MessageListener listener) {
		super(historySize, listener);
		this.port = port;
	}
	
	public void send(byte[] data) throws SocketException, IOException {
		send(new DatagramPacket(data, data.length, new InetSocketAddress("192.168.0.100", port)));
	}
	
	@Override
	protected DatagramSocket getSocket() throws IOException {
		return new DatagramSocket(this.port);
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
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				byte[] messageData = ("Message " + String.valueOf(messagePrefix * 100 + i)).getBytes();
				DatagramPacket packet = null;
				try {
					packet = new DatagramPacket(messageData, messageData.length, new InetSocketAddress("255.255.255.255", 8888));
				} catch (SocketException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				try {
					this.flooder.send(packet);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			flooder.stop();
		}
	}

}
