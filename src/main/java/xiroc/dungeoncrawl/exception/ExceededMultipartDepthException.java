package xiroc.dungeoncrawl.exception;

import net.minecraft.core.Holder;
import org.jetbrains.annotations.Nullable;
import xiroc.dungeoncrawl.dungeon.blueprint.Blueprint;

/**
 * Indicates that, while populating a blueprint with blueprint parts, the maximum depth was exceeded.
 */
public class ExceededMultipartDepthException extends RuntimeException {
    public final Holder<Blueprint> blueprint;
    @Nullable
    public final ExceededMultipartDepthException cause;

    public ExceededMultipartDepthException(Holder<Blueprint> blueprint) {
        this(blueprint, null);
    }

    public ExceededMultipartDepthException(Holder<Blueprint> blueprint, ExceededMultipartDepthException cause) {
        super("Max multipart depth was exceeded while populating blueprint: " + blueprint.getKey(), cause);
        this.blueprint = blueprint;
        this.cause = null;
    }
}
