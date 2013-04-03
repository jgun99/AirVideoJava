package com.tabamo.airvideo.connect.serialization;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

class SerializableUtil {
	private static Map<String, Set<String>> propertiesCache = new ConcurrentHashMap();

	private static Set<String> getSerializableProperties(Class<?> objectClass) {
		Set result = new HashSet();

		Class klass = objectClass;
		while (klass != null) {
			for (Field f : klass.getDeclaredFields()) {
				if (f.getAnnotation(Serialized.class) != null) {
					result.add(f.getName());
				}
			}
			klass = klass.getSuperclass();
		}

		klass = objectClass;
		while (klass != null) {
			for (Method m : klass.getDeclaredMethods()) {
				if ((m.getAnnotation(Serialized.class) != null)
						&& (m.getReturnType() != Void.TYPE)
						&& (m.getParameterTypes().length == 0)) {
					String name = m.getName();
					if ((name.startsWith("get")) && (name.length() > 3)) {
						String property = name.substring(3, 4).toLowerCase(
								Locale.ENGLISH)
								+ name.substring(4);
						result.add(property);
					} else if ((m.getName().startsWith("is"))
							&& (m.getName().length() > 2)) {
						String property = name.substring(2, 3).toLowerCase(
								Locale.ENGLISH)
								+ name.substring(3);
						result.add(property);
					}
				}
			}
			klass = klass.getSuperclass();
		}

		return result;
	}

	public static Set<String> getSerializableProperties(Object object) {
		//Check.argumentNotNull(object, "object");

		Class klass = object.getClass();
		String className = klass.getName();
		Set properties = (Set) propertiesCache.get(className);
		if (properties == null) {
			properties = getSerializableProperties(klass);
			propertiesCache.put(className, properties);
		}
		return properties;
	}
}