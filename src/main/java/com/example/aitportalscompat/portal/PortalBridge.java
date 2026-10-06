package com.example.aitportalscompat.portal;

import com.example.aitportalscompat.AitPortalsCompat;
import com.example.aitportalscompat.Config;
import net.minecraftforge.fml.ModList;

/** Single place that decides whether the compat is on. Safe to call very early. */
public final class PortalBridge {
    private PortalBridge() {}

    public static boolean isActive() {
        try {
            ModList mods = ModList.get();
            return mods != null
                    && mods.isLoaded(AitPortalsCompat.IMMERSIVE_PORTALS_ID)
                    && Config.ENABLED.get();
        } catch (IllegalStateException configNotLoadedYet) {
            return false;
        }
    }
}
