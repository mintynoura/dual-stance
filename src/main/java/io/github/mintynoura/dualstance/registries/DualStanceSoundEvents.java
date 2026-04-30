package io.github.mintynoura.dualstance.registries;

import io.github.mintynoura.dualstance.DualStance;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;

public class DualStanceSoundEvents {
	public static final SoundEvent PAIR_LINK = register("pair.link");
	public static final SoundEvent PAIR_UNLINK = register("pair.unlink");
	public static final SoundEvent PAIR_OFFER = register("pair.offer");
	public static final SoundEvent HEART_SEAL_INSERT = register("item.heart_seal.insert");
	public static final SoundEvent HEART_SEAL_INSERT_FAIL = register("item.heart_seal.insert_fail");
	public static final SoundEvent HEART_SEAL_REMOVE = register("item.heart_seal.remove");
	public static final SoundEvent DAMAGE_BOOST = register("entity.damage_boost");
	public static final SoundEvent DAMAGE_NEGATION = register("entity.damage_negation");

	public static SoundEvent register(String name) {
		return Registry.register(BuiltInRegistries.SOUND_EVENT, Identifier.fromNamespaceAndPath(DualStance.ID, name), SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath(DualStance.ID, name)));
	}

	public static void initialize() {}
}
