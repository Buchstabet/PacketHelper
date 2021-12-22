package dev.buchstabet.packethelper;

import net.minecraft.server.v1_8_R3.EntityLiving;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface Lookable<V extends EntityLiving> extends Rotatable<V> {

  boolean isLooking();

  default void look(Player player) {
    Location location = getLocation().clone();
    location.setDirection(player.getLocation().toVector().subtract(location.toVector()));
    getEntity()
            .setLocation(
                    location.getX(),
                    location.getY(),
                    location.getZ(),
                    location.getYaw(),
                    location.getPitch());
    teleport(player);
    rotate(player, location.getYaw());
  }

  V getEntity();

  Location getLocation();

}
