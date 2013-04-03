package com.tabamo.airvideo.connect.serialization;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PropertyUtil {
	private static final Setter NO_SETTER = new Setter() {
		public void set(Object object, Object value) {
		}
	};

	private static final Getter NO_GETTER = new Getter() {
		public Object get(Object object) {
			return null;
		}
	};

	private static Map<ClassPropertyKey, Setter> setterMap = new ConcurrentHashMap();
	private static Map<ClassPropertyKey, Getter> getterMap = new ConcurrentHashMap();

	public static Object convertIfNecessary(Class<?> type, Object object) {
		if ((object instanceof Number)) {
			Number number = (Number) object;
			if ((type.equals(Byte.class)) || (type.equals(Byte.TYPE))) {
				return Byte.valueOf(number.byteValue());
			}
			if ((type.equals(Short.class)) || (type.equals(Short.TYPE))) {
				return Short.valueOf(number.shortValue());
			}
			if ((type.equals(Integer.class)) || (type.equals(Integer.TYPE))) {
				return Integer.valueOf(number.intValue());
			}
			if ((type.equals(Long.class)) || (type.equals(Long.TYPE))) {
				return Long.valueOf(number.longValue());
			}
			if ((type.equals(Float.class)) || (type.equals(Float.TYPE))) {
				return Float.valueOf(number.floatValue());
			}
			if ((type.equals(Double.class)) || (type.equals(Double.TYPE))) {
				return Double.valueOf(number.doubleValue());
			}
			if ((type.equals(Boolean.class)) || (type.equals(Boolean.TYPE))) {
				if (number.intValue() != 0)
					return Boolean.valueOf(true);
				return Boolean.valueOf(false);
			}
			if ((type.equals(Character.class)) || (type.equals(Character.TYPE))) {
				return Character.valueOf((char) number.intValue());
			}
			if (Enum.class.isAssignableFrom(type)) {
				Enum[] enums = enumValues(type);
				for (Enum e : enums) {
					if (e.ordinal() == number.intValue()) {
						return e;
					}
				}
			}
		}
		return object;
	}

	private static Enum<?>[] enumValues(Class<?> enumClass) {
		try {
			Method method = enumClass.getMethod("values", new Class[0]);
			return (Enum[]) method.invoke(null, new Object[0]);
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	private static Getter getGetter(Class<?> klass, String name) {
		Method m = getDeclaredMethod(klass, getMethodName("is", name));
		if (m != null) {
			if ((m.getReturnType().equals(Boolean.TYPE))
					|| (m.getReturnType().equals(Boolean.class))) {
				return new MethodGetter(m);
			}
		}
		m = getDeclaredMethod(klass, getMethodName("get", name));
		if (m != null) {
			return new MethodGetter(m);
		}
		Field f = getDeclaredField(klass, name);
		if (f != null) {
			return new FieldGetter(f);
		}
		return null;
	}

	private static Setter getSetter(Class<?> klass, String name) {
		Method m = getDeclaredMethod(klass, getMethodName("set", name));
		if (m != null) {
			return new MethodSetter(m);
		}

		Field f = getDeclaredField(klass, name);
		if (f != null) {
			return new FieldSetter(f);
		}

		return null;
	}

	private static Field getDeclaredField(Class<?> klass, String fieldName) {
		while (klass != null) {
			for (Field f : klass.getDeclaredFields()) {
				if (f.getName().equals(fieldName)) {
					f.setAccessible(true);
					return f;
				}
			}
			klass = klass.getSuperclass();
		}
		return null;
	}

	private static Method getDeclaredMethod(Class<?> klass, String methodName) {
		while (klass != null) {
			for (Method m : klass.getDeclaredMethods()) {
				if (m.getName().equals(methodName)) {
					m.setAccessible(true);
					return m;
				}
			}
			klass = klass.getSuperclass();
		}
		return null;
	}

	private static String getMethodName(String prefix, String propertyName) {
		return prefix + propertyName.substring(0, 1).toUpperCase()
				+ propertyName.substring(1);
	}

	public static Object getProperty(Object object, String name) {
		//Check.argumentNotNull(object, "object");
		//Check.argumentNotEmpty(name, "name");

		Class klass = object.getClass();
		ClassPropertyKey key = new ClassPropertyKey(klass, name);
		Getter getter = (Getter) getterMap.get(key);
		if (getter == null) {
			getter = getGetter(klass, name);
			if (getter == null) {
				getter = NO_GETTER;
				getterMap.put(key, getter);
			}
		}
		if (getter == NO_SETTER) {
			throw new RuntimeException("Object '" + object
					+ "' has no property called '" + name + "'.");
		}
		return getter.get(object);
	}

	public static void setProperty(Object object, String name, Object value) {
//		Check.argumentNotNull(object, "object");
//		Check.argumentNotEmpty(name, "name");

		Class klass = object.getClass();
		ClassPropertyKey key = new ClassPropertyKey(klass, name);
		Setter setter = (Setter) setterMap.get(key);
		if (setter == null) {
			setter = getSetter(klass, name);
			if (setter == null) {
				setter = NO_SETTER;
				setterMap.put(key, setter);
			}
		}
		if (setter == NO_SETTER) {
			throw new RuntimeException("Object '" + object
					+ "' has no property called '" + name + "'.");
		}
		setter.set(object, value);
	}

	private static class ClassPropertyKey {
		private final String className;
		private final String propertyName;

		public ClassPropertyKey(Class<?> klass, String propertyName) {
//			Check.argumentNotNull(klass, "klass");
//			Check.argumentNotNull(propertyName, "propertyName");

			this.className = klass.getName();
			this.propertyName = propertyName;
		}

		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (!(obj instanceof ClassPropertyKey)) {
				return false;
			}

			ClassPropertyKey rhs = (ClassPropertyKey) obj;
			return (this.className.equals(rhs.className))
					&& (this.propertyName.equals(rhs.propertyName));
		}

		public int hashCode() {
			return this.className.hashCode() + 131
					* this.propertyName.hashCode();
		}
	}

	private static class FieldGetter implements PropertyUtil.Getter {
		private final Field field;

		public FieldGetter(Field field) {
//			Check.argumentNotNull(field, "field");

			this.field = field;
		}

		public Object get(Object object) {
			try {
				return this.field.get(object);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	private static class FieldSetter implements PropertyUtil.Setter {
		private final Field field;

		public FieldSetter(Field field) {
//			Check.argumentNotNull(field, "field");

			this.field = field;
		}

		public void set(Object object, Object value) {
			try {
				value = PropertyUtil.convertIfNecessary(this.field.getType(),
						value);
				this.field.set(object, value);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	private static abstract interface Getter {
		public abstract Object get(Object paramObject);
	}

	private static class MethodGetter implements PropertyUtil.Getter {
		private final Method method;

		public MethodGetter(Method method) {
//			Check.argumentNotNull(method, "method");

			this.method = method;
		}

		public Object get(Object object) {
			try {
				return this.method.invoke(object, new Object[0]);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	private static class MethodSetter implements PropertyUtil.Setter {
		private final Method method;

		public MethodSetter(Method method) {
//			Check.argumentNotNull(method, "method");

			this.method = method;
		}

		public void set(Object object, Object value) {
			try {
				value = PropertyUtil.convertIfNecessary(
						this.method.getParameterTypes()[0], value);
				this.method.invoke(object, new Object[] { value });
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	private static abstract interface Setter {
		public abstract void set(Object paramObject1, Object paramObject2);
	}
}
