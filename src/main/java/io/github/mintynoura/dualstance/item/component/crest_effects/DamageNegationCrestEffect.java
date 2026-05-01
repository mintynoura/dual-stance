package io.github.mintynoura.dualstance.item.component.crest_effects;

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

public record DamageNegationCrestEffect(float baseChance, boolean doProximityBoost) implements CrestEffect {
	public static final MapCodec<DamageNegationCrestEffect> CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
		ExtraCodecs.floatRange(0f, 1f).optionalFieldOf("base_chance", 0.5f).forGetter(DamageNegationCrestEffect::baseChance),
		Codec.BOOL.fieldOf("do_proximity_boost").forGetter(DamageNegationCrestEffect::doProximityBoost)
	).apply(builder, DamageNegationCrestEffect::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, DamageNegationCrestEffect> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.FLOAT,
		DamageNegationCrestEffect::baseChance,
		ByteBufCodecs.BOOL,
		DamageNegationCrestEffect::doProximityBoost,
		DamageNegationCrestEffect::new
	);
	@Override
	public void trigger(Level level, LivingEntity entity) {

	}

	@Override
	public Type<? extends CrestEffect> getType() {
		return CrestEffectTypes.DAMAGE_NEGATION_CREST_EFFECT_TYPE;
	}
}
