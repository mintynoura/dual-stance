package io.github.mintynoura.dualstance.registries;

import io.github.mintynoura.dualstance.DualStance;
import io.github.mintynoura.dualstance.item.component.crest_effects.*;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;

public class CrestEffectTypes {
	public static final CrestEffect.Type<AttributeCrestEffect> ATTRIBUTE_CREST_EFFECT_TYPE = register("attribute", new CrestEffect.Type<>(AttributeCrestEffect.CODEC, AttributeCrestEffect.STREAM_CODEC));
	public static final CrestEffect.Type<MobEffectCrestEffect> MOB_EFFECT_CREST_EFFECT_TYPE = register("effect", new CrestEffect.Type<>(MobEffectCrestEffect.CODEC, MobEffectCrestEffect.STREAM_CODEC));
	public static final CrestEffect.Type<DamageBoostCrestEffect> DAMAGE_BOOST_CREST_EFFECT_TYPE = register("damage_boost", new CrestEffect.Type<>(DamageBoostCrestEffect.CODEC, DamageBoostCrestEffect.STREAM_CODEC));
	public static final CrestEffect.Type<DamageNegationCrestEffect> DAMAGE_NEGATION_CREST_EFFECT_TYPE = register("damage_negation", new CrestEffect.Type<>(DamageNegationCrestEffect.CODEC, DamageNegationCrestEffect.STREAM_CODEC));
	public static final CrestEffect.Type<HealthLeechCrestEffect> HEALTH_LEECH_CREST_EFFECT_TYPE = register("health_leech", new CrestEffect.Type<>(HealthLeechCrestEffect.CODEC, HealthLeechCrestEffect.STREAM_CODEC));
	public static final CrestEffect.Type<FoodLeechCrestEffect> FOOD_LEECH_CREST_EFFECT_TYPE = register("food_leech", new CrestEffect.Type<>(FoodLeechCrestEffect.CODEC, FoodLeechCrestEffect.STREAM_CODEC));
	public static final CrestEffect.Type<PairUpRangeModifierCrestEffect> PAIR_UP_RANGE_MODIFIER_CREST_EFFECT_TYPE = register("pair_up_range_modifier", new CrestEffect.Type<>(PairUpRangeModifierCrestEffect.CODEC, PairUpRangeModifierCrestEffect.STREAM_CODEC));

	public static <T extends CrestEffect> CrestEffect.Type<T> register(String id, CrestEffect.Type<T> crestEffectType) {
		return Registry.register(CrestEffect.Type.CREST_EFFECT_TYPE_REGISTRY, Identifier.fromNamespaceAndPath(DualStance.ID, id), crestEffectType);
	}
	public static void initialize() {}

}
