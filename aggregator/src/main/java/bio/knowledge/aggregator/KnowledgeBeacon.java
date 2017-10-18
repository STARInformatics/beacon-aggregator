package bio.knowledge.aggregator;

import bio.knowledge.client.impl.ApiClient;

public interface KnowledgeBeacon {

	String getId();

	String getName();

	ApiClient getApiClient();

}