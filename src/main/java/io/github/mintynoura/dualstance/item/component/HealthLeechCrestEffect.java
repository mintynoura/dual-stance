package io.github.mintynoura.dualstance.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.mintynoura.dualstance.registries.CrestEffectTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public record HealthLeechCrestEffect(float amount, boolean multiplyByDamage) implements CrestEffect {
	public static final MapCodec<HealthLeechCrestEffect> CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
		ExtraCodecs.NON_NEGATIVE_FLOAT.optionalFieldOf("float", 0.5f).forGetter(HealthLeechCrestEffect::amount),
		Codec.BOOL.optionalFieldOf("multiply_by_damage", true).forGetter(HealthLeechCrestEffect::multiplyByDamage)
	).apply(builder, HealthLeechCrestEffect::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, HealthLeechCrestEffect> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.FLOAT,
		HealthLeechCrestEffect::amount,
		ByteBufCodecs.BOOL,
		HealthLeechCrestEffect::multiplyByDamage,
		HealthLeechCrestEffect::new
	);
	@Override
	public void trigger(Level level, LivingEntity entity) {

	}

	@Override
	public Type<? extends CrestEffect> getType() {
		return CrestEffectTypes.HEALTH_LEECH_CREST_EFFECT_TYPE;
	}
}
