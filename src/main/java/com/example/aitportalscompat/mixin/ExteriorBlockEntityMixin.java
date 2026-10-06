package com.example.aitportalscompat.mixin;

import com.example.aitportalscompat.portal.PortalBridge;
import dev.amble.ait.core.blockentities.ExteriorBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = ExteriorBlockEntity.class, remap = false)
public abstract class ExteriorBlockEntityMixin {

    @Redirect(
        method = "onEntityCollision",
        at = @At(value = "INVOKE",
                 target = "Ldev/drtheo/aitforger/bootstrap/remapped/dev/amble/ait/compat/DependencyChecker;hasPortals()Z"),
        remap = false)
    private boolean aitportals$hasPortals() {
        return PortalBridge.isActive();
    }
}
