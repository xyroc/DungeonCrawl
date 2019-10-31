package xiroc.dungeoncrawl.dungeon.treasure.function;

import java.util.Random;

/*
 * DungeonCrawl (C) 2019 XYROC (XIROC1337), All Rights Reserved 
 */

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootFunction;
import net.minecraft.world.storage.loot.conditions.ILootCondition;
import xiroc.dungeoncrawl.DungeonCrawl;

public class SuspiciousStew extends LootFunction {

	public static CompoundNBT[] EFFECTS;

	static {
		CompoundNBT regeneration = new CompoundNBT();
		regeneration.putInt("EffectId", 10);
		regeneration.putInt("EffectDuration", 160);

		CompoundNBT weakness = new CompoundNBT();
		weakness.putInt("EffectId", 18);
		weakness.putInt("EffectDuration", 320);

		CompoundNBT poison = new CompoundNBT();
		poison.putInt("EffectId", 19);
		poison.putInt("EffectDuration", 160);

		CompoundNBT healthBoost = new CompoundNBT();
		healthBoost.putInt("EffectId", 21);
		healthBoost.putInt("EffectDuration", 320);

		CompoundNBT blindness = new CompoundNBT();
		blindness.putInt("EffectId", 15);
		blindness.putInt("EffectDuration", 120);

		CompoundNBT resistance = new CompoundNBT();
		resistance.putInt("EffectId", 11);
		resistance.putInt("EffectDuration", 320);

		CompoundNBT nausea = new CompoundNBT();
		nausea.putInt("EffectId", 9);
		nausea.putInt("EffectDuration", 120);

		CompoundNBT absorption = new CompoundNBT();
		absorption.putInt("EffectId", 22);
		absorption.putInt("EffectDuration", 320);

		CompoundNBT hunger = new CompoundNBT();
		hunger.putInt("EffectId", 17);
		hunger.putInt("EffectDuration", 200);

		CompoundNBT saturation = new CompoundNBT();
		saturation.putInt("EffectId", 23);
		saturation.putInt("EffectDuration", 80);

		CompoundNBT fireResistance = new CompoundNBT();
		fireResistance.putInt("EffectId", 12);
		fireResistance.putInt("EffectDuration", 320);

		CompoundNBT strength = new CompoundNBT();
		strength.putInt("EffectId", 5);
		strength.putInt("EffectDuration", 320);

		CompoundNBT speed = new CompoundNBT();
		speed.putInt("EffectId", 1);
		speed.putInt("EffectDuration", 220);

		CompoundNBT slowness = new CompoundNBT();
		slowness.putInt("EffectId", 2);
		slowness.putInt("EffectDuration", 160);

		CompoundNBT miningFatique = new CompoundNBT();
		miningFatique.putInt("EffectId", 4);
		miningFatique.putInt("EffectDuration", 160);

		CompoundNBT haste = new CompoundNBT();
		haste.putInt("EffectId", 3);
		haste.putInt("EffectDuration", 320);

		CompoundNBT jumpBoost = new CompoundNBT();
		jumpBoost.putInt("EffectId", 8);
		jumpBoost.putInt("EffectDuration", 160);

		CompoundNBT wither = new CompoundNBT();
		wither.putInt("EffectId", 20);
		wither.putInt("EffectDuration", 80);

		EFFECTS = new CompoundNBT[] { regeneration, weakness, poison, healthBoost, blindness, resistance, nausea,
				hunger, saturation, fireResistance, strength, speed, slowness, miningFatique, haste, wither };
	}

	public SuspiciousStew(ILootCondition[] conditionsIn) {
		super(conditionsIn);
	}

	@Override
	public ItemStack doApply(ItemStack stack, LootContext context) {
		stack.getOrCreateTag().put("Effects", createEffectList(context.getRandom()));
		return stack;
	}

	public static class Serializer extends LootFunction.Serializer<SuspiciousStew> {

		public Serializer() {
			super(DungeonCrawl.locate("suspicious_stew"), SuspiciousStew.class);
		}

		@Override
		public SuspiciousStew deserialize(JsonObject object, JsonDeserializationContext deserializationContext,
				ILootCondition[] conditionsIn) {
			return new SuspiciousStew(conditionsIn);
		}

	}

	public static ListNBT createEffectList(Random rand) {
		int counter = rand.nextInt(2), max = EFFECTS.length;
		ListNBT effects = new ListNBT();

		while (counter < max) {
			effects.add(EFFECTS[counter].copy());
			counter += 1 + rand.nextInt(3);
		}

		return effects;
	}

}
