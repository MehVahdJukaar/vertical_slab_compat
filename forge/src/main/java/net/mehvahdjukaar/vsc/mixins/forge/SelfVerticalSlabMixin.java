package net.mehvahdjukaar.vsc.mixins.forge;

import net.mehvahdjukaar.moonlight.api.block.VerticalSlabBlock;
import net.mehvahdjukaar.vsc.CompatVerticalSlab;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(CompatVerticalSlab.class)
public abstract class SelfVerticalSlabMixin extends VerticalSlabBlock {

    @Shadow
    @Final
    private BlockState mimic;

    protected SelfVerticalSlabMixin(Properties properties) {
        super(properties);
    }

    @Override
    public int getFireSpreadSpeed(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
        return mimic.getFireSpreadSpeed(world, pos, face);
    }

    @Override
    public int getFlammability(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
        return mimic.getFlammability(world, pos, face);
    }

}
