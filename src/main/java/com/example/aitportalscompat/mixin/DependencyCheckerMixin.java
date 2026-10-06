package com.example.aitportalscompat.mixin;

import com.example.aitportalscompat.portal.PortalBridge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * AIT Forger ships DependencyChecker with hasPortals() hard-coded to "return false".
 * (Verified in aitforger-1.0.4+1.2.12: bytecode is just iconst_0 / ireturn.)
 * Target is addressed by name so no compile-time dependency on the aitforger bootstrap jar is needed.
 */
@Pseudo
@Mixin(targets = "dev.drtheo.aitforger.bootstrap.remapped.dev.amble.ait.compat.DependencyChecker", remap = false)
public abstract class DependencyCheckerMixin {

    @Inject(method = "hasPortals", at = @At("HEAD"), cancellable = true, remap = false)
    private static void aitportals$hasPortals(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(PortalBridge.isActive());
    }
}
