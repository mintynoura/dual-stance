package io.github.mintynoura.dualstance.datagen;

import io.github.mintynoura.dualstance.registries.DualStanceItems;
import io.github.mintynoura.dualstance.util.DualStanceTags;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.concurrent.CompletableFuture;

public class CraftingRecipeProvider extends FabricRecipeProvider {
	public CraftingRecipeProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
		super(output, registriesFuture);
	}

	@Override
	protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
		return new RecipeProvider(registries, output) {
			@Override
			public void buildRecipes() {
				HolderLookup.RegistryLookup<Item> itemLookup = registries.lookupOrThrow(Registries.ITEM);
				shaped(RecipeCategory.COMBAT, DualStanceItems.HEART_SEAL)
					.pattern(" # ")
					.pattern("#X#")
					.pattern(" # ")
					.define('#', Items.GOLD_INGOT)
					.define('X', Items.ENDER_PEARL)
					.unlockedBy(getHasName(DualStanceItems.HEART_SEAL), has(DualStanceItems.HEART_SEAL))
					.save(output);

				crest(DualStanceItems.PEACE_CREST, DualStanceTags.Items.PEACE_CREST_INGREDIENTS);
				crest(DualStanceItems.BONES_CREST, DualStanceTags.Items.BONES_CREST_INGREDIENTS);
				crest(DualStanceItems.STONE_CREST, DualStanceTags.Items.STONE_CREST_INGREDIENTS);
				crest(DualStanceItems.EMBLEMS_CREST, DualStanceTags.Items.EMBLEMS_CREST_INGREDIENTS);
				crest(DualStanceItems.SPECTERS_CREST, DualStanceTags.Items.SPECTERS_CREST_INGREDIENTS);
				crest(DualStanceItems.SHELLS_CREST, DualStanceTags.Items.SHELLS_CREST_INGREDIENTS);
				crest(DualStanceItems.PACIFISM_CREST, DualStanceTags.Items.PACIFISM_CREST_INGREDIENTS);
				crest(DualStanceItems.ENCHANTER_CREST, DualStanceTags.Items.ENCHANTER_CREST_INGREDIENTS);
				crest(DualStanceItems.HATRED_CREST, DualStanceTags.Items.HATRED_CREST_INGREDIENTS);
			}

			private void crest(Item result, TagKey<Item> ingredient) {
				shapeless(RecipeCategory.COMBAT, result)
					.requires(DualStanceTags.Items.CRESTS)
					.requires(DualStanceTags.Items.CRESTS)
					.requires(ingredient)
					.unlockedBy(getHasName(result), has(result))
					.unlockedBy(getHasName(DualStanceItems.HEART_SEAL), has(DualStanceItems.HEART_SEAL))
					.save(output);
			}
		};
	}

	@Override
	public String getName() {
		return "CraftingRecipeProvider";
	}
}
