package pl.edu.agh.mobile.adhoccom;

public class IntentResult {
	
	private String contents;
	private String formatName;

	public IntentResult(String contents, String formatName) {
		this.contents = contents;
		this.formatName = formatName;
	}

	public String getContents() {
		return contents;
	}

	public void setContents(String contents) {
		this.contents = contents;
	}

	public String getFormatName() {
		return formatName;
	}

	public void setFormatName(String formatName) {
		this.formatName = formatName;
	}

}
