package com.tabamo.airvideo.connect.serialization;

import java.io.DataInput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ObjectDeserializer {
	private final DataReader dataReader;
	private final Map<Integer, Object> idToObjectMap = new HashMap();

	public ObjectDeserializer(DataInput dataInput) {
		//Check.argumentNotNull(dataInput, "dataInput");

		this.dataReader = new DataReader(dataInput);
	}

	protected ClassRegistry getClassRegistry() {
		return ClassRegistry.getInstance();
	}

	private Object readInteger() throws IOException {
		return Integer.valueOf(this.dataReader.readInteger());
	}

	private Object readLong() throws IOException {
		return Long.valueOf(this.dataReader.readLong());
	}

	private Object readTime() throws IOException {
		long time = this.dataReader.readLong();
		return new Date(time);
	}

	private Object readDouble() throws IOException {
		return Double.valueOf(this.dataReader.readDouble());
	}

	private Object readStringDefinition() throws IOException {
		int id = this.dataReader.readInteger();
		String string = this.dataReader.readString();
		this.idToObjectMap.put(Integer.valueOf(id), string);
		return string;
	}

	private Object readDataDefinition() throws IOException {
		int id = this.dataReader.readInteger();
		byte[] data = this.dataReader.readData();
		this.idToObjectMap.put(Integer.valueOf(id), data);
		return data;
	}

	private Object readArrayDefinition() throws IOException {
		int id = this.dataReader.readInteger();
		int size = this.dataReader.readInteger();
		List array = new ArrayList(size);
		this.idToObjectMap.put(Integer.valueOf(id), array);
		while (size > 0) {
			array.add(deserialize());
			size--;
		}
		return array;
	}

	private Object readSetDefinition() throws IOException {
		int id = this.dataReader.readInteger();
		int size = this.dataReader.readInteger();
		Set set = new HashSet(size);
		this.idToObjectMap.put(Integer.valueOf(id), set);
		while (size > 0) {
			set.add(deserialize());
			size--;
		}
		return set;
	}

	private Object readDictionaryDefinition() throws IOException {
		int id = this.dataReader.readInteger();
		int size = this.dataReader.readInteger();
		Map map = new HashMap();
		this.idToObjectMap.put(Integer.valueOf(id), map);
		while (size > 0) {
			Object key = deserialize();
			Object value = deserialize();
			map.put(key, value);
			size--;
		}
		return map;
	}

	private Object readObjectDefinition() throws IOException {
		int id = this.dataReader.readInteger();
		String className = this.dataReader.readString();
		int version = this.dataReader.readInteger();
		int propertiesCount = this.dataReader.readInteger();
		Class klass = getClassRegistry().getClassForName(className, version);
		if (klass == null) {
			throw new CanNotDeserializeObjectVersionException(
					"Couldn't find class " + className + " for version "
							+ version);
		}

		Object object;
		try {
			object = klass.newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		this.idToObjectMap.put(Integer.valueOf(id), object);

		while (propertiesCount > 0) {
			String key = this.dataReader.readString();
			Object value = deserialize();

			PropertyUtil.setProperty(object, key, value);

			propertiesCount--;
		}

		if ((object instanceof SerializationAware)) {
			SerializationAware aware = (SerializationAware) object;
			aware.deserializedVersion(version);
		}

		return object;
	}

	private Object readReference() throws IOException {
		int id = this.dataReader.readInteger();
		Object object = this.idToObjectMap.get(Integer.valueOf(id));
		if (object == null) {
			throw new IllegalStateException(
					"Invalid object reference in serialized data.");
		}
		return object;
	}

	public Object deserialize() throws IOException {
		byte type = this.dataReader.readByte();

		if (type == 105) {
			return readInteger();
		}
		if (type == 108) {
			return readLong();
		}
		if (type == 102) {
			return readDouble();
		}
		if (type == 116) {
			return readTime();
		}
		if (type == 110) {
			return null;
		}
		if (type == 115) {
			return readStringDefinition();
		}
		if (type == 120) {
			return readDataDefinition();
		}
		if (type == 97) {
			return readArrayDefinition();
		}
		if (type == 101) {
			return readSetDefinition();
		}
		if (type == 100) {
			return readDictionaryDefinition();
		}
		if (type == 111) {
			return readObjectDefinition();
		}
		if (type == 114) {
			return readReference();
		}

		return null;
	}

	public static class CanNotDeserializeObjectVersionException extends
			RuntimeException {
		private static final long serialVersionUID = 1L;

		public CanNotDeserializeObjectVersionException(String message) {
			super();
		}
	}
}
