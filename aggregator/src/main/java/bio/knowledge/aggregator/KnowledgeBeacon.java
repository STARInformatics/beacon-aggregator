package bio.knowledge.aggregator;

import java.net.URI;
import java.net.URISyntaxException;

import bio.knowledge.client.impl.ApiClient;

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
	private String contact;
	private String wraps;
	private String repo;
	
	private boolean isEnabled;
	
	private final ApiClient apiClient;
	
	public KnowledgeBeacon(String id, String url) {
		this(id, url, true);
	}
	
	public KnowledgeBeacon(String id, String url, boolean isEnabled) {
		url = validateAndFixUrl(url);
		
		this.id = id;
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
	
	public String getId() {
		return id;
	}

	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getDescription() {
		return this.description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getUrl() {
		return this.apiClient.getBasePath();
	}
	
	public String getContact() {
		return contact;
	}
	
	public void setContact(String contact) {
		this.contact = contact;
	}
	
	public String getWraps() {
		return wraps;
	}

	public void setWraps(String wraps) {
		this.wraps = wraps;
	}

	public String getRepo() {
		return repo;
	}

	public void setRepo(String repo) {
		this.repo = repo;
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