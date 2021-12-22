package dev.buchstabet.packethelper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.PacketPlayInUseEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.function.BiConsumer;

public interface Clickable<V extends EntityLiving> {

  default void registerClickEvent(BiConsumer<Player, PacketContainer> consumer, JavaPlugin javaPlugin) {
    ProtocolLibrary.getProtocolManager()
        .addPacketListener(
            new PacketAdapter(javaPlugin, PacketType.Play.Client.USE_ENTITY) {
              @Override
              public void onPacketReceiving(PacketEvent event) {
                int entityId = (int) event.getPacket().getModifier().getValues().get(0);
                PacketPlayInUseEntity.EnumEntityUseAction action =
                    (PacketPlayInUseEntity.EnumEntityUseAction)
                        event.getPacket().getModifier().getValues().get(1);

                  if (!action.equals(PacketPlayInUseEntity.EnumEntityUseAction.INTERACT)) {
                      return;
                  }

                if (entityId != getEntity().getId()) {
                  return;
                }

                consumer.accept(event.getPlayer(), event.getPacket());
              }
            });
  }

  V getEntity();

}
