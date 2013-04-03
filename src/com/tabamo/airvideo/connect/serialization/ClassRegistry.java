package com.tabamo.airvideo.connect.serialization;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class ClassRegistry {
	private final Map<Class<?>, ClassWrapper> classToWrapper = new ConcurrentHashMap();
	private final ConcurrentMap<String, List<ClassWrapper>> nameToWrappers = new ConcurrentHashMap();

	private static ClassRegistry instance = new ClassRegistry();

	public void registerClass(Class<?> klass) {
		ClassWrapper wrapper = new ClassWrapper(klass);
		this.classToWrapper.put(klass, wrapper);

		List newList = new CopyOnWriteArrayList();
		List existing = (List) this.nameToWrappers.putIfAbsent(
				wrapper.getName(), newList);
		if (existing == null) {
			existing = newList;
		}
		existing.add(wrapper);
	}

	public static ClassRegistry getInstance() {
		return instance;
	}

	String getNameOfClass(Class<?> klass) {
		ClassWrapper wrapper = (ClassWrapper) this.classToWrapper.get(klass);
		return wrapper != null ? wrapper.getName() : null;
	}

	Class<?> getClassForName(String name, int version) {
		List<ClassWrapper> wrappers = (List) this.nameToWrappers.get(name);
		
		if (wrappers != null) {
			ClassWrapper universal = null;
			
			for (ClassWrapper wrapper : wrappers) {
				if (wrapper.canDeserializeVersion(version)) {
					return wrapper.getWrappedClass();
				}
				if (wrapper.canDeserializeAnyVersion()) {
					universal = wrapper;
				}
			}
			if (universal != null) {
				return universal.getWrappedClass();
			}
		}
		return null;
	}

	int getClassVersion(Class<?> klass) {
		ClassWrapper wrapper = (ClassWrapper) this.classToWrapper.get(klass);
		return wrapper != null ? wrapper.getVersion() : -1;
	}

	private static class ClassWrapper {
		final Class<?> wrappedClass;
		private final String name;
		private final int version;
		private final int[] deserializeVersions;

		public ClassWrapper(Class<?> klass) {
			//Check.argumentNotNull(klass, "klass");
			Serializable annotation = (Serializable) klass
					.getAnnotation(Serializable.class);
			if (annotation == null) {
				throw new IllegalArgumentException("Class '" + klass
						+ "' must have serializable annotation.");
			}
			this.name = annotation.name();
			this.version = annotation.version();
			this.deserializeVersions = annotation.deserializeVersions();
			this.wrappedClass = klass;
		}

		public Class<?> getWrappedClass() {
			return this.wrappedClass;
		}

		public String getName() {
			return this.name;
		}

		public boolean canDeserializeAnyVersion() {
			return (this.version == -1)
					&& (this.deserializeVersions.length == 0);
		}

		public boolean canDeserializeVersion(int version) {
			for (int i : this.deserializeVersions) {
				if (i == version) {
					return true;
				}
			}

			if ((this.version == -1) && (this.deserializeVersions.length > 0)) {
				return false;
			}

			return this.version == version;
		}

		public int getVersion() {
			return this.version;
		}
	}
}