package io.github.mintynoura.dualstance.item.component;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.mintynoura.dualstance.registries.CrestEffectTypes;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public record MobEffectCrestEffect(List<MobEffectInstance> effects, int interval) implements CrestEffect {
	public static MapCodec<MobEffectCrestEffect> CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
		MobEffectInstance.CODEC.listOf().fieldOf("effects").forGetter(MobEffectCrestEffect::effects),
		ExtraCodecs.POSITIVE_INT.optionalFieldOf("interval", 200).forGetter(MobEffectCrestEffect::interval)
		).apply(builder, MobEffectCrestEffect::new)
	);
	public static StreamCodec<RegistryFriendlyByteBuf, MobEffectCrestEffect> STREAM_CODEC = StreamCodec.composite(
		MobEffectInstance.STREAM_CODEC.apply(ByteBufCodecs.list()),
		MobEffectCrestEffect::effects,
		ByteBufCodecs.VAR_INT,
		MobEffectCrestEffect::interval,
		MobEffectCrestEffect::new
	);
	@Override
	public void trigger(Level level, LivingEntity entity) {
		if (level instanceof ServerLevel serverLevel) {
			for (MobEffectInstance effectInstance: effects) {
				if (effectInstance.getEffect().value().isInstantenous()) {
					effectInstance.getEffect().value().applyInstantenousEffect(serverLevel, entity, entity, entity, effectInstance.getAmplifier(), 1.0);
				} else {
					entity.addEffect(new MobEffectInstance(effectInstance));
				}
			}
		}
	}

	public static void display(Iterable<MobEffectInstance> effects, Consumer<Component> consumer) {
		List<Pair<Holder<Attribute>, AttributeModifier>> list = new ArrayList<>();
		for (MobEffectInstance effectInstance : effects) {
			Holder<MobEffect> effect = effectInstance.getEffect();
			int amplifier = effectInstance.getAmplifier();
			effect.value().createModifiers(amplifier, (attribute, modifier) -> list.add(new Pair<>(attribute, modifier)));
			MutableComponent mutableText = PotionContents.getPotionDescription(effect, amplifier);
			consumer.accept(mutableText.withStyle(effect.value().getCategory().getTooltipFormatting()));
		}
		if (!list.isEmpty()) {
			for (Pair<Holder<Attribute>, AttributeModifier> pair : list) {
				AttributeModifier attributeModifier = pair.getSecond();
				double amount = attributeModifier.amount();
				double displayAmount;
				if (attributeModifier.operation() != AttributeModifier.Operation.ADD_MULTIPLIED_BASE
					&& attributeModifier.operation() != AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL) {
					displayAmount = attributeModifier.amount();
				} else {
					displayAmount = attributeModifier.amount() * 100.0;
				}
				if (amount > 0.0) {
					consumer.accept(
						Component.literal("  ").append(
						Component.translatable(
								"attribute.modifier.plus." + attributeModifier.operation().id(),
								ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format(displayAmount),
								Component.translatable(pair.getFirst().value().getDescriptionId())
							)
							.withStyle(ChatFormatting.BLUE)
						)
					);
				} else if (amount < 0.0) {
					displayAmount *= -1.0;
					consumer.accept(
						Component.literal("\t").append(
							Component.translatable(
								"attribute.modifier.take." + attributeModifier.operation().id(),
								ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format(displayAmount),
								Component.translatable(pair.getFirst().value().getDescriptionId())
							)
							.withStyle(ChatFormatting.RED)
						)
					);
				}
			}
		}
	}

	@Override
	public Type<? extends CrestEffect> getType() {
		return CrestEffectTypes.MOB_EFFECT_CREST_EFFECT_TYPE;
	}
}
