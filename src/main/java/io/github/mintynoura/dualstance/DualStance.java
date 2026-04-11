package io.github.mintynoura.dualstance;

import io.github.mintynoura.dualstance.component.DualStanceComponents;
import io.github.mintynoura.dualstance.item.DualStanceItems;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DualStance implements ModInitializer {
	public static final String ID = "dual_stance";
	public static final Logger LOGGER = LoggerFactory.getLogger(ID);

	@Override
	public void onInitialize() {
		DualStanceItems.initialize();
		DualStanceComponents.initialize();
	}
}
