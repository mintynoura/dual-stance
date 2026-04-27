package io.github.mintynoura.dualstance;

import io.github.mintynoura.dualstance.gamerule.DualStanceGameRules;
import io.github.mintynoura.dualstance.registries.CrestEffectTypes;
import io.github.mintynoura.dualstance.registries.DualStanceComponents;
import io.github.mintynoura.dualstance.registries.DualStanceItems;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ExtraCodecs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DualStance implements ModInitializer {
	public static final String ID = "dual_stance";
	public static final Logger LOGGER = LoggerFactory.getLogger(ID);

	public static final AttachmentType<Integer> PAIR_OFFER_TIMER = AttachmentRegistry.createPersistent(Identifier.fromNamespaceAndPath(ID, "pair_offer_timer"), ExtraCodecs.NON_NEGATIVE_INT);


	@Override
	public void onInitialize() {
		LOGGER.info("[Dual Stance] I fight for my friends.");
		CrestEffectTypes.initialize();
		DualStanceComponents.initialize();
		DualStanceItems.initialize();
		DualStanceGameRules.initialize();
	}
}
