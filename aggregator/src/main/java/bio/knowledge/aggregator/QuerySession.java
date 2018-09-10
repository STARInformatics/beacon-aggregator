package bio.knowledge.aggregator;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import bio.knowledge.model.aggregator.QueryTracker;

public interface QuerySession<Q> {
	
	public void clearBeaconCall(Integer beaconId);

	/**
	 * 
	 * @param query
	 * @return
	 */
	public String makeQueryString();
	
	/**
	 * 
	 * @return
	 */
	public int makeThreshold();
	
	/**
	 * 
	 * @return
	 */
	public Q getQuery();

	/**
	 * 
	 * @return
	 */
	public List<Integer> getQueryBeacons();
	
	/**
	 * 
	 * @param tracker
	 */
	public void setQueryTracker(QueryTracker tracker);
	
	/**
	 * 
	 * @return
	 */
	public QueryTracker getQueryTracker();
}
