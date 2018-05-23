package bio.knowledge.aggregator;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.springframework.core.task.TaskExecutor;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;

/**
 * Extending classes must be @Service annotated
 */
public abstract class BaseCache<T> {

	private static final int PAGE_SIZE = 2;
	
	private static final int EXTRA_TIME_INCREMENT_AMOUNT = 5;
	private static final int MAX_TIMEOUT = 60;
	
	abstract protected QueryTracker<T> getQueryTracker();
	
	abstract protected TaskExecutor getExecutor();
	
	public interface BeaconInterface<T> {
		public ResponseEntity<List<T>> getData(Integer pageNumber, Integer pageSize) throws InterruptedException, ExecutionException, TimeoutException;
	}
	
	public interface DatabaseInterface<T> {
		@Async public boolean cacheData(T data, String queryString);
		public List<T> getDataPage();
	}
	
	public interface RelevanceTester<T> {
		public boolean isPageRelevant(Collection<T> data);
	}
	
	protected CompletableFuture<List<T>> initiateHarvest(
			String queryString,
			Integer threashold,
			BeaconInterface<T> beaconInterface,
			DatabaseInterface<T> databaseInterface,
			RelevanceTester<T> relevanceTester
	) {
		if (!getQueryTracker().isWorking(queryString)) {
			CompletableFuture<List<T>> future = new CompletableFuture<List<T>>();
			getQueryTracker().addQuery(queryString, future);
			
			getExecutor().execute(() -> {
				System.out.println(">Beginning getExecutor() " + queryString);
				try {
					int N = 1;
					int dataCount = 0;
					
					while (getQueryTracker().isWorking(queryString)) {
						try {
							Timer.setTime("total cache loop " + Integer.toString(N));
							ResponseEntity<List<T>> responseEntity = beaconInterface.getData(N, PAGE_SIZE);
							
							List<T> dataPage = responseEntity.getBody();
							
							for (T dataItem : dataPage) {
								if (databaseInterface.cacheData(dataItem, queryString)) {
									dataCount += 1;
								}
							}
							
							System.out.println("Data found: " + Integer.toString(dataCount));
							
							if (dataCount >= threashold && !future.isDone()) {
								future.complete(databaseInterface.getDataPage());
							}
							
							if (!relevanceTester.isPageRelevant(dataPage) || dataPage.isEmpty()) {
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
					getQueryTracker().removeQuery(queryString);
					
					if (!future.isDone()) {
						future.complete(databaseInterface.getDataPage());
					}
					
					System.out.println(">Finished " + queryString);
				}
			});
		}
		
		return getQueryTracker().getFuture(queryString);
		
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
	
	protected final int makeThreshold(Integer pageNumber, Integer pageSize) {
		pageNumber = sanitizeInt(pageNumber);
		pageSize = sanitizeInt(pageSize);
		return ((pageNumber - 1) * pageSize) + pageSize;
	}

}