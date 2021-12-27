package dev.buchstabet.packethelper.utils;

import dev.buchstabet.packethelper.property.Clickable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/********************************************
 * Copyright (c) by Konstantin Krötz
 *******************************************/
@Getter
@RequiredArgsConstructor
public class PacketEntityClickedEvent extends Event {

  public static HandlerList handlerList = new HandlerList();
  public boolean cancelled = false;

  private final Player player;
  private final Clickable<?> clickable;

  public static HandlerList getHandlerList() {
    return handlerList;
  }

  @Override
  public HandlerList getHandlers() {
    return handlerList;
  }

}
