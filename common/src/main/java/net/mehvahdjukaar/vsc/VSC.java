package net.mehvahdjukaar.vsc;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.mehvahdjukaar.candlelight.api.PlatformImpl;
import net.mehvahdjukaar.moonlight.api.item.WoodBasedBlockItem;
import net.mehvahdjukaar.moonlight.api.misc.Registrator;
import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;
import net.mehvahdjukaar.moonlight.api.platform.RegHelper;
import net.mehvahdjukaar.moonlight.api.platform.configs.ConfigBuilder;
import net.mehvahdjukaar.moonlight.api.platform.configs.ConfigType;
import net.mehvahdjukaar.moonlight.api.set.BlockSetAPI;
import net.mehvahdjukaar.moonlight.api.util.Utils;
import net.mehvahdjukaar.vsc.dynamicpack.ServerDynamicResourcesHandler;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Author: MehVahdJukaar
 */
public class VSC {

    public static final String MOD_ID = "v_slab_compat";
    public static final Logger LOGGER = LogManager.getLogger();
    public static final boolean QUARK = PlatHelper.isModLoaded("quark");

    //same value quark uses for its own wooden vertical slabs
    public static final int WOOD_BURN_TIME = 150;

    public static ResourceLocation res(String name) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, name);
    }

    public static final List<String> VERTICAL_SLABS_MODS = Stream.of("quark", "buildersaddition", "compatoplenty", "everycomp")
            .filter(PlatHelper::isModLoaded).toList();


    public static final Map<CutBlockType, Block> VERTICAL_SLABS = new Object2ObjectOpenHashMap<>();
    public static final Map<CutBlockType, Item> VERTICAL_SLABS_ITEMS = new Object2ObjectOpenHashMap<>();

    public static Supplier<List<String>> BLACKLIST;

    public static void commonInit() {
        ConfigBuilder c = ConfigBuilder.create(MOD_ID, ConfigType.COMMON);
        c.push("general");
        BLACKLIST = c.comment("mod ids blacklist")
                .define("blacklist", List.of("securitycraft"), o -> o instanceof String);
        c.pop();
        c.build();

        if (PlatHelper.getPhysicalSide().isClient()) {
            VSCClient.init();
        }
        BlockSetAPI.registerBlockSetDefinition(new CutBlockTypeRegistry("cut_block_type"));

        BlockSetAPI.addDynamicRegistration(MOD_ID, VSC::registerVerticalSlabs, BuiltInRegistries.BLOCK);
        BlockSetAPI.addDynamicRegistration(MOD_ID, VSC::registerItems, BuiltInRegistries.ITEM);

        RegHelper.registerDynamicResourceProvider(ServerDynamicResourcesHandler.INSTANCE);

        RegHelper.addItemsToTabsRegistration(VSC::addItemsToTabs);
    }

    private static void registerItems(Registrator<Item> itemRegistrator) {
        for (var v : VERTICAL_SLABS.entrySet()) {
            var type = v.getKey();
            var block = v.getValue();
            Item i;
            var prop = new Item.Properties();
            if (type.getWoodType() != null) {
                i = new WoodBasedBlockItem(block, prop, type.getWoodType());
                //only does anything on fabric. neoforge reads the furnace fuel data map the server pack writes
                RegHelper.registerItemBurnTime(i, WOOD_BURN_TIME);
            } else {
                i = new BlockItem(block, prop);
            }
            itemRegistrator.register(Utils.getID(block), i);
            VERTICAL_SLABS_ITEMS.put(type, i);
        }
    }

    private static void registerVerticalSlabs(Registrator<Block> blockRegistrator) {
        for (var type : BlockSetAPI.getBlockSet(CutBlockType.class).getValues()) {
            if (type.getChild("vertical_slab") != null) continue;
            String name = type.getTypeName() + "_vertical_slab";
            ResourceLocation newId = res(type.getNamespace().equals("minecraft") ? name : type.getNamespace() + "/" + name);

            Block block = createVSlab(type);
            blockRegistrator.register(newId, block);
            VERTICAL_SLABS.put(type, block);
            type.addChild("vertical_slab", block);
        }
    }

    @PlatformImpl
    public static Block createVSlab(CutBlockType type) {
        throw new AssertionError();
    }

    private static void addItemsToTabs(RegHelper.ItemToTabEvent event) {
        for (var v : VERTICAL_SLABS_ITEMS.entrySet()) {
            String namespace = v.getKey().getNamespace();
            if (BLACKLIST.get().contains(namespace)) continue;
            event.addAfter(CreativeModeTabs.BUILDING_BLOCKS, i -> i.is(v.getKey().slab.asItem()), v.getValue());
        }
    }

}
