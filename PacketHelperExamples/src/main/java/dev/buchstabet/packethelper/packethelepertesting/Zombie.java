package dev.buchstabet.packethelper.packethelepertesting;

import dev.buchstabet.packethelper.implementation.PacketAnimal;
import dev.buchstabet.packethelper.property.AutoRotatable;
import net.minecraft.server.v1_8_R3.EntityZombie;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

/********************************************
 * Copyright (c) by Konstantin Krötz
 *******************************************/
public class Zombie extends PacketAnimal<EntityZombie> implements AutoRotatable<EntityZombie> {

    public Zombie(Location location, Class<EntityZombie> clazz, JavaPlugin plugin) {
        super(location, false, clazz, plugin);
    }

    @Override
    public float getSpeed() {
        return 5F;
    }
}
