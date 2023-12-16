package net.mehvahdjukaar.vsc;

import net.mehvahdjukaar.moonlight.api.platform.ForgeHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.violetmoon.quark.content.building.block.VerticalSlabBlock;

import java.util.List;

public class CompatVerticalSlab extends VerticalSlabBlock   {
    private final CutBlockType blockType;
    private final BlockState mimic;

    public CompatVerticalSlab(Properties properties, CutBlockType type) {
        super(() -> type.base, properties);
        this.blockType = type;
        this.mimic = blockType.slab.defaultBlockState();
    }

    public CutBlockType getBlockType() {
        return blockType;
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        if (builder.getOptionalParameter(LootContextParams.THIS_ENTITY) instanceof ServerPlayer player) {
            if (ForgeHelper.canHarvestBlock(mimic, builder.getLevel(), BlockPos.containing(builder.getParameter(LootContextParams.ORIGIN)), player)) {
                return super.getDrops(state, builder);
            }
        }
        return List.of();
    }



    @Override
    public float getDestroyProgress(BlockState state, Player player, BlockGetter worldIn, BlockPos pos) {
        return mimic.getDestroyProgress(player, worldIn, pos);
    }

}
