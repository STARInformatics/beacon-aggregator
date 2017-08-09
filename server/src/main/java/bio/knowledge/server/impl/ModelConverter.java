package bio.knowledge.server.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class ModelConverter {
	
	public static <T, S> T convert(S source, Class<T> destClass) {
		try {
			T destObject = destClass.newInstance();
			
			for (Method method : source.getClass().getMethods()) {
				if (method.getName().startsWith("get")) {
					String name = "set" + method.getName().substring(3);
					System.out.println(name);
					if (name.equals("setClass")) continue;
					Method setter = destObject.getClass().getMethod(name, method.getReturnType());
					setter.invoke(destObject, method.getReturnType().cast(method.invoke(source)));		
				}
			}
			
			return destObject;
			
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			throw new RuntimeException(e);
		}
	}
}
