package io.github.mintynoura.dualstance.registries;

import io.github.mintynoura.dualstance.DualStance;
import io.github.mintynoura.dualstance.item.component.CrestComponent;
import io.github.mintynoura.dualstance.item.component.HeartSealContents;
import net.fabricmc.fabric.api.item.v1.ItemComponentTooltipProviderRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

import java.util.UUID;

public class DualStanceComponents {
	// This data component holds the crest's effect on the crest item
	public static final DataComponentType<CrestComponent> CREST =
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

	public static final DataComponentType<HeartSealContents> HEART_SEAL_CONTENTS = Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, Identifier.fromNamespaceAndPath(DualStance.ID, "heart_seal_contents"),
		DataComponentType.<HeartSealContents>builder().persistent(HeartSealContents.CODEC).build());

	public static void initialize() {
		ItemComponentTooltipProviderRegistry.addFirst(CREST);
		ItemComponentTooltipProviderRegistry.addFirst(CREST);
	}
}
