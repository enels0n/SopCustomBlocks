package net.enelson.sopcustomblocks.managers.blocks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.enelson.sopcustomblocks.SopCustomBlocks;
import net.enelson.sopcustomblocks.managers.blocks.CustomBlock;
import net.enelson.sopcustomblocks.managers.config.ConfigManager;
import net.enelson.sopcustomblocks.managers.config.ConfigType;
import net.enelson.sopcustomblocks.utils.Utils;
import net.enelson.sopli.lib.SopLib;
import net.enelson.sopli.lib.customblocks.CustomBlockVisualOptions;
import net.enelson.sopli.lib.customblocks.CustomBlockVisualService;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class BlockManager {
    private final SopCustomBlocks plugin;
    private final ConfigManager configManager;
    private final CustomBlockVisualService visualService;
    private final Map<String, CustomBlock> blocksByLocation = new LinkedHashMap<String, CustomBlock>();
    private final Map<UUID, CustomBlock> blocksByEntityUuid = new LinkedHashMap<UUID, CustomBlock>();

    public BlockManager(SopCustomBlocks plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getConfigManager();
        this.visualService = SopLib.getInstance().getCustomBlockVisualService();
        plugin.getLogger().info("VisualService: " + this.visualService.getClass().getName());
        this.cleanupOrphanedEntitiesInLoadedWorlds();
        this.loadFromDb();
    }

    public Entity createEntityWithoutBlock(Location location, ItemStack item, String id, CustomBlockVisualOptions options) {
        return this.visualService.createEntityWithoutBlock(location, item, id, options);
    }

    public void removeEntityWithoutBlock(Entity entity) {
        this.visualService.removeEntityWithoutBlock(entity);
    }

    public boolean isCustomBlockArmorStand(Entity entity) {
        return this.visualService.isManagedEntity(entity);
    }

    public CustomBlock getBlock(Location location) {
        if (location == null || location.getWorld() == null) {
            return null;
        }
        return this.blocksByLocation.get(this.key(location));
    }

    public CustomBlock getBlock(Entity entity) {
        if (entity == null) {
            return null;
        }
        return this.blocksByEntityUuid.get(entity.getUniqueId());
    }

    public CustomBlock addBlock(String id, Location location, Player player) {
        id = Utils.normalizeId(id);
        if (id == null || location == null || location.getWorld() == null) {
            return null;
        }
        ItemStack item = Utils.generateItem(id);
        if (item == null || item.getType() == Material.AIR) {
            this.plugin.getLogger().warning("Failed to create visual item for custom block id: " + id);
            return null;
        }
        float yaw = player != null ? this.getDirection(player, id) : 0.0f;
        float pitch = player != null ? this.getPitch(player, id) : 0.0f;
        return this.addBlock(id, location, yaw, pitch);
    }

    public CustomBlock addBlock(String id, Location location, float yaw, float pitch) {
        id = Utils.normalizeId(id);
        if (id == null || location == null || location.getWorld() == null) {
            return null;
        }
        ItemStack item = Utils.generateItem(id);
        if (item == null || item.getType() == Material.AIR) {
            this.plugin.getLogger().warning("Failed to create visual item for custom block id: " + id);
            return null;
        }
        boolean usePitch = this.shouldUsePitch(id);
        CustomBlockVisualOptions options = this.readVisualOptions(id, yaw, pitch, usePitch);
        Location base = location.getBlock().getLocation();
        Location spawn = base.clone().add(options.getOffsetX(), options.getOffsetY(), options.getOffsetZ());
        CustomBlockVisualOptions spawnOptions = CustomBlockVisualOptions.of((float)options.getYaw(), (float)options.getPitch(), (boolean)options.isUsePitch(), (double)0.0, (double)0.0, (double)0.0, (float)options.getScaleX(), (float)options.getScaleY(), (float)options.getScaleZ());
        Entity entity = this.createEntityWithoutBlock(spawn, item, id, spawnOptions);
        if (entity == null) {
            this.plugin.getLogger().warning("Failed to create visual entity for custom block id: " + id);
            return null;
        }
        CustomBlock customBlock = new CustomBlock(id, base, entity, yaw, pitch, usePitch, entity != null ? entity.getUniqueId().toString() : null);
        this.put(customBlock);
        this.save();
        return customBlock;
    }

    public void breakBlock(CustomBlock block, Player player) {
        if (block == null) {
            return;
        }
        this.removeStoredBlock(block);
        Location location = block.getLocation();
        if (location != null && location.getWorld() != null) {
            location.getBlock().setType(Material.AIR);
        }
        if (location != null && location.getWorld() != null) {
            ItemStack drop;
            boolean shouldDrop;
            boolean bl = shouldDrop = player != null && player.getGameMode() != GameMode.CREATIVE;
            if (shouldDrop && (drop = Utils.generateItem(block.getId())) != null) {
                location.getWorld().dropItemNaturally(location.clone().add(0.5, 0.2, 0.5), drop);
            }
        }
        this.save();
    }

    public int debug(Location center, int radius) {
        if (center == null || center.getWorld() == null) {
            return 0;
        }
        int removed = 0;
        Collection<Entity> entities = center.getWorld().getNearbyEntities(center, (double)radius, (double)radius, (double)radius);
        for (Entity entity : entities) {
            if (!this.visualService.isManagedEntity(entity)) continue;
            entity.remove();
            ++removed;
        }
        return removed;
    }

    public void save() {
        YamlConfiguration db = this.configManager.getDB();
        for (String key : db.getKeys(false)) {
            db.set(key, null);
        }
        for (CustomBlock block : this.blocksByLocation.values()) {
            String base = this.key(block.getLocation());
            db.set(base + ".id", (Object)block.getId());
            db.set(base + ".location", (Object)Utils.getSerializedLocation(block.getLocation()));
            db.set(base + ".yaw", (Object)Float.valueOf(block.getRotationYaw()));
            db.set(base + ".pitch", (Object)Float.valueOf(block.getRotationPitch()));
            db.set(base + ".usePitch", (Object)block.isUsePitch());
            db.set(base + ".entityUUID", (Object)block.getEntityUUID());
        }
        this.configManager.saveDB();
    }

    public void deInit(boolean clearStoredBlocks) {
        for (CustomBlock block : new ArrayList<CustomBlock>(this.blocksByLocation.values())) {
            if (block.getEntity() != null) {
                this.visualService.removeEntityWithoutBlock(block.getEntity());
                continue;
            }
            if (block.getEntityUUID() == null) continue;
            try {
                this.visualService.removeEntityWithoutBlock(UUID.fromString(block.getEntityUUID()));
            }
            catch (IllegalArgumentException illegalArgumentException) {}
        }
        if (clearStoredBlocks) {
            this.clearDatabase();
        } else {
            this.save();
        }
        this.blocksByLocation.clear();
        this.blocksByEntityUuid.clear();
    }

    public void reconcileChunk(Chunk chunk) {
        if (chunk == null || !chunk.isLoaded()) {
            return;
        }
        boolean changed = false;
        for (CustomBlock block : new ArrayList<CustomBlock>(this.blocksByLocation.values())) {
            Location location = block.getLocation();
            if (location == null || location.getWorld() == null || !location.getWorld().getUID().equals(chunk.getWorld().getUID()) || location.getBlockX() >> 4 != chunk.getX() || location.getBlockZ() >> 4 != chunk.getZ() || this.reconcileBlock(block) == block) continue;
            changed = true;
        }
        if (changed) {
            this.save();
        }
    }

    public void reconcileWorld(World world) {
        if (world == null) {
            return;
        }
        for (Chunk chunk : world.getLoadedChunks()) {
            this.reconcileChunk(chunk);
        }
    }

    private void loadFromDb() {
        YamlConfiguration db = this.configManager.getDB();
        ConfigurationSection root = db.getConfigurationSection("");
        if (root == null) {
            return;
        }
        for (String entryKey : root.getKeys(false)) {
            Location location;
            String id = db.getString(entryKey + ".id");
            String locationSerialized = db.getString(entryKey + ".location");
            float yaw = (float)db.getDouble(entryKey + ".yaw", db.getDouble(entryKey + ".rotation"));
            float pitch = (float)db.getDouble(entryKey + ".pitch");
            boolean usePitch = db.getBoolean(entryKey + ".usePitch");
            String entityUuid = db.getString(entryKey + ".entityUUID");
            id = Utils.normalizeId(id);
            if (id == null) {
                this.plugin.getLogger().warning("Skipping broken db entry with empty custom block id: " + entryKey);
                continue;
            }
            if (locationSerialized == null) continue;
            try {
                location = Utils.getDeserializedLocation(locationSerialized);
            }
            catch (Exception ex) {
                this.plugin.getLogger().warning("Skipping broken db entry: " + entryKey);
                continue;
            }
            CustomBlock loadedBlock = new CustomBlock(id, location.getBlock().getLocation(), null, yaw, pitch, usePitch, entityUuid);
            this.put(loadedBlock);
            this.reconcileBlock(loadedBlock);
        }
    }

    private CustomBlock reconcileBlock(CustomBlock block) {
        ItemStack item;
        Entity entityByUuid;
        if (block == null) {
            return null;
        }
        Location location = block.getLocation();
        if (location == null || location.getWorld() == null) {
            return block;
        }
        if (!location.getChunk().isLoaded()) {
            return block;
        }
        Entity chosen = null;
        String storedUuid = block.getEntityUUID();
        UUID parsedStoredUuid = this.parseUuid(storedUuid);
        List<Entity> nearbyMatches = this.findManagedEntities(location, block.getId());
        if (parsedStoredUuid != null && (entityByUuid = Bukkit.getEntity((UUID)parsedStoredUuid)) != null && this.visualService.isManagedEntity(entityByUuid) && this.key(location).equals(this.key(entityByUuid.getLocation()))) {
            chosen = entityByUuid;
        }
        if (chosen == null && !nearbyMatches.isEmpty()) {
            chosen = nearbyMatches.get(0);
        }
        for (Entity entity : nearbyMatches) {
            if (chosen == null) {
                chosen = entity;
                continue;
            }
            if (entity.getUniqueId().equals(chosen.getUniqueId())) continue;
            this.visualService.removeEntityWithoutBlock(entity);
        }
        if (chosen == null && (item = Utils.generateItem(block.getId())) != null && item.getType() != Material.AIR) {
            CustomBlockVisualOptions options = this.readVisualOptions(block.getId(), block.getRotationYaw(), block.getRotationPitch(), block.isUsePitch());
            Location spawn = location.getBlock().getLocation().clone().add(options.getOffsetX(), options.getOffsetY(), options.getOffsetZ());
            CustomBlockVisualOptions spawnOptions = CustomBlockVisualOptions.of((float)options.getYaw(), (float)options.getPitch(), (boolean)options.isUsePitch(), (double)0.0, (double)0.0, (double)0.0, (float)options.getScaleX(), (float)options.getScaleY(), (float)options.getScaleZ());
            chosen = this.createEntityWithoutBlock(spawn, item, block.getId(), spawnOptions);
        }
        CustomBlock reconciled = new CustomBlock(block.getId(), block.getLocation(), chosen, block.getRotationYaw(), block.getRotationPitch(), block.isUsePitch(), chosen != null ? chosen.getUniqueId().toString() : block.getEntityUUID());
        this.replace(block, reconciled);
        return reconciled;
    }

    private void put(CustomBlock customBlock) {
        this.blocksByLocation.put(this.key(customBlock.getLocation()), customBlock);
        if (customBlock.getEntity() != null) {
            this.blocksByEntityUuid.put(customBlock.getEntity().getUniqueId(), customBlock);
        } else {
            UUID uuid = this.parseUuid(customBlock.getEntityUUID());
            if (uuid != null) {
                this.blocksByEntityUuid.put(uuid, customBlock);
            }
        }
    }

    private void replace(CustomBlock oldBlock, CustomBlock newBlock) {
        if (oldBlock != null) {
            UUID oldUuid;
            this.blocksByLocation.remove(this.key(oldBlock.getLocation()));
            UUID uUID = oldUuid = oldBlock.getEntity() != null ? oldBlock.getEntity().getUniqueId() : this.parseUuid(oldBlock.getEntityUUID());
            if (oldUuid != null) {
                this.blocksByEntityUuid.remove(oldUuid);
            }
        }
        this.put(newBlock);
    }

    private void removeStoredBlock(CustomBlock customBlock) {
        Location location = customBlock.getLocation();
        this.blocksByLocation.remove(this.key(customBlock.getLocation()));
        if (customBlock.getEntity() != null) {
            this.blocksByEntityUuid.remove(customBlock.getEntity().getUniqueId());
            this.visualService.removeEntityWithoutBlock(customBlock.getEntity());
        } else if (customBlock.getEntityUUID() != null) {
            try {
                UUID uuid = UUID.fromString(customBlock.getEntityUUID());
                this.visualService.removeEntityWithoutBlock(uuid);
                this.blocksByEntityUuid.remove(uuid);
            }
            catch (IllegalArgumentException illegalArgumentException) {
                // empty catch block
            }
        }
        if (location != null) {
            for (Entity entity : this.findManagedEntities(location, customBlock.getId())) {
                this.visualService.removeEntityWithoutBlock(entity);
                this.blocksByEntityUuid.remove(entity.getUniqueId());
            }
        }
    }

    private CustomBlockVisualOptions readVisualOptions(String id, float yaw, float pitch, boolean usePitch) {
        double posX = this.readPosition(id + ".pos-x", 0.5);
        double posY = this.readPosition(id + ".pos-y", 0.5);
        double posZ = this.readPosition(id + ".pos-z", 0.5);
        float scaleX = (float)this.readScale(id + ".scale-x", 1.002);
        float scaleY = (float)this.readScale(id + ".scale-y", 1.002);
        float scaleZ = (float)this.readScale(id + ".scale-z", 1.002);
        float resolvedYaw = this.resolveConfiguredYaw(id, yaw);
        float resolvedPitch = this.resolveConfiguredPitch(id, pitch);
        boolean resolvedUsePitch = usePitch || this.hasConfiguredPitch(id);
        return CustomBlockVisualOptions.of((float)resolvedYaw, (float)(resolvedUsePitch ? resolvedPitch : 0.0f), (boolean)resolvedUsePitch, (double)posX, (double)posY, (double)posZ, (float)scaleX, (float)scaleY, (float)scaleZ);
    }

    private double readPosition(String path, double fallback) {
        YamlConfiguration blocks = this.configManager.getConfig(ConfigType.BLOCKS);
        return blocks.contains(path) ? blocks.getDouble(path) : fallback;
    }

    private double readScale(String path, double fallback) {
        YamlConfiguration blocks = this.configManager.getConfig(ConfigType.BLOCKS);
        return blocks.contains(path) ? blocks.getDouble(path) : fallback;
    }

    private float getDirection(Player player, String id) {
        YamlConfiguration blocks = this.configManager.getConfig(ConfigType.BLOCKS);
        if (blocks.contains(id + ".fixed-yaw")) {
            return (float) blocks.getDouble(id + ".fixed-yaw");
        }
        if (blocks.contains(id + ".yaw")) {
            return (float) blocks.getDouble(id + ".yaw");
        }
        float rotation = 0.0f;
        boolean useYaw = blocks.contains(id + ".use-yaw")
                ? blocks.getBoolean(id + ".use-yaw")
                : blocks.getBoolean(id + ".use-player-rotation");
        if (useYaw) {
            int round = blocks.contains(id + ".yaw-rotation-round")
                    ? blocks.getInt(id + ".yaw-rotation-round")
                    : blocks.getInt(id + ".rotation-round");
            round = round > 0 ? round : 90;
            round = round <= 90 ? round : 90;
            float yaw = player.getLocation().getYaw();
            float roundedYaw = Math.round(yaw / (float)round) * round;
            rotation = (roundedYaw + 180.0f) % 360.0f;
        }
        return rotation;
    }

    private float getPitch(Player player, String id) {
        YamlConfiguration blocks = this.configManager.getConfig(ConfigType.BLOCKS);
        if (blocks.contains(id + ".fixed-pitch")) {
            return (float) blocks.getDouble(id + ".fixed-pitch");
        }
        if (blocks.contains(id + ".pitch")) {
            return (float) blocks.getDouble(id + ".pitch");
        }
        if (!blocks.getBoolean(id + ".use-pitch")) {
            return 0.0f;
        }
        int round = blocks.getInt(id + ".pitch-rotation-round");
        if (round <= 0) {
            return player.getLocation().getPitch();
        }
        float pitch = player.getLocation().getPitch();
        return Math.round(pitch / (float)round) * round;
    }

    private float resolveConfiguredYaw(String id, float fallbackYaw) {
        YamlConfiguration blocks = this.configManager.getConfig(ConfigType.BLOCKS);
        if (blocks.contains(id + ".fixed-yaw")) {
            return (float) blocks.getDouble(id + ".fixed-yaw");
        }
        if (blocks.contains(id + ".yaw")) {
            return (float) blocks.getDouble(id + ".yaw");
        }
        return fallbackYaw;
    }

    private float resolveConfiguredPitch(String id, float fallbackPitch) {
        YamlConfiguration blocks = this.configManager.getConfig(ConfigType.BLOCKS);
        if (blocks.contains(id + ".fixed-pitch")) {
            return (float) blocks.getDouble(id + ".fixed-pitch");
        }
        if (blocks.contains(id + ".pitch")) {
            return (float) blocks.getDouble(id + ".pitch");
        }
        return fallbackPitch;
    }

    private boolean hasConfiguredPitch(String id) {
        YamlConfiguration blocks = this.configManager.getConfig(ConfigType.BLOCKS);
        return blocks.contains(id + ".fixed-pitch") || blocks.contains(id + ".pitch");
    }

    private boolean shouldUsePitch(String id) {
        YamlConfiguration blocks = this.configManager.getConfig(ConfigType.BLOCKS);
        return blocks.getBoolean(id + ".use-pitch") || this.hasConfiguredPitch(id);
    }

    private String key(Location location) {
        return Utils.getSerializedLocation(location.getBlock().getLocation());
    }

    private void clearDatabase() {
        YamlConfiguration db = this.configManager.getDB();
        for (String key : new ArrayList<String>(db.getKeys(false))) {
            db.set(key, null);
        }
        this.configManager.saveDB();
    }

    private void cleanupOrphanedEntitiesInLoadedWorlds() {
        HashSet<String> persistedLocations = new HashSet<String>();
        YamlConfiguration db = this.configManager.getDB();
        ConfigurationSection root = db.getConfigurationSection("");
        if (root != null) {
            for (String entryKey : root.getKeys(false)) {
                String locationSerialized = db.getString(entryKey + ".location");
                if (locationSerialized == null || locationSerialized.isEmpty()) continue;
                persistedLocations.add(locationSerialized);
            }
        }
        int removed = 0;
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (!this.visualService.isManagedEntity(entity) || persistedLocations.contains(this.key(entity.getLocation()))) continue;
                this.visualService.removeEntityWithoutBlock(entity);
                ++removed;
            }
        }
        if (removed > 0) {
            this.plugin.getLogger().info("Removed " + removed + " orphaned custom block entities.");
        }
    }

    private Entity findManagedEntity(Location location, String id) {
        List<Entity> matches = this.findManagedEntities(location, id);
        return matches.isEmpty() ? null : matches.get(0);
    }

    private List<Entity> findManagedEntities(Location location, String id) {
        ArrayList<Entity> matches = new ArrayList<Entity>();
        if (location == null || location.getWorld() == null || !location.getChunk().isLoaded()) {
            return matches;
        }
        Collection<Entity> nearby = location.getWorld().getNearbyEntities(location.getBlock().getLocation().clone().add(0.5, 0.5, 0.5), 0.75, 0.75, 0.75);
        String expectedKey = this.key(location);
        String idTag = id != null && !id.isEmpty() ? "soplib_customblock_id:" + id : null;
        for (Entity entity : nearby) {
            if (!this.visualService.isManagedEntity(entity) || !expectedKey.equals(this.key(entity.getLocation())) || idTag != null && !entity.getScoreboardTags().contains(idTag)) continue;
            matches.add(entity);
        }
        return matches;
    }

    private UUID parseUuid(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        try {
            return UUID.fromString(value);
        }
        catch (IllegalArgumentException ignored) {
            return null;
        }
    }

    public SopCustomBlocks getPlugin() {
        return this.plugin;
    }

    public ConfigManager getConfigManager() {
        return this.configManager;
    }
}


