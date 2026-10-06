package com.example.aitportalscompat;

import com.mojang.logging.LogUtils;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.slf4j.Logger;

@Mod(AitPortalsCompat.MOD_ID)
public class AitPortalsCompat {
    public static final String MOD_ID = "ait_portals_compat";
    public static final Logger LOGGER = LogUtils.getLogger();

    /** Mod ids, taken from the jars' own mods.toml. */
    public static final String IMMERSIVE_PORTALS_ID = "imm_ptl_core";
    public static final String AIT_ID = "ait";

    public AitPortalsCompat() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        LOGGER.info("AiT Portals Compat loaded");
    }
}
