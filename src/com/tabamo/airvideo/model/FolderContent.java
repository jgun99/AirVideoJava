package com.tabamo.airvideo.model;

import java.util.List;

import com.inmethod.air.connect.serialization.Serializable;
import com.inmethod.air.connect.serialization.Serialized;
import com.inmethod.air.video.common.registry.Registry;
import com.inmethod.air.video.v0221.model.Item;

@Serializable(name="air.video.FolderContent", version=221)
public class FolderContent
{

  @Serialized
  private String name;
  
  @Serialized
  private double serverVersion;

  @Serialized
  private List<? extends Item> items;

  @Serialized
  public double getServerVersion()
  {
    return Registry.get().getVersionManager().getCurrentVersion();
  }
  
  public void setServerVersion(double serverVersion) {
	  this.serverVersion = serverVersion;
  }

  public String getFolderName()
  {
    return this.name;
  }

  public void setFolderName(String folderName)
  {
    this.name = folderName;
  }

  public List<? extends Item> getItems()
  {
    return this.items;
  }

  public void setItems(List<? extends Item> items)
  {
    this.items = items;
  }
}