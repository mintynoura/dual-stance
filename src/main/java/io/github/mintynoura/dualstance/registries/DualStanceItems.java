package io.github.mintynoura.dualstance.registries;

import io.github.mintynoura.dualstance.DualStance;
import io.github.mintynoura.dualstance.item.component.crest_effects.AttributeCrestEffect;
import io.github.mintynoura.dualstance.item.component.CrestComponent;
import io.github.mintynoura.dualstance.item.HeartSealItem;
import io.github.mintynoura.dualstance.item.component.crest_effects.HeartSealedCrest;
import io.github.mintynoura.dualstance.item.component.crest_effects.MobEffectCrestEffect;
import io.github.mintynoura.dualstance.util.CrestIdentifiers;
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
		.component(DualStanceComponents.CREST, new CrestComponent(CrestIdentifiers.PEACE_CREST,
			List.of(new AttributeCrestEffect(List.of(
				new AttributeCrestEffect.Entry(Attributes.ARMOR, new AttributeModifier(CrestIdentifiers.PEACE_CREST, 1.0f, AttributeModifier.Operation.ADD_VALUE)),
				new AttributeCrestEffect.Entry(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(CrestIdentifiers.PEACE_CREST, 1.0f, AttributeModifier.Operation.ADD_VALUE)
				)))
			))), CreativeModeTabs.COMBAT);

	public static final Item BONES_CREST = registerItem("bones_crest", Item::new, new Item.Properties()
		.component(DualStanceComponents.CREST, createSingleAttributeCrestComponent(CrestIdentifiers.BONES_CREST, Attributes.ATTACK_SPEED, 0.1f, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)),
		CreativeModeTabs.COMBAT);

	public static final Item STONE_CREST = registerItem("stone_crest", Item::new, new Item.Properties()
		.component(DualStanceComponents.CREST, createSingleAttributeCrestComponent(CrestIdentifiers.STONE_CREST, Attributes.ATTACK_DAMAGE, 1.0f, AttributeModifier.Operation.ADD_VALUE)),
		CreativeModeTabs.COMBAT);

	public static final Item EMBLEMS_CREST = registerItem("emblems_crest", Item::new, new Item.Properties()
		.component(DualStanceComponents.CREST, new CrestComponent(CrestIdentifiers.EMBLEMS_CREST,
			List.of(new AttributeCrestEffect(List.of(
				new AttributeCrestEffect.Entry(Attributes.LUCK, new AttributeModifier(CrestIdentifiers.EMBLEMS_CREST, 1.0f, AttributeModifier.Operation.ADD_VALUE)
				))),
				new MobEffectCrestEffect(List.of(new MobEffectInstance(MobEffects.INSTANT_HEALTH, 1, 0, true, true)), 400)
			)
		)), CreativeModeTabs.COMBAT);

	public static final Item SPECTERS_CREST = registerItem("specters_crest", Item::new, new Item.Properties()
		.component(DualStanceComponents.CREST, createSingleAttributeCrestComponent(CrestIdentifiers.SPECTERS_CREST, Attributes.MOVEMENT_SPEED, 0.1f, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)),
		CreativeModeTabs.COMBAT);

	public static final Item SHELLS_CREST = registerItem("shells_crest", Item::new, new Item.Properties()
		.component(DualStanceComponents.CREST, new CrestComponent(CrestIdentifiers.SHELLS_CREST,
			List.of(new AttributeCrestEffect(List.of(
				new AttributeCrestEffect.Entry(Attributes.BURNING_TIME, new AttributeModifier(CrestIdentifiers.SHELLS_CREST, -0.5f, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)),
				new AttributeCrestEffect.Entry(Attributes.OXYGEN_BONUS, new AttributeModifier(CrestIdentifiers.SHELLS_CREST, 0.25f, AttributeModifier.Operation.ADD_VALUE)
				))),
				new MobEffectCrestEffect(List.of(new MobEffectInstance(MobEffects.SATURATION, 1, 0, true, true)), 200)
			))), CreativeModeTabs.COMBAT);

	public static final Item PACIFISM_CREST = registerItem("pacifism_crest", Item::new, new Item.Properties()
		.component(DualStanceComponents.CREST, new CrestComponent(CrestIdentifiers.PACIFISM_CREST, List.of())),
		CreativeModeTabs.COMBAT);

	public static final Item ENCHANTER_CREST = registerItem("enchanter_crest", Item::new, new Item.Properties()
			.component(DualStanceComponents.CREST, new CrestComponent(CrestIdentifiers.ENCHANTER_CREST,
				List.of(new AttributeCrestEffect(List.of(
					new AttributeCrestEffect.Entry(Attributes.ARMOR, new AttributeModifier(CrestIdentifiers.ENCHANTER_CREST, 4.0f, AttributeModifier.Operation.ADD_VALUE)),
					new AttributeCrestEffect.Entry(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(CrestIdentifiers.ENCHANTER_CREST, 2.0f, AttributeModifier.Operation.ADD_VALUE)),
					new AttributeCrestEffect.Entry(Attributes.ATTACK_DAMAGE, new AttributeModifier(CrestIdentifiers.ENCHANTER_CREST, 4.0f, AttributeModifier.Operation.ADD_VALUE)),
					new AttributeCrestEffect.Entry(Attributes.MOVEMENT_SPEED, new AttributeModifier(CrestIdentifiers.ENCHANTER_CREST, 0.2f, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))
					)
					)
				)
				)),
		CreativeModeTabs.COMBAT);

	public static final Item HATRED_CREST = registerItem("hatred_crest", Item::new, new Item.Properties()
			.component(DualStanceComponents.CREST, new CrestComponent(CrestIdentifiers.HATRED_CREST, List.of())),
		CreativeModeTabs.COMBAT);

	public static Item registerItem(String name, Function<Item.Properties, Item> factory, Item.Properties settings, ResourceKey<CreativeModeTab> tab) {
		ResourceKey<Item> itemRegistryKey = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(DualStance.ID, name));
		Item item = factory.apply(settings.setId(itemRegistryKey));
		Registry.register(BuiltInRegistries.ITEM, itemRegistryKey, item);
		CreativeModeTabEvents.modifyOutputEvent(tab).register(creativeTab -> creativeTab.accept(item));
		return item;
	}
	public static CrestComponent createSingleAttributeCrestComponent(Identifier identifier, Holder<Attribute> attribute, float amount, AttributeModifier.Operation operation) {
		return new CrestComponent(identifier, List.of(new AttributeCrestEffect(List.of(
			new AttributeCrestEffect.Entry(attribute, new AttributeModifier(identifier, amount, operation))
		))));
	}
	public static void initialize() {}
}
