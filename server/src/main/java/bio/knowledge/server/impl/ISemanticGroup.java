/**
 * 
 */
package bio.knowledge.server.impl;


/**
 * This interface needs to be patched into every 
 * Server* class which have properties linked to
 * Beacon SemanticGroup methods
 * 
 * @author Richard
 *
 */
public interface ISemanticGroup {
	/*
	 * Impedance mismatch between the Beacon API
	 * and the Server Beacon Aggregator API
	 */
	abstract String getType();

	default String getSemanticGroup() {
		return getType();
	}

	abstract void setType(String type);

	default void setSemanticGroup(String type) {
		setType(type);
	}

	/* 
	 * Until the two API's are harmonized, the lack 
	 * of the above methods needs to be fixed
	 * using this wrapper class for ServerConcept
	 */		
}
