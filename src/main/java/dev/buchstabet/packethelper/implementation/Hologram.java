package dev.buchstabet.packethelper.implementation;

import dev.buchstabet.packethelper.property.PacketEntity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.server.v1_8_R3.EntityArmorStand;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.UUID;
import java.util.function.Function;

/********************************************
 * Copyright (c) by Konstantin Krötz
 *******************************************/
@Getter
@RequiredArgsConstructor
public class Hologram extends ArrayList<UUID> implements PacketEntity<EntityArmorStand> {

  private final Function<Player, String> nameFunction;
  private final Location location;
  private EntityArmorStand entity;
  @Nullable private final Function<Player, Boolean> allowed;

  public Hologram(@Nullable Function<Player, String> nameFunction, Location location) {
    this(nameFunction, location, null);
  }

  @Override
  public void run() {
    location.add(0, 1.8, 0);
    entity = new EntityArmorStand(((CraftWorld) location.getWorld()).getHandle());
    entity.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    entity.setCustomNameVisible(true);
    entity.setInvisible(true);
    entity.n(true);
  }

  @Override
  public void spawn(Player player) {
    if (nameFunction == null) throw new NullPointerException("nameFunction could not be null");
    entity.setCustomName(nameFunction.apply(player));
    PacketEntity.super.spawn(player);
  }
}
