package xiroc.dungeoncrawl.dungeon.blueprint.assembly.type;

import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import xiroc.dungeoncrawl.dungeon.blueprint.Blueprint;
import xiroc.dungeoncrawl.dungeon.blueprint.EntranceConfiguration;
import xiroc.dungeoncrawl.dungeon.blueprint.EntranceContainer;
import xiroc.dungeoncrawl.dungeon.blueprint.anchor.Anchor;
import xiroc.dungeoncrawl.dungeon.blueprint.assembly.config.CardinalEntrancesAssemblyConfig;
import xiroc.dungeoncrawl.dungeon.blueprint.assembly.context.AssemblyContext;
import xiroc.dungeoncrawl.dungeon.blueprint.assembly.context.CardinalEntrancesAssemblyContext;

public record CardinalEntrancesAssembly(EntranceContainer entrances,
                                        CardinalEntrancesAssemblyConfig config) implements AssemblyType {

    @Override
    public AssemblyContext createContext() {
        return new CardinalEntrancesAssemblyContext(this);
    }

    public static class Builder implements AssemblyTypeBuilder {
        private final CardinalEntrancesAssemblyConfig config;

        public Builder(CardinalEntrancesAssemblyConfig config) {
            this.config = config;
        }

        @Override
        public void noteAnchor(ResourceLocation anchorType, Anchor anchor) {
        }

        @Override
        public AssemblyType buildAssemblyType(Blueprint blueprint) {
            int x = blueprint.xSpan() / 2;
            int z = blueprint.zSpan() / 2;

            EntranceContainer.Builder entrances = new EntranceContainer.Builder();
            EntranceConfiguration entranceConfiguration = config.entranceConfiguration();

            entrances.addEntrance(entranceConfiguration.type(), entranceConfiguration.make(Anchor.of(x, config.entranceHeight(), 0, Direction.NORTH)));
            entrances.addEntrance(entranceConfiguration.type(), entranceConfiguration.make(Anchor.of(0, config.entranceHeight(), z, Direction.WEST)));
            entrances.addEntrance(entranceConfiguration.type(), entranceConfiguration.make(Anchor.of(x, config.entranceHeight(), blueprint.zSpan() - 1, Direction.SOUTH)));
            entrances.addEntrance(entranceConfiguration.type(), entranceConfiguration.make(Anchor.of(blueprint.xSpan() - 1, config.entranceHeight(), z, Direction.EAST)));

            return new CardinalEntrancesAssembly(entrances.build(), config);
        }
    }
}
