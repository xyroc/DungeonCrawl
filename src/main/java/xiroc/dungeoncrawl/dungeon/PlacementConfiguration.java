package xiroc.dungeoncrawl.dungeon;

import xiroc.dungeoncrawl.dungeon.model.DungeonModelBlockType;
import xiroc.dungeoncrawl.dungeon.model.PlacementBehaviour;

public class PlacementConfiguration {

    public static final PlacementConfiguration DEFAULT = new Builder().build();

    public static final PlacementConfiguration BOTTOM_STAIRCASE = new Builder()
            .generic(DungeonModelBlockType.GENERIC.defaultPlacementBehavior.withAirBlock((theme, secondaryTheme) -> theme.getFencing()))
            .build();

    public static final PlacementConfiguration CORRIDOR = new Builder()
            .floor(PlacementBehaviour.STRIPES).build();

    public static final PlacementConfiguration ROOM = new Builder()
            .floor(PlacementBehaviour.SMALL_GRID).build();

    public static final PlacementConfiguration NODE = new Builder()
            .floor(PlacementBehaviour.LARGE_GRID).build();

    public final PlacementBehaviour solid;
    public final PlacementBehaviour solidStairs;
    public final PlacementBehaviour solidSlab;
    public final PlacementBehaviour generic;
    public final PlacementBehaviour genericOrFencing;
    public final PlacementBehaviour slab;
    public final PlacementBehaviour solidPillar;
    public final PlacementBehaviour solidFloor;
    public final PlacementBehaviour fencing;
    public final PlacementBehaviour floor;
    public final PlacementBehaviour fluid;
    public final PlacementBehaviour looseGround;
    public final PlacementBehaviour stairs;
    public final PlacementBehaviour wall;

    public final PlacementBehaviour pillar;
    public final PlacementBehaviour trapdoor;
    public final PlacementBehaviour door;
    public final PlacementBehaviour fence;
    public final PlacementBehaviour fenceGate;
    public final PlacementBehaviour material;
    public final PlacementBehaviour materialButton;
    public final PlacementBehaviour materialPressurePlate;
    public final PlacementBehaviour materialSlab;
    public final PlacementBehaviour materialStairs;

    public final PlacementBehaviour chest;
    public final PlacementBehaviour carpet;
    public final PlacementBehaviour other;
    public final PlacementBehaviour skull;

    public PlacementConfiguration(PlacementBehaviour solid,
                                  PlacementBehaviour solidStairs,
                                  PlacementBehaviour solidSlab,
                                  PlacementBehaviour generic,
                                  PlacementBehaviour genericOrFencing,
                                  PlacementBehaviour slab,
                                  PlacementBehaviour solidPillar,
                                  PlacementBehaviour solidFloor,
                                  PlacementBehaviour fencing,
                                  PlacementBehaviour floor,
                                  PlacementBehaviour fluid,
                                  PlacementBehaviour looseGround,
                                  PlacementBehaviour stairs,
                                  PlacementBehaviour wall,
                                  PlacementBehaviour pillar,
                                  PlacementBehaviour trapdoor,
                                  PlacementBehaviour door,
                                  PlacementBehaviour fence,
                                  PlacementBehaviour fenceGate,
                                  PlacementBehaviour material,
                                  PlacementBehaviour materialButton,
                                  PlacementBehaviour materialPressurePlate,
                                  PlacementBehaviour materialSlab,
                                  PlacementBehaviour materialStairs,
                                  PlacementBehaviour chest,
                                  PlacementBehaviour carpet,
                                  PlacementBehaviour other,
                                  PlacementBehaviour skull) {
        this.solid = solid;
        this.solidStairs = solidStairs;
        this.solidSlab = solidSlab;
        this.generic = generic;
        this.genericOrFencing = genericOrFencing;
        this.slab = slab;
        this.solidPillar = solidPillar;
        this.solidFloor = solidFloor;
        this.fencing = fencing;
        this.floor = floor;
        this.fluid = fluid;
        this.looseGround = looseGround;
        this.stairs = stairs;
        this.wall = wall;
        this.pillar = pillar;
        this.trapdoor = trapdoor;
        this.door = door;
        this.fence = fence;
        this.fenceGate = fenceGate;
        this.material = material;
        this.materialButton = materialButton;
        this.materialPressurePlate = materialPressurePlate;
        this.materialSlab = materialSlab;
        this.materialStairs = materialStairs;
        this.chest = chest;
        this.carpet = carpet;
        this.other = other;
        this.skull = skull;
    }

    public PlacementBehaviour getSolid() {
        return solid;
    }

    public PlacementBehaviour getSolidStairs() {
        return solidStairs;
    }

    public PlacementBehaviour getSolidSlab() {
        return solidSlab;
    }

    public PlacementBehaviour getGeneric() {
        return generic;
    }

    public PlacementBehaviour getGenericOrFencing() {
        return genericOrFencing;
    }

    public PlacementBehaviour getSlab() {
        return slab;
    }

    public PlacementBehaviour getSolidPillar() {
        return solidPillar;
    }

    public PlacementBehaviour getSolidFloor() {
        return solidFloor;
    }

    public PlacementBehaviour getFencing() {
        return fencing;
    }

    public PlacementBehaviour getFloor() {
        return floor;
    }

    public PlacementBehaviour getFluid() {
        return fluid;
    }

    public PlacementBehaviour getLooseGround() {
        return looseGround;
    }

    public PlacementBehaviour getStairs() {
        return stairs;
    }

    public PlacementBehaviour getWall() {
        return wall;
    }

    public PlacementBehaviour getPillar() {
        return pillar;
    }

    public PlacementBehaviour getTrapdoor() {
        return trapdoor;
    }

    public PlacementBehaviour getDoor() {
        return door;
    }

    public PlacementBehaviour getFence() {
        return fence;
    }

    public PlacementBehaviour getFenceGate() {
        return fenceGate;
    }

    public PlacementBehaviour getMaterial() {
        return material;
    }

    public PlacementBehaviour getMaterialButton() {
        return materialButton;
    }

    public PlacementBehaviour getMaterialPressurePlate() {
        return materialPressurePlate;
    }

    public PlacementBehaviour getMaterialSlab() {
        return materialSlab;
    }

    public PlacementBehaviour getMaterialStairs() {
        return materialStairs;
    }

    public PlacementBehaviour getChest() {
        return chest;
    }

    public PlacementBehaviour getCarpet() {
        return carpet;
    }

    public PlacementBehaviour getOther() {
        return other;
    }

    public PlacementBehaviour getSkull() {
        return skull;
    }

    private static class Builder {
        private PlacementBehaviour solid = DungeonModelBlockType.SOLID.defaultPlacementBehavior;
        private PlacementBehaviour solidStairs = DungeonModelBlockType.SOLID_STAIRS.defaultPlacementBehavior;
        private PlacementBehaviour solidSlab = DungeonModelBlockType.SOLID_SLAB.defaultPlacementBehavior;
        private PlacementBehaviour generic = DungeonModelBlockType.GENERIC.defaultPlacementBehavior;
        private PlacementBehaviour genericOrFencing = DungeonModelBlockType.GENERIC_OR_FENCING.defaultPlacementBehavior;
        private PlacementBehaviour slab = DungeonModelBlockType.SLAB.defaultPlacementBehavior;
        private PlacementBehaviour solidPillar = DungeonModelBlockType.SOLID_PILLAR.defaultPlacementBehavior;
        private PlacementBehaviour solidFloor = DungeonModelBlockType.SOLID_FLOOR.defaultPlacementBehavior;
        private PlacementBehaviour fencing = DungeonModelBlockType.FENCING.defaultPlacementBehavior;
        private PlacementBehaviour floor = DungeonModelBlockType.FLOOR.defaultPlacementBehavior;
        private PlacementBehaviour fluid = DungeonModelBlockType.FLOOR.defaultPlacementBehavior;
        private PlacementBehaviour looseGround = DungeonModelBlockType.LOOSE_GROUND.defaultPlacementBehavior;
        private PlacementBehaviour stairs = DungeonModelBlockType.STAIRS.defaultPlacementBehavior;
        private PlacementBehaviour wall = DungeonModelBlockType.WALL.defaultPlacementBehavior;

        private PlacementBehaviour pillar = DungeonModelBlockType.PILLAR.defaultPlacementBehavior;
        private PlacementBehaviour trapdoor = DungeonModelBlockType.TRAPDOOR.defaultPlacementBehavior;
        private PlacementBehaviour door = DungeonModelBlockType.DOOR.defaultPlacementBehavior;
        private PlacementBehaviour fence = DungeonModelBlockType.FENCE.defaultPlacementBehavior;
        private PlacementBehaviour fenceGate = DungeonModelBlockType.FENCE_GATE.defaultPlacementBehavior;
        private PlacementBehaviour material = DungeonModelBlockType.MATERIAL.defaultPlacementBehavior;
        private PlacementBehaviour materialButton = DungeonModelBlockType.MATERIAL_BUTTON.defaultPlacementBehavior;
        private PlacementBehaviour materialPressurePlate = DungeonModelBlockType.MATERIAL_PRESSURE_PLATE.defaultPlacementBehavior;
        private PlacementBehaviour materialSlab = DungeonModelBlockType.MATERIAL_SLAB.defaultPlacementBehavior;
        private PlacementBehaviour materialStairs = DungeonModelBlockType.MATERIAL_STAIRS.defaultPlacementBehavior;

        private PlacementBehaviour chest = DungeonModelBlockType.MATERIAL_STAIRS.defaultPlacementBehavior;
        private PlacementBehaviour carpet = DungeonModelBlockType.MATERIAL_STAIRS.defaultPlacementBehavior;
        private PlacementBehaviour other = DungeonModelBlockType.MATERIAL_STAIRS.defaultPlacementBehavior;
        private PlacementBehaviour skull = DungeonModelBlockType.MATERIAL_STAIRS.defaultPlacementBehavior;

        public Builder solid(PlacementBehaviour solid) {
            this.solid = solid;
            return this;
        }

        public Builder solidStairs(PlacementBehaviour solidStairs) {
            this.solidStairs = solidStairs;
            return this;
        }

        public Builder solidSlab(PlacementBehaviour solidSlab) {
            this.solidSlab = solidSlab;
            return this;
        }

        public Builder generic(PlacementBehaviour generic) {
            this.generic = generic;
            return this;
        }

        public Builder genericOrFencing(PlacementBehaviour genericOrFencing) {
            this.genericOrFencing = genericOrFencing;
            return this;
        }

        public Builder slab(PlacementBehaviour slab) {
            this.slab = slab;
            return this;
        }

        public Builder solidPillar(PlacementBehaviour solidPillar) {
            this.solidPillar = solidPillar;
            return this;
        }

        public Builder solidFloor(PlacementBehaviour solidFloor) {
            this.solidFloor = solidFloor;
            return this;
        }

        public Builder fencing(PlacementBehaviour fencing) {
            this.fencing = fencing;
            return this;
        }

        public Builder floor(PlacementBehaviour floor) {
            this.floor = floor;
            return this;
        }

        public Builder fluid(PlacementBehaviour fluid) {
            this.fluid = fluid;
            return this;
        }

        public Builder looseGround(PlacementBehaviour looseGround) {
            this.looseGround = looseGround;
            return this;
        }

        public Builder stairs(PlacementBehaviour stairs) {
            this.stairs = stairs;
            return this;
        }

        public Builder wall(PlacementBehaviour wall) {
            this.wall = wall;
            return this;
        }

        public Builder pillar(PlacementBehaviour pillar) {
            this.pillar = pillar;
            return this;
        }

        public Builder trapdoor(PlacementBehaviour trapdoor) {
            this.trapdoor = trapdoor;
            return this;
        }

        public Builder door(PlacementBehaviour door) {
            this.door = door;
            return this;
        }

        public Builder fence(PlacementBehaviour fence) {
            this.fence = fence;
            return this;
        }

        public Builder fenceGate(PlacementBehaviour fenceGate) {
            this.fenceGate = fenceGate;
            return this;
        }

        public Builder material(PlacementBehaviour material) {
            this.material = material;
            return this;
        }

        public Builder materialButton(PlacementBehaviour materialButton) {
            this.materialButton = materialButton;
            return this;
        }

        public Builder materialPressurePlate(PlacementBehaviour materialPressurePlate) {
            this.materialPressurePlate = materialPressurePlate;
            return this;
        }

        public Builder materialSlab(PlacementBehaviour materialSlab) {
            this.materialSlab = materialSlab;
            return this;
        }

        public Builder materialStairs(PlacementBehaviour materialStairs) {
            this.materialStairs = materialStairs;
            return this;
        }

        public Builder chest(PlacementBehaviour chest) {
            this.chest = chest;
            return this;
        }

        public Builder carpet(PlacementBehaviour carpet) {
            this.carpet = carpet;
            return this;
        }

        public Builder other(PlacementBehaviour other) {
            this.other = other;
            return this;
        }

        public Builder skull(PlacementBehaviour skull) {
            this.skull = skull;
            return this;
        }

        public PlacementConfiguration build() {
            return new PlacementConfiguration(
                    solid,
                    solidStairs,
                    solidSlab,
                    generic,
                    genericOrFencing,
                    slab,
                    solidPillar,
                    solidFloor,
                    fencing,
                    floor,
                    fluid,
                    looseGround,
                    stairs,
                    wall,
                    pillar,
                    trapdoor,
                    door,
                    fence,
                    fenceGate,
                    material,
                    materialButton,
                    materialPressurePlate,
                    materialSlab,
                    materialStairs,
                    chest,
                    carpet,
                    other,
                    skull);
        }

    }

}
