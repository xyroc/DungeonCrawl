package xiroc.dungeoncrawl.dungeon.blueprint.template.block;

import com.google.common.collect.ImmutableList;
import net.minecraft.core.Vec3i;

/**
 * Represents a column of template blocks at certain x,z coordinates within a template blueprint.
 *
 * @param x       the x coordinate of the column.
 * @param z       the z coordinate of the column.
 * @param lowestY the y coordinate of the lowest block in the column.
 * @param blocks  the template blocks, in no particular order.
 */
public record TemplateBlockColumn(int x, int z, int lowestY, ImmutableList<TemplateBlock> blocks) {
    public static class Builder {
        private final int x;
        private final int z;
        private final ImmutableList.Builder<TemplateBlock> blocks = ImmutableList.builder();

        private int lowestY = Integer.MAX_VALUE;

        public Builder(int x, int z) {
            this.x = x;
            this.z = z;
        }

        public void addBlock(TemplateBlock block) {
            Vec3i position = block.position();
            if (position.getX() != this.x || position.getZ() != this.z) {
                throw new IllegalArgumentException("The template block at " + position + " is not part of the column at (" + x + "," + z + ")");
            }
            blocks.add(block);
            lowestY = Math.min(lowestY, position.getY());
        }

        public TemplateBlockColumn build() {
            var blocks = this.blocks.build();
            if (blocks.isEmpty()) {
                throw new IllegalStateException("A template block column must contain at least one block");
            }
            return new TemplateBlockColumn(x, z, lowestY, blocks);
        }
    }
}
