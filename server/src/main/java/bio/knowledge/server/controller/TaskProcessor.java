package bio.knowledge.server.controller;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.annotation.PostConstruct;

import bio.knowledge.client.ApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import scala.reflect.internal.Trees;

@Service
public class TaskProcessor {
	private final static Logger logger = LoggerFactory.getLogger(TaskProcessor.class);
	
	private final ConcurrentLinkedQueue<Task> queue = new ConcurrentLinkedQueue<>();

	public void add(String queryId, ThrowingRunnable runnable) {
		queue.add(new Task(queryId, runnable));
	}

	@PostConstruct
	private void start() {
		Timer timer = new Timer();

		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				while (!queue.isEmpty()) {
					Task task = queue.poll();

					try {
						task.run();
					} catch (ApiException e) {
						logger.warn("Beacon failure: {}", task, e);
					} catch (Exception e) {
						logger.error("Server failure: {}", task, e);
					} catch (Throwable t) {
						logger.error("Server failure: {}", task, t);
					}
				}
			}
		};

		timer.schedule(task, 500, 1000);
	}

	private static class Task {
		private final ThrowingRunnable runnable;
		private final String queryId;
		private Long startTime = null;

		public Task(String queryId, ThrowingRunnable runnable) {
			this.runnable = runnable;
			this.queryId = queryId;
		}

		public String getQueryId() {
			return queryId;
		}

		public void run() throws Exception {
			if (startTime != null) {
				throw new IllegalStateException("Cannot be rerun: " + this);
			}

			startTime = System.currentTimeMillis();

			runnable.run();
		}

		@Override
		public String toString() {
			Long duration = startTime != null ? System.currentTimeMillis() - startTime : null;
			return getClass().getSimpleName() + "[queryId=" + queryId + ", duration=" + duration + "]";
		}
	}
}
