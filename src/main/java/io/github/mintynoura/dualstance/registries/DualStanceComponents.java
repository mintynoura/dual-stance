package io.github.mintynoura.dualstance.registries;

import io.github.mintynoura.dualstance.DualStance;
import io.github.mintynoura.dualstance.component.CrestComponent;
import net.fabricmc.fabric.api.item.v1.ItemComponentTooltipProviderRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

public class DualStanceComponents {
	public static final DataComponentType<CrestComponent> CREST_COMPONENT = Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, Identifier.fromNamespaceAndPath(DualStance.ID, "crest"), DataComponentType.<CrestComponent>builder().persistent(CrestComponent.CODEC).build());
	public static void initialize() {
		ItemComponentTooltipProviderRegistry.addFirst(CREST_COMPONENT);
	}
}
