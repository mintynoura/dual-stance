package io.github.mintynoura.dualstance.registries;

import io.github.mintynoura.dualstance.DualStance;
import io.github.mintynoura.dualstance.item.component.CrestComponent;
import io.github.mintynoura.dualstance.item.component.HeartSealedCrest;
import io.github.mintynoura.dualstance.item.component.LinkedPlayerComponent;
import net.fabricmc.fabric.api.item.v1.ItemComponentTooltipProviderRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

public class DualStanceComponents {
	// This data component holds the crest's effect on the crest item
	public static final DataComponentType<CrestComponent> CREST =
		Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, Identifier.fromNamespaceAndPath(DualStance.ID, "crest"),
			DataComponentType.<CrestComponent>builder().persistent(CrestComponent.CODEC).networkSynchronized(CrestComponent.STREAM_CODEC).build());

	// This data component holds the linked player on the other player's heart seal
	// TODO: Make players unable to damage their linked player
	public static final DataComponentType<LinkedPlayerComponent> LINKED_PLAYER = Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE,
		Identifier.fromNamespaceAndPath(DualStance.ID,"linked_player"),
		DataComponentType.<LinkedPlayerComponent>builder().persistent(LinkedPlayerComponent.CODEC).networkSynchronized(LinkedPlayerComponent.STREAM_CODEC).build());

	// This data component holds the linked crest effect on the other player's heart seal
	public static final DataComponentType<CrestComponent> LINKED_CREST =
		Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, Identifier.fromNamespaceAndPath(DualStance.ID, "linked_crest"),
			DataComponentType.<CrestComponent>builder().persistent(CrestComponent.CODEC).networkSynchronized(CrestComponent.STREAM_CODEC).build());

	public static final DataComponentType<HeartSealedCrest> HEART_SEALED_CREST = Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, Identifier.fromNamespaceAndPath(DualStance.ID, "heart_sealed_crest"),
		DataComponentType.<HeartSealedCrest>builder().persistent(HeartSealedCrest.CODEC).networkSynchronized(HeartSealedCrest.STREAM_CODEC).build());

	public static void initialize() {
		ItemComponentTooltipProviderRegistry.addFirst(CREST); // crest of itself
		ItemComponentTooltipProviderRegistry.addAfter(CREST,HEART_SEALED_CREST); // crest inside of it
		ItemComponentTooltipProviderRegistry.addAfter(HEART_SEALED_CREST, LINKED_CREST); // crest linked
	}
}
