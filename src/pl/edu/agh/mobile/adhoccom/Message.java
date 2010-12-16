package pl.edu.agh.mobile.adhoccom;

public class Message {
	private String body;
	private String sender;
	private int date;
	private String group;
	
	public static final String DEFAULT_GROUP = "default";
	
	public Message(String body, String sender, int date) {
		this(body, sender, date, DEFAULT_GROUP);
	}
	
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
		this.group = group;
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
	
	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	@Override
	public String toString() {
		StringBuilder retVal = new StringBuilder();
		retVal.append(sender).append(": ").append(body);
		return retVal.toString();
	}
}
