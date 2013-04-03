package com.airvideo;

import java.util.ArrayList;

public class AVFolder extends AVResource {
	AVFolder(AVClient server, String name, String location) {
		this.server = server;
		this.name = name;
		if (location == null) {
			this.location = null;
		} else {
			this.location = "/" + location;			
		}
	}
	
	AVFolder cd () {
		return this.server.cd(this);
	}
	
	ArrayList<AVResource> ls () {
		return this.server.ls(this);
	}
}
