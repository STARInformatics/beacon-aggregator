package bio.knowledge.aggregator;

public interface QuerySession<Q> {

	/**
	 * 
	 * @param query
	 * @return
	 */
	public String makeQueryString();
	
	public int makeThreshold();
	
	public Q getQuery();
}
