/**
 * 
 */
package bio.knowledge.aggregator.blackboard;

/**
 * @author richard
 *
 */
public interface Query {
	
	default public String makeQueryString(String name, Object... objects) {
		String queryString = name + ":";
		for (Object object : objects) {
			if (object != null) {
				queryString += object.toString();
			}
			queryString += ";";
		}
		return queryString;
	}
	
}
