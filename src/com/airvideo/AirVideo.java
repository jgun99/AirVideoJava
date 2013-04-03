package com.airvideo;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

import com.inmethod.air.connect.serialization.ObjectSerializer;

public class AirVideo {

	public static void serialize(Object object, OutputStream out) {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		DataOutputStream stream = new DataOutputStream(outputStream);
		try {
			new ObjectSerializer(stream).serialize(object);
			outputStream.writeTo(out);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static ByteArrayOutputStream serialize(Object object) {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		DataOutputStream stream = new DataOutputStream(outputStream);
		try {
			new ObjectSerializer(stream).serialize(object);
			return outputStream;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @param args
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	public static void main(String[] args) throws InstantiationException, IllegalAccessException {
//		// TODO Auto-generated method stub
//		Initializer.init();
//
//	    LoggerManager.getInstance().setDebug(true);
//	    
//		ClassRegistry.getInstance().registerClass(Request.class);
//		ClassRegistry.getInstance().registerClass(Response.class);
//
//		HttpClient httpclient = new DefaultHttpClient();
//
//		HttpPost httppost = new HttpPost("http://localhost:45631/service");
//
//		httppost.addHeader("User-Agent",
//				"AirVideo/2.2.4 CFNetwork/459 Darwin/10.0.0d3");
//		httppost.addHeader("Accept", "*/*");
//		httppost.addHeader("Accept-Language", "en-us");
//		httppost.addHeader("Accept-Encoding", "gzip, deflat");
//
//		Request req = new Request();
//		List<Object> param = new ArrayList();
//		param.add(null);
//
//		req.setPasswordDigest("9E8709DFEA8CA68B333650150B3398914D124927");
//		req.setRequestURL("http://127.0.0.1:45631/service");
//		req.setServiceName("browseService");
//		req.setClientIdentifier("89eae483355719f119d698e8d11e8b356525ecfb");
//		req.setClientVersion(221);
//		req.setMethodName("getItems");
//		req.setParameters(param);
//
//		// methodNamegetItemsclientVersion221requestURLhttp://127.0.0.1:45631/serviceclientIdentifier89eae483355719f119d698e8d11e8b356525ecfbserviceNamebrowseServiceparameterspasswordDigest9E8709DFEA8CA68B333650150B3398914D124927
//
//		ByteArrayOutputStream out = serialize(req);
//
//		httppost.setEntity(new ByteArrayEntity(out.toByteArray()));
//
//		DefaultHttpClient client = new DefaultHttpClient();
//		try {
//			HttpResponse response = client.execute(httppost);
//
//			System.out.println(response);
//			System.out.println(response.getEntity().getContentLength());
//			//System.out.println(EntityUtils.toString(response.getEntity()));
//
//			ByteArrayInputStream in = new ByteArrayInputStream(
//					EntityUtils.toByteArray(response.getEntity()));
//
//			DataInputStream inputStream = new DataInputStream(in);
//			ObjectDeserializer deserializer = new ObjectDeserializer(
//					inputStream);
//			Response svrResponse = (Response) deserializer.deserialize();
//
//			System.out.println(svrResponse.getErrorMessage());
//		} catch (ClientProtocolException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

		AVClient client = new AVClient("127.0.0.1",45631,"1234");
		
		
		AVFolder pwd = new AVFolder(client, "root", null);
		ArrayList <AVResource> items = client.ls(pwd);
		
		System.out.println(items.get(0) instanceof AVFolder);
		
		AVFolder folder = (AVFolder)items.get(0);
		
		System.out.println(folder.location);
		System.out.println(folder.name);
	}

}
