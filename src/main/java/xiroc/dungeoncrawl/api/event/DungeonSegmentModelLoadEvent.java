package xiroc.dungeoncrawl.api.event;

/*
 * DungeonCrawl (C) 2019 - 2020 XYROC (XIROC1337), All Rights Reserved 
 */

import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

@Cancelable
public class DungeonSegmentModelLoadEvent extends Event {

	public String path;

	public DungeonSegmentModelLoadEvent(String path) {
		this.path = path;
	}

}
