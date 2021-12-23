package dev.buchstabet.packethelper;

import net.minecraft.server.v1_8_R3.EntityLiving;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface AutoRotatable<V extends EntityLiving> extends Teleportable<V> {

    Location getLocation();

    float getSpeed();
}
