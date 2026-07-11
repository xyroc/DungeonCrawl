package xiroc.dungeoncrawl.dungeon.blueprint.feature.settings;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootTable;
import xiroc.dungeoncrawl.dungeon.blueprint.anchor.Anchor;
import xiroc.dungeoncrawl.dungeon.component.feature.ChestComponent;
import xiroc.dungeoncrawl.dungeon.generator.level.LevelGenerator;
import xiroc.dungeoncrawl.dungeon.tier.TieredResource;

import java.util.Optional;

public record ChestSettings(Optional<TieredResource<ResourceKey<LootTable>>> lootTable) {
    public static final Codec<ChestSettings> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            TieredResource.Codecs.LOOT_TABLE.optionalFieldOf("loot_table").forGetter(ChestSettings::lootTable)
    ).apply(instance, ChestSettings::new));

    public ChestSettings() {
        this(Optional.empty());
    }

    public ChestComponent create(Anchor placement, LevelGenerator levelGenerator) {
        BlockState chestState = levelGenerator.secondaryTheme.value().chest().get(placement.position(), levelGenerator.random);
        return create(chestState, placement, levelGenerator);
    }

    public ChestComponent create(BlockState chestState, Anchor placement, LevelGenerator levelGenerator) {
        return new ChestComponent(chestState, placement, getLootTable(levelGenerator));
    }

    public ResourceKey<LootTable> getLootTable(LevelGenerator levelGenerator) {
        return lootTable.map(lootTables -> lootTables.forTier(levelGenerator.stage))
                .orElse(levelGenerator.levelType.lootTable());
    }
}
