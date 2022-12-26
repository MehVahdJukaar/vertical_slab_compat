package net.mehvahdjukaar.vsc;

import net.mehvahdjukaar.moonlight.api.set.BlockSetAPI;
import net.mehvahdjukaar.moonlight.api.set.BlockType;
import net.mehvahdjukaar.moonlight.api.set.wood.WoodType;
import net.mehvahdjukaar.moonlight.api.set.wood.WoodTypeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CutBlockType extends BlockType {

    public final Block base;
    public final Block slab;
    private WoodType woodType;

    public CutBlockType(ResourceLocation id, Block base, Block slab) {
        super(id);
        this.base = base;
        this.slab = slab;
    }

    public WoodType getWoodType() {
        return woodType;
    }

    @Override
    public ItemLike mainChild() {
        return base;
    }

    @Override
    public String getTranslationKey() {
        return "cut_block_type." + this.getNamespace() + "." + this.getTypeName();
    }

    @Override
    public void initializeChildrenBlocks() {
        this.addChild("base", (Object) base);
        this.addChild("slab", (Object) slab);
        List<String> list = new ArrayList<>();
        list.add(this.id.getNamespace());
        list.addAll(VSC.VERTICAL_SLABS_MODS);
        for (var s : list) {
            var o = Registry.BLOCK.getOptional(new ResourceLocation(s, this.getTypeName() + "_vertical_slab"));
            if (o.isPresent()) {
                this.addChild("vertical_slab", (Object) o.get());
                break;
            }
        }
        this.woodType = getEarlyWoodType();
        if(woodType != null){
            woodType.addChild("quark:vertical_slab", this.getChild("vertical_slab"));
        }
    }

    @Nullable
    private WoodType getEarlyWoodType() {
        return BlockSetAPI.getBlockTypeOf(base, WoodType.class);
    }

    @Override
    public void initializeChildrenItems() {

    }

}
