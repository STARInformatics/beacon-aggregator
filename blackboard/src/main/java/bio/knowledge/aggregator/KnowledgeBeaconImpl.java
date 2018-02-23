/*-------------------------------------------------------------------------------
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-17 STAR Informatics / Delphinai Corporation (Canada) - Dr. Richard Bruskiewich
 * Copyright (c) 2017    NIH National Center for Advancing Translational Sciences (NCATS)
 * Copyright (c) 2015-16 Scripps Institute (USA) - Dr. Benjamin Good
 *                       
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *-------------------------------------------------------------------------------
 */
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
public class KnowledgeBeaconImpl {
		
	private String name;
	private String description;
	private String contact;
	private String wraps;
	private String repo;
	
	private boolean isEnabled;
	
	private final ApiClient apiClient;
	
	public KnowledgeBeaconImpl(String id, String url) {
		this(id, url, true);
	}
	
	public KnowledgeBeaconImpl(String id, String url, boolean isEnabled) {
		url = validateAndFixUrl(url);
		
		this.isEnabled = isEnabled;
		
		this.apiClient = new ApiClient(id, url);
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
	
	/**
	 * 
	 * @return Knowledge Beacon identifier
	 */
	public String getId() {
		return apiClient.getBeaconId();
	}

	/**
	 * 
	 * @return Knowledge Beacon name
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * @param name of Knowledge Beacon
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * 
	 * @return Knowledge Beacon description
	 */
	public String getDescription() {
		return this.description;
	}
	
	/**
	 * 
	 * @param description of Knowledge Beacon
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	/**
	 * 
	 * @return url of Knowledge Beacon
	 */
	public String getUrl() {
		return this.apiClient.getBasePath();
	}
	
	/**
	 * 
	 * @return contact person for Knowledge Beacon
	 */
	public String getContact() {
		return contact;
	}
	
	/**
	 * 
	 * @param contact person for Knowledge Beacon
	 */
	public void setContact(String contact) {
		this.contact = contact;
	}
	
	/**
	 * 
	 * @return description of what knowledge source the Knowledge Beacon API wraps
	 */
	public String getWraps() {
		return wraps;
	}

	/**
	 * 
	 * @param wraps description of what knowledge source the Knowledge Beacon API wraps
	 */
	public void setWraps(String wraps) {
		this.wraps = wraps;
	}

	/**
	 * 
	 * @return Github repository URI where Knowledge Beacon code is archived
	 */
	public String getRepo() {
		return repo;
	}

	/**
	 * 
	 * @param repo Github repository URI where Knowledge Beacon code is archived
	 */
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
		} else if (! (other instanceof KnowledgeBeaconImpl)) {
			return false;
		} else {
			KnowledgeBeaconImpl otherKnowledgeBeacon = (KnowledgeBeaconImpl) other;
			
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