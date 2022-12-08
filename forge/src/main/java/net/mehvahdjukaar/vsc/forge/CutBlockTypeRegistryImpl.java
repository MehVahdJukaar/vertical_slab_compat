package net.mehvahdjukaar.vsc.forge;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.WeatheringCopperSlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

public class CutBlockTypeRegistryImpl {

    @org.jetbrains.annotations.Contract
    public static boolean hasRightShapeHack(Block block) {
        var c = block.getClass();
        if (c != SlabBlock.class && c != WeatheringCopperSlabBlock.class) {
            try {
                var m = ObfuscationReflectionHelper.findMethod(c, "getShape",
                        BlockState.class, BlockGetter.class, BlockPos.class, CollisionContext.class);
                if (m.getDeclaringClass() != SlabBlock.class) return false;
            } catch (Exception ignored) {
            }
            try {
                var m = ObfuscationReflectionHelper.findMethod(c, "getCollisionShape",
                        BlockState.class, BlockGetter.class, BlockPos.class, CollisionContext.class);
                if (m.getDeclaringClass() != SlabBlock.class) return false;
            } catch (Exception ignored) {
            }
        }
        return true;
    }
}
