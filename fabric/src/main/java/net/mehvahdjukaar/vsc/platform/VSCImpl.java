package net.mehvahdjukaar.vsc.platform;

import net.mehvahdjukaar.moonlight.api.util.Utils;
import net.mehvahdjukaar.vsc.CutBlockType;
import net.mehvahdjukaar.vsc.temp.TempVerticalSlabBlock;
import net.minecraft.world.level.block.Block;

public class VSCImpl {

    public static Block createVSlab(CutBlockType type) {
        return new TempVerticalSlabBlock(Utils.copyPropertySafe(type.base), type);
    }
}
