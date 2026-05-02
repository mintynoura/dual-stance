package io.github.mintynoura.dualstance.item.component.crest_effects;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.mintynoura.dualstance.registries.CrestEffectTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.component.KineticWeapon;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public record KineticDamageBoostCrestEffect(float multiplier, float max, float minSpeed) implements CrestEffect {
	public static final MapCodec<KineticDamageBoostCrestEffect> CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
		ExtraCodecs.NON_NEGATIVE_FLOAT.optionalFieldOf("multiplier", 0.5f).forGetter(KineticDamageBoostCrestEffect::multiplier),
		ExtraCodecs.NON_NEGATIVE_FLOAT.optionalFieldOf("max", 8.0f).forGetter(KineticDamageBoostCrestEffect::max),
		Codec.FLOAT.optionalFieldOf("min_speed", 3.0f).forGetter(KineticDamageBoostCrestEffect::minSpeed)
	).apply(builder, KineticDamageBoostCrestEffect::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, KineticDamageBoostCrestEffect> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.FLOAT,
		KineticDamageBoostCrestEffect::multiplier,
		ByteBufCodecs.FLOAT,
		KineticDamageBoostCrestEffect::max,
		ByteBufCodecs.FLOAT,
		KineticDamageBoostCrestEffect::minSpeed,
		KineticDamageBoostCrestEffect::new
	);

	public static float damageBoost(LivingEntity attacker, LivingEntity target, float multiplier, float max, float minSpeed) {
		Vec3 attackerLookVector = attacker.getLookAngle();
		double attackerSpeedProjection = attackerLookVector.dot(KineticWeapon.getMotion(attacker));
		double targetSpeedProjection = attackerLookVector.dot(KineticWeapon.getMotion(target));
		double relativeSpeed = Math.max(1.0, attackerSpeedProjection - targetSpeedProjection - minSpeed);
		return Math.min(Mth.floor(Math.log(relativeSpeed) * multiplier), max);
	}

	@Override
	public void trigger(Level level, LivingEntity entity) {

	}

	@Override
	public Type<? extends CrestEffect> getType() {
		return CrestEffectTypes.KINETIC_DAMAGE_BOOST_CREST_EFFECT_TYPE;
	}
}
