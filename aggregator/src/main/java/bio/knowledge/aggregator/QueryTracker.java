package bio.knowledge.aggregator;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class QueryTracker<S> {
	
	private final static int STARTING_PAGE = 1;
	
	private final Set<Query<S>> queries = new HashSet<Query<S>>();
	
	@Async private Query<S> getByString(String queryString) {
		for (Query<S> query : queries) {
			if (query.getString().equals(queryString)) {
				return query;
			}
		}
		
		return null;
	}
	
	@Async public boolean isWorking(String queryString) {
		return getByString(queryString) != null;
	}
	
	@Async public void addQuery(String queryString, CompletableFuture<List<S>> future) {
		if (!isWorking(queryString)) {
			this.queries.add(new Query<S>(queryString, STARTING_PAGE, future));
		}
	}
	
	@Async public void removeQuery(String queryString) {
		Query<S> query = getByString(queryString);
		queries.remove(query);
	}
	
	@Async public int getPageNumber(String queryString) {
		Query<S> query = getByString(queryString);
		return query.getPageNumber();
	}
	
	@Async public void incrementPageNumber(String queryString) {
		Query<S> query = getByString(queryString);
		query.incrementPageNumber();
	}
	
	@Async public CompletableFuture<List<S>> getFuture(String queryString) {
		Query<S> query = getByString(queryString);
		return query.getFuture();
	}
	
	private class Query<T> {
		private final String queryString;
		private final CompletableFuture<List<T>> future;
		private int pageNumber = 1;
		
		private Query(String queryString, int pageNumber, CompletableFuture<List<T>> future) {
			this.queryString = queryString;
			this.pageNumber = pageNumber;
			this.future = future;
		}
		
		private String getString() {
			return this.queryString;
		}
		
		private int getPageNumber() {
			return this.pageNumber;
		}
		
		private void incrementPageNumber() {
			this.pageNumber += 1;
		}
		
		private CompletableFuture<List<T>>getFuture() {
			return this.future;
		}
		
		@Override
		public boolean equals(Object other) {
			if (other instanceof Query) {
				@SuppressWarnings("unchecked")
				Query<S> otherQuery = (Query<S>) other;
				return otherQuery.getString().equals(getString());
			} else {
				return false;
			}
		}
	}

}