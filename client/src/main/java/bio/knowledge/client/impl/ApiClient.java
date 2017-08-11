package bio.knowledge.client.impl;

import java.util.List;
import java.util.Map;

import com.squareup.okhttp.Call;

import bio.knowledge.client.ApiException;
import bio.knowledge.client.Pair;
import bio.knowledge.client.ProgressRequestBody;

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
		return query = super.buildUrl(path, queryParams);
	}

	public String getQuery() {
		return query;
	}

	public String getBeaconId() {
		return beaconId;
	}
	
}
