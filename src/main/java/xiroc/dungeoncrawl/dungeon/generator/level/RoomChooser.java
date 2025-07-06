package xiroc.dungeoncrawl.dungeon.generator.level;

import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.Nullable;
import xiroc.dungeoncrawl.datapack.registry.Delegate;
import xiroc.dungeoncrawl.dungeon.blueprint.Blueprint;
import xiroc.dungeoncrawl.dungeon.generator.element.NodeElement;
import xiroc.dungeoncrawl.dungeon.type.level.LevelType;
import xiroc.dungeoncrawl.dungeon.type.level.SpecialRoom;
import xiroc.dungeoncrawl.util.random.IRandom;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

public class RoomChooser {
    private final IRandom<Delegate<Blueprint>> standardRooms;
    private final List<RoomEntry> specialRooms;

    /**
     * The index of the current room choice in the {@code specialRooms} list.
     * Negative if the current choice is not a special room.
     */
    private int currentChoice = -1;
    /**
     * The number of standard and special rooms placed.
     */
    private int roomsPlaced = 0;

    public RoomChooser(LevelType levelType, List<RoomEntry> additionalSpecialRooms, RandomSource random) {
        this.standardRooms = levelType.rooms();
        this.specialRooms = new ArrayList<>();
        this.specialRooms.addAll(additionalSpecialRooms);
        for (SpecialRoom specialRoom : levelType.specialRooms()) {
            this.specialRooms.add(new RoomEntry(specialRoom.variants(), specialRoom.minDepth().nextInt(random), specialRoom.amount().nextInt(random)));
        }
        this.specialRooms.sort(Comparator.comparingInt(entry -> entry.minDepth));
    }

    /**
     * Randomly choose the next room blueprint.
     * If the chosen blueprint ends up in use, it has to be confirmed by calling {@code commit()} before the next call to {@code nextRoom()}.
     *
     * @param depth  The depth at which the room will be.
     * @param random Random number generator.
     * @return The room choice.
     */
    public Delegate<Blueprint> nextRoom(int depth, RandomSource random) {
        final int eligibleSpecialRooms = numberOfEligibleSpecialRooms(depth);
        for (int attempt = 0; attempt < eligibleSpecialRooms; ++attempt) {
            currentChoice = random.nextInt(eligibleSpecialRooms);
            final RoomEntry entry = specialRooms.get(currentChoice);
            if (entry.nextPlacement > roomsPlaced) {
                // Room is on cooldown, try again.
                continue;
            }
            return entry.variants.roll(random);
        }
        currentChoice = -1;
        return standardRooms.roll(random);
    }

    /**
     * Find the number of special room entries eligible for this depth.
     * Rooms on cooldown are still considered eligible.
     *
     * @param depth The depth the room would be placed at.
     * @return The number of rooms. Might be zero.
     */
    private int numberOfEligibleSpecialRooms(int depth) {
        int eligibleRooms = 0;
        for (final RoomEntry entry : specialRooms) {
            if (entry.minDepth > depth) {
                // The list is sorted by min depth, so we can exit the loop here.
                break;
            }
            ++eligibleRooms;
        }
        return eligibleRooms;
    }

    /**
     * Signals that the last choice retrieved via {@code nextRoom()} is now in use.
     *
     * @param node The node that is using the chosen room.
     */
    public void commit(NodeElement node) {
        ++roomsPlaced;
        if (currentChoice < 0) {
            // The current choice is not a special room, nothing to do.
            return;
        }
        // The current choice is a special room, update state.
        final RoomEntry entry = specialRooms.get(currentChoice);
        entry.nextPlacement = roomsPlaced + 3;
        if (entry.callback != null) {
            entry.callback.accept(node);
        }
        entry.amountLeft -= 1;
        if (entry.amountLeft <= 0) {
            specialRooms.remove(currentChoice);
        }
    }

    public static class RoomEntry {
        private final IRandom<Delegate<Blueprint>> variants;
        private final int minDepth;
        private int amountLeft;
        @Nullable
        private final Consumer<NodeElement> callback;

        /**
         * The total number of rooms that must have been placed until this room can be placed again.
         */
        private int nextPlacement = 0;

        public RoomEntry(IRandom<Delegate<Blueprint>> variants, int minDepth, int amount, @Nullable Consumer<NodeElement> callback) {
            this.variants = variants;
            this.minDepth = minDepth;
            this.amountLeft = amount;
            this.callback = callback;
        }

        public RoomEntry(IRandom<Delegate<Blueprint>> variants, int minDepth, int amount) {
            this(variants, minDepth, amount, null);
        }
    }
}
