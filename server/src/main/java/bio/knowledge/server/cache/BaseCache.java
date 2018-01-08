package bio.knowledge.server.cache;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import bio.knowledge.server.impl.ControllerImpl;

/**
 * Extending classes must be @Service annotated
 */
public abstract class BaseCache {

	private static final int PAGE_SIZE = 2;
	
	private static final int EXTRA_TIME_INCREMENT_AMOUNT = 5;
	private static final int MAX_TIMEOUT = 60;
	
	private static final int TIMEOUT = 15;
	private static final TimeUnit TIME_UNIT = TimeUnit.SECONDS;


	@Autowired
	QueryTracker queryTracker;
	@Autowired
	TaskExecutor executor;
	
	@Autowired ControllerImpl impl;
	
	public interface BeaconInterface<T> {
		public ResponseEntity<List<T>> getData(Integer pageNumber, Integer pageSize) throws InterruptedException, ExecutionException, TimeoutException;
	}
	
	public interface DatabaseInterface<T> {
		@Async public boolean cacheData(T data);
		public List<T> getDataPage();
	}
	
	public interface RelevanceTester<T> {
		public boolean isPageRelevant(Collection<T> data);
	}
	
	
	
	protected <T> CompletableFuture<List<T>> initiateHarvest(
			String queryString,
			Integer threashold,
			BeaconInterface<T> beaconInterface,
			DatabaseInterface<T> databaseInterface,
			RelevanceTester<T> relevanceTester
	) {
		if (!queryTracker.isWorking(queryString)) {
			CompletableFuture<List<T>> future = new CompletableFuture<List<T>>();
			queryTracker.addQuery(queryString, future);
			
			executor.execute(() -> {
				System.out.println(">Beginning executor " + queryString);
				try {
					int N = 1;
					int dataCount = 0;
					
					while (queryTracker.isWorking(queryString)) {
						try {
							ControllerImpl.setTime("total cache loop " + Integer.toString(N));
							ResponseEntity<List<T>> r = beaconInterface.getData(N, PAGE_SIZE);
							
							List<T> concepts = r.getBody();
							
							for (T concept : concepts) {
								if (databaseInterface.cacheData(concept)) {
									dataCount += 1;
								}
							}
							
							System.out.println("Data found: " + Integer.toString(dataCount));
							
							if (dataCount >= threashold && !future.isDone()) {
								future.complete(databaseInterface.getDataPage());
							}
							
							if (!relevanceTester.isPageRelevant(concepts)) {
								break;
							}
							
							ControllerImpl.printTime("total cache loop " + Integer.toString(N));
							
							N += 1;
							
							
						} catch (TimeoutException e) {
							impl.increaseExtraTime(EXTRA_TIME_INCREMENT_AMOUNT);
							
							if (impl.isExtraTimeGreaterThan(MAX_TIMEOUT)) {
								break;
							}
						} catch (Exception e) {
							e.printStackTrace();
							break;
						}
					}
					
				} finally {
					impl.resetExtraTime();
					queryTracker.removeQuery(queryString);
					
					if (!future.isDone()) {
						future.complete(databaseInterface.getDataPage());
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
	
	protected final String makeQueryString(String name, Object... objects) {
		String queryString = name + ":";
		for (Object object : objects) {
			if (object != null) {
				queryString += object.toString();
			}
			queryString += ";";
		}
		return queryString;
	}
	
	protected final int makeThreashold(Integer pageNumber, Integer pageSize) {
		pageNumber = sanitizeInt(pageNumber);
		pageSize = sanitizeInt(pageSize);
		return ((pageNumber - 1) * pageSize) + pageSize;
	}

}