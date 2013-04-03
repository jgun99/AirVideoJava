package com.airvideo;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class AVBinary {
	public byte [] data;
	
	AVBinary() {
		
	}
	
	public void read(DataInputStream d, int length) {
		try {
			int available = d.available();
			if (length < 0 || available < length) {
				this.data = null;
				return;
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		this.data = new byte [length];
		try {
			d.read(this.data);
		} catch (IOException e) {
			e.printStackTrace();
			// wait!
		}
	}
	
	public void writeTo (String path) {
		
	}
	public int length() {
		return data.length;
	}
	
	public ByteArrayInputStream getInputStream() {
		return new ByteArrayInputStream (this.data);
	}
}
