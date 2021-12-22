# PacketHelper
First you need a instance of `PacketEntityManager.class`.
Create a new instance using `PacketEntityManager.create(JavaPlugin);`.
You have to register each NPC or hologram in the manager, you do that with `PacketEntityManager#register()`.

## Installation
You have to compile the API into the finished plugin.

## NPC
How to create a NPC?
`NPC.create(Location location, boolean looking, Function<Player, String> nameFunction, JavaPlugin javaPlugin)`

#### NameFunction?
NameFunction sets the name of the player, which can be different from player to player.

#### Teleport
You can teleport the npc: `NPC#teleport()`

#### Equipment
You can change the equipment of the npc: `NPC#equip()`

## Hologram
How to create a Hologram?
`Hologram.create(Function<Player, String> nameFunction, Location location)`

#### NameFunction?
NameFunction sets the name of the player, which can be different from player to player.

#### ClickEvent
You can set a clickevent: `NPC#setClickEvent()`

## Clickable
`NPC.class` inherits from Clickable.
You can register a ClickEvent: `Clickable#registerClickEvent(BiConsumer<Player, PacketContainer> consumer, JavaPlugin javaPlugin)`
