package dev.buchstabet.packethelper.implementation;

import dev.buchstabet.packethelper.property.Clickable;
import dev.buchstabet.packethelper.property.Equipable;
import dev.buchstabet.packethelper.property.Lookable;
import dev.buchstabet.packethelper.property.PacketEntity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.server.v1_8_R3.EntityInsentient;
import net.minecraft.server.v1_8_R3.World;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

/********************************************
 * Copyright (c) by Konstantin Krötz
 *******************************************/
@Getter
@RequiredArgsConstructor
public class PacketAnimal<V extends EntityInsentient> extends ArrayList<UUID> implements PacketEntity<V>, Clickable<V>, Lookable<V>, Equipable<V> {

  private V entity = null;
  private final Consumer<V> config;
  private final Location location;
  private final boolean looking;
  private final Class<V> clazz;
  @Nullable private final Function<Player, Boolean> allowed;

  public PacketAnimal(Consumer<V> config, Location location, boolean looking, Class<V> clazz) {
    this(config, location, looking, clazz, null);
  }

  public PacketAnimal(Location location, boolean looking, Class<V> clazz) {
    this(v -> {
    }, location, looking, clazz, null);
  }

  @Override
  public void run() {
    World world = ((CraftWorld) location.getWorld()).getHandle();
    try {
      Constructor<V> constructor = clazz.getConstructor(World.class);
      entity = constructor.newInstance(world);
      config.accept(entity);
      setLocation(location);
    } catch (InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void spawn(Player player) {
    PacketEntity.super.spawn(player);
    teleport(player);
    rotate(player, location.getYaw());
  }
}
