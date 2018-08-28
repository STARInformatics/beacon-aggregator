package bio.knowledge.server.blackboard;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.ListUtils;

public final class Utilities {
	private Utilities() {
		
	}
	
	/**
	 * Converts a list into a list of sublists, each sublist being a batch of the
	 * original
	 * 
	 * @param list
	 * @param batchSize
	 * @return
	 */
	public static <T> List<List<T>> buildBatches(List<T> list, int batchSize) {
		List<List<T>> batches = new ArrayList<List<T>>();
		
		for (int i = 0; i < list.size(); i += batchSize) {
			batches.add(list.subList(i, Math.min(i + batchSize, list.size())));
		}
		
		return batches;
	}
}
