package io.github.mintynoura.dualstance.util;

import io.github.mintynoura.dualstance.DualStance;
import net.minecraft.resources.Identifier;

public class CrestIdentifiers {
	public static final Identifier PEACE_CREST = create("peace_crest");
	public static final Identifier BONES_CREST = create("bones_crest");
	public static final Identifier STONE_CREST = create("stone_crest");
	public static final Identifier EMBLEMS_CREST = create("emblems_crest");
	public static final Identifier SPECTERS_CREST = create("specters_crest");
	public static final Identifier SHELLS_CREST = create("shells_crest");
	public static final Identifier PACIFISM_CREST = create("pacifism_crest");
	public static final Identifier ENCHANTER_CREST = create("enchanter_crest");
	public static final Identifier HATRED_CREST = create("hatred_crest");

	public static Identifier create(String name) {
		return Identifier.fromNamespaceAndPath(DualStance.ID, name);
	}
}
