package brachy.modularui.utils;

import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.Vec3;

import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.BiFunction;

public class RenderUtil {

    // @formatter:off
    private static final Map<Direction, Vector3fc[]> DIRECTION_POSITION_MAP = Util.make(new EnumMap<>(Direction.class), map -> {
        map.put(Direction.UP, new Vector3fc[]{ vec3f(0, 1, 1), vec3f(1, 1, 1), vec3f(1, 1, 0), vec3f(0, 1, 0) });
        map.put(Direction.DOWN, new Vector3fc[]{ vec3f(1, 0, 1), vec3f(0, 0, 1), vec3f(0, 0, 0), vec3f(1, 0, 0) });
        map.put(Direction.SOUTH, new Vector3fc[]{ vec3f(1, 1, 0), vec3f(1, 0, 0), vec3f(0, 0, 0), vec3f(0, 1, 0) });
        map.put(Direction.NORTH, new Vector3fc[]{ vec3f(0, 1, 1), vec3f(0, 0, 1), vec3f(1, 0, 1), vec3f(1, 1, 1) });
        map.put(Direction.EAST, new Vector3fc[]{ vec3f(0, 1, 0), vec3f(0, 0, 0), vec3f(0, 0, 1), vec3f(0, 1, 1) });
        map.put(Direction.WEST, new Vector3fc[]{ vec3f(1, 1, 1), vec3f(1, 0, 1), vec3f(1, 0, 0), vec3f(1, 1, 0) });
    });
    // @formatter:off
    private static final Map<Direction, Vector3fc> DIRECTION_NORMAL_MAP = Util.make(new EnumMap<>(Direction.class), map -> {
        map.put(Direction.UP, vec3f(0, 1, 0));
        map.put(Direction.DOWN, vec3f(0, 1, 0));
        map.put(Direction.SOUTH, vec3f(0, 0, 1));
        map.put(Direction.NORTH, vec3f(0, 0, 1));
        map.put(Direction.EAST, vec3f(1, 0, 0));
        map.put(Direction.WEST, vec3f(1, 0, 0));
    });

    public static Vec3 vec3(double x, double y, double z) {
        return new Vec3(x, y, z);
    }

    public static Vector3f vec3f(float x, float y, float z) {
        return new Vector3f(x, y, z);
    }
    // @formatter:on

    public static Vector3fc[] getVertices(Direction direction) {
        return DIRECTION_POSITION_MAP.get(direction);
    }

    public static Vector3fc getNormal(Direction direction) {
        return DIRECTION_NORMAL_MAP.get(direction);
    }
    // @formatter:on

    public static int getFluidLight(Fluid fluid, BlockPos pos) {
        if (Minecraft.getInstance().level == null) return 0;
        return LevelRenderer.getLightColor(Minecraft.getInstance().level, fluid.defaultFluidState().createLegacyBlock(),
                pos);
    }
}
