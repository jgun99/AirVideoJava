package com.tabamo.airvideo.connect.serialization;

import java.io.DataOutput;
import java.io.IOException;

public class DataWriter {
	private final DataOutput dataOutput;

	public DataWriter(DataOutput dataOutput) {
		this.dataOutput = dataOutput;
	}

	public void writeByte(byte b) throws IOException {
		this.dataOutput.writeByte(b);
	}

	public void writeShort(short s) throws IOException {
		this.dataOutput.writeShort(s);
	}

	public void writeInteger(int i) throws IOException {
		this.dataOutput.writeInt(i);
	}

	public void writeLong(long l) throws IOException {
		this.dataOutput.writeLong(l);
	}

	public void writeDouble(double d) throws IOException {
		this.dataOutput.writeDouble(d);
	}

	public void writeData(byte[] b) throws IOException {
		writeData(b, 0, b.length);
	}

	public void writeData(byte[] b, int offset, int length) throws IOException {
		writeInteger(length);
		writeDataRaw(b, offset, length);
	}

	public void writeDataRaw(byte[] b, int offset, int length)
			throws IOException {
		this.dataOutput.write(b, offset, length);
	}

	public void writeDataRaw(byte[] b) throws IOException {
		writeDataRaw(b, 0, b.length);
	}

	public void writeString(String s) throws IOException {
		byte[] bytes = s.getBytes("utf-8");
		writeData(bytes);
	}
}