package dev.buchstabet.packethelper.utils;

import java.lang.reflect.Field;

public class FieldManager<V> {

  public void setValue(Class<?> clazz, String name, V value) {
    try {
      Field field = clazz.getDeclaredField(name);
      field.setAccessible(true);
      field.set(value, value);
      field.setAccessible(false);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public V getValue(Class<V> clazz, Object obj, String name) {
    try {
      Object o;
      Field field = obj.getClass().getDeclaredField(name);
      field.setAccessible(true);
      o = field.get(obj);
      field.setAccessible(true);
      return clazz.cast(o);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

}
