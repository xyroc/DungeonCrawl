package xiroc.dungeoncrawl.dungeon.misc;

/*
 * DungeonCrawl (C) 2019 XYROC (XIROC1337), All Rights Reserved 
 */

import java.util.Random;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

public class Banner {

	public static final int PATTERNS = 3;

	public static final ResourceLocation[] BANNERS = new ResourceLocation[] {
			new ResourceLocation("minecraft:white_banner"), new ResourceLocation("minecraft:red_banner"),
			new ResourceLocation("minecraft:green_banner"), new ResourceLocation("minecraft:blue_banner"),
			new ResourceLocation("minecraft:yellow_banner"), new ResourceLocation("minecraft:light_blue_banner"),
			new ResourceLocation("minecraft:orange_banner"), new ResourceLocation("minecraft:gray_banner"),
			new ResourceLocation("minecraft:light_gray_banner"), new ResourceLocation("minecraft:pink_banner"),
			new ResourceLocation("minecraft:magenta_banner"), new ResourceLocation("minecraft:cyan_banner"),
			new ResourceLocation("minecraft:purple_banner"), new ResourceLocation("minecraft:brown_banner"),
			new ResourceLocation("minecraft:lime_banner"), new ResourceLocation("minecraft:black_banner") };

	public static final String[] BANNER_PATTERNS = new String[] { "gru", "bl", "br", "bri", "tr", "bs", "ts", "ls",
			"rs", "bl", "tl", "hh", "vhr", "hhb", "bo", "cbo", "gra", "bts", "tts", "ld", "rd", "lud", "rud", "mc",
			"mr", "vh", "dls", "cs", "ms", "drs", "ss", "cr", "sc", "bt", "tt" };

	public static final String[] BANNER_PATTERNS_FINAL = new String[] { "sku", "cre" };

	public static final String[] BANNER_PATTERNS_FINAL_RARE = new String[] { "glb", "flo" };

	public static CompoundNBT createPatterns(Random rand) {
		CompoundNBT blockEntityTag = new CompoundNBT();
		ListNBT patterns = new ListNBT();
		for (int i = 0; i < PATTERNS; i++) {
			CompoundNBT pattern = new CompoundNBT();
			pattern.putInt("Color", rand.nextInt(15));
			pattern.putString("Pattern",
					i == PATTERNS - 1 && rand.nextDouble() < 0.25
							? (rand.nextDouble() < 0.05
									? BANNER_PATTERNS_FINAL_RARE[rand.nextInt(BANNER_PATTERNS_FINAL_RARE.length)]
									: BANNER_PATTERNS_FINAL[rand.nextInt(BANNER_PATTERNS_FINAL.length)])
							: BANNER_PATTERNS[rand.nextInt(BANNER_PATTERNS.length)]);
			patterns.add(pattern);
		}
		blockEntityTag.put("Patterns", patterns);
		return blockEntityTag;
	}

	public static ItemStack createBanner(Random rand) {
		ItemStack banner = new ItemStack(ForgeRegistries.ITEMS.getValue(BANNERS[rand.nextInt(BANNERS.length)]));
		CompoundNBT nbt = new CompoundNBT();
		nbt.put("BlockEntityTag", createPatterns(rand));
		banner.setTag(nbt);
		return banner;
	}

	public static ItemStack createShield(Random rand) {
		ItemStack banner = new ItemStack(Items.SHIELD);
		CompoundNBT nbt = new CompoundNBT();
		nbt.put("BlockEntityTag", createPatterns(rand));
		banner.setTag(nbt);
		return banner;
	}

}
