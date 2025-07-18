package xiroc.dungeoncrawl.data.blueprint;

import net.minecraft.data.worldgen.BootstrapContext;
import xiroc.dungeoncrawl.data.blueprint.provider.CorridorBlueprints;
import xiroc.dungeoncrawl.data.blueprint.provider.EntranceBlueprints;
import xiroc.dungeoncrawl.data.blueprint.provider.PartBlueprints;
import xiroc.dungeoncrawl.data.blueprint.provider.RoomBlueprints;
import xiroc.dungeoncrawl.dungeon.blueprint.Blueprint;

public interface Blueprints {
    static void generate(BootstrapContext<Blueprint> context) {
        CorridorBlueprints.generate(context);
        EntranceBlueprints.generate(context);
        PartBlueprints.generate(context);
        RoomBlueprints.generate(context);
    }
}
