/**
 * 
 */
package bio.knowledge.aggregator;

/**
 * @author richard
 *
 */
public interface KnowledgeBeacon {
	
	/**
	 * 
	 * @return Beacon index ID
	 */
	Integer getId();
	
	/**
	 * 
	 * @return
	 */
	String getName();
	
	/**
	 * 
	 * @return
	 */
	String getDescription();
	
	/**
	 * 
	 * @return
	 */
	String getUrl();
	
	/**
	 * 
	 * @return responsible developer
	 */
	String getContact();
	
	/**
	 * 
	 * @return resource target of beacon wrapping
	 */
	String getWraps();
	
	/**
	 * 
	 * @return code repository containing the beacon code
	 */
	String getRepo();

	/**
	 * 
	 * @return
	 */
	boolean isEnabled();
}
