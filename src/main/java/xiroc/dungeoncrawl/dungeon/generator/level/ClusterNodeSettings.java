package xiroc.dungeoncrawl.dungeon.generator.level;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import xiroc.dungeoncrawl.dungeon.blueprint.Blueprint;
import xiroc.dungeoncrawl.util.random.IRandom;

/**
 *
 * @param minSize      The minimum number of nodes forming a cluster node.
 * @param maxSize      The maximum allowed number of nodes forming a cluster node.
 * @param fanOut       The chance of each cluster node being used for further level generation. Requires some non-cluster entrances.
 * @param nonTerminals Nodes that do not terminate the cluster node.
 * @param terminals    Nodes that terminate a cluster node.
 */
public record ClusterNodeSettings(int minSize,
                                  int maxSize,
                                  float fanOut,
                                  IRandom<Holder<Blueprint>> nonTerminals,
                                  IRandom<Holder<Blueprint>> terminals) {

    private static final float DEFAULT_FAN_OUT = 0.5f;

    public static final Codec<ClusterNodeSettings> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            Codec.INT.fieldOf("min_size").forGetter(ClusterNodeSettings::minSize),
            Codec.INT.fieldOf("max_size").forGetter(ClusterNodeSettings::maxSize),
            Codec.floatRange(0.0f, 1.0f).optionalFieldOf("fan_out_chance", DEFAULT_FAN_OUT).forGetter(ClusterNodeSettings::fanOut),
            Blueprint.RANDOM_HOLDER_CODEC.fieldOf("non_terminals").forGetter(ClusterNodeSettings::nonTerminals),
            Blueprint.RANDOM_HOLDER_CODEC.fieldOf("terminals").forGetter(ClusterNodeSettings::terminals)
    ).apply(builder, ClusterNodeSettings::new));

    public static final Codec<IRandom<ClusterNodeSettings>> RANDOM_CODEC = IRandom.<ClusterNodeSettings>codecBuilder()
            .valueCodec("settings", CODEC)
            .build();

}
