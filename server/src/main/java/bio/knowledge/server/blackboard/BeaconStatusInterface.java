/**
 * 
 */
package bio.knowledge.server.blackboard;

/**
 * @author richard
 *
 */
public interface BeaconStatusInterface {

	public void setBeacon(Integer beaconId);
	
	public void setStatus(Integer httpStatus);
	
	public void setDiscovered(Integer discovered);
	
	public void setProcessed(Integer processed);
	
	public void setCount(Integer count);
}
