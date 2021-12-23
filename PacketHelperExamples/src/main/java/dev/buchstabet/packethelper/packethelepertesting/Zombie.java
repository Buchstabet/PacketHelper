package dev.buchstabet.packethelper.packethelepertesting;

import dev.buchstabet.packethelper.AutoRotatable;
import dev.buchstabet.packethelper.implementation.PacketAnimal;
import net.minecraft.server.v1_8_R3.EntityZombie;

import java.util.Random;
import java.util.function.Consumer;

public class Zombie extends PacketAnimal<EntityZombie> implements AutoRotatable<EntityZombie> {

    public Zombie(Consumer<EntityZombie> config, boolean looking, Class<EntityZombie> clazz) {
        super(config, looking, clazz);
    }


    @Override
    public float getSpeed() {
        return 1;
    }
}
