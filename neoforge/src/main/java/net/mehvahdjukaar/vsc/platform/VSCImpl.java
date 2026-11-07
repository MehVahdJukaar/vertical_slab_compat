package net.mehvahdjukaar.vsc.platform;

import net.mehvahdjukaar.moonlight.api.util.Utils;
import net.mehvahdjukaar.vsc.CompatVerticalSlab;
import net.mehvahdjukaar.vsc.CutBlockType;
import net.mehvahdjukaar.vsc.VSC;
import net.mehvahdjukaar.vsc.temp.TempVerticalSlabBlock;
import net.minecraft.world.level.block.Block;

public class VSCImpl {

    public static Block createVSlab(CutBlockType type) {
        var properties = Utils.copyPropertySafe(type.base);
        //has togo through a method returning Block or the verifier loads quark classes...
        if (VSC.QUARK) return CompatVerticalSlab.create(properties, type);
        return new TempVerticalSlabBlock(properties, type);
    }
}
