package bio.knowledge.aggregator;

import java.net.URI;
import java.net.URISyntaxException;

import bio.knowledge.client.ApiClient;

/**
 * Wraps an ApiClient
 * 
 * @author Lance Hannestad
 *
 */
public class KnowledgeBeacon {
	
	private String id;
	
	private String name;
	private String description;
	private boolean isEnabled;
	
	private final ApiClient apiClient;
	
	public KnowledgeBeacon(String id, String url, String name, String description) {
		this(id, url, name, description, true);
	}
	
	public KnowledgeBeacon(String id, String url, String name, String description, boolean isEnabled) {
		url = validateAndFixUrl(url);
		
		this.setId(id);
		this.name = name;
		this.description = description;
		this.isEnabled = isEnabled;
		
		this.apiClient = new ApiClient();
		this.apiClient.setBasePath(url);
	}

	private String validateAndFixUrl(String url) {
		try {
			if(!(url.startsWith("http://") || url.startsWith("https://"))) url = "http://"+url;
			if (url.endsWith("/")) {
				url = url.substring(0, url.length() - 1);
			}
			
			// If url is improperly formatted, this constructor will throw an exception
			new URI(url);
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException("URL: " + url + " is not valid.");
		}
		return url;
	}
	
	public KnowledgeBeacon(String id, String url) {
		this(id, url, null, null);
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}
	
	public String getDescription() {
		return this.description;
	}
	
	public String getUrl() {
		return this.apiClient.getBasePath();
	}
	
	protected ApiClient getApiClient() {
		return this.apiClient;
	}
	
	public boolean isEnabled() {
		return this.isEnabled;
	}
	
	public void setEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
	}
	
	@Override
	public boolean equals(Object other) {
		if (other == this) {
			return true;
		} else if (! (other instanceof KnowledgeBeacon)) {
			return false;
		} else {
			KnowledgeBeacon otherKnowledgeBeacon = (KnowledgeBeacon) other;
			
			return this.apiClient.getBasePath().equals(otherKnowledgeBeacon.apiClient.getBasePath());
		}		
	}
	
	@Override
	public int hashCode() {
		return this.apiClient.getBasePath().hashCode();
	}
	
	@Override
	public String toString() {
		if (getName() != null) {
			return "KnowledgeBeacon[url=" + getUrl() + ", name=" + getName() + "]";
		} else {
			return "KnowledgeBeacon[url=" + getUrl() + "]";
		}
	}
}