package bio.knowledge.server.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class ModelConverter {
	
	public static <T, S> T convert(S srcObject, Class<T> destClass) {
		try {
			T destObject = destClass.newInstance();
			
			for (Method method : srcObject.getClass().getMethods()) {
				if (method.getName().startsWith("get")) {
					
					Method getter = method;
					Class<?> returnType = getter.getReturnType();
					
					String setterName = "set" + getter.getName().substring(3);
					System.out.println(setterName);
					if (setterName.equals("setClass")) continue;
					
					Method setter = getSetter(destClass, setterName, returnType);
					Class<?> paramType = setter.getParameterTypes()[0];
					
					Object innerObject = returnType.cast(getter.invoke(srcObject));
					Object convertedInnerObject = innerObject;
					
					if ( !paramType.isAssignableFrom(returnType) ) {
						convertedInnerObject = convert(innerObject, paramType);
					}
					
					setter.invoke(destObject, convertedInnerObject);		
				}
			}
			
			return destObject;
			
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			throw new RuntimeException(e);
		}
	}
	
	private static <S, T> Method getSetter(Class<S> clazz, String name, Class<T> paramType) throws NoSuchMethodException {
		try {
			
			return clazz.getMethod(name, paramType);
		
		} catch (NoSuchMethodException e) {
			
			for (Method method: clazz.getMethods()) {
				if (method.getName().equals(name) && method.getParameterCount() == 1) {
					return method;
				}
			}
			
			throw e;
		}
	}
	
}
