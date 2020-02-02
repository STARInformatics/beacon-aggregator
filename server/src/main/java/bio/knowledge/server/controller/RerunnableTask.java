package bio.knowledge.server.controller;

public class RerunnableTask {
	private final Runnable runnable;
	private final String queryId;
	
	private int runCount = 0;
	
	public RerunnableTask(String queryId, Runnable runnable) {
		this.runnable = runnable;
		this.queryId = queryId;
	}
	
	public int getRunCount() {
		return runCount;
	}
	
	public String getQueryId() {
		return queryId;
	}
	
	public void run() {
		runCount++;
		runnable.run();
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + "[queryId=" + queryId + ", runCount=" + runCount + "]";
	}
}