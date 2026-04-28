package io.github.mintynoura.dualstance.registries;

import io.github.mintynoura.dualstance.DualStance;
import io.github.mintynoura.dualstance.item.component.CrestComponent;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

public class CrestTypesRegister {
	public static final ResourceKey<Registry<CrestComponent>> CREST_REGISTRY_KEY =
		ResourceKey.createRegistryKey(Identifier.fromNamespaceAndPath(
			DualStance.ID, "crest"));
	public static void initialize(){
		DynamicRegistries.registerSynced(CREST_REGISTRY_KEY, CrestComponent.CODEC);
	}

}
