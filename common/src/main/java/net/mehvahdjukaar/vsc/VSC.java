package net.mehvahdjukaar.vsc;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.mehvahdjukaar.moonlight.api.item.WoodBasedBlockItem;
import net.mehvahdjukaar.moonlight.api.misc.Registrator;
import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;
import net.mehvahdjukaar.moonlight.api.platform.RegHelper;
import net.mehvahdjukaar.moonlight.api.set.BlockSetAPI;
import net.mehvahdjukaar.moonlight.api.util.Utils;
import net.mehvahdjukaar.vsc.dynamicpack.ClientDynamicResourcesHandler;
import net.mehvahdjukaar.vsc.dynamicpack.ServerDynamicResourcesHandler;
import net.mehvahdjukaar.vsc.temp.QuarkCompat;
import net.mehvahdjukaar.vsc.temp.TempVerticalSlabBlock;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Author: MehVahdJukaar
 */
public class VSC {

    public static final String MOD_ID = "v_slab_compat";
    public static final Logger LOGGER = LogManager.getLogger();
    public static final boolean QUARK = PlatHelper.isModLoaded("quark");

    public static ResourceLocation res(String name) {
        return new ResourceLocation(MOD_ID, name);
    }

    public static final List<String> VERTICAL_SLABS_MODS = Stream.of("quark", "buildersaddition", "compatoplenty", "everycomp")
            .filter(PlatHelper::isModLoaded).toList();


    public static final Map<CutBlockType, Block> VERTICAL_SLABS = new Object2ObjectOpenHashMap<>();
    public static final Map<CutBlockType, Item> VERTICAL_SLABS_ITEMS = new Object2ObjectOpenHashMap<>();

    public static void commonInit() {
        if (PlatHelper.getPhysicalSide().isClient()) {
            VSCClient.init();
        }
        BlockSetAPI.registerBlockSetDefinition(new CutBlockTypeRegistry("cut_block_type"));

        BlockSetAPI.addDynamicBlockRegistration(VSC::registerVerticalSlab, CutBlockType.class);
        BlockSetAPI.addDynamicRegistration(VSC::registerItems, CutBlockType.class, BuiltInRegistries.ITEM);

        ServerDynamicResourcesHandler.INSTANCE.register();

        if (PlatHelper.getPhysicalSide().isClient()) {
            ClientDynamicResourcesHandler.INSTANCE.register();
        }

        RegHelper.addItemsToTabsRegistration(VSC::addItemsToTabs);
    }

    private static void registerItems(Registrator<Item> itemRegistrator, Collection<CutBlockType> types) {
        for (var v : VERTICAL_SLABS.entrySet()) {
            var type = v.getKey();
            var block = v.getValue();
            Item i;
            var prop = new Item.Properties();
            if (type.getWoodType() != null) {
                i = new WoodBasedBlockItem(block, prop, type.getWoodType(), 150);
            } else {
                i = new BlockItem(block, prop);
            }
            itemRegistrator.register(Utils.getID(v.getValue()), i);
            VERTICAL_SLABS_ITEMS.put(type, i);
        }
    }

    private static void registerVerticalSlab(Registrator<Block> blockRegistrator, Collection<CutBlockType> types) {
        for (var type : types) {
            if (type.getChild("vertical_slab") != null) continue;
            String name = type.getTypeName() + "_vertical_slab";
            ResourceLocation newId = res(type.getNamespace().equals("minecraft") ? name : type.getNamespace() + "/" + name);

            Block block = createVSlab(type);
            blockRegistrator.register(newId, block);
            VERTICAL_SLABS.put(type, block);
            type.addChild("vertical_slab", block);
        }
    }

    @NotNull
    private static Block createVSlab(CutBlockType type) {
        return QUARK ? QuarkCompat.createVSlab(type) : new TempVerticalSlabBlock(Utils.copyPropertySafe(type.base), type);
    }

    private static void addItemsToTabs(RegHelper.ItemToTabEvent event) {
        for (var v : VERTICAL_SLABS_ITEMS.entrySet()) {
            event.addAfter(CreativeModeTabs.BUILDING_BLOCKS, i -> i.is(v.getKey().slab.asItem()), v.getValue());
        }
    }

}
