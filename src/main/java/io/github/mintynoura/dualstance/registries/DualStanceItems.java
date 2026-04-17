package io.github.mintynoura.dualstance.registries;

import io.github.mintynoura.dualstance.DualStance;
import io.github.mintynoura.dualstance.item.component.AttributeCrestEffect;
import io.github.mintynoura.dualstance.item.component.CrestComponent;
import io.github.mintynoura.dualstance.item.CrestItem;
import io.github.mintynoura.dualstance.item.HeartSealItem;
import io.github.mintynoura.dualstance.item.component.HeartSealedCrest;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;

import java.util.List;
import java.util.function.Function;

public class DualStanceItems {
	public static final Item HEART_SEAL = registerItem("heart_seal", HeartSealItem::new, new Item.Properties()
		.component(DualStanceComponents.HEART_SEALED_CREST, HeartSealedCrest.EMPTY)
		.stacksTo(1));

	public static final Item PEACE_CREST = registerItem("peace_crest", CrestItem::new, new Item.Properties()
		// we should probably make a helper method to make the crest components easier to read/write
		.component(DualStanceComponents.CREST, new CrestComponent(Identifier.fromNamespaceAndPath(DualStance.ID, "peace_crest"),
			List.of(
			new AttributeCrestEffect(List.of(
				new AttributeCrestEffect.Entry(Attributes.ARMOR, new AttributeModifier(Identifier.fromNamespaceAndPath(DualStance.ID, "peace_crest"), 1.0f, AttributeModifier.Operation.ADD_VALUE)),
				new AttributeCrestEffect.Entry(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(Identifier.fromNamespaceAndPath(DualStance.ID, "peace_crest"), 1.0f, AttributeModifier.Operation.ADD_VALUE)
				))
			)))));

	public static Item registerItem(String name, Function<Item.Properties, Item> factory, Item.Properties settings) {
		ResourceKey<Item> itemRegistryKey = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(DualStance.ID, name));
		Item item = factory.apply(settings.setId(itemRegistryKey));
		Registry.register(BuiltInRegistries.ITEM, itemRegistryKey, item);
		return item;
	}
	public static void initialize() {}
}
