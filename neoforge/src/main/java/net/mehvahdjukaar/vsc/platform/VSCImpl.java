package net.mehvahdjukaar.vsc.platform;

import net.mehvahdjukaar.vsc.CutBlockType;
import net.mehvahdjukaar.vsc.EVSCompat;
import net.mehvahdjukaar.vsc.VSC;
import net.mehvahdjukaar.vsc.temp.TempVerticalSlabBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class VSCImpl {

    public static Block createVSlab(CutBlockType type, BlockBehaviour.Properties properties) {
        //has togo through a method returning Block or the verifier loads quark classes...
        //if (VSC.QUARK) return CompatVerticalSlab.create(properties, type);
        if (VSC.USE_EVS_BLOCKS) return EVSCompat.createVSlab(properties);
        //if (VSC.USE_BA_BLOCKS) return BuildersAdditionCompat.createVSlab(properties);
        return new TempVerticalSlabBlock(properties, type);
    }
}
