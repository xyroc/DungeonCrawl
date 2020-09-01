package xiroc.dungeoncrawl.dungeon.decoration;

import com.google.gson.JsonObject;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.model.DungeonModel;
import xiroc.dungeoncrawl.dungeon.piece.DungeonPiece;
import xiroc.dungeoncrawl.theme.JsonThemeHandler;
import xiroc.dungeoncrawl.util.IBlockStateProvider;

@FunctionalInterface
public interface IDungeonDecoration {

    void decorate(DungeonModel model, IWorld world, BlockPos pos, int width, int height, int length, MutableBoundingBox boundingBox, DungeonPiece piece, int stage);

    static IDungeonDecoration fromJson(JsonObject object) {
        if (object.has("type")) {
            String type = object.get("type").getAsString().toLowerCase();
            switch (type) {
                case "vines":
                    return new VineDecoration();
                case "scattered": {
                    float chance = object.has("chance") ? object.get("chance").getAsFloat() : 0.25F;
                    IBlockStateProvider blockStateProvider;

                    if (object.has("block")) {
                        //JsonObject block = object.getAsJsonObject("block");
                        blockStateProvider = JsonThemeHandler.deserialize(object, "block");
                        if (blockStateProvider != null) {
                            return new ScatteredDecoration(blockStateProvider, chance);
                        }
                    } else {
                        DungeonCrawl.LOGGER.warn("Missing entry 'block'");
                        return null;
                    }
                }
                case "floor": {
                    float chance = object.has("chance") ? object.get("chance").getAsFloat() : 0.5F;
                    IBlockStateProvider blockStateProvider;

                    if (object.has("block")) {
                        //JsonObject block = object.getAsJsonObject("block");
                        blockStateProvider = JsonThemeHandler.deserialize(object, "block");
                        if (blockStateProvider != null) {
                            return new FloorDecoration(blockStateProvider, chance);
                        }
                    } else {
                        DungeonCrawl.LOGGER.warn("Missing entry 'block'");
                        return null;
                    }
                }
                default:
                    DungeonCrawl.LOGGER.warn("Unknown decoration type '{}'", type);
                    return null;
            }
        } else {
            return null;
        }
    }

}
