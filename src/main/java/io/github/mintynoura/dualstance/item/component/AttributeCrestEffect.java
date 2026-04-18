package io.github.mintynoura.dualstance.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.mintynoura.dualstance.registries.CrestEffectTypes;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.function.Consumer;

public record AttributeCrestEffect(List<Entry> modifiers) implements CrestEffect {
	public static final MapCodec<AttributeCrestEffect> CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
		Entry.CODEC.listOf().fieldOf("modifiers").forGetter(AttributeCrestEffect::modifiers)
	).apply(builder, AttributeCrestEffect::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, AttributeCrestEffect> STREAM_CODEC = StreamCodec.composite(
		Entry.STREAM_CODEC.apply(ByteBufCodecs.list()),
		AttributeCrestEffect::modifiers,
		AttributeCrestEffect::new
	);

	public static void display(Consumer<Component> consumer, final Holder<Attribute> attribute, final AttributeModifier modifier) {
		double amount = modifier.amount();
		double displayAmount;
		if (modifier.operation() == AttributeModifier.Operation.ADD_MULTIPLIED_BASE || modifier.operation() == AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL) {
			displayAmount = amount * 100.0;
		} else if (attribute.is(Attributes.KNOCKBACK_RESISTANCE.unwrapKey().get())) {
			displayAmount = amount * 10.0;
		} else {
			displayAmount = amount;
		}
		if (amount > 0.0) {
			consumer.accept(
				Component.translatable(
						"attribute.modifier.plus." + modifier.operation().id(),
						ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format(displayAmount),
						Component.translatable(attribute.value().getDescriptionId())
					)
					.withStyle(attribute.value().getStyle(true))
			);
		} else if (amount < 0.0) {
			consumer.accept(
				Component.translatable(
						"attribute.modifier.take." + modifier.operation().id(),
						ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format(-displayAmount),
						Component.translatable(attribute.value().getDescriptionId())
					)
					.withStyle(attribute.value().getStyle(false))
			);
		}
	}

	@Override
	public void trigger(Level level, LivingEntity entity, ItemStack itemStack) {
	}

	@Override
	public Type<? extends CrestEffect> getType() {
		return CrestEffectTypes.ATTRIBUTE_CREST_EFFECT_TYPE;
	}

	public record Entry(Holder<Attribute> attribute, AttributeModifier modifier) {
		public static final Codec<Entry> CODEC = RecordCodecBuilder.create(builder -> builder.group(
			Attribute.CODEC.fieldOf("type").forGetter(Entry::attribute),
			AttributeModifier.MAP_CODEC.forGetter(Entry::modifier)
		).apply(builder, Entry::new));
		public static final StreamCodec<RegistryFriendlyByteBuf, Entry> STREAM_CODEC = StreamCodec.composite(
			Attribute.STREAM_CODEC,
			Entry::attribute,
			AttributeModifier.STREAM_CODEC,
			Entry::modifier,
			Entry::new
		);

	}
}
