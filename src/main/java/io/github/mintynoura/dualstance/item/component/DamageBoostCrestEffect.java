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

public record DamageBoostCrestEffect(float baseChance, boolean doProximityBoost, Modifier modifier) implements CrestEffect {
	public static MapCodec<DamageBoostCrestEffect> CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
		ExtraCodecs.floatRange(0f, 1f).optionalFieldOf("base_chance", 0.5f).forGetter(DamageBoostCrestEffect::baseChance),
		Codec.BOOL.fieldOf("do_proximity_boost").forGetter(DamageBoostCrestEffect::doProximityBoost),
		Modifier.CODEC.fieldOf("modifier").forGetter(DamageBoostCrestEffect::modifier)
	).apply(builder, DamageBoostCrestEffect::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, DamageBoostCrestEffect> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.FLOAT,
		DamageBoostCrestEffect::baseChance,
		ByteBufCodecs.BOOL,
		DamageBoostCrestEffect::doProximityBoost,
		Modifier.STREAM_CODEC,
		DamageBoostCrestEffect::modifier,
		DamageBoostCrestEffect::new
	);

	@Override
	public void trigger(Level level, LivingEntity entity) {

	}

	@Override
	public Type<? extends CrestEffect> getType() {
		return CrestEffectTypes.DAMAGE_BOOST_CREST_EFFECT_TYPE;
	}

	public record Modifier(float amount, boolean multiply) {
		public static final Codec<Modifier> CODEC = RecordCodecBuilder.create(builder -> builder.group(
			Codec.FLOAT.fieldOf("amount").forGetter(Modifier::amount),
			Codec.BOOL.fieldOf("multiply").forGetter(Modifier::multiply)
		).apply(builder, Modifier::new));
		public static final StreamCodec<RegistryFriendlyByteBuf, Modifier> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.FLOAT,
			Modifier::amount,
			ByteBufCodecs.BOOL,
			Modifier::multiply,
			Modifier::new
		);
	}
}
