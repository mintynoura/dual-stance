package io.github.mintynoura.dualstance.client;

import io.github.mintynoura.dualstance.item.component.HeartSealTooltip;
import io.github.mintynoura.dualstance.registries.ModRenderers;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.ClientTooltipComponentCallback;

public class DualStanceClient implements ClientModInitializer {
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
