package bio.knowledge.server.controller;

import bio.knowledge.client.ApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class TaskProcessor {
	private final static Logger logger = LoggerFactory.getLogger(TaskProcessor.class);

	private final ExecutorService pool;

	public TaskProcessor(@Value("${executor.pool.size}") Integer poolSize) {
		pool = Executors.newFixedThreadPool(poolSize);
	}

	public void run(String queryId, ThrowingRunnable runnable) {
		pool.execute(() -> {
			try {
				runnable.run();
			} catch (ApiException e) {
				logger.warn("Beacon failure: {}", queryId, e);
			} catch (Throwable e) {
				logger.error("Server failure: {}", queryId, e);
			}
		});
	}
}
