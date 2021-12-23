package dev.buchstabet.packethelper;

import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityTeleport;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public interface Teleportable<V extends EntityLiving> extends List<UUID> {

  default void teleport(Player player) {
      ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityTeleport(getEntity()));
  }

  default void teleport(Location location) {
      setLocation(location);
      Bukkit.getOnlinePlayers().stream().filter(player -> contains(player.getUniqueId())).forEach(this::teleport);
  }

  void setLocation(Location location);

  V getEntity();

}
