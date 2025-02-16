package xiroc.dungeoncrawl.dungeon.generator.level;

import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.generator.element.CorridorElement;
import xiroc.dungeoncrawl.dungeon.type.SecretRoom;

import java.util.List;

/**
 * Responsible for generating secret rooms for a specific dungeon layer.
 */
public record SecretRoomGenerator(List<SecretRoom> secretRooms) {
    private static final int MAX_ROOM_PLACEMENT_ATTEMPTS = 16;

    public void generateSecretRooms(LevelGenerator levelGenerator) {
        for (final SecretRoom secretRoom : secretRooms) {
            generateSecretRoom(secretRoom, levelGenerator);
        }
    }

    private void generateSecretRoom(SecretRoom secretRoom, LevelGenerator levelGenerator) {
        final List<CorridorElement> corridors = levelGenerator.corridors;
        for (int attempt = 0; attempt < MAX_ROOM_PLACEMENT_ATTEMPTS; ++attempt) {
            final CorridorElement corridor = corridors.get(levelGenerator.random.nextInt(corridors.size()));
            if (corridor.attachSecretRoomWithEntrance(secretRoom)) {
                DungeonCrawl.LOGGER.debug("Created a secret room after {} attempts!", attempt + 1);
                return;
            }
        }
        DungeonCrawl.LOGGER.debug("Could not create a secret room on layer {}! :-|", levelGenerator.stage);
    }
}
