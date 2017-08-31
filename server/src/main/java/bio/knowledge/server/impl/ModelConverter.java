package bio.knowledge.server.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class ModelConverter {
	
	/**
	 * Creates an instance of {@code destClass} and populates its fields by calling
	 * its setters with input from the getters of the same name on {@code srcObject}.
	 * Objects gotten from the getters of {@code srcObject} are recursively converted
	 * if they cannot be directly given to a setter of the same name.
	 * 
	 * @param srcObject
	 * @param destClass
	 * @return
	 */
	public static <T, S> T convert(S srcObject, Class<T> destClass) {
		try {
			T destObject = destClass.newInstance();
			
			for (Method method : srcObject.getClass().getMethods()) {
				if (method.getName().startsWith("get")) {
					
					Method getter = method;
					Class<?> returnType = getter.getReturnType();
					
					String setterName = "set" + getter.getName().substring(3);
					
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
