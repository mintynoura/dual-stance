package io.github.mintynoura.dualstance.util;

import io.github.mintynoura.dualstance.DualStance;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class DualStanceTags {
	public static class Items {
		public static final TagKey<Item> CRESTS = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(DualStance.ID, "crests"));
	}
}
