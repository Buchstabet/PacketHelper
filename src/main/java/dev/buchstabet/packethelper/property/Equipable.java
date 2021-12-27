package dev.buchstabet.packethelper.property;

import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityEquipment;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;

/********************************************
 * Copyright (c) by Konstantin Krötz
 *******************************************/
public interface Equipable<V extends EntityLiving> {

  default void equip(Player player, int slot, org.bukkit.inventory.ItemStack itemStack) {
    PacketPlayOutEntityEquipment packetPlayOutEntityEquipment =
        new PacketPlayOutEntityEquipment(
            getEntity().getId(), slot, CraftItemStack.asNMSCopy(itemStack));
    ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packetPlayOutEntityEquipment);
  }

  V getEntity();
}
