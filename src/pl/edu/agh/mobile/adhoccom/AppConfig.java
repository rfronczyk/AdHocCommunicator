package pl.edu.agh.mobile.adhoccom;

import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.content.Intent;

public class AppConfig {
	private String userNickname = "Me";
	public static final String USER_NICKNAME_ID = "USER_NICKNAME_ID";
	private int maxMessages = 20;
	public static final String MAX_MESSAGES_ID = "MAX_MESSAGES_ID";
	private int port = 8889;
	public static final String PORT_ID = "PORT_ID";
	private String address = "192.168.0.100";
	public static final String ADDRESS_ID = "ADDRESS_ID";
	private int serverPort = 0;
	public static final String SERVER_PORT_ID = "SERVER_PORT_ID";
	private String serverAddress = "";
	public static final String SERVER_ADDRESS_ID = "SERVER_ADDRESS_ID";
	private static AppConfig instance;
	private int flooderHistorySize = 20;
	public static final String FLOODER_HISTORY_SIZE_ID = "FLOODER_HISTORY_SIZE_ID";
	private Context appContext;
	private List<String> updatedFields = new LinkedList<String>();
	public static final String CONFIG_CHANGED = "CONFIG CHANGED";
	
	public int getServerPort() {
		return serverPort;
	}

	public void setServerPort(int serverPort) {
		if (this.serverPort != serverPort) {
			this.serverPort = serverPort;
			updatedFields.add(SERVER_PORT_ID);
		}
	}

	public String getServerAddress() {
		return serverAddress;
	}

	public void setServerAddress(String serverAddress) {
		if (this.serverAddress != serverAddress) {
			this.serverAddress = serverAddress;
			updatedFields.add(SERVER_ADDRESS_ID);
		}
	}

	public Context getAppContext() {
		return appContext;
	}

	public void setAppContext(Context appContext) {
		this.appContext = appContext;
	}

	public int getFlooderHistorySize() {
		return flooderHistorySize;
	}

	public void setFlooderHistorySize(int flooderHistorySize) {
		if (this.flooderHistorySize != flooderHistorySize) {
			this.flooderHistorySize = flooderHistorySize;
			updatedFields.add(FLOODER_HISTORY_SIZE_ID);
		}
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
		if (this.port != port) {
			this.port = port;
			updatedFields.add(PORT_ID);
		}
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		if (!this.address.equals(address)) {
			this.address = address;
			updatedFields.add(ADDRESS_ID);
		}
	}

	public String getUserNickname() {
		return userNickname;
	}

	public void setUserNickname(String userNickname) {
		if (!this.userNickname.equals(userNickname)) {
			this.userNickname = userNickname;
			updatedFields.add(USER_NICKNAME_ID);
		}
	}
	
	public int getMaxMessages() {
		return maxMessages;
	}
	
	public void setMaxMessages(int maxMessages) {
		if (this.maxMessages != maxMessages) {
			this.maxMessages = maxMessages;
			updatedFields.add(MAX_MESSAGES_ID);
		}
	}
	
	public void saveConfig() {
		if (appContext != null && updatedFields.size() > 0) {
			Intent intent = new Intent(CONFIG_CHANGED);
			for(String field : updatedFields) {
				intent.putExtra(field, 1);
			}
			appContext.sendBroadcast(intent);
			updatedFields.clear();
		}
		// TODO implement config saving
	}
	
	public void loadConfig() {
		// TODO implement config loading
	}
}
