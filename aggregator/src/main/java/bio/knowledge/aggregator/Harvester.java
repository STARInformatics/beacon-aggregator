package bio.knowledge.aggregator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.Async;

import bio.knowledge.aggregator.harvest.QueryUtil;


/**
 * 
 * @author lance
 * 
 * TODO: There probably isn't any need to inject the QueryTracker dependency
 *
 * @param <B>
 * 		The beacon object, e.g. BeaconConcept, that is being received from the knowledge beacon layer
 * @param <S>
 * 		The server object, e.g. ServerConcept, that is being sent to the controller layer
 */
public class Harvester<B, S> implements QueryUtil {
	
private static final int PAGE_SIZE = 2;
	
	private static final int EXTRA_TIME_INCREMENT_AMOUNT = 5;
	private static final int MAX_TIMEOUT = 60;
	
	/**
	 * TODO: Consider adding timeout parameters to this method, so that harvester can control the timeout
	 * more directly.
	 * @author lance
	 *
	 * @param <B>
	 */
	public interface BeaconInterface<B> {
		public Map<KnowledgeBeacon, List<BeaconItemWrapper<B>>> getDataFromBeacons(Integer pageNumber, Integer pageSize)
				throws InterruptedException, ExecutionException, TimeoutException;
	}
	

	
	public interface RelevanceTester<B> {
		public boolean isItemRelevant(BeaconItemWrapper<B> dataItem);
	}
	
	private final BeaconInterface<B> beaconInterface;
	private final DatabaseInterface<B, S> databaseInterface;
	private final RelevanceTester<B> relevanceTester;
	private final TaskExecutor executor;
	private final QueryTracker<S> queryTracker;
	private final List<Integer> beaconsToHarvest;
	
	public Harvester(
			ConceptsQueryInterface query,
			BeaconInterface<B> beaconInterface,
			DatabaseInterface<B,S> databaseInterface,
			RelevanceTester<B> relevanceTester,
			TaskExecutor executor,
			QueryTracker<S> queryTracker,
			List<Integer> beaconsToHarvest
	) {
		this.beaconInterface = beaconInterface;
		this.databaseInterface = databaseInterface;
		this.relevanceTester = relevanceTester;
		this.executor = executor;
		this.queryTracker = queryTracker;
		this.beaconsToHarvest = beaconsToHarvest;
	}
	
	@Async public CompletableFuture<List<S>> initiateConceptHarvest(
			ConceptsQueryInterface query
	) {
		String queryString = makeQueryString("concept", query.getKeywords(), query.getConceptTypes());
		
		int threshold = makeThreshold(query.getPageNumber(), query.getPageSize());
		
		if (!queryTracker.isWorking(queryString)) {
			CompletableFuture<List<S>> future = new CompletableFuture<List<S>>();
			queryTracker.addQuery(queryString, future);
			
			executor.execute(() -> {
				System.out.println(">Beginning getExecutor() " + queryString);
				try {
					int N = 1;
					int dataCount = 0;
					
					while (queryTracker.isWorking(queryString)) {
						try {
							Timer.setTime("total cache loop " + Integer.toString(N));
							Map<KnowledgeBeacon, List<BeaconItemWrapper<B>>> m = 
												beaconInterface.getDataFromBeacons(N, PAGE_SIZE);
							
							List<BeaconItemWrapper<B>> data = new ArrayList<BeaconItemWrapper<B>>();
							
							for (KnowledgeBeacon kb : m.keySet()) {
								List<BeaconItemWrapper<B>> dataPage = m.get(kb);
								for (BeaconItemWrapper<B> beaconItem : dataPage) {
									data.add(beaconItem);
									if (databaseInterface.cacheData(kb, beaconItem, queryString)) {
										dataCount += 1;
									}
								}
							}
							
							System.out.println("Data found: " + Integer.toString(dataCount));
							
							/*
							 * TODO: This legacy code doesn't seem correct...
							 * Beacon data is simply loaded into the database and the front end 
							 * notified about the available data in the database 
							 * 
							 * if (dataCount >= threshold && !future.isDone()) {
								future.complete(
										databaseInterface.getDataPage(
												query.getKeywords(), query.getConceptTypes(), 
												query.getPageNumber(), query.getPageSize(), queryString));
							}
							 */
							
							boolean isPageRelevant = false;
							
							for (BeaconItemWrapper<B> beaconItem : data) {
								if (relevanceTester.isItemRelevant(beaconItem)) {
									isPageRelevant = true;
									break;
								}
							}
							
							if (!isPageRelevant) {
								break;
							}
							
							Timer.printTime("total cache loop " + Integer.toString(N));
							
							N += 1;
							
							
						} catch (TimeoutException e) {
							Timer.increaseExtraTime(EXTRA_TIME_INCREMENT_AMOUNT);
							
							if (Timer.isExtraTimeGreaterThan(MAX_TIMEOUT)) {
								break;
							}
						} catch (Exception e) {
							e.printStackTrace();
							break;
						}
					}
					
				} finally {
					Timer.resetExtraTime();
					queryTracker.removeQuery(queryString);
					
					/*
					 * TODO: data is no longer retrieved here from the database?
					 * 					if (!future.isDone()) {
						future.complete(databaseInterface.getDataPage(
								query.getKeywords(), query.getConceptTypes(), 
								query.getPageNumber(), query.getPageSize(), queryString));
					}
					 */

					
					System.out.println(">Finished " + queryString);
				}
			});
		}
		
		return queryTracker.getFuture(queryString);
	}
	
	public final static int sanitizeInt(Integer i) {
		return i != null && i >= 1 ? i : 1;
	}

	protected final String[] split(String terms, String delimiter) {
		return terms != null && !terms.isEmpty() ? terms.split(delimiter) : null;
	}

	protected final String[] split(String terms) {
		return split(terms, " ");
	}
	
	public final static int makeThreshold(Integer pageNumber, Integer pageSize) {
		pageNumber = sanitizeInt(pageNumber);
		pageSize = sanitizeInt(pageSize);
		return ((pageNumber - 1) * pageSize) + pageSize;
	}
	
	public List<Integer> getBeaconsToHarvest() {
		return beaconsToHarvest;
	}
}
