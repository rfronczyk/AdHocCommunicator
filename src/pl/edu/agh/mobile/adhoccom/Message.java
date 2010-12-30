package pl.edu.agh.mobile.adhoccom;

import java.net.DatagramPacket;

import com.google.protobuf.InvalidProtocolBufferException;

import pl.edu.agh.mobile.adhoccom.ChatProtocol.ChatMessage;

public class Message {
	private String body;
	private String sender;
	private int date;
	private String groupName;
	private String groupChalenge;

	/**
	 * 
	 * @param body
	 * @param sender
	 * @param date Number of <b>seconds</b> from 1970
	 * @param group
	 */
	public Message(String body, String sender, int date, String group) {
		this.body = body;
		this.sender = sender;
		this.date = date;
		this.groupName = group;
	}

	public Message(String body, String sender, int date, String groupName,
			String groupChalenge) {
		this(body, sender, date, groupName);
		this.groupChalenge = groupChalenge;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
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
	
	public String getGroupChalenge() {
		return groupChalenge;
	}

	public void setGroupChalenge(String groupChalenge) {
		this.groupChalenge = groupChalenge;
	}

	@Override
	public String toString() {
		StringBuilder retVal = new StringBuilder();
		retVal.append(sender).append(": ").append(body);
		return retVal.toString();
	}

	public byte[] toByteArray() {
		ChatMessage.Builder chatMessage = ChatMessage.newBuilder();
		chatMessage.setBody(getBody());
		chatMessage.setDate(getDate());
		chatMessage.setSender(getSender());
		chatMessage.setGroupName(getGroupName());
		if (getGroupChalenge() != null) {
			chatMessage.setGroupChalenge(getGroupChalenge());
		}
		
		return chatMessage.build().toByteArray();
	}

	public static Message parseFrom(DatagramPacket packet) throws InvalidProtocolBufferException {
		ChatMessage chatMessage = ChatMessage.parseFrom(packet.getData());
		Message msg = new Message(chatMessage.getBody(), chatMessage.getSender(),
								  chatMessage.getDate(), chatMessage.getGroupName(), 
								  chatMessage.getGroupChalenge());
		return msg;
	}
}
