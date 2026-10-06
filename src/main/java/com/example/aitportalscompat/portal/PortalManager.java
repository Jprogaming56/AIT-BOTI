package com.example.aitportalscompat.portal;

import com.example.aitportalscompat.AitPortalsCompat;
import com.example.aitportalscompat.Config;
import dev.amble.ait.core.tardis.ServerTardis;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.handler.travel.TravelHandlerBase;
import dev.amble.ait.data.schema.exterior.ExteriorVariantSchema;
import dev.amble.ait.lib.data.CachedDirectedGlobalPos;
import dev.amble.lib.data.DirectedBlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;
import qouteall.imm_ptl.core.api.PortalAPI;
import qouteall.imm_ptl.core.platform_specific.IPRegistry;
import qouteall.imm_ptl.core.portal.Portal;
import qouteall.q_misc_util.my_util.DQuaternion;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Creates/removes the Immersive Portals pair that links a TARDIS exterior door to its interior door.
 *
 * AIT (with DependencyChecker.hasPortals() == true) already:
 *  - uses walk-through collision shapes on the exterior block, and
 *  - skips its own teleportInside()/door teleport for variants where hasPortals() is true.
 * So whenever the flag is on and the door is open, a portal MUST exist, otherwise the TARDIS can't be entered.
 */
public final class PortalManager {
    public static final String TAG_PREFIX = AitPortalsCompat.MOD_ID + ":";

    private static final Map<UUID, Set<Portal>> TRACKED = new ConcurrentHashMap<>();

    private PortalManager() {}

    /** Make the portal state match the TARDIS' current door/landing state. */
    public static void sync(Tardis tardis) {
        if (!(tardis instanceof ServerTardis server)) return; // ignore client-side tardises

        UUID id = server.getUuid();
        removeAll(id);

        if (!PortalBridge.isActive()) return;
        if (server.door().isClosed()) return;
        if (server.travel().getState() != TravelHandlerBase.State.LANDED) return;

        ExteriorVariantSchema variant = server.getExterior().getVariant();
        if (!variant.hasPortals()) return;

        try {
            spawn(server, variant, id);
        } catch (Exception e) {
            AitPortalsCompat.LOGGER.error("Failed to create portals for TARDIS {}", id, e);
        }
    }

    private static void spawn(ServerTardis tardis, ExteriorVariantSchema variant, UUID id) {
        CachedDirectedGlobalPos ext = tardis.travel().position();
        ServerLevel extLevel = ext.getWorld();
        ServerLevel intLevel = tardis.world();
        DirectedBlockPos door = tardis.getDesktop().getDoorPos();
        if (extLevel == null || intLevel == null || door == null) return;

        Direction extDir = ext.getRotationDirection();
        Direction intDir = door.toMinecraftDirection();
        if (Config.FLIP_EXTERIOR_FACING.get()) extDir = extDir.getOpposite();
        if (Config.FLIP_INTERIOR_FACING.get()) intDir = intDir.getOpposite();

        double w = variant.portalWidth() > 0 ? variant.portalWidth() : 1.0;
        double h = variant.portalHeight() > 0 ? variant.portalHeight() : 2.0;
        double off = Config.PLANE_OFFSET.get();

        Vec3 up = new Vec3(0, 1, 0);
        Vec3 extN = horizontal(extDir);
        Vec3 intN = horizontal(intDir);

        Vec3 extCenter = Vec3.atBottomCenterOf(ext.getPos()).add(extN.scale(off)).add(0, h / 2.0, 0);
        Vec3 intCenter = Vec3.atBottomCenterOf(door.getPos()).add(intN.scale(off)).add(0, h / 2.0, 0);

        // A viewer in front of the exterior door looks along -extN and must see the room looking along +intN,
        // so the portal rotation must send extN -> -intN.
        Vec3 target = intN.scale(-1);
        double yaw = Math.toDegrees(Math.atan2(
                -(extN.x * target.z - extN.z * target.x),
                extN.x * target.x + extN.z * target.z)) + Config.YAW_OFFSET_DEGREES.get();
        DQuaternion rotation = DQuaternion.rotationByDegrees(up, yaw);

        // normal = axisW x axisH = extN  =>  axisW = up x extN
        Vec3 axisW = up.cross(extN);

        Portal portal = new Portal(portalType(), extLevel);
        portal.setOriginPos(extCenter);
        portal.setOrientationAndSize(axisW, up, w, h);
        PortalAPI.setPortalTransformation(portal, intLevel.dimension(), intCenter, rotation, 1.0);
        portal.setTeleportable(true);
        portal.portalTag = TAG_PREFIX + id;
        PortalAPI.spawnServerEntity(portal);

        Portal reverse = PortalAPI.createReversePortal(portal);
        reverse.portalTag = portal.portalTag;
        PortalAPI.spawnServerEntity(reverse);
    }

    @SuppressWarnings("unchecked")
    private static EntityType<Portal> portalType() {
        Object raw = IPRegistry.PORTAL.get();
        return (EntityType<Portal>) raw;
    }

    private static Vec3 horizontal(Direction d) {
        return new Vec3(d.getStepX(), 0, d.getStepZ());
    }

    public static void removeAll(UUID id) {
        Set<Portal> set = TRACKED.remove(id);
        if (set == null) return;
        for (Portal p : set) {
            if (!p.isRemoved()) p.discard();
        }
    }

    // --- tracking of portals that were saved with the world and reload later ---

    public static void track(Portal p) {
        UUID id = tardisOf(p);
        if (id != null) {
            TRACKED.computeIfAbsent(id, k -> Collections.newSetFromMap(new ConcurrentHashMap<>())).add(p);
        }
    }

    public static void untrack(Portal p) {
        UUID id = tardisOf(p);
        if (id == null) return;
        Set<Portal> set = TRACKED.get(id);
        if (set != null) set.remove(p);
    }

    private static UUID tardisOf(Portal p) {
        String tag = p.portalTag;
        if (tag == null || !tag.startsWith(TAG_PREFIX)) return null;
        try {
            return UUID.fromString(tag.substring(TAG_PREFIX.length()));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
