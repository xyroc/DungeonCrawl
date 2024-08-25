package xiroc.dungeoncrawl.dungeon.piece;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import xiroc.dungeoncrawl.datapack.registry.DatapackRegistries;
import xiroc.dungeoncrawl.datapack.registry.Delegate;
import xiroc.dungeoncrawl.dungeon.theme.BuiltinThemes;
import xiroc.dungeoncrawl.dungeon.theme.PrimaryTheme;
import xiroc.dungeoncrawl.dungeon.theme.SecondaryTheme;

public abstract class BaseDungeonPiece extends StructurePiece {
    protected static final String NBT_KEY_PRIMARY_THEME = "PrimaryTheme";
    protected static final String NBT_KEY_SECONDARY_THEME = "SecondaryTheme";

    public final Delegate<PrimaryTheme> primaryTheme;
    public final Delegate<SecondaryTheme> secondaryTheme;

    public BaseDungeonPiece(StructurePieceType type, BoundingBox boundingBox, Delegate<PrimaryTheme> primaryTheme, Delegate<SecondaryTheme> secondaryTheme) {
        super(type, 0, boundingBox);
        this.primaryTheme = primaryTheme;
        this.secondaryTheme = secondaryTheme;
    }

    public BaseDungeonPiece(StructurePieceType type, CompoundTag nbt) {
        super(type, nbt);
        if (nbt.contains(NBT_KEY_PRIMARY_THEME)) {
            this.primaryTheme = DatapackRegistries.PRIMARY_THEME.delegateOrThrow(new ResourceLocation(nbt.getString(NBT_KEY_PRIMARY_THEME)));
        } else {
            this.primaryTheme = DatapackRegistries.PRIMARY_THEME.delegateOrThrow(BuiltinThemes.DEFAULT);
        }

        if (nbt.contains(NBT_KEY_SECONDARY_THEME)) {
            this.secondaryTheme = DatapackRegistries.SECONDARY_THEME.delegateOrThrow(new ResourceLocation(nbt.getString(NBT_KEY_SECONDARY_THEME)));
        } else {
            this.secondaryTheme = DatapackRegistries.SECONDARY_THEME.delegateOrThrow(BuiltinThemes.DEFAULT);
        }
    }

    @Override
    protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag nbt) {
        nbt.putString(NBT_KEY_PRIMARY_THEME, primaryTheme.key().toString());
        nbt.putString(NBT_KEY_SECONDARY_THEME, secondaryTheme.key().toString());
    }
}
