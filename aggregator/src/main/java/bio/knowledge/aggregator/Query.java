package bio.knowledge.aggregator;

public interface Query<Q> {

	/**
	 * 
	 * @param query
	 * @return
	 */
	public String makeQueryString();
	
	public int makeThreshold();
	
	public Q getQuery();
}
