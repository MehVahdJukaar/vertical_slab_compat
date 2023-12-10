package net.mehvahdjukaar.vsc.temp;

import net.mehvahdjukaar.moonlight.api.util.Utils;
import net.mehvahdjukaar.vsc.CompatVerticalSlab;
import net.mehvahdjukaar.vsc.CutBlockType;
import net.minecraft.world.level.block.Block;

public interface QuarkCompat {

    static Block createVSlab(CutBlockType type){
        return new CompatVerticalSlab(Utils.copyPropertySafe(type.base), type);
    }
}
