package io.github.mintynoura.dualstance.registries;

import io.github.mintynoura.dualstance.DualStance;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

public class DualStanceParticles {
	public static final SimpleParticleType PACIFISM_PARTICLE = FabricParticleTypes.simple();

	public static void initialize() {

		// Register our custom particle type in the mod initializer.
		Registry.register(BuiltInRegistries.PARTICLE_TYPE, Identifier.fromNamespaceAndPath(DualStance.ID, "pacifism_particle"), PACIFISM_PARTICLE);
	}
}
