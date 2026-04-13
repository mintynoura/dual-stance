package io.github.mintynoura.dualstance;

import io.github.mintynoura.dualstance.registries.DualStanceComponents;
import io.github.mintynoura.dualstance.registries.DualStanceItems;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DualStance implements ModInitializer {
	public static final String ID = "dual_stance";
	public static final Logger LOGGER = LoggerFactory.getLogger(ID);

	@Override
	public void onInitialize() {
		LOGGER.info("[Dual Stance] I fight for my friends.");
		DualStanceItems.initialize();
		DualStanceComponents.initialize();
	}
}
