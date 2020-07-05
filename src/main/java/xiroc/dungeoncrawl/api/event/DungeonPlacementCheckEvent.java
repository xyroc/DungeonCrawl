package xiroc.dungeoncrawl.api.event;

/*
 * DungeonCrawl (C) 2019 - 2020 XYROC (XIROC1337), All Rights Reserved
 */

import net.minecraft.world.biome.Biome;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

/**
 * Cancel this event to prevent the dungeon from getting placed.
 */

@Cancelable
public class DungeonPlacementCheckEvent extends Event {

    public final ServerWorld world;
    public final Biome biome;
    public final int chunkX, chunkZ;

    public DungeonPlacementCheckEvent(ServerWorld world, Biome biome, int chunkX, int chunkZ) {
        this.world = world;
        this.biome = biome;
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
    }

}
