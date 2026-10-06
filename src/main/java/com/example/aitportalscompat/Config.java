package com.example.aitportalscompat;

import net.minecraftforge.common.ForgeConfigSpec;

/**
 * Tuning knobs. Portal orientation/placement could not be tested without a running game,
 * so these let you correct it in config/ait_portals_compat-common.toml without recompiling.
 */
public final class Config {
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.BooleanValue ENABLED;
    public static final ForgeConfigSpec.BooleanValue FLIP_EXTERIOR_FACING;
    public static final ForgeConfigSpec.BooleanValue FLIP_INTERIOR_FACING;
    public static final ForgeConfigSpec.DoubleValue PLANE_OFFSET;
    public static final ForgeConfigSpec.DoubleValue YAW_OFFSET_DEGREES;

    static {
        ForgeConfigSpec.Builder b = new ForgeConfigSpec.Builder();

        ENABLED = b.comment("Master switch. When false, AIT behaves exactly like the stock Forge port (no portals).")
                .define("enabled", true);

        FLIP_EXTERIOR_FACING = b.comment("Flip the facing direction used for the exterior (outside) portal.")
                .define("flipExteriorFacing", false);

        FLIP_INTERIOR_FACING = b.comment("Flip the facing direction used for the interior (door) portal.")
                .define("flipInteriorFacing", false);

        PLANE_OFFSET = b.comment("How far from the block centre (towards the door face) the portal plane sits, in blocks.")
                .defineInRange("planeOffset", 0.45D, 0.0D, 0.5D);

        YAW_OFFSET_DEGREES = b.comment("Extra yaw (degrees) added to the exterior->interior portal rotation.")
                .defineInRange("yawOffsetDegrees", 0.0D, -360.0D, 360.0D);

        SPEC = b.build();
    }

    private Config() {}
}
