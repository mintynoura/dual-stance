package io.github.mintynoura.dualstance.item;

import io.github.mintynoura.dualstance.DualStance;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

import java.util.function.Function;

public class DualStanceItems {
	public static final Item HEART_SEAL = registerItem("heart_seal", HeartSealItem::new, new Item.Properties()
		.stacksTo(1));

	public static Item registerItem(String name, Function<Item.Properties, Item> factory, Item.Properties settings) {
		ResourceKey<Item> itemRegistryKey = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(DualStance.ID, name));
		Item item = factory.apply(settings.setId(itemRegistryKey));
		Registry.register(BuiltInRegistries.ITEM, itemRegistryKey, item);
		return item;
	}

	public static void initialize() {}
}
