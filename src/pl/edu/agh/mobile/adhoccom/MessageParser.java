package pl.edu.agh.mobile.adhoccom;

import java.net.DatagramPacket;

public class MessageParser {

	public Message parse(DatagramPacket packet) {
		return new Message(new String(packet.getData()), "", 0);
	}

	public byte[] getBytes(Message msg) {
		return msg.getBody().getBytes();
	}
}
