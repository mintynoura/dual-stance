package io.github.mintynoura.dualstance.datagen;

import io.github.mintynoura.dualstance.registries.DualStanceItems;
import io.github.mintynoura.dualstance.util.DualStanceTags;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;

import java.util.concurrent.CompletableFuture;

public class ItemTagProvider extends FabricTagsProvider.ItemTagsProvider {
	public ItemTagProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookupFuture) {
		super(output, registryLookupFuture);
	}

	@Override
	protected void addTags(HolderLookup.Provider registries) {
		valueLookupBuilder(DualStanceTags.Items.CRESTS)
			.add(DualStanceItems.PEACE_CREST)
			.add(DualStanceItems.BONES_CREST)
			.add(DualStanceItems.STONE_CREST)
			.add(DualStanceItems.EMBLEMS_CREST)
			.add(DualStanceItems.SPECTERS_CREST)
			.add(DualStanceItems.SHELLS_CREST)
			.add(DualStanceItems.PACIFISM_CREST);
	}
}
