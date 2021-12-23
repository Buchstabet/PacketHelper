package dev.buchstabet.packethelper;

import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.List;
import java.util.UUID;

public interface PacketEntity<V extends EntityLiving> extends List<UUID> {

  default void spawn(Player player) {
    PlayerConnection playerConnection = ((CraftPlayer) player).getHandle().playerConnection;
    playerConnection.sendPacket(new PacketPlayOutSpawnEntityLiving(getEntity()));
    playerConnection.sendPacket(
            new PacketPlayOutEntityMetadata(getEntity().getId(), getEntity().getDataWatcher(), true));
  }

  default void destroy(Player player) {
    ((CraftPlayer) player)
        .getHandle()
        .playerConnection
        .sendPacket(new PacketPlayOutEntityDestroy(getEntity().getId()));
  }

  Location getLocation();

  V getEntity();

  static void setValue(Object obj, String name, Object value) {
    try {
      Field field = obj.getClass().getDeclaredField(name);
      field.setAccessible(true);
      field.set(obj, value);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  static Object getValue(Object obj, String name) {
    try {
      Field field = obj.getClass().getDeclaredField(name);
      field.setAccessible(true);
      return field.get(obj);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }
}
