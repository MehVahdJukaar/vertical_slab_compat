package net.mehvahdjukaar.vsc;

import net.mehvahdjukaar.moonlight.api.misc.EventCalled;
import net.mehvahdjukaar.moonlight.api.platform.ClientHelper;
import net.minecraft.world.level.block.Block;

public class VSCClient {

    public static void init() {
        ClientHelper.addBlockColorsRegistration(VSCClient::registerBlockColors);
    }

    public static void setup() {
    }

    @EventCalled
    private static void registerBlockColors(ClientHelper.BlockColorEvent event) {
        for(var e : VSC.VERTICAL_SLABS.entrySet()){
            Block value = e.getValue();
            Block slab = e.getKey().slab;
            event.register((blockState, blockAndTintGetter, blockPos, i) -> event.getColor(slab.defaultBlockState(),blockAndTintGetter, blockPos, i), value);
        }
    }

}
