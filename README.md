# SopCustomBlocks

`SopCustomBlocks` is a Spigot plugin for custom block placement based on item models and `SopLib` visual entities.

The plugin stores placed custom blocks, restores them after restart, and exposes a public API for other plugins.

## Requirements

- `Spigot/Paper 1.16.5+`
- `SopLib`
- `PlaceholderAPI`
- `NBTAPI`

## Commands

- `/sopcustomblocks`
- `/sopcustomblocks give <player> <id> [amount]`
- `/sopcustomblocks reload`

## Permission

- `sopcustomblocks.admin`

## Block Config

Main file:

- `plugins/SopCustomBlocks/blocks.yml`

Example:

```yml
'1':
  material: STONE
  model: 1
  replacement-block: ANDESITE
  name: "Custom Block #1"
  lore:
    - "This is a"
    - "custom block"

'2':
  material: STONE
  model: 2
  name: "Custom Block name"
  lore:
    - "Custom Block lore"
  replacement-block: BARRIER
  break-only-admin: true
  break-by-hit: true
  can-exploded: true
  use-player-rotation: true
  rotation-round: 45
  scale-x: 5.0
  scale-y: 5.0
  scale-z: 5.0
  pos-x: 0.5
  pos-y: 2.5
  pos-z: 0.5
```

## Public API

`SopCustomBlocks` now exposes a stable API for other plugins.

### Static facade

Use:

```java
import net.enelson.sopli.customblocks.SopCustomBlocksAPI;
```

Example:

```java
if (SopCustomBlocksAPI.isAvailable()) {
    SopCustomBlocksAPI.placeBlock("core_1", location);
    String blockId = SopCustomBlocksAPI.getBlockId(location);
}
```

Available methods:

- `isAvailable()`
- `getService()`
- `placeBlock(String id, Location location)`
- `placeBlock(String id, Location location, float yaw, float pitch)`
- `placeBlock(String id, Location location, Player player)`
- `removeBlock(Location location)`
- `isCustomBlock(Location location)`
- `getBlockId(Location location)`
- `getBlock(Location location)`
- `getBlockItem(String id)`
- `getItemBlockId(ItemStack item)`

### Service interface

Service interface:

```java
import net.enelson.sopcustomblocks.api.SopCustomBlocksService;
```

Example:

```java
SopCustomBlocksService service = SopCustomBlocksAPI.getService();
if (service != null && service.isAvailable()) {
    service.placeBlock("core_1", location, player);
}
```

## Notes For Integrators

- Do not place custom blocks by calling `location.getBlock().setType(...)`.
- Use the API placement methods instead.
- To give a player the item form of a custom block, use:
  - `SopCustomBlocksAPI.getBlockItem(id)`
- To detect whether an item belongs to a custom block, use:
  - `SopCustomBlocksAPI.getItemBlockId(item)`

## Events

`SopCustomBlocks` now also exposes plugin events for cleaner integrations.

Available events:

- `SopCustomBlockPlaceEvent`
- `SopCustomBlockBreakEvent`
- `SopCustomBlockInteractEvent`

Break causes:

- `PLAYER_BREAK`
- `PLAYER_DAMAGE`
- `API`
- `UNKNOWN`

Example:

```java
@EventHandler
public void onCustomBlockBreak(SopCustomBlockBreakEvent event) {
    if ("generator".equalsIgnoreCase(event.getCustomBlock().getId())) {
        event.setCancelled(true);
    }
}
```

## Build

```bash
mvn -q -DskipTests package
```
