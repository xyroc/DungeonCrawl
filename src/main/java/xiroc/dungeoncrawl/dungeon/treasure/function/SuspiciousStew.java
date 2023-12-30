/*
        Dungeon Crawl, a procedural dungeon generator for Minecraft 1.14 and later.
        Copyright (C) 2020

        This program is free software: you can redistribute it and/or modify
        it under the terms of the GNU General Public License as published by
        the Free Software Foundation, either version 3 of the License, or
        (at your option) any later version.

        This program is distributed in the hope that it will be useful,
        but WITHOUT ANY WARRANTY; without even the implied warranty of
        MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
        GNU General Public License for more details.

        You should have received a copy of the GNU General Public License
        along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/

package xiroc.dungeoncrawl.dungeon.treasure.function;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import xiroc.dungeoncrawl.dungeon.treasure.Loot;

import java.util.List;

public class SuspiciousStew extends LootItemConditionalFunction {
    public static final Codec<SuspiciousStew> CODEC = RecordCodecBuilder.create((builder) -> commonFields(builder).apply(builder, SuspiciousStew::new));

    public static CompoundTag[] EFFECTS;

    static {
        CompoundTag regeneration = new CompoundTag();
        regeneration.putInt("EffectId", 10);
        regeneration.putInt("EffectDuration", 160);

        CompoundTag weakness = new CompoundTag();
        weakness.putInt("EffectId", 18);
        weakness.putInt("EffectDuration", 320);

        CompoundTag poison = new CompoundTag();
        poison.putInt("EffectId", 19);
        poison.putInt("EffectDuration", 160);

        CompoundTag healthBoost = new CompoundTag();
        healthBoost.putInt("EffectId", 21);
        healthBoost.putInt("EffectDuration", 320);

        CompoundTag blindness = new CompoundTag();
        blindness.putInt("EffectId", 15);
        blindness.putInt("EffectDuration", 120);

        CompoundTag resistance = new CompoundTag();
        resistance.putInt("EffectId", 11);
        resistance.putInt("EffectDuration", 320);

        CompoundTag nausea = new CompoundTag();
        nausea.putInt("EffectId", 9);
        nausea.putInt("EffectDuration", 120);

        CompoundTag absorption = new CompoundTag();
        absorption.putInt("EffectId", 22);
        absorption.putInt("EffectDuration", 320);

        CompoundTag hunger = new CompoundTag();
        hunger.putInt("EffectId", 17);
        hunger.putInt("EffectDuration", 200);

        CompoundTag saturation = new CompoundTag();
        saturation.putInt("EffectId", 23);
        saturation.putInt("EffectDuration", 80);

        CompoundTag fireResistance = new CompoundTag();
        fireResistance.putInt("EffectId", 12);
        fireResistance.putInt("EffectDuration", 320);

        CompoundTag strength = new CompoundTag();
        strength.putInt("EffectId", 5);
        strength.putInt("EffectDuration", 320);

        CompoundTag speed = new CompoundTag();
        speed.putInt("EffectId", 1);
        speed.putInt("EffectDuration", 220);

        CompoundTag slowness = new CompoundTag();
        slowness.putInt("EffectId", 2);
        slowness.putInt("EffectDuration", 160);

        CompoundTag miningFatique = new CompoundTag();
        miningFatique.putInt("EffectId", 4);
        miningFatique.putInt("EffectDuration", 160);

        CompoundTag haste = new CompoundTag();
        haste.putInt("EffectId", 3);
        haste.putInt("EffectDuration", 320);

        CompoundTag jumpBoost = new CompoundTag();
        jumpBoost.putInt("EffectId", 8);
        jumpBoost.putInt("EffectDuration", 160);

        CompoundTag wither = new CompoundTag();
        wither.putInt("EffectId", 20);
        wither.putInt("EffectDuration", 80);

        EFFECTS = new CompoundTag[]{regeneration, weakness, poison, healthBoost, blindness, resistance, nausea,
                hunger, saturation, fireResistance, strength, speed, slowness, miningFatique, haste, wither};
    }

    public SuspiciousStew(List<LootItemCondition> conditions) {
        super(conditions);
    }

    @Override
    public ItemStack run(ItemStack stack, LootContext context) {
        stack.getOrCreateTag().put("Effects", createEffectList(context.getRandom()));
        return stack;
    }

    public static LootItemConditionalFunction.Builder<?> suspiciousStew() {
        return simpleBuilder(SuspiciousStew::new);
    }

    @Override
    public LootItemFunctionType getType() {
        return Loot.SUSPICIOUS_STEW.get();
    }

    public static ListTag createEffectList(RandomSource rand) {
        int counter = rand.nextInt(2), max = EFFECTS.length;
        ListTag effects = new ListTag();

        while (counter < max) {
            effects.add(EFFECTS[counter].copy());
            counter += 1 + rand.nextInt(3);
        }

        return effects;
    }

}
