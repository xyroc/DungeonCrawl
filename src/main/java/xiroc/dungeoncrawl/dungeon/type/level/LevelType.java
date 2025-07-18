package xiroc.dungeoncrawl.dungeon.type.level;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jetbrains.annotations.Nullable;
import xiroc.dungeoncrawl.datapack.registry.DatapackRegistries;
import xiroc.dungeoncrawl.dungeon.blueprint.Blueprint;
import xiroc.dungeoncrawl.dungeon.generator.level.LevelGeneratorSettings;
import xiroc.dungeoncrawl.dungeon.monster.SpawnerType;
import xiroc.dungeoncrawl.dungeon.type.SecretRoom;
import xiroc.dungeoncrawl.util.random.IRandom;
import xiroc.dungeoncrawl.util.storage.GlobalCodecs;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public record LevelType(LevelGeneratorSettings settings,
                        LevelRooms rooms,
                        List<SpecialRoom> specialRooms,
                        List<SecretRoom> secretRooms,
                        IRandom<CorridorStyle> corridorStyles,
                        IRandom<Holder<SpawnerType>> spawners,
                        ResourceKey<LootTable> lootTable) {

    public static final Codec<LevelType> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            LevelGeneratorSettings.CODEC.fieldOf("settings").forGetter(LevelType::settings),
            GlobalCodecs.LOOT_TABLE.fieldOf("loot_table").forGetter(LevelType::lootTable),
            LevelRooms.DIRECT_CODEC.fieldOf("rooms").forGetter(LevelType::rooms),
            SpawnerType.RANDOM_CODEC.fieldOf("spawner_types").forGetter(LevelType::spawners),
            CorridorStyle.Codecs.RANDOM.fieldOf("corridor_styles").forGetter(LevelType::corridorStyles),
            SpecialRoom.CODEC.listOf().optionalFieldOf("special_rooms").forGetter(builder -> Optional.ofNullable(builder.specialRooms.isEmpty() ? null : builder.specialRooms)),
            SecretRoom.CODEC.listOf().optionalFieldOf("secret_rooms").forGetter(builder -> Optional.ofNullable(builder.secretRooms.isEmpty() ? null : builder.secretRooms))
    ).apply(instance, (settings, lootTable, rooms, spawnerTypes, corridorStyles, specialRooms, secretRooms) ->
            new LevelType(
                    settings,
                    rooms,
                    specialRooms.orElse(ImmutableList.of()),
                    secretRooms.orElse(ImmutableList.of()),
                    corridorStyles,
                    spawnerTypes,
                    lootTable
            )
    ));

    public static final Codec<Holder<LevelType>> HOLDER_CODEC = RegistryFileCodec.create(DatapackRegistries.LEVEL_TYPE, DIRECT_CODEC, true);

    public record LevelRooms(IRandom<Holder<Blueprint>> ordinary,
                             IRandom<Holder<Blueprint>> upperStaircase,
                             IRandom<Holder<Blueprint>> lowerStaircase,
                             @Nullable IRandom<IRandom<Holder<Blueprint>>> cluster) {
        public static final Codec<LevelRooms> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
                        Blueprint.RANDOM_HOLDER_CODEC.fieldOf("ordinary").forGetter(LevelRooms::ordinary),
                        Blueprint.RANDOM_HOLDER_CODEC.fieldOf("upper_staircase").forGetter(LevelRooms::upperStaircase),
                        Blueprint.RANDOM_HOLDER_CODEC.fieldOf("lower_staircase").forGetter(LevelRooms::lowerStaircase),
                        Blueprint.RANDOM_RANDOM_HOLDER_CODEC.optionalFieldOf("cluster").forGetter(levelRooms -> Optional.ofNullable(levelRooms.cluster))
                ).apply(instance, (rooms, upperStaircaseRooms, lowerStaircaseRooms, clusterRooms) ->
                        new LevelRooms(rooms, upperStaircaseRooms, lowerStaircaseRooms, clusterRooms.orElse(null)))
        );

        public static class Builder {
            @Nullable
            private IRandom.Builder<Holder<Blueprint>> ordinary = null;
            @Nullable
            private IRandom.Builder<Holder<Blueprint>> upperStaircase = null;
            @Nullable
            private IRandom.Builder<Holder<Blueprint>> lowerStaircase = null;
            @Nullable
            private IRandom.Builder<IRandom<Holder<Blueprint>>> cluster = null;

            public Builder ordinary(@Nullable IRandom.Builder<Holder<Blueprint>> ordinary) {
                this.ordinary = ordinary;
                return this;
            }

            public Builder upperStaircase(@Nullable IRandom.Builder<Holder<Blueprint>> upperStaircase) {
                this.upperStaircase = upperStaircase;
                return this;
            }

            public Builder lowerStaircase(@Nullable IRandom.Builder<Holder<Blueprint>> lowerStaircase) {
                this.lowerStaircase = lowerStaircase;
                return this;
            }

            public Builder cluster(@Nullable IRandom.Builder<IRandom<Holder<Blueprint>>> cluster) {
                this.cluster = cluster;
                return this;
            }

            public LevelRooms build() {
                Objects.requireNonNull(ordinary, "No ordinary rooms were specified.");
                Objects.requireNonNull(upperStaircase, "No upper staircase rooms were specified.");
                Objects.requireNonNull(lowerStaircase, "No lower staircase rooms were specified.");
                return new LevelRooms(ordinary.build(), upperStaircase.build(), lowerStaircase.build(), cluster != null ? cluster.build() : null);
            }
        }
    }

    public static class Builder {
        @Nullable
        private LevelGeneratorSettings.Builder settings = null;
        @Nullable
        private LevelRooms rooms = null;
        @Nullable
        private IRandom.Builder<Holder<SpawnerType>> spawnerTypes = null;

        @Nullable
        private IRandom.Builder<CorridorStyle> corridorStyles = null;

        private final List<SpecialRoom> specialRooms = new ArrayList<>();
        private final List<SecretRoom> secretRooms = new ArrayList<>();

        @Nullable
        private ResourceKey<LootTable> lootTable = null;


        public LevelType build() {
            Objects.requireNonNull(settings, "No generation settings were specified");
            Objects.requireNonNull(rooms, "No rooms were specified");
            Objects.requireNonNull(corridorStyles, "No corridor styles were specified");
            Objects.requireNonNull(spawnerTypes, "No spawner types were specified");
            Objects.requireNonNull(lootTable, "No loot table was specified.");
            return new LevelType(settings.build(),
                    rooms,
                    ImmutableList.copyOf(specialRooms),
                    ImmutableList.copyOf(secretRooms),
                    corridorStyles.build(),
                    spawnerTypes.build(),
                    lootTable);
        }

        public Builder settings(@Nullable LevelGeneratorSettings.Builder settings) {
            this.settings = settings;
            return this;
        }

        public Builder rooms(@Nullable LevelRooms rooms) {
            this.rooms = rooms;
            return this;
        }

        public Builder specialRoom(SpecialRoom specialRoom) {
            this.specialRooms.add(specialRoom);
            return this;
        }

        public Builder secretRoom(SecretRoom.Builder secretRoom) {
            this.secretRooms.add(secretRoom.build());
            return this;
        }

        public Builder corridorStyles(@Nullable IRandom.Builder<CorridorStyle> corridorStyles) {
            this.corridorStyles = corridorStyles;
            return this;
        }

        public Builder spawnerTypes(@Nullable IRandom.Builder<Holder<SpawnerType>> spawnerTypes) {
            this.spawnerTypes = spawnerTypes;
            return this;
        }

        public Builder lootTable(@Nullable ResourceKey<LootTable> lootTable) {
            this.lootTable = lootTable;
            return this;
        }
    }
}
