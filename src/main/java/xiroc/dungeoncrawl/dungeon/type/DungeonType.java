package xiroc.dungeoncrawl.dungeon.type;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.world.level.biome.Biome;
import xiroc.dungeoncrawl.datapack.registry.DatapackRegistries;
import xiroc.dungeoncrawl.dungeon.blueprint.Blueprint;
import xiroc.dungeoncrawl.util.random.IRandom;
import xiroc.dungeoncrawl.util.random.RandomMapping;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public record DungeonType(IRandom<Holder<Blueprint>> entrances, List<DungeonSection> sections, List<SecretRoom> secretRooms) {
    public static final Codec<DungeonType> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Blueprint.RANDOM_HOLDER_CODEC.fieldOf("entrances").forGetter(DungeonType::entrances),
            DungeonSection.CODEC.listOf().fieldOf("sections").forGetter(DungeonType::sections),
            SecretRoom.CODEC.listOf().fieldOf("secret_rooms").forGetter(DungeonType::secretRooms)
    ).apply(instance, DungeonType::new));

    public static final Codec<Holder<DungeonType>> HOLDER_CODEC = RegistryFileCodec.create(DatapackRegistries.DUNGEON_TYPE, DIRECT_CODEC);
    public static final Codec<IRandom<Holder<DungeonType>>> RANDOM_HOLDER_CODEC = IRandom.<Holder<DungeonType>>codecBuilder()
            .valueCodec("type", HOLDER_CODEC)
            .build();
    public static final Codec<RandomMapping<Biome, DungeonType>> BIOME_MAPPING_DIRECT_CODEC = RandomMapping.makeDirectCodec(Biome.LIST_CODEC, RANDOM_HOLDER_CODEC);

    public static class Builder {
        @Nullable
        private IRandom<Holder<Blueprint>> entrances = null;
        private final List<DungeonSection> sections = new ArrayList<>();
        private final List<SecretRoom> secretRooms = new ArrayList<>();

        public Builder entrances(IRandom<Holder<Blueprint>> entrances) {
            this.entrances = entrances;
            return this;
        }

        public Builder section(DungeonSection section) {
            this.sections.add(section);
            return this;
        }

        public Builder secretRoom(SecretRoom.Builder secretRoom) {
            this.secretRooms.add(secretRoom.build());
            return this;
        }

        public DungeonType build() {
            Objects.requireNonNull(entrances);
            if (sections.isEmpty()) {
                throw new IllegalStateException("A dungeon type must have at least one section");
            }
            return new DungeonType(entrances, ImmutableList.copyOf(sections), ImmutableList.copyOf(secretRooms));
        }
    }
}
