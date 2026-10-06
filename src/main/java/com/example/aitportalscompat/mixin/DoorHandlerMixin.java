package com.example.aitportalscompat.mixin;

import com.example.aitportalscompat.portal.PortalManager;
import dev.amble.ait.core.tardis.handler.DoorHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * AIT's port contains no portal-spawning code, so we hook the door state changes ourselves
 * (DoorHandler.openDoors() / closeDoors(), both verified present in AIT 1.2.12).
 */
@Mixin(value = DoorHandler.class, remap = false)
public abstract class DoorHandlerMixin {

    @Inject(method = {"openDoors", "closeDoors"}, at = @At("RETURN"), remap = false)
    private void aitportals$syncPortals(CallbackInfo ci) {
        PortalManager.sync(((DoorHandler) (Object) this).tardis());
    }
}
