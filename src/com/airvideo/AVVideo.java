package com.airvideo;

import java.net.URL;
import java.util.HashMap;

public class AVVideo extends AVResource {
	AVMap details;
	AVMap videoStream;
	AVMap audioStream;
	AVMap streams;
	
	AVVideo (AVClient server, String name, String location, AVMap detail) {
		this.server = server;
		this.name = name;
		this.location = "/" + location;
		this.details = detail;
		this.videoStream = new AVMap();
		this.audioStream = new AVMap();
		this.videoStream.put("index", new Integer(1));
		this.audioStream.put("index", new Integer(0));
	}
	
	Object details() {
		streams = new AVMap();
		streams.put("video", new HashMap<String, Object>());
		streams.put("audio", new HashMap<String, Object>());
		streams.put("unknown", new HashMap<String, Object>());
		for (int ixStream = 0; ixStream < streams.size(); ixStream++ ) {
			if (ixStream == 0) {
				
			} else if (ixStream == 1) {
				
			} else {
				
			}
		}
		
		audioStream = (AVMap)((AVMap)details.get("streams")).get(0);
		videoStream = (AVMap)((AVMap)details.get("streams")).get(0);
		//audio_stream = streams
		return this.details;
	}
	
//	Bitmap thumbnail() {
//		AVBinary d = (AVBinary) this.details.get("videoThumbnail");
//		return BitmapFactory.decodeStream(d.getInputStream());
//	}
	
	void audioStream(AVMap stream) {
		AVMap index = (AVMap)stream.get("index");
		details = server.getDetails(this);
		
		audioStream = index;
	}
	
	void videoStream(HashMap <Object,Object> stream) {
		@SuppressWarnings("unused")
		Integer index = (Integer)stream.get("index");
	}
	
	URL url () {
		try {
			return server.getUrl(this, false);
		}
		catch (Exception e) {
			return null;
		}
	}
	
	URL live_url () {
		try {
			return server.getUrl(this, true);
		}
		catch (Exception e) {
			return null;
		}
	}
}
