package net.mehvahdjukaar.vsc.platform;

import net.mehvahdjukaar.moonlight.api.util.Utils;
import net.mehvahdjukaar.vsc.CutBlockType;
import net.mehvahdjukaar.vsc.EVSCompat;
import net.mehvahdjukaar.vsc.VSC;
import net.mehvahdjukaar.vsc.temp.TempVerticalSlabBlock;
import net.minecraft.world.level.block.Block;

public class VSCImpl {

    public static Block createVSlab(CutBlockType type) {
        var properties = Utils.copyPropertySafe(type.base);
        if (VSC.USE_EVS_BLOCKS) return EVSCompat.createVSlab(properties);
        return new TempVerticalSlabBlock(properties, type);
    }
}
