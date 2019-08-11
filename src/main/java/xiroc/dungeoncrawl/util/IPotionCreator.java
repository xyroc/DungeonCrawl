package xiroc.dungeoncrawl.util;

import java.util.Random;

import net.minecraft.item.ItemStack;

public interface IPotionCreator {
	
	ItemStack create(Random rand, int stage);

}
