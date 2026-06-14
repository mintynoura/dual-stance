package io.github.mintynoura.dualstance.util;

import io.github.mintynoura.dualstance.DualStance;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.item.Item;

public class DualStanceTags {
	public static class Items {
		public static final TagKey<Item> CRESTS = tag("crests");
		public static final TagKey<Item> NON_ENCHANTER_CRESTS = tag("non_enchanter_crests");

		public static final TagKey<Item> PEACE_CREST_INGREDIENTS = tag("peace_crest_ingredients");
		public static final TagKey<Item> BONES_CREST_INGREDIENTS = tag("bones_crest_ingredients");
		public static final TagKey<Item> STONE_CREST_INGREDIENTS = tag("stone_crest_ingredients");
		public static final TagKey<Item> EMBLEMS_CREST_INGREDIENTS = tag("emblems_crest_ingredients");
		public static final TagKey<Item> SPECTERS_CREST_INGREDIENTS = tag("specters_crest_ingredients");
		public static final TagKey<Item> SHELLS_CREST_INGREDIENTS = tag("shells_crest_ingredients");
		public static final TagKey<Item> PACIFISM_CREST_INGREDIENTS = tag("pacifism_crest_ingredients");
		public static final TagKey<Item> ENCHANTER_CREST_INGREDIENTS = tag("enchanter_crest_ingredients");
		public static final TagKey<Item> HATRED_CREST_INGREDIENTS = tag("hatred_crest_ingredients");

		public static TagKey<Item> tag(String name) {
			return TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(DualStance.ID, name));
		}
	}
	public static class DamageTypes {
		public static final TagKey<DamageType> CREST_INCREASE_EXEMPT = TagKey.create(Registries.DAMAGE_TYPE, Identifier.fromNamespaceAndPath(DualStance.ID, "crest_increase_exempt"));
	}
}
