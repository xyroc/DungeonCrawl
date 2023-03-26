package xiroc.dungeoncrawl.mixin.accessor;

import net.minecraft.world.level.levelgen.structure.BoundingBox;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BoundingBox.class)
public interface BoundingBoxAccessor {

    @Accessor("minX")
    void setMinX(int minX);

    @Accessor("minY")
    void setMinY(int minY);

    @Accessor("minZ")
    void setMinZ(int minZ);

    @Accessor("maxX")
    void setMaxX(int maxX);

    @Accessor("maxY")
    void setMaxY(int maxY);

    @Accessor("maxZ")
    void setMaxZ(int maxZ);

}