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

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.SuspiciousStewEffects;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import xiroc.dungeoncrawl.dungeon.treasure.Loot;

import java.util.ArrayList;
import java.util.List;

public class SuspiciousStew extends LootItemConditionalFunction {
    public static final MapCodec<SuspiciousStew> CODEC = RecordCodecBuilder.mapCodec((builder) -> commonFields(builder).apply(builder, SuspiciousStew::new));

    private static final SuspiciousStewEffects.Entry[] EFFECTS;

    static {
        SuspiciousStewEffects.Entry regeneration = new SuspiciousStewEffects.Entry(MobEffects.REGENERATION, 160);
        SuspiciousStewEffects.Entry weakness = new SuspiciousStewEffects.Entry(MobEffects.WEAKNESS, 320);
        SuspiciousStewEffects.Entry poison = new SuspiciousStewEffects.Entry(MobEffects.POISON, 160);
        SuspiciousStewEffects.Entry healthBoost = new SuspiciousStewEffects.Entry(MobEffects.HEALTH_BOOST, 320);
        SuspiciousStewEffects.Entry blindness = new SuspiciousStewEffects.Entry(MobEffects.BLINDNESS, 120);
        SuspiciousStewEffects.Entry resistance = new SuspiciousStewEffects.Entry(MobEffects.DAMAGE_RESISTANCE, 320);
        SuspiciousStewEffects.Entry nausea = new SuspiciousStewEffects.Entry(MobEffects.CONFUSION, 120);
        SuspiciousStewEffects.Entry absorption = new SuspiciousStewEffects.Entry(MobEffects.ABSORPTION, 320);
        SuspiciousStewEffects.Entry hunger = new SuspiciousStewEffects.Entry(MobEffects.HUNGER, 200);
        SuspiciousStewEffects.Entry saturation = new SuspiciousStewEffects.Entry(MobEffects.SATURATION, 80);
        SuspiciousStewEffects.Entry fireResistance = new SuspiciousStewEffects.Entry(MobEffects.FIRE_RESISTANCE, 320);
        SuspiciousStewEffects.Entry strength = new SuspiciousStewEffects.Entry(MobEffects.DAMAGE_BOOST, 320);
        SuspiciousStewEffects.Entry speed = new SuspiciousStewEffects.Entry(MobEffects.MOVEMENT_SPEED, 220);
        SuspiciousStewEffects.Entry slowness = new SuspiciousStewEffects.Entry(MobEffects.MOVEMENT_SLOWDOWN, 160);
        SuspiciousStewEffects.Entry miningFatigue = new SuspiciousStewEffects.Entry(MobEffects.DIG_SLOWDOWN, 160);
        SuspiciousStewEffects.Entry haste = new SuspiciousStewEffects.Entry(MobEffects.DIG_SPEED, 320);
        SuspiciousStewEffects.Entry jumpBoost = new SuspiciousStewEffects.Entry(MobEffects.JUMP, 160);
        SuspiciousStewEffects.Entry wither = new SuspiciousStewEffects.Entry(MobEffects.WITHER, 80);

        EFFECTS = new SuspiciousStewEffects.Entry[]{regeneration, weakness, poison, healthBoost, blindness, resistance, nausea,
                absorption, hunger, saturation, fireResistance, strength, speed, slowness, miningFatigue, haste, jumpBoost, wither};
    }

    public SuspiciousStew(List<LootItemCondition> conditions) {
        super(conditions);
    }

    @Override
    public ItemStack run(ItemStack stack, LootContext context) {
        stack.set(DataComponents.SUSPICIOUS_STEW_EFFECTS, createEffectList(context.getRandom()));
        return stack;
    }

    public static LootItemConditionalFunction.Builder<?> suspiciousStew() {
        return simpleBuilder(SuspiciousStew::new);
    }

    @Override
    public LootItemFunctionType getType() {
        return Loot.SUSPICIOUS_STEW.get();
    }

    public static SuspiciousStewEffects createEffectList(RandomSource rand) {
        int counter = rand.nextInt(2);
        int max = EFFECTS.length;
        List<SuspiciousStewEffects.Entry> effects = new ArrayList<>();

        while (counter < max) {
            effects.add(EFFECTS[counter]);
            counter += 1 + rand.nextInt(3);
        }

        return new SuspiciousStewEffects(effects);
    }

}
