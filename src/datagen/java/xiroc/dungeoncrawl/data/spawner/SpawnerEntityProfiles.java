package xiroc.dungeoncrawl.data.spawner;

import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.item.Item;
import xiroc.dungeoncrawl.data.tags.ItemPoolTags;
import xiroc.dungeoncrawl.datapack.registry.DatapackRegistries;
import xiroc.dungeoncrawl.dungeon.monster.SpawnerEntityProperties;
import xiroc.dungeoncrawl.util.random.IRandom;

public interface SpawnerEntityProfiles {
    static void generate(BootstrapContext<SpawnerEntityProperties> context) {
        var itemPools = context.lookup(DatapackRegistries.ITEM_POOLS);

        context.register(SpawnerEntityProfileKeys.ARCHER_LEVEL_0, new SpawnerEntityProperties.Builder()
                .mainHand(new SpawnerEntityProperties.EquipmentWithChance(IRandom.<Item>builder()
                        .setPools(itemPools.getOrThrow(ItemPoolTags.BOW_LEVEL_0)).build(),
                        1.0f))
                .build());

        context.register(SpawnerEntityProfileKeys.ARCHER_LEVEL_1, new SpawnerEntityProperties.Builder()
                .mainHand(new SpawnerEntityProperties.EquipmentWithChance(IRandom.<Item>builder()
                        .setPools(itemPools.getOrThrow(ItemPoolTags.BOW_LEVEL_1)).build(),
                        1.0f))
                .build());

        context.register(SpawnerEntityProfileKeys.ARCHER_LEVEL_2, new SpawnerEntityProperties.Builder()
                .mainHand(new SpawnerEntityProperties.EquipmentWithChance(IRandom.<Item>builder()
                        .setPools(itemPools.getOrThrow(ItemPoolTags.BOW_LEVEL_2)).build(),
                        1.0f))
                .build());

        context.register(SpawnerEntityProfileKeys.ARCHER_LEVEL_3, new SpawnerEntityProperties.Builder()
                .mainHand(new SpawnerEntityProperties.EquipmentWithChance(IRandom.<Item>builder()
                        .setPools(itemPools.getOrThrow(ItemPoolTags.BOW_LEVEL_3)).build(),
                        1.0f))
                .build());

        context.register(SpawnerEntityProfileKeys.ARCHER_LEVEL_4, new SpawnerEntityProperties.Builder()
                .mainHand(new SpawnerEntityProperties.EquipmentWithChance(IRandom.<Item>builder()
                        .setPools(itemPools.getOrThrow(ItemPoolTags.BOW_LEVEL_4)).build(),
                        1.0f))
                .build());

        context.register(SpawnerEntityProfileKeys.ARMORED_LEVEL_0, new SpawnerEntityProperties.Builder()
                .helmet(new SpawnerEntityProperties.EquipmentWithChance(
                        IRandom.<Item>builder()
                                .setPools(itemPools.getOrThrow(ItemPoolTags.HELMET_LEVEL_0))
                                .build(), 0.5f))
                .chestplate(new SpawnerEntityProperties.EquipmentWithChance(
                        IRandom.<Item>builder()
                                .setPools(itemPools.getOrThrow(ItemPoolTags.CHESTPLATE_LEVEL_0))
                                .build(), 0.5f))
                .leggings(new SpawnerEntityProperties.EquipmentWithChance(
                        IRandom.<Item>builder()
                                .setPools(itemPools.getOrThrow(ItemPoolTags.LEGGINGS_LEVEL_0))
                                .build(), 0.5f))
                .boots(new SpawnerEntityProperties.EquipmentWithChance(
                        IRandom.<Item>builder()
                                .setPools(itemPools.getOrThrow(ItemPoolTags.BOOTS_LEVEL_0))
                                .build(), 0.5f))
                .build());

        context.register(SpawnerEntityProfileKeys.ARMORED_LEVEL_1, new SpawnerEntityProperties.Builder()
                .helmet(new SpawnerEntityProperties.EquipmentWithChance(
                        IRandom.<Item>builder()
                                .setPools(itemPools.getOrThrow(ItemPoolTags.HELMET_LEVEL_1))
                                .build(), 0.6f))
                .chestplate(new SpawnerEntityProperties.EquipmentWithChance(
                        IRandom.<Item>builder()
                                .setPools(itemPools.getOrThrow(ItemPoolTags.CHESTPLATE_LEVEL_1))
                                .build(), 0.6f))
                .leggings(new SpawnerEntityProperties.EquipmentWithChance(
                        IRandom.<Item>builder()
                                .setPools(itemPools.getOrThrow(ItemPoolTags.LEGGINGS_LEVEL_1))
                                .build(), 0.6f))
                .boots(new SpawnerEntityProperties.EquipmentWithChance(
                        IRandom.<Item>builder()
                                .setPools(itemPools.getOrThrow(ItemPoolTags.BOOTS_LEVEL_1))
                                .build(), 0.6f))
                .build());

        context.register(SpawnerEntityProfileKeys.ARMORED_LEVEL_2, new SpawnerEntityProperties.Builder()
                .helmet(new SpawnerEntityProperties.EquipmentWithChance(
                        IRandom.<Item>builder()
                                .setPools(itemPools.getOrThrow(ItemPoolTags.HELMET_LEVEL_2))
                                .build(), 0.7f))
                .chestplate(new SpawnerEntityProperties.EquipmentWithChance(
                        IRandom.<Item>builder()
                                .setPools(itemPools.getOrThrow(ItemPoolTags.CHESTPLATE_LEVEL_2))
                                .build(), 0.7f))
                .leggings(new SpawnerEntityProperties.EquipmentWithChance(
                        IRandom.<Item>builder()
                                .setPools(itemPools.getOrThrow(ItemPoolTags.LEGGINGS_LEVEL_2))
                                .build(), 0.7f))
                .boots(new SpawnerEntityProperties.EquipmentWithChance(
                        IRandom.<Item>builder()
                                .setPools(itemPools.getOrThrow(ItemPoolTags.BOOTS_LEVEL_2))
                                .build(), 0.7f))
                .build());

        context.register(SpawnerEntityProfileKeys.ARMORED_LEVEL_3, new SpawnerEntityProperties.Builder()
                .helmet(new SpawnerEntityProperties.EquipmentWithChance(
                        IRandom.<Item>builder()
                                .setPools(itemPools.getOrThrow(ItemPoolTags.HELMET_LEVEL_3))
                                .build(), 0.8f))
                .chestplate(new SpawnerEntityProperties.EquipmentWithChance(
                        IRandom.<Item>builder()
                                .setPools(itemPools.getOrThrow(ItemPoolTags.CHESTPLATE_LEVEL_3))
                                .build(), 0.8f))
                .leggings(new SpawnerEntityProperties.EquipmentWithChance(
                        IRandom.<Item>builder()
                                .setPools(itemPools.getOrThrow(ItemPoolTags.LEGGINGS_LEVEL_3))
                                .build(), 0.8f))
                .boots(new SpawnerEntityProperties.EquipmentWithChance(
                        IRandom.<Item>builder()
                                .setPools(itemPools.getOrThrow(ItemPoolTags.BOOTS_LEVEL_3))
                                .build(), 0.8f))
                .build());

        context.register(SpawnerEntityProfileKeys.ARMORED_LEVEL_4, new SpawnerEntityProperties.Builder()
                .helmet(new SpawnerEntityProperties.EquipmentWithChance(
                        IRandom.<Item>builder()
                                .setPools(itemPools.getOrThrow(ItemPoolTags.HELMET_LEVEL_4))
                                .build(), 0.9f))
                .chestplate(new SpawnerEntityProperties.EquipmentWithChance(
                        IRandom.<Item>builder()
                                .setPools(itemPools.getOrThrow(ItemPoolTags.CHESTPLATE_LEVEL_4))
                                .build(), 0.9f))
                .leggings(new SpawnerEntityProperties.EquipmentWithChance(
                        IRandom.<Item>builder()
                                .setPools(itemPools.getOrThrow(ItemPoolTags.LEGGINGS_LEVEL_4))
                                .build(), 0.9f))
                .boots(new SpawnerEntityProperties.EquipmentWithChance(
                        IRandom.<Item>builder()
                                .setPools(itemPools.getOrThrow(ItemPoolTags.BOOTS_LEVEL_4))
                                .build(), 0.9f))
                .build());

        context.register(SpawnerEntityProfileKeys.MELEE_LEVEL_0, new SpawnerEntityProperties.Builder()
                .mainHand(new SpawnerEntityProperties.EquipmentWithChance(
                        IRandom.<Item>builder()
                                .setPools(itemPools.getOrThrow(ItemPoolTags.MELEE_WEAPON_LEVEL_0))
                                .build(), 0.6f))
                .build());

        context.register(SpawnerEntityProfileKeys.MELEE_LEVEL_1, new SpawnerEntityProperties.Builder()
                .mainHand(new SpawnerEntityProperties.EquipmentWithChance(
                        IRandom.<Item>builder()
                                .setPools(itemPools.getOrThrow(ItemPoolTags.MELEE_WEAPON_LEVEL_1))
                                .build(), 0.7f))
                .build());

        context.register(SpawnerEntityProfileKeys.MELEE_LEVEL_2, new SpawnerEntityProperties.Builder()
                .mainHand(new SpawnerEntityProperties.EquipmentWithChance(
                        IRandom.<Item>builder()
                                .setPools(itemPools.getOrThrow(ItemPoolTags.MELEE_WEAPON_LEVEL_2))
                                .build(), 0.8f))
                .build());

        context.register(SpawnerEntityProfileKeys.MELEE_LEVEL_3, new SpawnerEntityProperties.Builder()
                .mainHand(new SpawnerEntityProperties.EquipmentWithChance(
                        IRandom.<Item>builder()
                                .setPools(itemPools.getOrThrow(ItemPoolTags.MELEE_WEAPON_LEVEL_3))
                                .build(), 0.9f))
                .build());

        context.register(SpawnerEntityProfileKeys.MELEE_LEVEL_4, new SpawnerEntityProperties.Builder()
                .mainHand(new SpawnerEntityProperties.EquipmentWithChance(
                        IRandom.<Item>builder()
                                .setPools(itemPools.getOrThrow(ItemPoolTags.MELEE_WEAPON_LEVEL_4))
                                .build(), 1.0f))
                .build());

        context.register(SpawnerEntityProfileKeys.SHIELD_BEARER_LEVEL_0, new SpawnerEntityProperties.Builder()
                .offHand(new SpawnerEntityProperties.EquipmentWithChance(
                        IRandom.<Item>builder()
                                .setPools(itemPools.getOrThrow(ItemPoolTags.SHIELD_LEVEL_0))
                                .build(), 0.5f))
                .build());

        context.register(SpawnerEntityProfileKeys.SHIELD_BEARER_LEVEL_1, new SpawnerEntityProperties.Builder()
                .offHand(new SpawnerEntityProperties.EquipmentWithChance(
                        IRandom.<Item>builder()
                                .setPools(itemPools.getOrThrow(ItemPoolTags.SHIELD_LEVEL_1))
                                .build(), 0.55f))
                .build());

        context.register(SpawnerEntityProfileKeys.SHIELD_BEARER_LEVEL_2, new SpawnerEntityProperties.Builder()
                .offHand(new SpawnerEntityProperties.EquipmentWithChance(
                        IRandom.<Item>builder()
                                .setPools(itemPools.getOrThrow(ItemPoolTags.SHIELD_LEVEL_2))
                                .build(), 0.6f))
                .build());

        context.register(SpawnerEntityProfileKeys.SHIELD_BEARER_LEVEL_3, new SpawnerEntityProperties.Builder()
                .offHand(new SpawnerEntityProperties.EquipmentWithChance(
                        IRandom.<Item>builder()
                                .setPools(itemPools.getOrThrow(ItemPoolTags.SHIELD_LEVEL_3))
                                .build(), 0.65f))
                .build());

        context.register(SpawnerEntityProfileKeys.SHIELD_BEARER_LEVEL_4, new SpawnerEntityProperties.Builder()
                .offHand(new SpawnerEntityProperties.EquipmentWithChance(
                        IRandom.<Item>builder()
                                .setPools(itemPools.getOrThrow(ItemPoolTags.SHIELD_LEVEL_4))
                                .build(), 0.7f))
                .build());
    }
}
