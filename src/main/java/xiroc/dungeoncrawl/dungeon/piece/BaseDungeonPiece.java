package xiroc.dungeoncrawl.dungeon.piece;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import xiroc.dungeoncrawl.dungeon.theme.PrimaryTheme;
import xiroc.dungeoncrawl.dungeon.theme.SecondaryTheme;
import xiroc.dungeoncrawl.dungeon.theme.Themes;
import xiroc.dungeoncrawl.util.Orientation;
import xiroc.dungeoncrawl.util.StorageHelper;

public abstract class BaseDungeonPiece extends StructurePiece {
    protected static final String NBT_KEY_POSITION = "Position";
    protected static final String NBT_KEY_ROTATION = "Rotation";
    protected static final String NBT_KEY_PRIMARY_THEME = "PrimaryTheme";
    protected static final String NBT_KEY_SECONDARY_THEME = "SecondaryTheme";

    public Rotation rotation;
    public BlockPos position;
    public PrimaryTheme primaryTheme;
    public SecondaryTheme secondaryTheme;

    public BaseDungeonPiece(StructurePieceType type, BoundingBox boundingBox) {
        super(type, 0, boundingBox);
        this.rotation = Rotation.NONE;
    }

    public BaseDungeonPiece(StructurePieceType type, CompoundTag nbt) {
        super(type, nbt);
        this.position = StorageHelper.decode(nbt.get(NBT_KEY_POSITION), BlockPos.CODEC);
        this.rotation = Orientation.getRotation(nbt.getInt(NBT_KEY_ROTATION));
        if (nbt.contains(NBT_KEY_PRIMARY_THEME)) {
            this.primaryTheme = Themes.getPrimary(new ResourceLocation(nbt.getString(NBT_KEY_PRIMARY_THEME)));
        }

        if (nbt.contains(NBT_KEY_SECONDARY_THEME)) {
            this.secondaryTheme = Themes.getSecondary(new ResourceLocation(nbt.getString(NBT_KEY_SECONDARY_THEME)));
        }
    }

    @Override
    protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag nbt) {
        nbt.put(NBT_KEY_POSITION, StorageHelper.encode(this.position, BlockPos.CODEC));
        nbt.putInt(NBT_KEY_ROTATION, Orientation.rotationAsInt(this.rotation));
        if (primaryTheme != null) {
            nbt.putString(NBT_KEY_PRIMARY_THEME, primaryTheme.key().toString());
        }

        if (secondaryTheme != null) {
            nbt.putString(NBT_KEY_SECONDARY_THEME, secondaryTheme.key().toString());
        }
    }
}
