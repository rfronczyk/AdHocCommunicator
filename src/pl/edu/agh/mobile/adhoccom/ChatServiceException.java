package pl.edu.agh.mobile.adhoccom;

public class ChatServiceException extends Exception {
	
	private static final long serialVersionUID = 1L;

	public ChatServiceException() {
		super();
	}

	public ChatServiceException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
		// TODO Auto-generated constructor stub
	}

	public ChatServiceException(String detailMessage) {
		super(detailMessage);
		// TODO Auto-generated constructor stub
	}

	public ChatServiceException(Throwable throwable) {
		super(throwable);
		// TODO Auto-generated constructor stub
	}
}
