package dev.buchstabet.packethelper.implementation;

import dev.buchstabet.packethelper.*;
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

@Getter
@RequiredArgsConstructor
public class PacketAnimal<V extends EntityInsentient> extends ArrayList<UUID> implements PacketEntity<V>, Clickable<V>, Lookable<V> {

  private V entity = null;
  private final Function<World, V> consumer;
  private Location location;
  private final boolean looking;

  public PacketAnimal<V> create(Location location) {
    World world = ((CraftWorld) location.getWorld()).getHandle();
    entity = consumer.apply(world);
    setLocation(location);
    return this;
  }

  @Override
  public void spawn(Player player) {
    if (entity == null)
      throw new NullPointerException(
          "You must run PacketAnimal#create(Location) before you run PacketAnimal#spawn(Player)");
    PlayerConnection playerConnection = ((CraftPlayer) player).getHandle().playerConnection;
    playerConnection.sendPacket(new PacketPlayOutSpawnEntityLiving(entity));
    playerConnection.sendPacket(
        new PacketPlayOutEntityMetadata(entity.getId(), entity.getDataWatcher(), true));
  }

  @Override
  public void destroy(Player player) {
    ((CraftPlayer) player)
        .getHandle()
        .playerConnection
        .sendPacket(new PacketPlayOutEntityDestroy(entity.getId()));
  }

  @Override
  public void setLocation(Location location) {
    this.location = location;
    entity.setLocation(
        location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
  }
}
