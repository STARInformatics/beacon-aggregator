package bio.knowledge.aggregator;

import java.util.HashMap;
import java.util.Map;

import org.springframework.scheduling.annotation.Async;

public class Timer {
	
	private static final Map<String, Long> timerMap = new HashMap<String, Long>();
	
	@Async public static void setTime(String name) {
		timerMap.put(name, System.currentTimeMillis());
	}
	
	@Async public static void printTime(String name) {
		try {
			Long start = timerMap.get(name);
			Long now = System.currentTimeMillis();
			Long time = now - start;
			System.out.println("TIME FOR " + name + " is " + Double.toString(time / 1000.) + "sec");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static int extraTime = 0;
	
	public static void increaseExtraTime(int amount) {
		extraTime += amount;
	}
	
	public static boolean isExtraTimeGreaterThan(int amount) {
		return extraTime > amount;
	}
	
	public static void resetExtraTime() {
		extraTime = 0;
	}

	public static int getExtraTime() {
		return extraTime;
	}

}
