package io.github.mintynoura.dualstance.datagen;

//import io.github.jvuong4.peacelily.registry.PLBlocks;
import io.github.mintynoura.dualstance.registries.DualStanceItems;
import io.github.mintynoura.dualstance.util.DualStanceTags;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.Items;
//import vectorwing.farmersdelight.common.registry.ModItems;
//import vectorwing.farmersdelight.common.tag.CommonTags;

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
			.add(DualStanceItems.PACIFISM_CREST)
			.add(DualStanceItems.ENCHANTER_CREST)
			.add(DualStanceItems.HATRED_CREST);
		valueLookupBuilder(DualStanceTags.Items.NON_ENCHANTER_CRESTS)
			.add(DualStanceItems.PEACE_CREST)
			.add(DualStanceItems.BONES_CREST)
			.add(DualStanceItems.STONE_CREST)
			.add(DualStanceItems.EMBLEMS_CREST)
			.add(DualStanceItems.SPECTERS_CREST)
			.add(DualStanceItems.SHELLS_CREST)
			.add(DualStanceItems.PACIFISM_CREST)
			.add(DualStanceItems.HATRED_CREST);

//		valueLookupBuilder(DualStanceTags.Items.PEACE_CREST_INGREDIENTS).add(Items.IRON_INGOT).addOptional(PLBlocks.PEACE_LILY.asItem());
//		valueLookupBuilder(DualStanceTags.Items.BONES_CREST_INGREDIENTS).add(Items.BONE_BLOCK).addOptional(PLBlocks.RED_SNAPDRAGON.asItem());
		valueLookupBuilder(DualStanceTags.Items.STONE_CREST_INGREDIENTS).add(Items.TORCHFLOWER).add(Items.PITCHER_PLANT).add(Items.COOKED_CHICKEN);
		valueLookupBuilder(DualStanceTags.Items.EMBLEMS_CREST_INGREDIENTS).add(Items.GLISTERING_MELON_SLICE).add(Items.SUNFLOWER);
//		valueLookupBuilder(DualStanceTags.Items.SPECTERS_CREST_INGREDIENTS).add(Items.GOLD_INGOT).addOptional(PLBlocks.BIRD_OF_PARADISE.asItem());
//		valueLookupBuilder(DualStanceTags.Items.SHELLS_CREST_INGREDIENTS).add(Items.NAUTILUS_SHELL).addOptionalTag(CommonTags.Items.CROPS_RICE).addOptional(ModItems.FRIED_RICE.get());
		valueLookupBuilder(DualStanceTags.Items.PACIFISM_CREST_INGREDIENTS).add(Items.ROSE_BUSH);
		valueLookupBuilder(DualStanceTags.Items.ENCHANTER_CREST_INGREDIENTS).add(Items.LAPIS_BLOCK);
		valueLookupBuilder(DualStanceTags.Items.HATRED_CREST_INGREDIENTS).add(Items.WITHER_ROSE);
	}
}
