/*-------------------------------------------------------------------------------
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-17 STAR Informatics / Delphinai Corporation (Canada) - Dr. Richard Bruskiewich
 * Copyright (c) 2017    NIH National Center for Advancing Translational Sciences (NCATS)
 * Copyright (c) 2015-16 Scripps Institute (USA) - Dr. Benjamin Good
 *                       
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *-------------------------------------------------------------------------------
 */
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
			throw new RuntimeException(e.getClass().getName()+" thrown: "+e.getMessage().toString()+e.getCause()!=null?e.getCause().getMessage():" Cause Unknown?");
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
