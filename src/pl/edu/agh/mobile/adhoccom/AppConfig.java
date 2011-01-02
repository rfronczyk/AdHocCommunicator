package pl.edu.agh.mobile.adhoccom;

public class AppConfig {
	private String userNickname = "Me";
	private int maxMessages = 20;
	private int port = 8888;
	private String address = "192.168.0.100";
	private static AppConfig instance;
	private int flooderHistorySize = 20;
	
	public int getFlooderHistorySize() {
		return flooderHistorySize;
	}

	public void setFlooderHistorySize(int flooderHistorySize) {
		this.flooderHistorySize = flooderHistorySize;
	}

	private AppConfig() {
		super();
	}
	
	public static AppConfig getInstance() {
		if (instance == null) {
			instance = new AppConfig();
		}
		return instance;
	}
	
	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

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
