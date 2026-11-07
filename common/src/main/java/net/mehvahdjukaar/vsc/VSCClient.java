package net.mehvahdjukaar.vsc;

import net.mehvahdjukaar.moonlight.api.misc.EventCalled;
import net.mehvahdjukaar.moonlight.api.platform.ClientHelper;
import net.mehvahdjukaar.moonlight.api.platform.RegHelper;
import net.mehvahdjukaar.vsc.dynamicpack.ClientDynamicResourcesHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

public class VSCClient {

    //slab sources might not be registered yet so we look them up lazily. 3 layers covers anything we copy
    private static final int COPIED_TINT_LAYERS = 3;

    public static void init() {
        RegHelper.registerDynamicResourceProvider(ClientDynamicResourcesHandler.INSTANCE);
        ClientHelper.addBlockColorsRegistration(VSCClient::registerBlockColors);
    }

    @EventCalled
    private static void registerBlockColors(ClientHelper.BlockColorEvent event) {
        for (var e : VSC.VERTICAL_SLABS.entrySet()) {
            Block slab = e.getKey().slab;
            List<BlockTintSource> layers = new ArrayList<>();

            for (int i = 0; i < COPIED_TINT_LAYERS; i++) {
                layers.add(new SlabTintSource(slab, i));
            }
            event.register(layers, e.getValue());
        }
    }

    private record SlabTintSource(Block slab, int layer) implements BlockTintSource {
        @Override
        public int color(BlockState state) {
            var slabState = slab.defaultBlockState();
            var source = Minecraft.getInstance().getBlockColors().getTintSource(slabState, layer);
            return source == null ? -1 : source.color(slabState);
        }
        @Override
        public int colorInWorld(BlockState state, BlockAndTintGetter level, BlockPos pos) {
            var slabState = slab.defaultBlockState();
            var source = Minecraft.getInstance().getBlockColors().getTintSource(slabState, layer);
            return (source == null) ? -1 : source.colorInWorld(slabState, level, pos);
        }
    }

}
