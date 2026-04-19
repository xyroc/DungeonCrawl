package xiroc.dungeoncrawl.dungeon.monster.attribute;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.UnboundedMapCodec;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;

import java.util.List;
import java.util.Map;

public record RandomizedAttributeMap(Map<Holder<Attribute>, List<RandomizedAttributeModifier>> modifiers) {
    public static final Codec<RandomizedAttributeMap> CODEC = new UnboundedMapCodec<>(
            Attribute.CODEC,
            RandomizedAttributeModifier.CODEC.listOf()
    ).xmap(RandomizedAttributeMap::new, RandomizedAttributeMap::modifiers);

    /**
     * Instantiates all attribute modifiers and applies them to the attribute map.
     * For attributes that aren't members of the target attribute map, no modifications are made.
     *
     * @param attributeMap The attribute map to apply the modifiers to.
     * @param randomSource The source of randomness.
     */
    public void applyRandomModifiers(AttributeMap attributeMap, RandomSource randomSource) {
        for (var entry : modifiers.entrySet()) {
            Holder<Attribute> attribute = entry.getKey();
            AttributeInstance instance = attributeMap.getInstance(attribute);
            if (instance == null) {
                continue;
            }
            for (RandomizedAttributeModifier modifier : entry.getValue()) {
                instance.addPermanentModifier(modifier.createRandomModifier(randomSource));
            }
        }
    }
}
