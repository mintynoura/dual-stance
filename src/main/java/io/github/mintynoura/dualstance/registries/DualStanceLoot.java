package io.github.mintynoura.dualstance.registries;

import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.EmptyLootItem;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

public class DualStanceLoot {
	public static void modify() {
		LootTableEvents.MODIFY.register((id, tableBuilder, source, registries) -> {
			if (source.isBuiltin()) {
				if (id.equals(BuiltInLootTables.ANCIENT_CITY)) tableBuilder.pool(LootPool.lootPool().add(EmptyLootItem.emptyItem().setWeight(11)).add(LootItem.lootTableItem(DualStanceItems.PEACE_CREST)).setRolls(UniformGenerator.between(1, 2)).build());

				if (id.equals(EntityType.SKELETON.getDefaultLootTable().get())) tableBuilder.pool(LootPool.lootPool()
					.add(LootItem.lootTableItem(DualStanceItems.BONES_CREST))
					.when(
						LootItemEntityPropertyCondition.hasProperties(
							LootContext.EntityTarget.ATTACKER, EntityPredicate.Builder.entity().of(registries.lookupOrThrow(Registries.ENTITY_TYPE), EntityType.PIGLIN)
						)
					).build());

				if (id.equals(BuiltInLootTables.ABANDONED_MINESHAFT)) tableBuilder.pool(LootPool.lootPool().add(EmptyLootItem.emptyItem().setWeight(4)).add(LootItem.lootTableItem(DualStanceItems.STONE_CREST)).setRolls(UniformGenerator.between(1, 2)).build());
				if (id.equals(BuiltInLootTables.SNIFFER_DIGGING)) tableBuilder.pool(LootPool.lootPool().add(EmptyLootItem.emptyItem().setWeight(9)).add(LootItem.lootTableItem(DualStanceItems.STONE_CREST)).build());

				if (id.equals(BuiltInLootTables.JUNGLE_TEMPLE)) tableBuilder.pool(LootPool.lootPool().add(EmptyLootItem.emptyItem().setWeight(3)).add(LootItem.lootTableItem(DualStanceItems.EMBLEMS_CREST)).setRolls(UniformGenerator.between(1, 2)).build());

				if (id.equals(BuiltInLootTables.WOODLAND_MANSION)) tableBuilder.pool(LootPool.lootPool().add(EmptyLootItem.emptyItem().setWeight(4)).add(LootItem.lootTableItem(DualStanceItems.SPECTERS_CREST)).setRolls(UniformGenerator.between(1, 2)).build());

				if (id.equals(BuiltInLootTables.BURIED_TREASURE)) tableBuilder.pool(LootPool.lootPool().add(EmptyLootItem.emptyItem().setWeight(2)).add(LootItem.lootTableItem(DualStanceItems.SHELLS_CREST)).setRolls(UniformGenerator.between(1, 2)).build());
				if (id.equals(BuiltInLootTables.UNDERWATER_RUIN_BIG) || id.equals(BuiltInLootTables.UNDERWATER_RUIN_SMALL)) tableBuilder.pool(LootPool.lootPool().add(EmptyLootItem.emptyItem().setWeight(5)).add(LootItem.lootTableItem(DualStanceItems.SHELLS_CREST)).setRolls(UniformGenerator.between(1, 2)).build());
				if (id.equals(EntityType.ZOMBIE_NAUTILUS.getDefaultLootTable().get())) tableBuilder.pool(LootPool.lootPool().add(EmptyLootItem.emptyItem().setWeight(4)).add(LootItem.lootTableItem(DualStanceItems.SHELLS_CREST)).build());

				if (id.equals(EntityType.WITHER.getDefaultLootTable().get())) tableBuilder.pool(LootPool.lootPool()
					.add(LootItem.lootTableItem(DualStanceItems.HATRED_CREST))
					.when(
						LootItemEntityPropertyCondition.hasProperties(
							LootContext.EntityTarget.ATTACKER, EntityPredicate.Builder.entity().of(registries.lookupOrThrow(Registries.ENTITY_TYPE), EntityType.WARDEN)
						)
					).build());
			}
		});
	}
}
