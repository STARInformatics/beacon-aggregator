package bio.knowledge.client.impl;

import java.util.List;

import bio.knowledge.client.Pair;

/**
 * Extends a regular ApiClient with the ability to
 * ask for its associated beacon ID and latest query.
 * Used to enable error-logging.
 * 
 * @author Meera Godden
 *
 */
public class ApiClient extends bio.knowledge.client.ApiClient {
	
	private String beaconId;
	private String query;
	
	public ApiClient(String beaconId, String basePath) {
		super();
		setBasePath(basePath);
		this.beaconId = beaconId;
	}

	@Override
    public String buildUrl(String path, List<Pair> queryParams) {
		query = super.buildUrl(path, queryParams);
		System.out.println(query);
		return query;
	}

	public String getQuery() {
		return query;
	}

	public String getBeaconId() {
		return beaconId;
	}
	
}
