package com.example.aitportalscompat.portal;

import com.example.aitportalscompat.AitPortalsCompat;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import qouteall.imm_ptl.core.portal.Portal;

@Mod.EventBusSubscriber(modid = AitPortalsCompat.MOD_ID)
public final class PortalEvents {
    private PortalEvents() {}

    @SubscribeEvent
    public static void onJoin(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide()) return;
        if (event.getEntity() instanceof Portal portal) PortalManager.track(portal);
    }

    @SubscribeEvent
    public static void onLeave(EntityLeaveLevelEvent event) {
        if (event.getLevel().isClientSide()) return;
        if (event.getEntity() instanceof Portal portal) PortalManager.untrack(portal);
    }
}
