package pl.edu.agh.mobile.adhoccom;

import java.net.DatagramPacket;

public class MessageParser {

	public Message parse(byte[] receivedData) {
		return new Message(new String(receivedData), "", "");
	}

	public byte[] getBytes(Message msg) {
		return msg.getBody().getBytes();
	}
}
