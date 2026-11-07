package net.mehvahdjukaar.vsc;

import net.mehvahdjukaar.moonlight.api.set.BlockSetAPI;
import net.mehvahdjukaar.moonlight.api.set.BlockType;
import net.mehvahdjukaar.moonlight.api.set.wood.WoodType;
import net.mehvahdjukaar.moonlight.api.util.Utils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CutBlockType extends BlockType {

    public final Block base;
    public final Block slab;
    private WoodType woodType;

    public CutBlockType(Identifier id, Block base, Block slab) {
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
    protected void initializeChildrenBlocks() {
        this.addChild("base", base);
        this.addChild("slab", slab);
        for (var res : getVerticalSlabCandidates()) {
            var o = BuiltInRegistries.BLOCK.getOptional(res);
            if (o.isPresent()) {
                this.addChild("vertical_slab", o.get());
                break;
            }
        }

    }

    private List<Identifier> getVerticalSlabCandidates() {
        String name = this.getTypeName();
        String namespace = this.getNamespace();
        List<Identifier> list = new ArrayList<>();
        list.add(Identifier.fromNamespaceAndPath(namespace, name + "_vertical_slab"));
        list.add(Identifier.fromNamespaceAndPath(namespace, name + "_slab_vert"));

        boolean clashesWithVanilla = !namespace.equals("minecraft") &&
                BuiltInRegistries.BLOCK.containsKey(Identifier.withDefaultNamespace(name + "_slab"));
        if (clashesWithVanilla) return list;
        for (var mod : VSC.VERTICAL_SLABS_MODS) {
            list.add(Identifier.fromNamespaceAndPath(mod, name + "_vertical_slab"));
        }
        if (VSC.EVERYCOMP) {
            list.add(Identifier.fromNamespaceAndPath("everycomp", "q/" + namespace + "/" + name + "_vertical_slab"));
        }
        Identifier slabId = Utils.getID(slab);
        if (slabId.getNamespace().equals("minecraft")) {
            list.add(Identifier.fromNamespaceAndPath("enchanted-vertical-slabs", "vertical_" + slabId.getPath()));
        }
        //we treat BA ones as vertical slabs enve tho they are called panels. low priprity
        list.add(Identifier.fromNamespaceAndPath("buildersaddition2", name + "_panel"));
        return list;
    }

    @Nullable
    private WoodType getEarlyWoodType() {
        return BlockSetAPI.getBlockTypeOf(base, WoodType.class);
    }

    @Override
    protected void initializeChildrenItems() {
        this.woodType = getEarlyWoodType();
        if (woodType != null) {
            woodType.addChild("quark:vertical_slab", this.getChild("vertical_slab"));
        }
    }

}
