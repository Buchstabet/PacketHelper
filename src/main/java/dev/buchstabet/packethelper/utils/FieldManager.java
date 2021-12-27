package dev.buchstabet.packethelper.utils;

import java.lang.reflect.Field;

/********************************************
 * Copyright (c) by Konstantin Krötz
 *******************************************/
public class FieldManager<V> {

  public void setValue(Object o, String name, V value) {
    try {
      Field field = o.getClass().getDeclaredField(name);
      field.setAccessible(true);
      field.set(o, value);
      field.setAccessible(false);
    } catch (Exception var5) {
      var5.printStackTrace();
    }

  }

  public V getValue(Class<V> clazz, Object obj, String name) {
    try {
      Field field = obj.getClass().getDeclaredField(name);
      field.setAccessible(true);
      Object o = field.get(obj);
      field.setAccessible(true);
      return clazz.cast(o);
    } catch (Exception var6) {
      var6.printStackTrace();
      return null;
    }
  }

}
