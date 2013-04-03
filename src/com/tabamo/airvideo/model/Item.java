package com.tabamo.airvideo.model;

import com.inmethod.air.connect.serialization.Serialized;

public abstract class Item {
	private final String itemId;

	protected Item(String id) {
		this.itemId = id;
	}

	@Serialized
	public String getItemId() {
		return this.itemId;
	}

	@Serialized
	public abstract String getName();

	@Serialized
	public Object getDetail() {
		return null;
	}

	@Serialized
	public boolean isDetailLoaded() {
		return true;
	}

	public void tryLoadDetail() {
	}

	public void loadDetail(boolean waitIfNecessary) {
	}
}
