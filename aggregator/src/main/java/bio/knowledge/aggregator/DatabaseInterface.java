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
	 * March 24, 2018 - new method to load data into blackboard graph database (replacing 'cacheData')
	 * 
	 * @param query
	 * @param results
	 * @param beacon
	 */
	public void loadData(QuerySession<Q> query, List<B> results, Integer beacon);
	
	/**
	 * 
	 * @param kb
	 * @param data
	 * @param queryString
	 * @return
	 */
	@Deprecated
	@Async public boolean cacheData(
			KnowledgeBeacon kb, 
			BeaconItemWrapper<B> data, 
			String queryString
	);
	
	/**
	 * 
	 * @param terms
	 * @param deliminator
	 * @return
	 */
	default public String[] split(String terms, String deliminator) {
		return terms != null && !terms.isEmpty() ? terms.split(deliminator) : null;
	}

	/**
	 * 
	 * @param terms
	 * @return
	 */
	default public String[] split(String terms) {
		return split(terms, " ");
	}
	
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
			QuerySession<Q> query, 
			List<Integer> beacons
	);
}