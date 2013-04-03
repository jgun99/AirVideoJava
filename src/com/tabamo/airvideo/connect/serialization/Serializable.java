package com.tabamo.airvideo.connect.serialization;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({java.lang.annotation.ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Serializable
{
  public abstract String name();

  public abstract int version();

  public abstract int[] deserializeVersions();
}