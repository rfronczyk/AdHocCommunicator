package pl.edu.agh.mobile.adhoccom;

import java.net.DatagramPacket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import pl.edu.agh.mobile.adhoccom.ChatProtocol.ChatMessage;

import android.util.Log;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;

public class Message {
	private byte[] body;
	private String sender;
	private int date;
	private String groupName;
	private byte[] groupChalenge;
	private static MessageDigest messageDigest;
	private static final String SECRET = "p28etluthlu0Luh";
	private static final String LOGGER_TAG = "MessageClass";
	private static PBEParameterSpec paramSpec;
	private static byte[] salt = { 0x7d, 0x60, 0x43, 0x5f, 0x02, (byte) 0xe9, (byte) 0xe0, (byte) 0xae };
	private static SecretKeyFactory keyFactory; 
	
	static {
		try {
			messageDigest = MessageDigest.getInstance("SHA-1");
			keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
			paramSpec = new PBEParameterSpec(salt, 30);
		} catch (NoSuchAlgorithmException e) {
			Log.e(LOGGER_TAG, "SHA-1 diggest not supported: " + e.getMessage());
		}
	}

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
		if (groupChalenge != null) {
			chatMessage.setGroupChalenge(ByteString.copyFrom(groupChalenge));
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

	public void encrypt(String pass) {
		body = encryptBody(body, pass);
		groupChalenge = generateChalenge(groupName, pass);
	}
	
	public void decrypt(String pass) {
		body = decryptBody(body, pass);
	}

	static private synchronized byte[] generateChalenge(String groupName, String pass) {
		messageDigest.reset();
		messageDigest.update(SECRET.getBytes());
		messageDigest.update(groupName.getBytes());
		messageDigest.update(pass.getBytes());
		messageDigest.update(SECRET.getBytes());
		return messageDigest.digest();
	}

	private byte[] encryptBody(byte[] body, String pass) {
		byte[] encryptedMessage = null;
		try {
			Cipher cipher = getCipher(Cipher.ENCRYPT_MODE, pass);
			encryptedMessage = cipher.doFinal(body);
		} catch(Exception e) {
			Log.e(LOGGER_TAG, "Encryption error: " + e.getMessage());
		}
		return encryptedMessage;
	}
	
	private byte[] decryptBody(byte[] body, String pass) {
		byte[] decryptedMessage = null;
		try {
			Cipher cipher = getCipher(Cipher.DECRYPT_MODE, pass);
			decryptedMessage = cipher.doFinal(body);
		} catch(Exception e) {
			Log.e(LOGGER_TAG, "Decryption error: " + e.getMessage()); 
		}
		return decryptedMessage;
	}

	public boolean cenBeDecrypted(String pass) {
		return Arrays.equals(groupChalenge, generateChalenge(this.groupName, pass));
	}
	
	private static Cipher getCipher(int mode, String pass) {
		Cipher cipher = null;
		try {
			PBEKeySpec keySpec = new PBEKeySpec(pass.toCharArray());
			SecretKey key = keyFactory.generateSecret(keySpec);
			cipher = Cipher.getInstance("PBEWithMD5AndDES");
			cipher.init(mode, key, paramSpec);
		} catch(Exception e) {
			Log.e(LOGGER_TAG, "Cipher error: " + e.getMessage());
		}
		
    	return cipher;
	}
}
