package pl.edu.agh.mobile.adhoccom.flooder;
import java.net.DatagramPacket;


public interface MessageListener {
	public void onMessageReceive(byte[] receivedData);
}
