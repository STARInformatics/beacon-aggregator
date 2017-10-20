package bio.knowledge.model.aggregator;

public class Aggregator {
	
	public static final String PREFIX = "kba";
	
	public static String CURIE(String id) {
		return PREFIX+":"+id;
	}
}
