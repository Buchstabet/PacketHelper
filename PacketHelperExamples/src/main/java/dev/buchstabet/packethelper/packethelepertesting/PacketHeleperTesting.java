package dev.buchstabet.packethelper.packethelepertesting;

import dev.buchstabet.packethelper.PacketEntityManager;
import dev.buchstabet.packethelper.implementation.*;
import net.minecraft.server.v1_8_R3.EntityZombie;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

/********************************************
 * Copyright (c) by Konstantin Krötz
 *******************************************/
public final class PacketHeleperTesting extends JavaPlugin {

  public static PacketEntityManager manager;

  @Override
  public void onEnable() {
    // Plugin startup logic
    manager = PacketEntityManager.create(this);

    Location location = new Location(Bukkit.getWorld("world"), 1461.5, 82, -307.5, 0, 0);
    NPC npc = new NPC(location, true, HumanEntity::getName, this);
    npc.registerClickEvent(player -> player.sendMessage("Cool, du kannst Ihn auch anklicken!"));
    manager.register(npc);

    Hologram hologram = new Hologram(player -> "§5§k###§r §7" + player.getName() + " §5§k###", location.clone().add(0, 1, 0), null);
    manager.register(hologram);

    PacketAnimal<EntityZombie> packetAnimal = new Zombie(location.clone().add(2, 0, 0), EntityZombie.class, this);
    packetAnimal.registerClickEvent(player -> player.sendMessage("Hallo, " + player.getName()));
    manager.register(packetAnimal);

    RotatingHead item = new RotatingHead(location.clone().add(3, 1, 0), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWQ5NzgyODRiNjE3NDY1MjU0Y2M2YTk3OGYyNTQzNDViNTZmNTVlZTJlNTZlNTVkNTU4YzZjNzU4YWM0ODcifX19");
    manager.register(item);

    FlyingItem flyingItem = new FlyingItem(location.clone().add(5, 1, 0), new ItemStack(Material.BEACON), null, Player::isOp);
    manager.register(flyingItem);
  }

  @Override
  public void onDisable() {
    // Plugin shutdown logic
  }
}
