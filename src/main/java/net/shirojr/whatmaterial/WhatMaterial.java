package net.shirojr.whatmaterial;

import net.fabricmc.api.ModInitializer;

import net.shirojr.whatmaterial.init.WhatMaterialCommonEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WhatMaterial implements ModInitializer {
	public static final String MOD_ID = "whatmaterial";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		WhatMaterialCommonEvents.initialize();

		LOGGER.info("We do be seeing things!");
	}
}