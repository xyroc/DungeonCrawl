package xiroc.dungeoncrawl.dungeon.piece;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import xiroc.dungeoncrawl.datapack.DatapackRegistries;
import xiroc.dungeoncrawl.dungeon.theme.BuiltinThemes;
import xiroc.dungeoncrawl.dungeon.theme.PrimaryTheme;
import xiroc.dungeoncrawl.dungeon.theme.SecondaryTheme;
import xiroc.dungeoncrawl.util.Orientation;
import xiroc.dungeoncrawl.util.StorageHelper;

public abstract class BaseDungeonPiece extends StructurePiece {
    protected static final String NBT_KEY_POSITION = "Position";
    protected static final String NBT_KEY_ROTATION = "Rotation";
    protected static final String NBT_KEY_PRIMARY_THEME = "PrimaryTheme";
    protected static final String NBT_KEY_SECONDARY_THEME = "SecondaryTheme";

    public final BlockPos position;
    public final Rotation rotation;
    public final PrimaryTheme primaryTheme;
    public final SecondaryTheme secondaryTheme;

    public BaseDungeonPiece(StructurePieceType type, BoundingBox boundingBox, BlockPos position, Rotation rotation, PrimaryTheme primaryTheme, SecondaryTheme secondaryTheme) {
        super(type, 0, boundingBox);
        this.rotation = rotation;
        this.position = position;
        this.primaryTheme = primaryTheme;
        this.secondaryTheme = secondaryTheme;
    }

    public BaseDungeonPiece(StructurePieceType type, CompoundTag nbt) {
        super(type, nbt);
        this.position = StorageHelper.decode(nbt.get(NBT_KEY_POSITION), BlockPos.CODEC);
        this.rotation = Orientation.getRotation(nbt.getInt(NBT_KEY_ROTATION));
        if (nbt.contains(NBT_KEY_PRIMARY_THEME)) {
            this.primaryTheme = DatapackRegistries.PRIMARY_THEME.get(new ResourceLocation(nbt.getString(NBT_KEY_PRIMARY_THEME)));
        } else {
            this.primaryTheme = DatapackRegistries.PRIMARY_THEME.get(BuiltinThemes.DEFAULT);
        }

        if (nbt.contains(NBT_KEY_SECONDARY_THEME)) {
            this.secondaryTheme = DatapackRegistries.SECONDARY_THEME.get(new ResourceLocation(nbt.getString(NBT_KEY_SECONDARY_THEME)));
        } else {
            this.secondaryTheme = DatapackRegistries.SECONDARY_THEME.get(BuiltinThemes.DEFAULT);
        }
    }

    @Override
    protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag nbt) {
        nbt.put(NBT_KEY_POSITION, StorageHelper.encode(this.position, BlockPos.CODEC));
        nbt.putInt(NBT_KEY_ROTATION, Orientation.rotationAsInt(this.rotation));
        nbt.putString(NBT_KEY_PRIMARY_THEME, primaryTheme.key().toString());
        nbt.putString(NBT_KEY_SECONDARY_THEME, secondaryTheme.key().toString());
    }
}
