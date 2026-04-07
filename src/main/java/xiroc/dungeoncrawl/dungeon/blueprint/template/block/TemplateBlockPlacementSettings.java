package xiroc.dungeoncrawl.dungeon.blueprint.template.block;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jetbrains.annotations.Nullable;
import xiroc.dungeoncrawl.dungeon.blueprint.template.block.type.TemplateBlockType;
import xiroc.dungeoncrawl.dungeon.tier.TieredResource;

import java.util.Objects;
import java.util.Optional;

/**
 * Specifies how a group of blocks within a blueprint should be generated.
 *
 * @param fillAir     Whether this block should generate through air blocks.
 * @param fillSolid   Whether this block should generate through non-air blocks.
 * @param alternative An alternative template to use if this block is not to be placed according to the above rules.
 * @param lootTable   A loot table to use for this block.
 */
public record TemplateBlockPlacementSettings(boolean fillAir,
                                             boolean fillSolid,
                                             boolean postProcess,
                                             @Nullable TemplateBlockType alternative,
                                             @Nullable TieredResource<ResourceKey<LootTable>> lootTable) {
    private interface Defaults {
        boolean FILL_AIR = false;
        boolean FILL_SOLID = true;
        boolean POST_PROCESS = false;
    }

    public static final Codec<TemplateBlockPlacementSettings> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.BOOL.optionalFieldOf("fill_air").forGetter(settings ->
                            settings.fillAir == Defaults.FILL_AIR ? Optional.empty() : Optional.of(settings.fillAir)),
                    Codec.BOOL.optionalFieldOf("fill_solid").forGetter(settings ->
                            settings.fillSolid == Defaults.FILL_SOLID ? Optional.empty() : Optional.of(settings.fillSolid)),
                    Codec.BOOL.optionalFieldOf("post_process").forGetter(settings ->
                            settings.postProcess == Defaults.POST_PROCESS ? Optional.empty() : Optional.of(settings.postProcess)),
                    TemplateBlockType.CODEC.optionalFieldOf("alternative").forGetter(settings -> Optional.ofNullable(settings.alternative)),
                    TieredResource.Codecs.LOOT_TABLE.optionalFieldOf("loot_table").forGetter(settings -> Optional.ofNullable(settings.lootTable))
            ).apply(instance, (fillAir, fillSolid, postProcess, alternative, lootTable) ->
                    new TemplateBlockPlacementSettings(fillAir.orElse(Defaults.FILL_AIR),
                            fillSolid.orElse(Defaults.FILL_SOLID),
                            postProcess.orElse(Defaults.POST_PROCESS),
                            alternative.orElse(null),
                            lootTable.orElse(null))));

    public static final TemplateBlockPlacementSettings NON_SOLID_PLACEMENT = new TemplateBlockPlacementSettings(false, true, false);
    public static final TemplateBlockPlacementSettings SOLID_PLACEMENT = new TemplateBlockPlacementSettings(true, true, false);
    public static final TemplateBlockPlacementSettings ANTI_SOLID = new TemplateBlockPlacementSettings(true, false, false);
    public static final TemplateBlockPlacementSettings DEFAULT = NON_SOLID_PLACEMENT;

    public TemplateBlockPlacementSettings(boolean fillAir, boolean fillSolid, boolean postProcess) {
        this(fillAir, fillSolid, postProcess, null, null);
    }

    public boolean canPlace(boolean isAir) {
        return isAir && fillAir || !isAir && fillSolid;
    }

    public TemplateBlockPlacementSettings withPostProcessing() {
        return new TemplateBlockPlacementSettings(fillAir, fillSolid, true, alternative, lootTable);
    }

    public TemplateBlockPlacementSettings withAlternative(TemplateBlockType alternative) {
        return new TemplateBlockPlacementSettings(fillAir, fillSolid, postProcess, alternative, lootTable);
    }

    public TemplateBlockPlacementSettings withLoot(TieredResource<ResourceKey<LootTable>> lootTables) {
        return new TemplateBlockPlacementSettings(fillAir, fillSolid, postProcess, alternative, lootTables);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        TemplateBlockPlacementSettings that = (TemplateBlockPlacementSettings) o;
        return fillAir == that.fillAir && fillSolid == that.fillSolid && Objects.equals(alternative, that.alternative) && Objects.equals(lootTable, that.lootTable);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fillAir, fillSolid, alternative, lootTable);
    }
}
