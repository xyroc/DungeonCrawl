package xiroc.dungeoncrawl.dungeon.blueprint.template.block;

import net.minecraft.core.Vec3i;
import xiroc.dungeoncrawl.dungeon.block.MetaBlock;
import xiroc.dungeoncrawl.dungeon.blueprint.template.block.type.TemplateBlockType;

public record TemplateBlock(TemplateBlockType type, Vec3i position, MetaBlock block, TemplateBlockPlacementSettings settings) {
}