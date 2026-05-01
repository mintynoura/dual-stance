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

public record PairUpRangeModifierCrestEffect(float amount, boolean multiply) implements CrestEffect {
	public static final MapCodec<PairUpRangeModifierCrestEffect> CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
		ExtraCodecs.POSITIVE_FLOAT.fieldOf("amount").forGetter(PairUpRangeModifierCrestEffect::amount),
		Codec.BOOL.optionalFieldOf("multiply", true).forGetter(PairUpRangeModifierCrestEffect::multiply)
	).apply(builder, PairUpRangeModifierCrestEffect::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, PairUpRangeModifierCrestEffect> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.FLOAT,
		PairUpRangeModifierCrestEffect::amount,
		ByteBufCodecs.BOOL,
		PairUpRangeModifierCrestEffect::multiply,
		PairUpRangeModifierCrestEffect::new
	);
	@Override
	public void trigger(Level level, LivingEntity entity) {

	}

	@Override
	public Type<? extends CrestEffect> getType() {
		return CrestEffectTypes.PAIR_UP_RANGE_MODIFIER_CREST_EFFECT_TYPE;
	}
}
