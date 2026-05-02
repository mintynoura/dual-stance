package io.github.mintynoura.dualstance.item.component.crest_effects;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.mintynoura.dualstance.registries.CrestEffectTypes;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.CommonComponents;
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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public record MobEffectCrestEffect(List<MobEffectInstance> effects, int interval, boolean requireMaxHealth, boolean requireMaxSaturation) implements CrestEffect {
	public static MapCodec<MobEffectCrestEffect> CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
		MobEffectInstance.CODEC.listOf().fieldOf("effects").forGetter(MobEffectCrestEffect::effects),
		ExtraCodecs.POSITIVE_INT.optionalFieldOf("interval", 200).forGetter(MobEffectCrestEffect::interval),
		Codec.BOOL.optionalFieldOf("require_max_health", false).forGetter(MobEffectCrestEffect::requireMaxHealth),
		Codec.BOOL.optionalFieldOf("require_max_saturation", false).forGetter(MobEffectCrestEffect::requireMaxSaturation)
		).apply(builder, MobEffectCrestEffect::new)
	);
	public static StreamCodec<RegistryFriendlyByteBuf, MobEffectCrestEffect> STREAM_CODEC = StreamCodec.composite(
		MobEffectInstance.STREAM_CODEC.apply(ByteBufCodecs.list()),
		MobEffectCrestEffect::effects,
		ByteBufCodecs.VAR_INT,
		MobEffectCrestEffect::interval,
		ByteBufCodecs.BOOL,
		MobEffectCrestEffect::requireMaxHealth,
		ByteBufCodecs.BOOL,
		MobEffectCrestEffect::requireMaxSaturation,
		MobEffectCrestEffect::new
	);

	public MobEffectCrestEffect(List<MobEffectInstance> effects, int interval) {
		this(effects, interval, false, false);
	}

	@Override
	public void trigger(Level level, LivingEntity entity) {
		if (level instanceof ServerLevel serverLevel) {
			if (requireMaxHealth && entity.getHealth() != entity.getMaxHealth()) {
				return;
			}
			if (entity instanceof Player player) {
				if (requireMaxSaturation && player.getFoodData().getSaturationLevel() != 20) {
					return;
				}
			}
			for (MobEffectInstance effectInstance: effects) {
				if (effectInstance.getEffect().value().isInstantenous()) {
					effectInstance.getEffect().value().applyInstantenousEffect(serverLevel, entity, entity, entity, effectInstance.getAmplifier(), 1.0);
				} else {
					entity.addEffect(new MobEffectInstance(effectInstance));
				}
			}
		}
	}

	public static void display(Iterable<MobEffectInstance> effects, Consumer<Component> consumer, int interval) {
		List<Pair<Holder<Attribute>, AttributeModifier>> list = new ArrayList<>();
		for (MobEffectInstance effectInstance : effects) {
			Holder<MobEffect> effect = effectInstance.getEffect();
			int amplifier = effectInstance.getAmplifier();
			effect.value().createModifiers(amplifier, (attribute, modifier) -> list.add(new Pair<>(attribute, modifier)));
			MutableComponent mutableText = PotionContents.getPotionDescription(effect, amplifier).append(CommonComponents.space())
				.append(Component.translatableWithFallback("tooltip.dual_stance.mob_effect_interval", "every %s seconds", Math.round((float) interval / 20)));
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
						Component.literal("  ").append(
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
