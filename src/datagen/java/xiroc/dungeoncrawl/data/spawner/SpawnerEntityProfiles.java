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

        context.register(SpawnerEntityProfileKeys.MELEE_UNIT_LEVEL_0, new SpawnerEntityProperties.Builder()
                .helmet(IRandom.<Item>builder()
                        .setPools(itemPools.getOrThrow(ItemPoolTags.HELMET_LEVEL_0)))
                .chestplate(IRandom.<Item>builder()
                        .setPools(itemPools.getOrThrow(ItemPoolTags.CHESTPLATE_LEVEL_0)))
                .leggings(IRandom.<Item>builder()
                        .setPools(itemPools.getOrThrow(ItemPoolTags.LEGGINGS_LEVEL_0)))
                .boots(IRandom.<Item>builder()
                        .setPools(itemPools.getOrThrow(ItemPoolTags.BOOTS_LEVEL_0)))
                .mainHand(IRandom.<Item>builder()
                        .setPools(itemPools.getOrThrow(ItemPoolTags.MELEE_WEAPON_LEVEL_0)))
                .offHand(IRandom.<Item>builder()
                        .setPools(itemPools.getOrThrow(ItemPoolTags.OFF_HAND_LEVEL_0)))
                .equipmentChances(new SpawnerEntityProperties.EquipmentChances(0.5F, 0.25F, 0.5F))
                .build());

        context.register(SpawnerEntityProfileKeys.MELEE_UNIT_LEVEL_1, new SpawnerEntityProperties.Builder()
                .helmet(IRandom.<Item>builder()
                        .setPools(itemPools.getOrThrow(ItemPoolTags.HELMET_LEVEL_1)))
                .chestplate(IRandom.<Item>builder()
                        .setPools(itemPools.getOrThrow(ItemPoolTags.CHESTPLATE_LEVEL_1)))
                .leggings(IRandom.<Item>builder()
                        .setPools(itemPools.getOrThrow(ItemPoolTags.LEGGINGS_LEVEL_1)))
                .boots(IRandom.<Item>builder()
                        .setPools(itemPools.getOrThrow(ItemPoolTags.BOOTS_LEVEL_1)))
                .mainHand(IRandom.<Item>builder()
                        .setPools(itemPools.getOrThrow(ItemPoolTags.MELEE_WEAPON_LEVEL_1)))
                .offHand(IRandom.<Item>builder()
                        .setPools(itemPools.getOrThrow(ItemPoolTags.OFF_HAND_LEVEL_1)))
                .equipmentChances(new SpawnerEntityProperties.EquipmentChances(0.6F, 0.25F, 0.6F))
                .build());

        context.register(SpawnerEntityProfileKeys.MELEE_UNIT_LEVEL_2, new SpawnerEntityProperties.Builder()
                .helmet(IRandom.<Item>builder()
                        .setPools(itemPools.getOrThrow(ItemPoolTags.HELMET_LEVEL_2)))
                .chestplate(IRandom.<Item>builder()
                        .setPools(itemPools.getOrThrow(ItemPoolTags.CHESTPLATE_LEVEL_2)))
                .leggings(IRandom.<Item>builder()
                        .setPools(itemPools.getOrThrow(ItemPoolTags.LEGGINGS_LEVEL_2)))
                .boots(IRandom.<Item>builder()
                        .setPools(itemPools.getOrThrow(ItemPoolTags.BOOTS_LEVEL_2)))
                .mainHand(IRandom.<Item>builder()
                        .setPools(itemPools.getOrThrow(ItemPoolTags.MELEE_WEAPON_LEVEL_2)))
                .offHand(IRandom.<Item>builder()
                        .setPools(itemPools.getOrThrow(ItemPoolTags.OFF_HAND_LEVEL_2)))
                .equipmentChances(new SpawnerEntityProperties.EquipmentChances(0.6F, 0.5F, 0.7F))
                .build());

        context.register(SpawnerEntityProfileKeys.MELEE_UNIT_LEVEL_3, new SpawnerEntityProperties.Builder()
                .helmet(IRandom.<Item>builder()
                        .setPools(itemPools.getOrThrow(ItemPoolTags.HELMET_LEVEL_3)))
                .chestplate(IRandom.<Item>builder()
                        .setPools(itemPools.getOrThrow(ItemPoolTags.CHESTPLATE_LEVEL_3)))
                .leggings(IRandom.<Item>builder()
                        .setPools(itemPools.getOrThrow(ItemPoolTags.LEGGINGS_LEVEL_3)))
                .boots(IRandom.<Item>builder()
                        .setPools(itemPools.getOrThrow(ItemPoolTags.BOOTS_LEVEL_3)))
                .mainHand(IRandom.<Item>builder()
                        .setPools(itemPools.getOrThrow(ItemPoolTags.MELEE_WEAPON_LEVEL_3)))
                .offHand(IRandom.<Item>builder()
                        .setPools(itemPools.getOrThrow(ItemPoolTags.OFF_HAND_LEVEL_3)))
                .equipmentChances(new SpawnerEntityProperties.EquipmentChances(0.6F, 0.5F, 0.75F))
                .build());

        context.register(SpawnerEntityProfileKeys.MELEE_UNIT_LEVEL_4, new SpawnerEntityProperties.Builder()
                .helmet(IRandom.<Item>builder()
                        .setPools(itemPools.getOrThrow(ItemPoolTags.HELMET_LEVEL_4)))
                .chestplate(IRandom.<Item>builder()
                        .setPools(itemPools.getOrThrow(ItemPoolTags.CHESTPLATE_LEVEL_4)))
                .leggings(IRandom.<Item>builder()
                        .setPools(itemPools.getOrThrow(ItemPoolTags.LEGGINGS_LEVEL_4)))
                .boots(IRandom.<Item>builder()
                        .setPools(itemPools.getOrThrow(ItemPoolTags.BOOTS_LEVEL_4)))
                .mainHand(IRandom.<Item>builder()
                        .setPools(itemPools.getOrThrow(ItemPoolTags.MELEE_WEAPON_LEVEL_4)))
                .offHand(IRandom.<Item>builder()
                        .setPools(itemPools.getOrThrow(ItemPoolTags.OFF_HAND_LEVEL_4)))
                .equipmentChances(new SpawnerEntityProperties.EquipmentChances(0.6F, 0.5F, 0.8F))
                .build());
    }
}
