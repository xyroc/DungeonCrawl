package xiroc.dungeoncrawl.dungeon.blueprint;

import com.google.common.collect.ImmutableList;

import java.util.List;

public record EntranceContainer(List<Entrance> normalEntrances, List<Entrance> hybridEntrances, List<Entrance> clusterEntrances) {
    public static class Builder {
        private final ImmutableList.Builder<Entrance> normalEntrances = ImmutableList.builder();
        private final ImmutableList.Builder<Entrance> hybridEntrances = ImmutableList.builder();
        private final ImmutableList.Builder<Entrance> clusterEntrances = ImmutableList.builder();

        public void addEntrance(BlueprintConfiguration.EntranceType type, Entrance entrance) {
            switch (type) {
                case NORMAL -> normalEntrances.add(entrance);
                case HYBRID -> hybridEntrances.add(entrance);
                case CLUSTER -> clusterEntrances.add(entrance);
            }
        }

        public EntranceContainer build() {
            return new EntranceContainer(normalEntrances.build(), hybridEntrances.build(), clusterEntrances.build());
        }
    }
}
