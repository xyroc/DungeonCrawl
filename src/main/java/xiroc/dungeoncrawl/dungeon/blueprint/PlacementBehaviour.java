package xiroc.dungeoncrawl.dungeon.blueprint;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Rotation;
import xiroc.dungeoncrawl.dungeon.block.provider.BlockStateProvider;
import xiroc.dungeoncrawl.dungeon.theme.SecondaryTheme;
import xiroc.dungeoncrawl.dungeon.theme.PrimaryTheme;

import javax.annotation.Nullable;
import java.util.Random;
import java.util.function.BiFunction;

public class PlacementBehaviour {
    public static final PlacementBehaviour NON_SOLID = new PlacementBehaviour((world, pos, rotation, rand) -> false);
    public static final PlacementBehaviour SOLID = new PlacementBehaviour((world, pos, rotation, rand) -> true);
    public static final PlacementBehaviour RANDOM_IF_SOLID_NEARBY = new PlacementBehaviour((world, pos, rotation, rand) -> {
        if (isSolid(world, pos.north()) || isSolid(world, pos.east()) || isSolid(world, pos.south()) || isSolid(world, pos.west())) {
            return rand.nextFloat() < 0.6F;
        } else {
            return false;
        }
    });
    public static PlacementBehaviour STRIPES = new PlacementBehaviour((world, pos, rotation, rand) -> {
        if (rotation == Rotation.NONE || rotation == Rotation.CLOCKWISE_180) {
            return switch (pos.getZ() % 3) {
                case 0 -> rand.nextFloat() < 0.9F;
                case 1, -1 -> rand.nextFloat() < 0.5F;
                case 2, -2 -> rand.nextFloat() < 0.25F;
                default -> false;
            };
        } else {
            return switch (pos.getX() % 3) {
                case 0 -> rand.nextFloat() < 0.9F;
                case 1, -1 -> rand.nextFloat() < 0.5F;
                case 2, -2 -> rand.nextFloat() < 0.25F;
                default -> false;
            };
        }
    });

    public static PlacementBehaviour SMALL_GRID = new PlacementBehaviour((world, pos, rotation, rand) ->
            rand.nextFloat() < (0.95F - (Math.abs(pos.getX() % 3) * 0.2F) - (Math.abs(pos.getZ() % 3) * 0.2F)));

    public static PlacementBehaviour LARGE_GRID = new PlacementBehaviour((world, pos, rotation, rand) ->
            rand.nextFloat() < (1F - (Math.abs(pos.getX() % 6) * 0.1F) - (Math.abs(pos.getZ() % 6) * 0.1F)));

    private final PlacementFunction function;
    @Nullable
    public final BiFunction<PrimaryTheme, SecondaryTheme, BlockStateProvider> airBlock;

    public PlacementBehaviour(PlacementFunction function) {
        this(function, null);
    }


    public PlacementBehaviour(PlacementFunction function, BiFunction<PrimaryTheme, SecondaryTheme, BlockStateProvider> airBlock) {
        this.function = function;
        this.airBlock = airBlock;
    }

    public PlacementBehaviour withAirBlock(BiFunction<PrimaryTheme, SecondaryTheme, BlockStateProvider> airBlock) {
        return new PlacementBehaviour(this.function, airBlock);
    }

    public boolean isSolid(LevelAccessor world, BlockPos pos, Rotation pieceRotation, Random rand) {
        return function.isSolid(world, pos, pieceRotation, rand);
    }

    private static boolean isSolid(LevelAccessor world, BlockPos pos) {
        if (world.hasChunk(pos.getX() >> 4, pos.getZ() >> 4)) {
            return world.getBlockState(pos).canOcclude() || world.getBlockState(pos.below()).canOcclude();
        } else {
            return false;
        }
    }

    public interface PlacementFunction {
        boolean isSolid(LevelAccessor world, BlockPos pos, Rotation rotation, Random rand);
    }
}