/**
 * 
 */
package bio.knowledge.aggregator;

import java.util.List;

/**
 * @author richard
 *
 */
public interface QueryPagingInterface {

	public int getPageNumber() ;

	public int getPageSize() ;
	
	public List<Integer> getQueryBeacons() ;

}
