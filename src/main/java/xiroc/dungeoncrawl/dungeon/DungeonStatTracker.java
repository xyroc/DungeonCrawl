/*
        Dungeon Crawl, a procedural dungeon generator for Minecraft 1.14 and later.
        Copyright (C) 2020

        This program is free software: you can redistribute it and/or modify
        it under the terms of the GNU General Public License as published by
        the Free Software Foundation, either version 3 of the License, or
        (at your option) any later version.

        This program is distributed in the hope that it will be useful,
        but WITHOUT ANY WARRANTY; without even the implied warranty of
        MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
        GNU General Public License for more details.

        You should have received a copy of the GNU General Public License
        along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/

package xiroc.dungeoncrawl.dungeon;


import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;

public class DungeonStatTracker {

    public int totalObjectives, layers, chests, spawners, rooms, traps;
    public LayerStatTracker[] stats;

    public DungeonStatTracker(int layers) {
        this.layers = layers;
        this.stats = new LayerStatTracker[layers];
    }

    public LayerStatTracker getLayerTracker(int layer) {
        return stats[layer];
    }

    public void build() {
        for (LayerStatTracker tracker : stats) {
            this.totalObjectives += tracker.totalObjectives;
            this.chests += tracker.chests;
            this.spawners += tracker.spawners;
            this.rooms += tracker.rooms;
            this.traps += tracker.traps;
            tracker.objectives
                    .addAll(Lists.newArrayList("  Objectives: " + tracker.totalObjectives, "  Chests: " + tracker.chests,
                            "  Spawners: " + tracker.spawners, "  Rooms: " + tracker.rooms, "  Traps: " + tracker.traps));
        }
    }

    public static class LayerStatTracker {

        public int totalObjectives, chests, spawners, rooms, traps;
        public List<String> objectives;

        public LayerStatTracker() {
            objectives = Lists.newArrayList();
        }

    }

    public ArrayList<String> getObjectives() {
        ArrayList<String> objectives = Lists.newArrayList();
        int layer = 1;
        for (LayerStatTracker tracker : stats) {
            objectives.add("----  Layer " + layer++ + "  ----");
            objectives.addAll(tracker.objectives);
            objectives.add("\n");
        }
        return objectives;
    }

}
