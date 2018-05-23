package bio.knowledge.aggregator;

/**
 * A wrapper for items returned by the beacons. See {@link bio.knowledge.aggregator.BeaconConceptWrapper}.
 * Such wrappers are useful for holding extra information (e.g., source beacon, concept clique) that
 * the server items need but the beacon items do not contain.
 * 
 * @author lance
 *
 * @param <T>
 */
public interface BeaconItemWrapper<T> {
	public T getItem();
	public void setItem(T item);
}
