package io.github.mintynoura.dualstance.registries;

import io.github.mintynoura.dualstance.DualStance;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;

public class DualStanceTags {
	///damage with the HATRED_EXEMPT damage type tag will NOT be boosted
	///this includes:
	/// - damage taken while the Crest of Hatred is equipped
	/// - damage dealt while the Crest of Hatred is equipped
	/// - damage taken while the Crest of the Enchanter is equipped
	///this does NOT include damage dealt while the Crest of the Enchanter is equipped; it will still be halved.
	public static final ResourceKey<DamageType> HATRED_EXEMPT = ResourceKey.create(Registries.DAMAGE_TYPE, Identifier.fromNamespaceAndPath(DualStance.ID, "hatred_exempt"));

	public static void initialize() {}
}
