/**
 * 
 */
package bio.knowledge.aggregator;

import java.util.List;

import org.springframework.scheduling.annotation.Async;

/**
 * @author richard
 *
 */
public interface DatabaseInterface<B, S> {
	@Async public boolean cacheData(KnowledgeBeacon kb, BeaconItemWrapper<B> data, String queryString);
	public List<S> getDataPage(String keywords, String conceptTypes, Integer pageNumber, Integer pageSize, String queryString);
}