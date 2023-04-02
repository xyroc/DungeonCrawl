package xiroc.dungeoncrawl.dungeon.blueprint.anchor;

import net.minecraft.resources.ResourceLocation;
import xiroc.dungeoncrawl.util.random.ArrayUrn;

import java.util.function.Consumer;

public class AnchorPoolReference {
    public final ResourceLocation anchorType;
    private ArrayUrn<Anchor> pool;

    public AnchorPoolReference(ResourceLocation anchorType) {
        this.anchorType = anchorType;
    }

    public void resolve(AnchorProvider anchorProvider, Consumer<String> errorHandler) {
        pool = anchorProvider.anchors(anchorType);
        if (pool == null) {
            errorHandler.accept("No anchors found of type " + anchorType.toString());
        }
    }

    public ArrayUrn<Anchor> get() {
        return pool;
    }
}