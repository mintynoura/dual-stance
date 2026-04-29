package io.github.mintynoura.dualstance.registries;

import io.github.mintynoura.dualstance.DualStance;
import io.github.mintynoura.dualstance.item.component.AttributeCrestEffect;
import io.github.mintynoura.dualstance.item.component.CrestComponent;
import io.github.mintynoura.dualstance.item.HeartSealItem;
import io.github.mintynoura.dualstance.item.component.HeartSealedCrest;
import io.github.mintynoura.dualstance.item.component.MobEffectCrestEffect;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;

import java.util.List;
import java.util.function.Function;

public class DualStanceItems {
	public static final Item HEART_SEAL = registerItem("heart_seal", HeartSealItem::new, new Item.Properties()
		.component(DualStanceComponents.HEART_SEALED_CREST, HeartSealedCrest.EMPTY)
		.stacksTo(1), CreativeModeTabs.COMBAT);

	public static final Item PEACE_CREST = registerItem("peace_crest", Item::new, new Item.Properties()
		// we should probably make a helper method to make the crest components easier to read/write
		.component(DualStanceComponents.CREST, new CrestComponent(Identifier.fromNamespaceAndPath(DualStance.ID, "peace_crest"),
			List.of(new AttributeCrestEffect(List.of(
				new AttributeCrestEffect.Entry(Attributes.ARMOR, new AttributeModifier(Identifier.fromNamespaceAndPath(DualStance.ID, "peace_crest"), 1.0f, AttributeModifier.Operation.ADD_VALUE)),
				new AttributeCrestEffect.Entry(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(Identifier.fromNamespaceAndPath(DualStance.ID, "peace_crest"), 1.0f, AttributeModifier.Operation.ADD_VALUE)
				)))
			))), CreativeModeTabs.COMBAT);

	public static final Item BONES_CREST = registerItem("bones_crest", Item::new, new Item.Properties()
		.component(DualStanceComponents.CREST, createSingleAttributeCrestComponent("bones_crest", Attributes.ATTACK_SPEED, 0.1f, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)),
		CreativeModeTabs.COMBAT);

	public static final Item STONE_CREST = registerItem("stone_crest", Item::new, new Item.Properties()
		.component(DualStanceComponents.CREST, createSingleAttributeCrestComponent("stone_crest", Attributes.ATTACK_DAMAGE, 1.0f, AttributeModifier.Operation.ADD_VALUE)),
		CreativeModeTabs.COMBAT);

	public static final Item EMBLEMS_CREST = registerItem("emblems_crest", Item::new, new Item.Properties()
		.component(DualStanceComponents.CREST, new CrestComponent(Identifier.fromNamespaceAndPath(DualStance.ID, "emblems_crest"),
			List.of(new AttributeCrestEffect(List.of(
				new AttributeCrestEffect.Entry(Attributes.LUCK, new AttributeModifier(Identifier.fromNamespaceAndPath(DualStance.ID, "emblems_crest"), 1.0f, AttributeModifier.Operation.ADD_VALUE)
				))),
				new MobEffectCrestEffect(List.of(new MobEffectInstance(MobEffects.INSTANT_HEALTH, 1, 0, true, true)), 400)
			)
		)), CreativeModeTabs.COMBAT);

	public static final Item SPECTERS_CREST = registerItem("specters_crest", Item::new, new Item.Properties()
		.component(DualStanceComponents.CREST, createSingleAttributeCrestComponent("specters_crest", Attributes.MOVEMENT_SPEED, 0.1f, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)),
		CreativeModeTabs.COMBAT);

	public static final Item SHELLS_CREST = registerItem("shells_crest", Item::new, new Item.Properties()
		.component(DualStanceComponents.CREST, new CrestComponent(Identifier.fromNamespaceAndPath(DualStance.ID, "shells_crest"),
			List.of(new AttributeCrestEffect(List.of(
				new AttributeCrestEffect.Entry(Attributes.BURNING_TIME, new AttributeModifier(Identifier.fromNamespaceAndPath(DualStance.ID, "shells_crest"), -0.5f, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)),
				new AttributeCrestEffect.Entry(Attributes.OXYGEN_BONUS, new AttributeModifier(Identifier.fromNamespaceAndPath(DualStance.ID, "shells_crest"), 0.25f, AttributeModifier.Operation.ADD_VALUE)
				))),
				new MobEffectCrestEffect(List.of(new MobEffectInstance(MobEffects.SATURATION, 1, 0, true, true)), 200)
			))), CreativeModeTabs.COMBAT);

	public static final Item PACIFISM_CREST = registerItem("pacifism_crest", Item::new, new Item.Properties()
		.component(DualStanceComponents.CREST, new CrestComponent(Identifier.fromNamespaceAndPath(DualStance.ID, "pacifism_crest"), List.of())),
		CreativeModeTabs.COMBAT);

	public static final Item ENCHANTER_CREST = registerItem("enchanter_crest", Item::new, new Item.Properties()
			.component(DualStanceComponents.CREST, new CrestComponent(Identifier.fromNamespaceAndPath(DualStance.ID, "enchanter_crest"),
				List.of(new AttributeCrestEffect(List.of(
					new AttributeCrestEffect.Entry(Attributes.ARMOR, new AttributeModifier(Identifier.fromNamespaceAndPath(DualStance.ID, "enchanter_crest"), 4.0f, AttributeModifier.Operation.ADD_VALUE)),
					new AttributeCrestEffect.Entry(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(Identifier.fromNamespaceAndPath(DualStance.ID, "enchanter_crest"), 2.0f, AttributeModifier.Operation.ADD_VALUE)),
					new AttributeCrestEffect.Entry(Attributes.ATTACK_DAMAGE, new AttributeModifier(Identifier.fromNamespaceAndPath(DualStance.ID, "enchanter_crest"), 4.0f, AttributeModifier.Operation.ADD_VALUE)),
					new AttributeCrestEffect.Entry(Attributes.MOVEMENT_SPEED, new AttributeModifier(Identifier.fromNamespaceAndPath(DualStance.ID, "enchanter_crest"), 0.2f, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))
					)
					)
				)
				)),
		CreativeModeTabs.COMBAT);

	public static final Item HATRED_CREST = registerItem("hatred_crest", Item::new, new Item.Properties()
			.component(DualStanceComponents.CREST, new CrestComponent(Identifier.fromNamespaceAndPath(DualStance.ID, "hatred_crest"), List.of())),
		CreativeModeTabs.COMBAT);

	public static Item registerItem(String name, Function<Item.Properties, Item> factory, Item.Properties settings, ResourceKey<CreativeModeTab> tab) {
		ResourceKey<Item> itemRegistryKey = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(DualStance.ID, name));
		Item item = factory.apply(settings.setId(itemRegistryKey));
		Registry.register(BuiltInRegistries.ITEM, itemRegistryKey, item);
		CreativeModeTabEvents.modifyOutputEvent(tab).register(creativeTab -> creativeTab.accept(item));
		return item;
	}
	public static CrestComponent createSingleAttributeCrestComponent(String name, Holder<Attribute> attribute, float amount, AttributeModifier.Operation operation) {
		return new CrestComponent(Identifier.fromNamespaceAndPath(DualStance.ID, name), List.of(new AttributeCrestEffect(List.of(
			new AttributeCrestEffect.Entry(attribute, new AttributeModifier(Identifier.fromNamespaceAndPath(DualStance.ID, name), amount, operation))
		))));
	}
	public static void initialize() {}
}
