package com.example.aitportalscompat.mixin;

import com.example.aitportalscompat.portal.PortalBridge;
import dev.amble.ait.core.blocks.ExteriorBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * DependencyChecker.hasPortals() is hard-coded to false in AIT Forger and the class is loaded before mixins
 * can touch it, so we redirect AIT's calls to it instead.
 * m_5940_ / m_5939_ / m_5909_ are the SRG names of the VoxelShape methods (outline/collision/visual shape).
 */
@Mixin(value = ExteriorBlock.class, remap = false)
public abstract class ExteriorBlockMixin {

    @Redirect(
        method = {"m_5940_", "m_5939_", "m_5909_", "getNormalShape"},
        at = @At(value = "INVOKE",
                 target = "Ldev/drtheo/aitforger/bootstrap/remapped/dev/amble/ait/compat/DependencyChecker;hasPortals()Z"),
        remap = false)
    private boolean aitportals$hasPortals() {
        return PortalBridge.isActive();
    }
}
