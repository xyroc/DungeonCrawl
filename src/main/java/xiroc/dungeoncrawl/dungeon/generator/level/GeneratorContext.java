package xiroc.dungeoncrawl.dungeon.generator.level;

import xiroc.dungeoncrawl.dungeon.generator.plan.DungeonPlan;

public record GeneratorContext(DungeonPlan dungeonPlan, LevelGenerator levelGenerator) {
}
