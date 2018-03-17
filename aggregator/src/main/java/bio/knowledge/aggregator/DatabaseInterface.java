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
public interface DatabaseInterface<B, S, Q> {
	
	/**
	 * 
	 * @param kb
	 * @param data
	 * @param queryString
	 * @return
	 */
	@Async public boolean cacheData(
			KnowledgeBeacon kb, 
			BeaconItemWrapper<B> data, 
			String queryString
	);
	
	/**
	 * 
	 * @param keywords
	 * @param conceptTypes
	 * @param pageNumber
	 * @param pageSize
	 * @param queryString
	 * @return
	 */
	public List<S> getDataPage(
			Query<Q> query, 
			List<Integer> beacons
);
}