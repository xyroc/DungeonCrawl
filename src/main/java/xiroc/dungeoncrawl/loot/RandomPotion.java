package xiroc.dungeoncrawl.loot;

import java.util.HashMap;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import xiroc.dungeoncrawl.util.IPotionCreator;

public class RandomPotion {

	public static final HashMap<String, IPotionCreator> POTIONS = new HashMap<String, IPotionCreator>();

	static {
		POTIONS.put("laudanum", (rand, stage) -> {
			int duration = 160;
			ItemStack potion = new ItemStack(Items.POTION);
			CompoundNBT nbt = new CompoundNBT();
			ListNBT customPotionEffects = new ListNBT();
			CompoundNBT nausea = new CompoundNBT();
			nausea.putInt("Id", 9);
			nausea.putInt("Duration", duration - 60);
			CompoundNBT blindness = new CompoundNBT();
			blindness.putInt("Id", 15);
			blindness.putInt("Duration", duration - 60);
			CompoundNBT regeneration = new CompoundNBT();
			regeneration.putInt("Id", 10);
			regeneration.putInt("Duration", duration);
			customPotionEffects.add(regeneration);
			customPotionEffects.add(blindness);
			customPotionEffects.add(nausea);
			nbt.put("CustomPotionEffects", customPotionEffects);
			nbt.putInt("CustomPotionColor", 7014144);
			CompoundNBT display = new CompoundNBT();
			ListNBT lore = new ListNBT();
			lore.add(new StringNBT(ITextComponent.Serializer.toJson(new StringTextComponent("A medicinal tincture."))));
			display.put("Lore", lore);
			nbt.put("display", display);
			potion.setTag(nbt);
			potion.setDisplayName(new StringTextComponent("Laudanum"));
			return potion;
		});
	}

}
