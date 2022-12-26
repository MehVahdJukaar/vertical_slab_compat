package net.mehvahdjukaar.vsc.dynamicpack;

import net.mehvahdjukaar.moonlight.api.platform.PlatformHelper;
import net.mehvahdjukaar.moonlight.api.resources.ResType;
import net.mehvahdjukaar.moonlight.api.resources.SimpleTagBuilder;
import net.mehvahdjukaar.moonlight.api.resources.StaticResource;
import net.mehvahdjukaar.moonlight.api.resources.pack.DynServerResourcesProvider;
import net.mehvahdjukaar.moonlight.api.resources.pack.DynamicDataPack;
import net.mehvahdjukaar.moonlight.api.util.Utils;
import net.mehvahdjukaar.vsc.CutBlockType;
import net.mehvahdjukaar.vsc.VSC;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.Item;
import org.apache.logging.log4j.Logger;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;

public class ServerDynamicResourcesHandler extends DynServerResourcesProvider {

    public static final ServerDynamicResourcesHandler INSTANCE = new ServerDynamicResourcesHandler();

    private final Set<String> recipeLocations;

    public ServerDynamicResourcesHandler() {
        super(new DynamicDataPack(VSC.res("generated_pack")));
        //needed for tags
        getPack().addNamespaces("minecraft");
        getPack().addNamespaces("forge");
        getPack().addNamespaces("quark");
        this.dynamicPack.generateDebugResources = PlatformHelper.isDev();

        this.recipeLocations = Set.of("recipe", "recipe_2", "recipe_stonecutter");
    }

    @Override
    public Logger getLogger() {
        return VSC.LOGGER;
    }

    @Override
    public boolean dependsOnLoadedPacks() {
        return true;
    }

    @Override
    public void regenerateDynamicAssets(ResourceManager manager) {
        this.recipeLocations.forEach(res -> {
            try {
                addBlocksRecipes(manager, ResType.GENERIC.getPath(VSC.res("template/" + res + ".json")));
            } catch (Exception e) {
                VSC.LOGGER.error("Failed to generate recipes for template at location {} ", res);
            }
        });
        addTags();

        addBlocksLootTable(manager, ResType.GENERIC.getPath(VSC.res("template/loot_table.json")));
    }

    @Override
    public void generateStaticAssetsOnStartup(ResourceManager manager) {
        addTags();
    }

    private void addTags() {
        SimpleTagBuilder tag = SimpleTagBuilder.of(VSC.res("vertical_slabs"));
        tag.addEntries(VSC.VERTICAL_SLABS_ITEMS.values());
        dynamicPack.addTag(tag, Registry.BLOCK_REGISTRY);
        dynamicPack.addTag(tag, Registry.ITEM_REGISTRY);
        SimpleTagBuilder quarkTag = SimpleTagBuilder.of(new ResourceLocation("quark:vertical_slabs"));
        SimpleTagBuilder quarkWoodenTag = SimpleTagBuilder.of(new ResourceLocation("quark:wooden_vertical_slabs"));
        quarkTag.addTag(tag);
        quarkWoodenTag.addEntries(VSC.VERTICAL_SLABS_ITEMS.entrySet().stream()
                .filter(t -> t.getKey().getWoodType() != null).map(Map.Entry::getValue).toList());
        dynamicPack.addTag(quarkTag, Registry.BLOCK_REGISTRY);
        dynamicPack.addTag(quarkTag, Registry.ITEM_REGISTRY);
        dynamicPack.addTag(quarkWoodenTag, Registry.BLOCK_REGISTRY);
        dynamicPack.addTag(quarkWoodenTag, Registry.ITEM_REGISTRY);
    }

    private void addBlocksLootTable(ResourceManager manager, ResourceLocation templateLootTable) {
        var template = StaticResource.getOrFail(manager, templateLootTable);

        VSC.VERTICAL_SLABS.forEach((w, i) -> {
            String fullText = new String(template.data, StandardCharsets.UTF_8);

            fullText = fullText.replace("$v_slab", Utils.getID(i).toString());

            String id = template.location.toString();
            id = id.replace("template/loot_table", "loot_tables/" + i.getLootTable().getPath());
            this.dynamicPack.addResource(StaticResource.create(fullText.getBytes(), new ResourceLocation(id)));
        });
    }

    private void addBlocksRecipes(ResourceManager manager, ResourceLocation templateRecipe) {
        var template = StaticResource.getOrFail(manager, templateRecipe);

        VSC.VERTICAL_SLABS_ITEMS.forEach((w, i) -> {
            if (isSlabEnabled(w, i)) {
                if (templateRecipe.getPath().contains("stone") && w.getWoodType() != null) return;
                String fullText = new String(template.data, StandardCharsets.UTF_8);

                fullText = fullText.replace("$slab", Utils.getID(w.slab).toString());
                fullText = fullText.replace("$v_slab", Utils.getID(w.getChild("vertical_slab")).toString());
                fullText = fullText.replace("$block", Utils.getID(w.base).toString());

                String id = template.location.toString();
                id = id.replace("template/recipe", "recipes/" + w.getAppendableId());
                this.dynamicPack.addResource(StaticResource.create(fullText.getBytes(), new ResourceLocation(id)));
            }
        });
    }

    private boolean isSlabEnabled(CutBlockType w, Item i) {
        return i.getItemCategory() != null;
    }


}
