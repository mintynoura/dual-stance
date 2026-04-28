package io.github.mintynoura.dualstance.registries;

import io.github.mintynoura.dualstance.DualStance;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRuleCategory;

public class DualStanceGameRules {
	public static final GameRule<Boolean> ALLOW_PAIRING_WITH_MOBS = GameRuleBuilder
		.forBoolean(false)
		.category(GameRuleCategory.MOBS)
		.buildAndRegister(Identifier.fromNamespaceAndPath(DualStance.ID, "allow_pairing_with_mobs"));

	public static void initialize() {}
}
