package net.mehvahdjukaar.vsc.platform;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;

public class CutBlockTypeRegistryImpl {

    public static boolean hasRightShapeHack(Block block) {
        for (Class<?> c = block.getClass(); c != SlabBlock.class; c = c.getSuperclass()) {
            if (overridesShape(c, "getShape") || overridesShape(c, "getCollisionShape")) return false;
        }
        return true;
    }

    private static boolean overridesShape(Class<?> c, String name) {
        try {
            c.getDeclaredMethod(name, BlockState.class, BlockGetter.class, BlockPos.class, CollisionContext.class);
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }
}
