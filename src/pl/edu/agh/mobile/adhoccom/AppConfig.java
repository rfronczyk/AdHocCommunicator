package pl.edu.agh.mobile.adhoccom;

public class AppConfig {
	private String userNickname = "Me";
	private int maxMessages = 20;
	
	public String getUserNickname() {
		return userNickname;
	}

	public void setUserNickname(String userNickname) {
		this.userNickname = userNickname;
	}
	
	public int getMaxMessages() {
		return maxMessages;
	}
	
	public void setMaxMessages(int maxMessages) {
		this.maxMessages = maxMessages;
	}
	
	public void saveConfig() {
		// TODO implement config saving
	}
	
	public void loadConfig() {
		// TODO implement config loading
	}
}
