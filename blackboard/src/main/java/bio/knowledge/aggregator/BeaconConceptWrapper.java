package bio.knowledge.aggregator;

import bio.knowledge.client.model.BeaconConcept;

/**
 * A class that wraps an instance of {@link bio.knowledge.client.model.BeaconConcept}, and
 * acts as a container for some of the attributes of a concept that BeaconConcept does not
 * have.
 * @author lance
 *
 */
public class BeaconConceptWrapper implements BeaconItemWrapper<BeaconConcept> {
	private BeaconConcept item;
	private String clique;
	private String beacon;
	
	@Override
	public BeaconConcept getItem() {
		return item;
	}

	@Override
	public void setItem(BeaconConcept item) {
		this.item = item;
	}

	public String getClique() {
		return clique;
	}

	public void setClique(String clique) {
		this.clique = clique;
	}

	public String getBeacon() {
		return beacon;
	}

	public void setBeacon(String beacon) {
		this.beacon = beacon;
	}
	
}
