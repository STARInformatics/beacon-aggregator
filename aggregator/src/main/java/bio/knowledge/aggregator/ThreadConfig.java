package bio.knowledge.aggregator;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class ThreadConfig {
	
	public static final int CORE_POOL_SIZE = 4;
	public static final int MAX_POOL_SIZE = 14;
	public static final String THREAD_NAME_PREFIX = "task_executor_thread_";
	
	@Bean
	public TaskExecutor makeTaskExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(CORE_POOL_SIZE);
		executor.setMaxPoolSize(MAX_POOL_SIZE);
		executor.setThreadNamePrefix(THREAD_NAME_PREFIX);
		executor.initialize();
		
		return executor;
	}

}
