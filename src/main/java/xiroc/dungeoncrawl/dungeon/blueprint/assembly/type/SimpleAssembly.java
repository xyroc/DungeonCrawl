package xiroc.dungeoncrawl.dungeon.blueprint.assembly.type;

import net.minecraft.resources.ResourceLocation;
import xiroc.dungeoncrawl.dungeon.blueprint.Blueprint;
import xiroc.dungeoncrawl.dungeon.blueprint.Entrance;
import xiroc.dungeoncrawl.dungeon.blueprint.EntranceConfiguration;
import xiroc.dungeoncrawl.dungeon.blueprint.EntranceContainer;
import xiroc.dungeoncrawl.dungeon.blueprint.anchor.Anchor;
import xiroc.dungeoncrawl.dungeon.blueprint.assembly.config.SimpleAssemblyConfig;
import xiroc.dungeoncrawl.dungeon.blueprint.assembly.context.AssemblyContext;
import xiroc.dungeoncrawl.dungeon.blueprint.assembly.context.SimpleAssemblyContext;

public record SimpleAssembly(EntranceContainer entrances) implements AssemblyType {

    @Override
    public AssemblyContext createContext() {
        return new SimpleAssemblyContext();
    }

    public static class Builder implements AssemblyTypeBuilder {
        private final SimpleAssemblyConfig config;
        private final EntranceContainer.Builder entrances =  new EntranceContainer.Builder();

        public Builder(SimpleAssemblyConfig config) {
            this.config = config;
        }

        @Override
        public void noteAnchor(ResourceLocation anchorType, Anchor anchor) {
            EntranceConfiguration entranceConfig = config.entranceTypes().get(anchorType);
            if (entranceConfig == null) {
                return;
            }
            Entrance entrance = entranceConfig.make(anchor);
            entrances.addEntrance(entranceConfig.type(), entrance);
        }

        @Override
        public AssemblyType buildAssemblyType(Blueprint blueprint) {
            return new SimpleAssembly(entrances.build());
        }
    }
}
