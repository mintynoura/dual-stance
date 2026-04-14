package io.github.mintynoura.dualstance.registries;

import io.github.mintynoura.dualstance.DualStance;
import io.github.mintynoura.dualstance.item.component.AttributeCrestEffect;
import io.github.mintynoura.dualstance.item.component.CrestEffect;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;

public class CrestEffectTypes {
	public static final CrestEffect.Type<AttributeCrestEffect> ATTRIBUTE_CREST_EFFECT_TYPE = register("attribute", new CrestEffect.Type<>(AttributeCrestEffect.CODEC));

	public static <T extends CrestEffect> CrestEffect.Type<T> register(String id, CrestEffect.Type<T> crestEffectType) {
		return Registry.register(CrestEffect.Type.REGISTRY, Identifier.fromNamespaceAndPath(DualStance.ID, id), crestEffectType);
	}
}
