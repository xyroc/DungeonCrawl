package xiroc.dungeoncrawl.dungeon;

/*
 * DungeonCrawl (C) 2019 - 2020 XYROC (XIROC1337), All Rights Reserved 
 */

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;

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
