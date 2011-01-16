package pl.edu.agh.mobile.adhoccom;

import java.net.DatagramPacket;

import pl.edu.agh.mobile.adhoccom.ChatProtocol.ChatMessage;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;

public class Message {
	private byte[] body;
	private String sender;
	private int date;
	private String groupName;
	private byte[] groupChalenge;

	/**
	 * 
	 * @param body
	 * @param sender
	 * @param date Number of <b>seconds</b> from 1970
	 * @param group
	 */
	public Message(byte[] body, String sender, int date, String group) {
		this.body = body;
		this.sender = sender;
		this.date = date;
		this.groupName = group;
	}

	public Message(byte[] body, String sender, int date, String groupName,
			byte[] groupChalenge) {
		this(body, sender, date, groupName);
		this.groupChalenge = groupChalenge;
	}
	
	public String getBody() {
		return new String(body);
	}
	
	public byte[] getBodyBytes() {
		return body;
	}

	public void setBody(String body) {
		this.body = body.getBytes();
	}
	
	public void setBodyBytes(byte[] body) {
		this.body = body;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public int getDate() {
		return date;
	}
	
	public void setDate(int date) {
		this.date = date;
	}
	
	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String group) {
		this.groupName = group;
	}
	
	public byte[] getGroupChalenge() {
		return groupChalenge;
	}

	public void setGroupChalenge(byte[] groupChalenge) {
		this.groupChalenge = groupChalenge;
	}

	@Override
	public String toString() {
		StringBuilder retVal = new StringBuilder();
		retVal.append(sender).append(": ").append(getBody());
		return retVal.toString();
	}

	public byte[] toByteArray() {
		ChatMessage.Builder chatMessage = ChatMessage.newBuilder();
		chatMessage.setBody(ByteString.copyFrom(getBodyBytes()));
		chatMessage.setDate(getDate());
		chatMessage.setSender(getSender());
		chatMessage.setGroupName(getGroupName());
		if (getGroupChalenge() != null) {
			chatMessage.setGroupChalenge(ByteString.copyFrom(getGroupChalenge()));
		}
		
		return chatMessage.build().toByteArray();
	}

	public static Message parseFrom(DatagramPacket packet) throws InvalidProtocolBufferException {
		ChatMessage chatMessage = ChatMessage.parseFrom(packet.getData());
		Message msg = new Message(chatMessage.getBody().toByteArray(), chatMessage.getSender(),
								  chatMessage.getDate(), chatMessage.getGroupName(), 
								  chatMessage.getGroupChalenge().toByteArray());
		return msg;
	}
}
