package io.github.mintynoura.dualstance.client;
import io.github.mintynoura.dualstance.item.component.HeartSealTooltip;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.ClientTooltipComponentCallback;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;


public class DualStanceClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ClientTooltipComponentCallback.EVENT.register(component ->{
			if (component instanceof HeartSealTooltip heartSealTooltip) {
				return new ClientHeartSealTooltip(heartSealTooltip.crest());
			} else return null;
		});
	}
}
