package io.github.mintynoura.dualstance;

import io.github.mintynoura.dualstance.client.ClientHeartSealTooltip;
import io.github.mintynoura.dualstance.item.component.HeartSealTooltip;
import io.github.mintynoura.dualstance.registries.DualStanceComponents;
import io.github.mintynoura.dualstance.registries.DualStanceItems;
import io.github.mintynoura.dualstance.registries.ModRenderers;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.ClientTooltipComponentCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DualStance implements ModInitializer, ClientModInitializer {
	public static final String ID = "dual_stance";
	public static final Logger LOGGER = LoggerFactory.getLogger(ID);

	@Override
	public void onInitialize() {
		LOGGER.info("[Dual Stance] I fight for my friends.");
		DualStanceItems.initialize();
		DualStanceComponents.initialize();
	}

	@Override
	public void onInitializeClient() {
		ModRenderers.register();
		ClientTooltipComponentCallback.EVENT.register(component ->{
			if (component instanceof HeartSealTooltip heartSealTooltip) {
				return new ClientHeartSealTooltip(heartSealTooltip.contents());
			} else return null;
		});
	}
}
