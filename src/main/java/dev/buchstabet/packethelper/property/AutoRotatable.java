package dev.buchstabet.packethelper.property;

import net.minecraft.server.v1_8_R3.EntityLiving;
import org.bukkit.Location;

public interface AutoRotatable<V extends EntityLiving> extends Teleportable<V> {

    Location getLocation();

    float getSpeed();
}
