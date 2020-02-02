package bio.knowledge.server.controller;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class TaskProcessor {
	private final static Logger logger = LoggerFactory.getLogger(TaskProcessor.class);
	
	private final ConcurrentLinkedQueue<RerunnableTask> queue = new ConcurrentLinkedQueue<RerunnableTask>();

	public void add(String queryId, Runnable runnable) {
		queue.add(new RerunnableTask(queryId, runnable));
	}

	@PostConstruct
	private void start() {
		Timer timer = new Timer();

		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				while (!queue.isEmpty()) {
					RerunnableTask task = queue.poll();
					try {
						task.run();
					} catch (Exception e) {
						if (task.getRunCount() < 3) {
							logger.warn("Task failed, pushing back onto queue: " + task);
							queue.add(task);
						} else {
							logger.warn("Task failed, dropping: " + task);
						}
					}
				}
			}
		};

		timer.schedule(task, 500, 1000);
	}
}
