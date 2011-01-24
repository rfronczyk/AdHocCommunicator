package pl.edu.agh.mobile.adhoccom.flooder;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.UnknownHostException;


public class BroadcastAdHocFlooder extends AdHocFlooder {
	
	private int port;
	private InetAddress address;
	
	public BroadcastAdHocFlooder(int port, String address, int historySize) throws UnknownHostException {
		this(port, address, historySize, null);	
	}
	
	public BroadcastAdHocFlooder(int port, String address, int historySize, MessageListener listener) throws UnknownHostException {
		super(historySize, listener);
		this.address = InetAddress.getByName(address);
		this.port = port;
	}
	
	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public InetAddress getAddress() {
		return address;
	}

	public void setAddress(InetAddress address) {
		this.address = address;
	}
	
	public void send(byte[] data) throws SocketException, IOException {
		DatagramPacket p = new DatagramPacket(data, data.length);
		p.setAddress(address);
		p.setPort(port);
		send(p);
	}
	
	@Override
	protected DatagramSocket getSocket() throws IOException {
		if (address.isMulticastAddress()) {
			MulticastSocket ms = new MulticastSocket(this.port);
			ms.joinGroup(address);
			return ms;
		} else {
			return new DatagramSocket(this.port);
		}
	}
	
	public static void main(String[] args) {
		BroadcastAdHocFlooder flooder;
		try {
			flooder = new BroadcastAdHocFlooder(8888, "192,168.0.100", 10);
			Thread worker = new Thread(flooder.new Sender(flooder, 1));
			worker.start();
			flooder.start();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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

	@Override
	protected void beforeClose(DatagramSocket s) {
		if (s.getLocalAddress().isMulticastAddress()) {
			MulticastSocket ms = (MulticastSocket)s;
			try {
				((MulticastSocket) s).leaveGroup(s.getLocalAddress());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
