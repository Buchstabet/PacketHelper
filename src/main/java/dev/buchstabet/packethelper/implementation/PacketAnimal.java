package dev.buchstabet.packethelper.implementation;

import dev.buchstabet.packethelper.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.UUID;
import java.util.function.Consumer;

@Getter
@RequiredArgsConstructor
public class PacketAnimal<V extends EntityInsentient> extends ArrayList<UUID>
    implements PacketEntity<V>, Clickable<V>, Lookable<V>, Equipable<V> {

  private V entity = null;
  private final Consumer<V> config;
  private Location location;
  private final boolean looking;
  private final Class<V> clazz;

  public PacketAnimal<V> create(Location location) {
    World world = ((CraftWorld) location.getWorld()).getHandle();
    try {
      Constructor<V> constructor = clazz.getConstructor(World.class);
      entity = constructor.newInstance(world);
      config.accept(entity);
      setLocation(location);
    } catch (InvocationTargetException
            | NoSuchMethodException
            | InstantiationException
            | IllegalAccessException e) {
      e.printStackTrace();
    }
    return this;
  }

  @Override
  public void setLocation(Location location) {
    this.location = location;
    entity.setLocation(
        location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
  }
}
