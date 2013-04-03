package com.tabamo.airvideo.connect;

import com.tabamo.airvideo.connect.serialization.Serializable;
import com.tabamo.airvideo.connect.serialization.Serialized;

@Serializable(name = "air.connect.Response", version = 1, deserializeVersions=1)
public class Response {

	@Serialized
	private State state;

	@Serialized
	private String errorMessage;

	@Serialized
	private String errorReport;

	@Serialized
	private Object result;

	public State getState() {
		return this.state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public String getErrorMessage() {
		return this.errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getErrorReport() {
		return this.errorReport;
	}

	public void setErrorReport(String errorReport) {
		this.errorReport = errorReport;
	}

	public Object getResult() {
		return this.result;
	}

	public void setResult(Object result) {
		this.result = result;
	}

	public static enum State {
		OK, INVALID_PASSWORD, SERVER_TOO_OLD, CLIENT_TOO_OLD, OTHER_ERROR, UNHANDLED_EXCEPTION;
	}
}
