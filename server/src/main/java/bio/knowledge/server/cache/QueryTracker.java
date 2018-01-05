package bio.knowledge.server.cache;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@SuppressWarnings("rawtypes")
public class QueryTracker {
	
	private final static int STARTING_PAGE = 1;
	
	private final Set<Query> queries = new HashSet<Query>();
	
	@Async private Query getByString(String queryString) {
		for (Query query : queries) {
			if (query.getString().equals(queryString)) {
				return query;
			}
		}
		
		return null;
	}
	
	@Async public boolean isWorking(String queryString) {
		return getByString(queryString) != null;
	}
	
	@Async public void addQuery(String queryString, CompletableFuture future) {
		if (!isWorking(queryString)) {
			this.queries.add(new Query(queryString, STARTING_PAGE, future));
		}
	}
	
	@Async public void removeQuery(String queryString) {
		Query query = getByString(queryString);
		queries.remove(query);
	}
	
	@Async public int getPageNumber(String queryString) {
		Query query = getByString(queryString);
		return query.getPageNumber();
	}
	
	@Async public void incrementPageNumber(String queryString) {
		Query query = getByString(queryString);
		query.incrementPageNumber();
	}
	
	@Async public CompletableFuture getFuture(String queryString) {
		Query query = getByString(queryString);
		return query.future;
	}
	
	private class Query {
		private final String queryString;
		private final CompletableFuture future;
		private int pageNumber = 1;
		
		private Query(String queryString, int pageNumber, CompletableFuture future) {
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
		
		private CompletableFuture getFuture() {
			return this.future;
		}
		
		@Override
		public boolean equals(Object other) {
			if (other instanceof Query) {
				Query otherQuery = (Query) other;
				return otherQuery.getString().equals(getString());
			} else {
				return false;
			}
		}
	}

}