package dev.buchstabet.packethelper;

import lombok.AccessLevel;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

/********************************************
 * Copyright (c) by Konstantin Krötz
 *******************************************/
public class PacketHelperPluginLoader extends JavaPlugin {

  @Getter(AccessLevel.PACKAGE) private static PacketEntityManager packetEntityManager;

  @Override
  public void onLoad() {
    packetEntityManager = new PacketEntityManager(this);
  }

  @Override
  public void onEnable() {
    saveDefaultConfig();
    packetEntityManager.start(this.getConfig().getInt("renderrange"));
    packetEntityManager.players.addAll(Bukkit.getOnlinePlayers());
  }

  @Override
  public void onDisable() {
    packetEntityManager.forEach(packetEntity -> packetEntity.forEach(uuid -> packetEntity.destroy(Bukkit.getPlayer(uuid))));
  }
}
