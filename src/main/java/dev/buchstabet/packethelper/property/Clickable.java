package dev.buchstabet.packethelper.property;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import dev.buchstabet.packethelper.PacketHelperPluginLoader;
import dev.buchstabet.packethelper.utils.PacketEntityClickedEvent;
import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.PacketPlayInUseEntity;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.function.Consumer;

/********************************************
 * Copyright (c) by Konstantin Krötz
 *******************************************/
public interface Clickable<V extends EntityLiving> {

  default void registerClickEvent(Consumer<Player> consumer) {
    registerClickEvent(consumer, PacketPlayInUseEntity.EnumEntityUseAction.INTERACT);
  }

  default void registerClickEvent(Consumer<Player> consumer, PacketPlayInUseEntity.EnumEntityUseAction enumEntityUseAction) {
    ProtocolLibrary.getProtocolManager()
            .addPacketListener(
                    new PacketAdapter(JavaPlugin.getPlugin(PacketHelperPluginLoader.class), PacketType.Play.Client.USE_ENTITY) {
                      @Override
                      public void onPacketReceiving(PacketEvent event) {
                        int entityId = event.getPacket().getIntegers().read(0);
                        PacketPlayInUseEntity.EnumEntityUseAction action =
                                (PacketPlayInUseEntity.EnumEntityUseAction)
                                        event.getPacket().getModifier().getValues().get(1);

                        if (!action.equals(enumEntityUseAction)) {
                          return;
                        }

                        if (entityId != getEntity().getId()) {
                          return;
                        }

                        consumer.accept(event.getPlayer());
                      }
                    });
  }

  default void enableBukkitEventCall() {
    registerClickEvent(player -> Bukkit.getServer().getPluginManager().callEvent(new PacketEntityClickedEvent(player, this)));
  }

  V getEntity();

}
