package com.airvideo;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import org.apache.commons.codec.binary.Hex;

public class AVClient {
	HttpURLConnection request;
	URL endpoint;
	String pwd;
	int max_w, max_h;
	String _server;
	int _port;
	String _password;
	String passwordDigest;
	
	AVClient(String server, int port, String password) {
		_server = server; _port = port; _password = password;
		_newRequest();
		
		pwd = "/";
		max_w = 640;
		max_h = 480;
		
		try {
			MessageDigest md=MessageDigest.getInstance("SHA1");
			md.update(("S@17" + _password + "@1r").getBytes());
	        byte[] hash=md.digest();           
	        
	        passwordDigest = Hex.encodeHexString(hash).toUpperCase();
	        System.out.println(passwordDigest);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	void _newRequest() {
		try {
			endpoint = new URL("http://" + _server + ":" + _port + "/service");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		try {
			request = (HttpURLConnection)endpoint.openConnection();
		} catch (IOException e) {
			e.printStackTrace();
		}
		request.addRequestProperty("User-Agent", "AirVideo/2.2.4 CFNetwork/459 Darwin/10.0.0d3");
		request.addRequestProperty("Accept", "*/*");
		request.addRequestProperty("Accept-Language", "en-us");
		request.addRequestProperty("Accept-Encoding", "gzip, deflate");
	}
	
	ArrayList <AVResource> ls(AVFolder dir) {
		ArrayList <AVResource> results = new ArrayList<AVResource>();
		ArrayList <Object> paths = new ArrayList <Object> ();
		
		String path = dir.location;
		if (path == null || path.equals(""))
			path = null;
		else
			path = path.substring(1);
		
		paths.add(path);
		AVMap files = request("browseService","getItems",paths);
		try {
			AVMap r = (AVMap)files.get("result");
			paths = (ArrayList)(r.get("items"));
			for (int i = 0; i < paths.size(); i++) {
				AVMap f = (AVMap) paths.get(i);
				if (f.name.equals("air.video.DiskRootFolder") ||
					f.name.equals("air.video.ITunesRootFolder") ||
					f.name.equals("air.video.Folder")) {
						results.add(new AVFolder(this, (String)f.get("name"), (String)f.get("itemId")));
				} else if (f.name.equals("air.video.VideoItem") || 
					f.name.equals("air.video.ITunesVideoItem")) {
						results.add(new AVVideo(this, (String)f.get("name"), (String)f.get("itemId"), (AVMap)f.get("detail")));
				} else {
					
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// files['result']['items'];
		return results;
	}
	
	AVFolder cd(AVFolder dir) {
		pwd = dir.location.substring(1);
		AVFolder f = new AVFolder(this, dir.name, pwd);
		return f;
	}
	
	URL getUrl(AVVideo video, boolean live) throws MalformedURLException {
		AVMap packet;
		URL contentURL;
		AVMap result;
		ArrayList<Object> parameters = new ArrayList<Object>();
		String method;
		String service;
		if (live) {
			parameters.add(conversionSettings(video));
			service = "livePlaybackService";
			method = "initLivePlayback";
		} else {
			parameters.add(video.location.substring(1));
			service = "playbackService";
			method = "initPlayback";
		}
		packet = request(service, method, parameters);
		result = (AVMap) packet.get("result");
		contentURL = new URL((String)result.get("contentURL"));
		return contentURL;
	}
	
	AVMap getDetails(Object item) {
		ArrayList <String> items = new ArrayList<String> ();
		if (item instanceof AVVideo) {
			items.add(((AVVideo)item).location.substring(1));
		} else if (item instanceof String) {
			items.add((String)item);
		}
		
		AVMap a = request("browseService","getItemsWithDetail",items);
		ArrayList result = (ArrayList) a.get("result");
		return (AVMap)a.get(0); 
	}
	
	// search
	
	AVMap conversionSettings(AVVideo file) {
		//double v_w = ((Integer)file.videoStream.get("width")).intValue();
		//double v_h = ((Integer)file.videoStream.get("height")).intValue();
		int desired_width = 480; //(int)v_w;
		int desired_height = 320; //(int)v_h;

		// code to convert width, height to max_width, max_height
		AVMap settings = new AVMap();
		settings.name = "air.video.ConversionRequest";
		settings.put("itemId", file.location.substring(1) );
		settings.put("audioStream", 1);
		settings.put("allowedBitrates", BitRateList.defaults());
		settings.put("audioBoost", 0.0);
		settings.put("cropRight", 0);
		settings.put("cropLeft", 0);
		settings.put("resolutionWidth", desired_width);
		settings.put("videoStream", 0);
		settings.put("cropBottom", 0);
		settings.put("cropTop", 0);
		settings.put("quality", 0.699999988079071);
		settings.put("subtitleInfo", null);
		settings.put("offset", 0.0);
		settings.put("resolutionHeight", desired_height);
		return settings;
	}
	
	AVMap request(String service, String method, Object params) {
		AVMap avrequest = new AVMap();
		try {
			request.setRequestMethod("POST");
			request.setDoInput(true);
			request.setDoOutput(true);
			OutputStream ost = request.getOutputStream();
			DataOutputStream broadcaster = new DataOutputStream(ost);
			avrequest.name = "air.connect.Request";
			avrequest.put("clientIdentifier", "89eae483355719f119d698e8d11e8b356525ecfb");
			//avrequest.put("passwordDigest", "9E8709DFEA8CA68B333650150B3398914D124927");
			avrequest.put("passwordDigest", passwordDigest);
			avrequest.put("methodName", method);
			avrequest.put("requestURL", endpoint.toString());
			avrequest.put("parameters", params);
			avrequest.put("clientVersion", 221);
			avrequest.put("serviceName", service);
			
			avrequest.to_avmap(avrequest, true, broadcaster);
			broadcaster.flush();
			broadcaster.close();
			
			InputStream result = request.getInputStream();
			BufferedInputStream bis = new BufferedInputStream(result, 65536);
			avrequest = AVMap.parse(bis);
		} 
		catch (Exception e) {
			e.printStackTrace();
			avrequest = null;
		}
		finally {
			_newRequest();
		}
		return avrequest;
	}
}
