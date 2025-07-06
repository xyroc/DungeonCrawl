package xiroc.dungeoncrawl.dungeon.blueprint.template.block;

import net.minecraft.core.Vec3i;
import xiroc.dungeoncrawl.dungeon.block.MetaBlock;
import xiroc.dungeoncrawl.dungeon.blueprint.template.block.type.TemplateBlockType;

/**
 * Represents a single block within a template blueprint.
 *
 * @param type     The type of this template block.
 * @param position The relative position within the blueprint.
 * @param block    The block state properties associated with this block.
 * @param settings A set of rules defining how this block is placed during world generation.
 */
public record TemplateBlock(TemplateBlockType type, Vec3i position, MetaBlock block,
                            TemplateBlockPlacementSettings settings) {
}