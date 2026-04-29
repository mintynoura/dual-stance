package io.github.mintynoura.dualstance.util;

import io.github.mintynoura.dualstance.DualStance;
import io.github.mintynoura.dualstance.item.component.AttributeCrestEffect;
import io.github.mintynoura.dualstance.item.component.CrestEffect;
import io.github.mintynoura.dualstance.item.component.MobEffectCrestEffect;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class CrestCombinations {
	public static HashMap<Set<Identifier>, List<CrestEffect>> crestCombinationMap = new HashMap<>();

	public static final List<CrestEffect> PEACE_BONES_COMBO = List.of(
		new MobEffectCrestEffect(List.of(
			new MobEffectInstance(MobEffects.HASTE, 300, 0, true, true)),
			200));
	public static final List<CrestEffect> PEACE_STONE_COMBO = List.of(
		new AttributeCrestEffect(List.of(
			new AttributeCrestEffect.Entry(Attributes.SCALE, new AttributeModifier(Identifier.fromNamespaceAndPath(DualStance.ID, "peace_stone_combo"), 0.2, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)),
			new AttributeCrestEffect.Entry(Attributes.STEP_HEIGHT, new AttributeModifier(Identifier.fromNamespaceAndPath(DualStance.ID, "peace_stone_combo"), 0.5, AttributeModifier.Operation.ADD_VALUE)))),
		new MobEffectCrestEffect(List.of(
			new MobEffectInstance(MobEffects.STRENGTH, 300, 0)),
			200)
	);
	public static final List<CrestEffect> PEACE_SHELLS_COMBO = List.of(
		new MobEffectCrestEffect(List.of(
			new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 300, 0),
			new MobEffectInstance(MobEffects.WATER_BREATHING, 300, 0)),
			200)
	);
	public static final List<CrestEffect> BONES_SPECTERS_COMBO = List.of(
		new MobEffectCrestEffect(List.of(
			new MobEffectInstance(MobEffects.JUMP_BOOST, 300, 0),
			new MobEffectInstance(MobEffects.SPEED, 300, 0),
			new MobEffectInstance(MobEffects.HASTE, 300, 0)),
			200)
	);
	public static final List<CrestEffect> EMBLEMS_SPECTERS_COMBO = List.of(
		new MobEffectCrestEffect(List.of(
			new MobEffectInstance(MobEffects.REGENERATION, 300, 0),
			new MobEffectInstance(MobEffects.SPEED, 300, 0)),
			200)
	);
	public static final List<CrestEffect> SPECTERS_SHELLS_COMBO = List.of(
		new MobEffectCrestEffect(List.of(
			new MobEffectInstance(MobEffects.SPEED, 300, 0),
			new MobEffectInstance(MobEffects.DOLPHINS_GRACE, 300, 0),
			new MobEffectInstance(MobEffects.WATER_BREATHING, 300, 0)),
			200)
	);
	public static final List<CrestEffect> PACIFISM_BONES_OR_SPECTERS_COMBO = List.of(
		new MobEffectCrestEffect(List.of(
			new MobEffectInstance(MobEffects.JUMP_BOOST, 300, 0),
			new MobEffectInstance(MobEffects.SPEED, 300, 0),
			new MobEffectInstance(MobEffects.HASTE, 300, 0)),
			200)
	);
	public static final List<CrestEffect> PACIFISM_STONE_COMBO = List.of(
		new MobEffectCrestEffect(List.of(
			new MobEffectInstance(MobEffects.STRENGTH, 300, 0),
			new MobEffectInstance(MobEffects.RESISTANCE, 300, 0)),
			200)
	);
	public static final List<CrestEffect> PACIFISM_SHELLS_COMBO = List.of(
		new MobEffectCrestEffect(List.of(
			new MobEffectInstance(MobEffects.SPEED, 300, 0),
			new MobEffectInstance(MobEffects.DOLPHINS_GRACE, 300, 0),
			new MobEffectInstance(MobEffects.WATER_BREATHING, 300, 0)),
			200)
	);
	public static final List<CrestEffect> HATRED_BONES_OR_SPECTERS_COMBO = List.of(
		new MobEffectCrestEffect(List.of(
			new MobEffectInstance(MobEffects.JUMP_BOOST, 300, 0),
			new MobEffectInstance(MobEffects.SPEED, 300, 0),
			new MobEffectInstance(MobEffects.HASTE, 300, 0)),
			200)
	);
	public static final List<CrestEffect> HATRED_STONE_COMBO = List.of(
		new MobEffectCrestEffect(List.of(
			new MobEffectInstance(MobEffects.STRENGTH, 300, 0),
			new MobEffectInstance(MobEffects.RESISTANCE, 300, 0)),
			200)
	);
	public static final List<CrestEffect> HATRED_SHELLS_COMBO = List.of(
		new MobEffectCrestEffect(List.of(
			new MobEffectInstance(MobEffects.SPEED, 300, 0),
			new MobEffectInstance(MobEffects.DOLPHINS_GRACE, 300, 0),
			new MobEffectInstance(MobEffects.WATER_BREATHING, 300, 0)),
			200)
	);


	public static List<CrestEffect> evaluateCombo(Set<Identifier> crestIds) {
		if (crestCombinationMap.containsKey(crestIds)) {
			return crestCombinationMap.get(crestIds);
		}
		return List.of();
	}

	public static void initializeCombos() {
		crestCombinationMap.put(Set.of(CrestIdentifiers.PEACE_CREST, CrestIdentifiers.BONES_CREST), PEACE_BONES_COMBO);
		crestCombinationMap.put(Set.of(CrestIdentifiers.PEACE_CREST, CrestIdentifiers.STONE_CREST), PEACE_STONE_COMBO);
		crestCombinationMap.put(Set.of(CrestIdentifiers.PEACE_CREST, CrestIdentifiers.SHELLS_CREST), PEACE_SHELLS_COMBO);
		crestCombinationMap.put(Set.of(CrestIdentifiers.BONES_CREST, CrestIdentifiers.SPECTERS_CREST), BONES_SPECTERS_COMBO);
		crestCombinationMap.put(Set.of(CrestIdentifiers.EMBLEMS_CREST, CrestIdentifiers.SPECTERS_CREST), EMBLEMS_SPECTERS_COMBO);
		crestCombinationMap.put(Set.of(CrestIdentifiers.SPECTERS_CREST, CrestIdentifiers.SHELLS_CREST), SPECTERS_SHELLS_COMBO);
		crestCombinationMap.put(Set.of(CrestIdentifiers.PACIFISM_CREST, CrestIdentifiers.BONES_CREST), PACIFISM_BONES_OR_SPECTERS_COMBO);
		crestCombinationMap.put(Set.of(CrestIdentifiers.PACIFISM_CREST, CrestIdentifiers.SPECTERS_CREST), PACIFISM_BONES_OR_SPECTERS_COMBO);
		crestCombinationMap.put(Set.of(CrestIdentifiers.PACIFISM_CREST, CrestIdentifiers.STONE_CREST), PACIFISM_STONE_COMBO);
		crestCombinationMap.put(Set.of(CrestIdentifiers.PACIFISM_CREST, CrestIdentifiers.SHELLS_CREST), PACIFISM_SHELLS_COMBO);
		crestCombinationMap.put(Set.of(CrestIdentifiers.HATRED_CREST, CrestIdentifiers.BONES_CREST), HATRED_BONES_OR_SPECTERS_COMBO);
		crestCombinationMap.put(Set.of(CrestIdentifiers.HATRED_CREST, CrestIdentifiers.SPECTERS_CREST), HATRED_BONES_OR_SPECTERS_COMBO);
		crestCombinationMap.put(Set.of(CrestIdentifiers.HATRED_CREST, CrestIdentifiers.STONE_CREST), HATRED_STONE_COMBO);
		crestCombinationMap.put(Set.of(CrestIdentifiers.HATRED_CREST, CrestIdentifiers.SHELLS_CREST), HATRED_SHELLS_COMBO);
	}
}
