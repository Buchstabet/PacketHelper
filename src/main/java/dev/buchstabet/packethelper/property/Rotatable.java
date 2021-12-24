package dev.buchstabet.packethelper.property;

import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityHeadRotation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public interface Rotatable<V extends EntityLiving> extends Teleportable<V> {

  default void rotate(Player player, float yaw) {
    ((CraftPlayer) player)
        .getHandle()
        .playerConnection
        .sendPacket(new PacketPlayOutEntityHeadRotation(getEntity(), (byte) (yaw * 256 / 360)));
  }

  @Override
  default void teleport(Location location) {
    getEntity()
        .setLocation(
            location.getX(),
            location.getY(),
            location.getZ(),
            location.getYaw(),
            location.getPitch());

    Bukkit.getOnlinePlayers().stream()
        .filter(player -> contains(player.getUniqueId()))
        .forEach(
            player -> {
              teleport(player);
              rotate(player, location.getYaw());
            });
  }


}
