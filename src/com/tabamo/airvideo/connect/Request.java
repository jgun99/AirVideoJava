package com.tabamo.airvideo.connect;

import java.util.List;

import com.tabamo.airvideo.connect.serialization.Serializable;
import com.tabamo.airvideo.connect.serialization.Serialized;

@Serializable(name="air.connect.Request", version=1, deserializeVersions=1)
public class Request
{
  private String requestURL;
  private int clientVersion;
  private String passwordDigest;
  private String serviceName;
  private String methodName;
  private String clientIdentifier;
  private List<Object> parameters;
  private static final ThreadLocal<Request> currentRequest = new ThreadLocal();

  public void setRequestURL(String requestURL)
  {
    this.requestURL = requestURL;
  }

  @Serialized
  public String getRequestURL()
  {
    return this.requestURL;
  }

  @Serialized
  public int getClientVersion()
  {
    return this.clientVersion;
  }

  public void setClientVersion(int clientVersion)
  {
    this.clientVersion = clientVersion;
  }

  @Serialized
  public String getPasswordDigest()
  {
    return this.passwordDigest;
  }

  public void setPasswordDigest(String passwordDigest)
  {
    this.passwordDigest = passwordDigest;
  }

  @Serialized
  public String getServiceName()
  {
    return this.serviceName;
  }

  public void setServiceName(String serviceName)
  {
    this.serviceName = serviceName;
  }

  @Serialized
  public String getMethodName()
  {
    return this.methodName;
  }

  public void setMethodName(String methodName)
  {
    this.methodName = methodName;
  }

  @Serialized
  public List<Object> getParameters()
  {
    return this.parameters;
  }

  public void setParameters(List<Object> parameters)
  {
    this.parameters = parameters;
  }

  public void setClientIdentifier(String clientIdentifier)
  {
    this.clientIdentifier = clientIdentifier;
  }

  @Serialized
  public String getClientIdentifier()
  {
    return this.clientIdentifier;
  }

  public static void setCurrentRequest(Request request)
  {
    currentRequest.set(request);
  }

  public static Request getCurrentRequest()
  {
    return (Request)currentRequest.get();
  }
}