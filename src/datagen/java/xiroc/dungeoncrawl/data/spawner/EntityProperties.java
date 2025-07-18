package xiroc.dungeoncrawl.data.spawner;

import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.datapack.registry.DatapackRegistries;
import xiroc.dungeoncrawl.dungeon.monster.SpawnerEntityProperties;
import xiroc.dungeoncrawl.util.random.IRandom;

public interface EntityProperties {
    static void generate(BootstrapContext<SpawnerEntityProperties> bootstrap) {
        bootstrap.register(ResourceKey.create(DatapackRegistries.SPAWNER_ENTITY_PROPERTIES, DungeonCrawl.locate("my_spawner_entity_properties")), new SpawnerEntityProperties.Builder()
                        .mainHand(IRandom.<Item>builder().add(Items.IRON_SWORD))
                .build());
    }
}
