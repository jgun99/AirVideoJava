package com.tabamo.airvideo.connect.serialization;

import java.io.DataOutput;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ObjectSerializer {
	private final DataWriter dataWriter;
	private final Map<Object, Integer> objectToIdMap = new HashMap();
	private int idCounter;

	public ObjectSerializer(DataOutput dataOutput) {
		//Check.argumentNotNull(dataOutput, "dataOutput");

		this.dataWriter = new DataWriter(dataOutput);
	}

	protected ClassRegistry getClassRegistry() {
		return ClassRegistry.getInstance();
	}

	private void writeNumber(Number number) throws IOException {
		if ((number instanceof Long)) {
			this.dataWriter.writeByte((byte) 108);
			this.dataWriter.writeLong(number.longValue());
		} else if (((number instanceof Float)) || ((number instanceof Double))) {
			this.dataWriter.writeByte((byte) 102);
			this.dataWriter.writeDouble(number.doubleValue());
		} else {
			this.dataWriter.writeByte((byte) 105);
			this.dataWriter.writeInteger(number.intValue());
		}
	}

	private boolean writeValue(Object object) throws IOException {
		if (object == null) {
			this.dataWriter.writeByte((byte) 110);
			return true;
		}
		if ((object instanceof Boolean)) {
			boolean b = ((Boolean) object).booleanValue();
			writeNumber(Integer.valueOf(b ? 1 : 0));
			return true;
		}
		if ((object instanceof Number)) {
			writeNumber((Number) object);
			return true;
		}
		if ((object instanceof Date)) {
			this.dataWriter.writeByte((byte) 116);
			this.dataWriter.writeLong(((Date) object).getTime());
			return true;
		}
		if ((object instanceof Enum)) {
			Number number = Integer.valueOf(((Enum) object).ordinal());
			writeNumber(number);
			return true;
		}

		return false;
	}

	private boolean writeReference(Object object) throws IOException {
		Integer id = (Integer) this.objectToIdMap.get(object);
		if (id != null) {
			this.dataWriter.writeByte((byte) 114);
			this.dataWriter.writeInteger(id.intValue());
			return true;
		}

		return false;
	}

	private void writeObjectId(Object object) throws IOException {
		if (this.objectToIdMap.containsKey(object)) {
			throw new IllegalStateException("Object id for '" + object
					+ "' already exists.'");
		}
		this.objectToIdMap.put(object, Integer.valueOf(this.idCounter));
		this.dataWriter.writeInteger(this.idCounter);
		this.idCounter += 1;
	}

	private boolean writeStringDefinition(Object object) throws IOException {
		if ((object instanceof String)) {
			this.dataWriter.writeByte((byte) 115);
			writeObjectId(object);
			this.dataWriter.writeString((String) object);
			return true;
		}

		return false;
	}

	private boolean writeDataDefinition(Object object) throws IOException {
		if ((object instanceof byte[])) {
			this.dataWriter.writeByte((byte) 120);
			writeObjectId(object);
			this.dataWriter.writeData((byte[]) object);
			return true;
		}

		return false;
	}

	private boolean writeArrayDefiniton(Object object) throws IOException {
		if ((object instanceof List)) {
			List list = (List) object;
			this.dataWriter.writeByte((byte) 97);
			writeObjectId(object);
			this.dataWriter.writeInteger(list.size());
			for (Iterator localIterator = list.iterator(); localIterator
					.hasNext();) {
				Object o = localIterator.next();

				serialize(o);
			}
			return true;
		}

		return false;
	}

	private boolean writeSetDefiniton(Object object) throws IOException {
		if ((object instanceof Set)) {
			Set set = (Set) object;
			this.dataWriter.writeByte((byte) 101);
			writeObjectId(object);
			this.dataWriter.writeInteger(set.size());
			for (Iterator localIterator = set.iterator(); localIterator
					.hasNext();) {
				Object o = localIterator.next();

				serialize(o);
			}
			return true;
		}

		return false;
	}

	private boolean writeDictionaryDefiniton(Object object) throws IOException {
		if ((object instanceof Map)) {
			Map<Object,Object> map = (Map) object;
			this.dataWriter.writeByte((byte) 100);
			writeObjectId(object);
			this.dataWriter.writeInteger(map.size());
			
			for (Map.Entry entry : map.entrySet()) {
				serialize(entry.getKey());
				serialize(entry.getValue());
			}
			return true;
		}

		return false;
	}

	private void writeObjectDefiniton(Object object) throws IOException {
		Class klass = object.getClass();
		int version = getClassRegistry().getClassVersion(klass);
		this.dataWriter.writeByte((byte) 111);
		writeObjectId(object);
		String className = getClassRegistry().getNameOfClass(klass);
		if (className == null) {
			throw new IllegalStateException(
					"Trying to serialize unregestered class: " + klass);
		}
		this.dataWriter.writeString(className);
		this.dataWriter.writeInteger(version);
		Set<String> properties = SerializableUtil.getSerializableProperties(object);
		this.dataWriter.writeInteger(properties.size());
		
		for (String s : properties) {
			this.dataWriter.writeString(s);
			serialize(PropertyUtil.getProperty(object, s));
		}
	}

	public void serialize(Object object) throws IOException {
		if (writeValue(object)) {
			return;
		}
		if (writeReference(object)) {
			return;
		}
		if (writeStringDefinition(object)) {
			return;
		}
		if (writeDataDefinition(object)) {
			return;
		}
		if (writeArrayDefiniton(object)) {
			return;
		}
		if (writeSetDefiniton(object)) {
			return;
		}
		if (writeDictionaryDefiniton(object)) {
			return;
		}

		writeObjectDefiniton(object);
	}
}