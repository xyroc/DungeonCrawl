package xiroc.dungeoncrawl.dungeon.blueprint.assembly.type;

import net.minecraft.resources.ResourceLocation;
import xiroc.dungeoncrawl.dungeon.blueprint.Blueprint;
import xiroc.dungeoncrawl.dungeon.blueprint.anchor.Anchor;

/**
 * Constructs an {@link AssemblyType} for a blueprint.
 * See {@link AssemblyType} for more information on blueprint assembly.
 */
public interface AssemblyTypeBuilder {
    void noteAnchor(ResourceLocation anchorType, Anchor anchor);

    AssemblyType buildAssemblyType(Blueprint blueprint);
}
