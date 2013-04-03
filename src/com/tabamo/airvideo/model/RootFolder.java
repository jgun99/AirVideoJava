package com.tabamo.airvideo.model;

import java.io.File;
import java.util.List;

import com.inmethod.air.connect.serialization.Serialized;
import com.inmethod.air.video.v0221.model.NestedItem;
import com.inmethod.air.video.v0221.model.fs.FileSystemFolder;

public abstract class RootFolder extends Item {
	private String name;
	private FileSystemFolder.Detail detail;

	public RootFolder(String id) {
		super(id);
	}

	public void setName(String name) {
		this.name = name;
	}

	@Serialized
	public String getName() {
		return this.name;
	}

	protected String getFirstSegment(String path) {
		int index = path.indexOf(File.separator);
		if (index != -1) {
			return path.substring(0, index);
		}

		return path;
	}

	protected String getAfterFirstSegment(String path) {
		int index = path.indexOf(File.separator);
		if (index != -1) {
			return path.substring(index + 1);
		}

		return "";
	}

	public boolean canHandleId(String id) {
		return getItemId().equals(getFirstSegment(id));
	}

	public abstract String getItemPath(Item paramItem);

	public abstract Item getItem(String paramString);

	public abstract List<NestedItem> getItems(String paramString);

	public String getParentItemId(String id) {
		int index = id.lastIndexOf(File.separator);
		return index != -1 ? id.substring(0, index) : null;
	}

	public Object getDetail() {
		return this.detail;
	}

	public boolean isDetailLoaded() {
		return this.detail != null;
	}

	public void loadDetail(boolean waitIfNecessary) {
		if (this.detail == null) {
			this.detail = new FileSystemFolder.Detail();
			this.detail.setChildrenCount(getItems(getItemId()).size());
		}
	}
}