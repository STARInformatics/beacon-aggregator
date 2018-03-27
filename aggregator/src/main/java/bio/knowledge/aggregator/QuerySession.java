package bio.knowledge.aggregator;

import java.util.List;

public interface QuerySession<Q> {

	/**
	 * 
	 * @param query
	 * @return
	 */
	public String makeQueryString();
	
	public int makeThreshold();
	
	public Q getQuery();

	public List<Integer> getQueryBeacons();
}
