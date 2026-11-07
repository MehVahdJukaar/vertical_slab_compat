package net.mehvahdjukaar.vsc;


import github.mrh0.buildersaddition2.blocks.panel.PanelBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class BuildersAdditionCompat {

    public static Block createVSlab(BlockBehaviour.Properties properties) {
        return new PanelBlock(properties);
    }
}
