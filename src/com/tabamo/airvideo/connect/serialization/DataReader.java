package com.tabamo.airvideo.connect.serialization;

import java.io.DataInput;
import java.io.IOException;

public class DataReader {
	private final DataInput dataInput;

	public DataReader(DataInput dataInput) {
		this.dataInput = dataInput;
	}

	public byte readByte() throws IOException {
		return this.dataInput.readByte();
	}

	public short readShort() throws IOException {
		return this.dataInput.readShort();
	}

	public int readInteger() throws IOException {
		return this.dataInput.readInt();
	}

	public long readLong() throws IOException {
		return this.dataInput.readLong();
	}

	public double readDouble() throws IOException {
		return this.dataInput.readDouble();
	}

	public byte[] readData() throws IOException {
		int length = readInteger();
		return readDataRaw(length);
	}

	public byte[] readDataRaw(int length) throws IOException {
		byte[] data = new byte[length];
		this.dataInput.readFully(data);
		return data;
	}

	public String readString() throws IOException {
		byte[] buffer = readData();
		return new String(buffer, "utf-8");
	}
}