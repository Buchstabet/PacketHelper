package dev.buchstabet.packethelper.implementation;

import dev.buchstabet.packethelper.PacketEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.UUID;
import java.util.function.Function;

public interface Hologram extends PacketEntity<EntityArmorStand> {

  static Hologram create(Function<Player, String> nameFunction, Location location) {
    return new HologramImpl(nameFunction, location).create();
  }

  @Getter
  @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
  class HologramImpl extends ArrayList<UUID> implements Hologram {

    private final Function<Player, String> nameFunction;
    private final Location location;
    private EntityArmorStand entity;

    private HologramImpl create() {
      entity = new EntityArmorStand(((CraftWorld) location.getWorld()).getHandle());
      entity.setLocation(
          location.getX(),
          location.getY(),
          location.getZ(),
          location.getYaw(),
          location.getPitch());
      entity.setCustomNameVisible(true);
      entity.setInvisible(true);
      return this;
    }

    @Override
    public void spawn(Player player) {
      PlayerConnection playerConnection = ((CraftPlayer) player).getHandle().playerConnection;
      entity.setCustomName(nameFunction.apply(player));

      PacketPlayOutSpawnEntityLiving packet = new PacketPlayOutSpawnEntityLiving(entity);
      playerConnection.sendPacket(packet);
    }

    @Override
    public void destroy(Player player) {
      PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(entity.getId());
      ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }
  }
}
