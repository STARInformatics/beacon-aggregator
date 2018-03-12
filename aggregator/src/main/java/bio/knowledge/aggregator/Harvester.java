package bio.knowledge.aggregator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.Async;

import bio.knowledge.aggregator.harvest.Query;


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
public class Harvester<B, S> implements Query {
	
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
		public Map<KnowledgeBeaconImpl, List<BeaconItemWrapper<B>>> getDataFromBeacons(Integer pageNumber, Integer pageSize)
				throws InterruptedException, ExecutionException, TimeoutException;
	}
	
	public interface DatabaseInterface<B, S> {
		@Async public boolean cacheData(KnowledgeBeaconImpl kb, BeaconItemWrapper<B> data, String queryString);
		public List<S> getDataPage(String keywords, String conceptTypes, Integer pageNumber, Integer pageSize, String queryString);
	}
	
	public interface RelevanceTester<B> {
		public boolean isItemRelevant(BeaconItemWrapper<B> dataItem);
	}
	
	private BeaconInterface<B> beaconInterface;
	private DatabaseInterface<B, S> databaseInterface;
	private RelevanceTester<B> relevanceTester;
	private TaskExecutor executor;
	private QueryTracker<S> queryTracker;
	
	public Harvester(
			BeaconInterface<B> beaconInterface,
			DatabaseInterface<B,S> databaseInterface,
			RelevanceTester<B> relevanceTester,
			TaskExecutor executor,
			QueryTracker<S> queryTracker
	) {
		this.beaconInterface = beaconInterface;
		this.databaseInterface = databaseInterface;
		this.relevanceTester = relevanceTester;
		this.executor = executor;
		this.queryTracker = queryTracker;
	}
	
	@Async public CompletableFuture<List<S>> initiateConceptHarvest(
			String keywords,
			String conceptTypes,
			Integer pageNumber,
			Integer pageSize
	) {
		String queryString = makeQueryString("concept", keywords, conceptTypes);
		
		int threshold = makeThreshold(pageNumber, pageSize);
		
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
							Map<KnowledgeBeaconImpl, List<BeaconItemWrapper<B>>> m = beaconInterface.getDataFromBeacons(N, PAGE_SIZE);
							
							List<BeaconItemWrapper<B>> data = new ArrayList<BeaconItemWrapper<B>>();
							
							for (KnowledgeBeaconImpl kb : m.keySet()) {
								List<BeaconItemWrapper<B>> dataPage = m.get(kb);
								for (BeaconItemWrapper<B> beaconItem : dataPage) {
									data.add(beaconItem);
									if (databaseInterface.cacheData(kb, beaconItem, queryString)) {
										dataCount += 1;
									}
								}
							}
							
							System.out.println("Data found: " + Integer.toString(dataCount));
							
							if (dataCount >= threshold && !future.isDone()) {
								future.complete(databaseInterface.getDataPage(keywords, conceptTypes, pageNumber, pageSize, queryString));
							}
							
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
					
					if (!future.isDone()) {
						future.complete(databaseInterface.getDataPage(keywords, conceptTypes, pageNumber, pageSize, queryString));
					}
					
					System.out.println(">Finished " + queryString);
				}
			});
		}
		
		return queryTracker.getFuture(queryString);
	}
	
	public final static int sanitizeInt(Integer i) {
		return i != null && i >= 1 ? i : 1;
	}

	protected final String[] split(String terms, String deliminator) {
		return terms != null && !terms.isEmpty() ? terms.split(deliminator) : null;
	}

	protected final String[] split(String terms) {
		return split(terms, " ");
	}
	
	public final static int makeThreshold(Integer pageNumber, Integer pageSize) {
		pageNumber = sanitizeInt(pageNumber);
		pageSize = sanitizeInt(pageSize);
		return ((pageNumber - 1) * pageSize) + pageSize;
	}
}
