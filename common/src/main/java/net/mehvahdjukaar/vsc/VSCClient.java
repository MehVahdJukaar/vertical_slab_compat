package net.mehvahdjukaar.vsc;

import net.mehvahdjukaar.moonlight.api.block.IBlockHolder;
import net.mehvahdjukaar.moonlight.api.misc.EventCalled;
import net.mehvahdjukaar.moonlight.api.platform.ClientPlatformHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class VSCClient {

    public static void init() {
        ClientPlatformHelper.addBlockColorsRegistration(VSCClient::registerBlockColors);

    }

    public static void setup() {
    }

    @EventCalled
    private static void registerBlockColors(ClientPlatformHelper.BlockColorEvent event) {
        VSC.VERTICAL_SLABS.values().forEach(b -> event.register(new MimicBlockColor(), b));
    }

    public static class MimicBlockColor implements BlockColor {

        @Override
        public int getColor(BlockState state, @Nullable BlockAndTintGetter world, @Nullable BlockPos pos, int tint) {
            return col(state, world, pos, tint);
        }

        public static int col(BlockState state, BlockAndTintGetter level, BlockPos pos, int tint) {
            if (level != null && pos != null) {
                BlockState mimic = ((CompatVerticalSlab) state.getBlock()).getBlockType().slab.defaultBlockState();
                return Minecraft.getInstance().getBlockColors().getColor(mimic, level, pos, tint);
            }
            return -1;
        }
    }

}
