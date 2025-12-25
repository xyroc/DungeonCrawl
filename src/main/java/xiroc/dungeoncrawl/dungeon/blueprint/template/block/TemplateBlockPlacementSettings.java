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

public record TemplateBlockPlacementSettings(boolean fillAir,
                                             boolean fillSolid,
                                             @Nullable TemplateBlockType alternative,
                                             @Nullable TieredResource<ResourceKey<LootTable>> lootTable) {
    public static final Codec<TemplateBlockPlacementSettings> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.BOOL.fieldOf("fill_air").forGetter(TemplateBlockPlacementSettings::fillAir),
                    Codec.BOOL.fieldOf("fill_solid").forGetter(TemplateBlockPlacementSettings::fillSolid),
                    TemplateBlockType.CODEC.optionalFieldOf("alternative").forGetter(settings -> Optional.ofNullable(settings.alternative)),
                    TieredResource.Codecs.LOOT_TABLE.optionalFieldOf("loot_table").forGetter(settings -> Optional.ofNullable(settings.lootTable))
            ).apply(instance, (fillAir, fillSolid, alternative, lootTable) ->
                    new TemplateBlockPlacementSettings(fillAir, fillSolid, alternative.orElse(null), lootTable.orElse(null))));

    public static final TemplateBlockPlacementSettings NON_SOLID_PLACEMENT = new TemplateBlockPlacementSettings(false, true, null, null);
    public static final TemplateBlockPlacementSettings SOLID_PLACEMENT = new TemplateBlockPlacementSettings(true, true, null, null);
    public static final TemplateBlockPlacementSettings DEFAULT = NON_SOLID_PLACEMENT;

    public boolean canPlace(boolean isAir) {
        return isAir && fillAir || !isAir && fillSolid;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        TemplateBlockPlacementSettings that = (TemplateBlockPlacementSettings) o;
        return fillAir == that.fillAir && fillSolid == that.fillSolid && Objects.equals(alternative, that.alternative);
    }

    @Override
    public int hashCode() {
        int result = Boolean.hashCode(fillAir);
        result = 31 * result + Boolean.hashCode(fillSolid);
        result = 31 * result + Objects.hashCode(alternative);
        return result;
    }
}
