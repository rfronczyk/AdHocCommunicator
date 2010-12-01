package pl.edu.agh.mobile.adhoccom;

public class Message {
	private String body;
	private String sender;
	private String date;
	
	public Message(String body, String sender, String date) {
		super();
		this.body = body;
		this.sender = sender;
		this.date = date;
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

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}
	
	@Override
	public String toString() {
		StringBuilder retVal = new StringBuilder();
		retVal.append(sender).append(": ").append(body);
		return retVal.toString();
	}
}
