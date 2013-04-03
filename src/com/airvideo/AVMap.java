package com.airvideo;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;

public class AVMap extends LinkedHashMap<String, Object> {
	private static final long serialVersionUID = -1614829932351196412L;
	
	String name;
	int __counter;
	DataInputStream __input;
	
	AVMap() {
		this.name = "";
		this.__counter = 0;
	}

	public void to_avmap(Object o, boolean resetCounter, DataOutputStream out) throws Exception {
		if (resetCounter) __counter = 0;
		if (o == null) {
			out.writeBytes("n");
		} else if (o instanceof BitRateList) {
			out.writeBytes("e");
			out.writeInt(__counter++);
			out.writeInt(((BitRateList)o).size());
			for (String i : (BitRateList) o) {
				to_avmap(i, false, out);
			}
		} else if (o instanceof ArrayList) {
			out.writeBytes("a");
			out.writeInt(__counter++);
			out.writeInt(((ArrayList)o).size());

			for (Object i : (ArrayList) o) {
				to_avmap(i, false, out);
			}
		} else if (o instanceof AVMap) {
			AVMap h = (AVMap)o;
			int version = 1;
			if (h.name == "air.video.ConversionRequest") {
				version = 221;
			}
			out.writeBytes("o");
			out.writeInt(__counter++);
			out.writeInt(h.name.length());
			out.writeBytes(h.name);
			out.writeInt(version);

			Iterator<String> i = h.keySet().iterator();
			out.writeInt(h.size());
			while (i.hasNext()) {
				Object k = i.next();
				out.writeInt(((String)k).length());
				out.writeBytes((String)k);
				to_avmap(h.get(k), false, out);
			}
		} else if (o instanceof AVBinary) {
			out.writeBytes("x");
			out.writeInt(__counter++);
			out.writeInt(((AVBinary)o).length());
			out.write(((AVBinary)o).data);
		} else if (o instanceof String) {
			out.writeBytes("s");
			out.writeInt(__counter++);
			out.writeInt(((String)o).length());
			out.writeBytes((String)o);
		} else if (o instanceof URL) {
			out.writeBytes("s");
			out.writeInt(__counter++);
			out.writeInt(((URL)o).toString().length());
			out.writeBytes(((URL)o).toString());
		} else if (o instanceof Integer) {
			out.writeBytes("i");
			out.writeInt(((Integer)o).intValue());
		} else if (o instanceof Double || o instanceof Float) {
			out.writeBytes("f");
			out.writeDouble((Double)o);
		} else {
			throw new Exception("Don't know how to package this datatype");
		}
	}
	
	public static AVMap parse (InputStream i) {
		AVMap obj = new AVMap();
		obj.__input = new DataInputStream(i);
		
		AVMap result = null;
		
		try {
			result = (AVMap)obj.readIdentifier(0);
		} catch (Exception e) {
			e.printStackTrace();
			result = null;
		}
		return result;
	}
	
	public String readString(int c_byte) throws IOException {
		if (c_byte < 0) {
			return null;
		}
		byte [] b = new byte[c_byte];
		__input.read(b);
		return new String(b);
	}
	
	Object readIdentifier(int depth) throws Exception {
		byte ident;	 
		int namelength;
		int payloadlength;
		String name;
		int childrencount;
		int unknown;
		int keylen;
		String key; 
		
		int counter;
		
		ident = __input.readByte();
		
		switch (ident) {
		case 'o': // hash
			AVMap map = new AVMap();
			unknown = __input.readInt();
			namelength = __input.readInt();
			name = readString(namelength);
			map.name = name;
			unknown = __input.readInt();
			childrencount = __input.readInt();
			for (counter = 0; counter < childrencount; counter++) {
				keylen = __input.readInt();
				key = readString(keylen);
				if (key == null) {
					return map;
				}
				Object d = readIdentifier (depth + 1 );
				map.put(key, d);
			}
			return map;
		case 's': // string
			unknown = __input.readInt();
			payloadlength = __input.readInt();
			return readString(payloadlength);
		case 'i':
		case 'r': // int
			unknown = __input.readInt();
			return unknown;
		case 'a':
		case 'e': // array
			unknown = __input.readInt();
			childrencount = __input.readInt();
			ArrayList <Object> a = new ArrayList <Object> ();
			for (counter = 0; counter < childrencount; counter++) {
				if (counter == 6) {
					Object brk = null;
				}
				a.add(readIdentifier(depth + 1));
			}
			return a;
		case 'n': // null
			return null;
		case 'f': // 8-byte float
			double f = __input.readDouble();
			return f;
		case 'x': // binary
			AVBinary bin = new AVBinary();
			unknown = __input.readInt();
			childrencount = __input.readInt();
			bin.read(__input, childrencount);
			return bin;
		case 'l': // big int
			return __input.readLong();
		default:
			//throw new Exception("Unknown identifier " + ident);
			Object r = null;
			return r;
		}
	}
}
