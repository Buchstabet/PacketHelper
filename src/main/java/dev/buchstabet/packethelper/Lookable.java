package dev.buchstabet.packethelper;

import net.minecraft.server.v1_8_R3.EntityLiving;
import org.bukkit.entity.Player;

public interface Lookable<V extends EntityLiving> extends SpawnableDestroyable<V> {

  boolean isLooking();

  void look(Player player);

  void setLooking(boolean b);
}
