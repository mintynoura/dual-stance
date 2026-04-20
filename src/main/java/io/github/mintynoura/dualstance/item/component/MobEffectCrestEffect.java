package io.github.mintynoura.dualstance.item.component;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.mintynoura.dualstance.registries.CrestEffectTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import java.util.List;

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

	@Override
	public Type<? extends CrestEffect> getType() {
		return CrestEffectTypes.MOB_EFFECT_CREST_EFFECT_TYPE;
	}
}
