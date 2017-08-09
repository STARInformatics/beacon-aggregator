package bio.knowledge.aggregator;

import java.sql.Timestamp;

public class LogEntry {
	
	private String timestamp;
	private String query;
	private String message;

	public LogEntry(String query, String message) {
		this.timestamp = new Timestamp(System.currentTimeMillis()).toString();
		this.query = query;
		this.message = message;
	}
	
	public String getTimestamp() {
		return timestamp;
	}

	public String getQuery() {
		return query;
	}

	public String getMessage() {
		return message;
	}
	
}
