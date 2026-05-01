package io.github.mintynoura.dualstance.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.mintynoura.dualstance.registries.CrestEffectTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public record FoodLeechCrestEffect(int food, float saturationModifier) implements CrestEffect {
	public static final MapCodec<FoodLeechCrestEffect> CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
		Codec.INT.fieldOf("food").forGetter(FoodLeechCrestEffect::food),
		Codec.FLOAT.fieldOf("saturation_modifier").forGetter(FoodLeechCrestEffect::saturationModifier)
	).apply(builder, FoodLeechCrestEffect::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, FoodLeechCrestEffect> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.INT,
		FoodLeechCrestEffect::food,
		ByteBufCodecs.FLOAT,
		FoodLeechCrestEffect::saturationModifier,
		FoodLeechCrestEffect::new
	);
	@Override
	public void trigger(Level level, LivingEntity entity) {

	}

	@Override
	public Type<? extends CrestEffect> getType() {
		return CrestEffectTypes.FOOD_LEECH_CREST_EFFECT_TYPE;
	}
}
