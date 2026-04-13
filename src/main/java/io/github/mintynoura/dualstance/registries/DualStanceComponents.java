package io.github.mintynoura.dualstance.registries;

import io.github.mintynoura.dualstance.DualStance;
import io.github.mintynoura.dualstance.component.CrestComponent;
import net.fabricmc.fabric.api.item.v1.ItemComponentTooltipProviderRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public class DualStanceComponents {
	// This data component holds the crest's effect on the crest item
	public static final DataComponentType<CrestComponent> CREST_COMPONENT =
		Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, Identifier.fromNamespaceAndPath(DualStance.ID, "crest"),
			DataComponentType.<CrestComponent>builder().persistent(CrestComponent.CODEC).build());

	// This data component holds the linked player on the other player's heart seal
	public static final DataComponentType<UUID> LINKED_PLAYER = Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE,
		Identifier.fromNamespaceAndPath(DualStance.ID,"linked_player"),
		DataComponentType.<UUID>builder().persistent(UUIDUtil.CODEC).build());

	// This data component holds the linked crest effect on the other player's heart seal
	public static final DataComponentType<CrestComponent> LINKED_CREST =
		Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, Identifier.fromNamespaceAndPath(DualStance.ID, "linked_crest"),
			DataComponentType.<CrestComponent>builder().persistent(CrestComponent.CODEC).build());

	public static void initialize() {
		ItemComponentTooltipProviderRegistry.addFirst(CREST_COMPONENT);
	}
}
