package net.mehvahdjukaar.vsc;

import games.enchanted.verticalslabs.block.VerticalSlabBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class EVSCompat {

    public static Block createVSlab(BlockBehaviour.Properties properties) {
        return new VerticalSlabBlock(properties);
    }


}
