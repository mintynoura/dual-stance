package io.github.mintynoura.dualstance.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;

public record AttributeCrestEffect(List<Entry> modifiers) implements CrestEffect{
	public static final MapCodec<AttributeCrestEffect> CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
		Entry.CODEC.listOf().fieldOf("modifiers").forGetter(AttributeCrestEffect::modifiers)
	).apply(builder, AttributeCrestEffect::new));

	@Override
	public void trigger(Level level, LivingEntity entity, ItemStack itemStack) {
	}

	@Override
	public Type<? extends CrestEffect> getType() {
		return CrestEffectTypes.ATTRIBUTE_CREST_EFFECT_TYPE;
	}

	public record Entry(Holder<Attribute> attribute, AttributeModifier modifier) {
		public static final Codec<Entry> CODEC = RecordCodecBuilder.create(builder -> builder.group(
			Attribute.CODEC.fieldOf("attribute").forGetter(Entry::attribute),
			AttributeModifier.MAP_CODEC.forGetter(Entry::modifier)
		).apply(builder, Entry::new));
	}
}
