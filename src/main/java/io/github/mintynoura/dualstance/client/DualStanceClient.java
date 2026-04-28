package io.github.mintynoura.dualstance.client;
import io.github.mintynoura.dualstance.client.particles.SigilParticle;
import io.github.mintynoura.dualstance.item.component.HeartSealTooltip;
import io.github.mintynoura.dualstance.registries.DualStanceParticles;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ClientTooltipComponentCallback;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.minecraft.client.particle.EndRodParticle;


public class DualStanceClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ClientTooltipComponentCallback.EVENT.register(component ->{
			if (component instanceof HeartSealTooltip heartSealTooltip) {
				return new ClientHeartSealTooltip(heartSealTooltip.crest());
			} else return null;
		});

		// Pacifism particles using the End Rod Particle physics/provider
		ParticleProviderRegistry.getInstance().register(DualStanceParticles.PACIFISM_PARTICLE, SigilParticle.Provider::new);
	}
}
