package net.mehvahdjukaar.vsc.dynamicpack;

import com.google.gson.JsonElement;
import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;
import net.mehvahdjukaar.moonlight.api.resources.RPUtils;
import net.mehvahdjukaar.moonlight.api.resources.ResType;
import net.mehvahdjukaar.moonlight.api.resources.SimpleTagBuilder;
import net.mehvahdjukaar.moonlight.api.resources.StaticResource;
import net.mehvahdjukaar.moonlight.api.resources.pack.DynServerResourcesGenerator;
import net.mehvahdjukaar.moonlight.api.resources.pack.DynamicDataPack;
import net.mehvahdjukaar.moonlight.api.util.Utils;
import net.mehvahdjukaar.vsc.CutBlockType;
import net.mehvahdjukaar.vsc.VSC;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ServerDynamicResourcesHandler extends DynServerResourcesGenerator {

    public static final ServerDynamicResourcesHandler INSTANCE = new ServerDynamicResourcesHandler();

    private final Set<String> recipeLocations;

    public ServerDynamicResourcesHandler() {
        super(new DynamicDataPack(VSC.res("generated_pack")));
        //needed for tags
        getPack().addNamespaces("minecraft");
        getPack().addNamespaces("forge");
        getPack().addNamespaces("quark");
        this.dynamicPack.setGenerateDebugResources(PlatHelper.isDev());

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

        addBlocksLootTable(manager, ResType.GENERIC.getPath(VSC.res("template/loot_table.json")));

        addTags(manager);

    }

    private void addTags(ResourceManager manager) {
        SimpleTagBuilder tag = SimpleTagBuilder.of(VSC.res("vertical_slabs"));
        tag.addEntries(VSC.VERTICAL_SLABS_ITEMS.values());
        dynamicPack.addTag(tag, Registries.BLOCK);
        dynamicPack.addTag(tag, Registries.ITEM);
        SimpleTagBuilder quarkTag = SimpleTagBuilder.of(new ResourceLocation("quark:vertical_slabs"));
        SimpleTagBuilder quarkWoodenTag = SimpleTagBuilder.of(new ResourceLocation("quark:wooden_vertical_slabs"));
        quarkTag.addTag(tag);
        quarkWoodenTag.addEntries(VSC.VERTICAL_SLABS_ITEMS.entrySet().stream()
                .filter(t -> t.getKey().getWoodType() != null).map(Map.Entry::getValue).toList());
        dynamicPack.addTag(quarkTag, Registries.BLOCK);
        dynamicPack.addTag(quarkTag, Registries.ITEM);
        dynamicPack.addTag(quarkWoodenTag, Registries.BLOCK);
        dynamicPack.addTag(quarkWoodenTag, Registries.ITEM);

        copyTags(manager, BlockTags.NEEDS_STONE_TOOL, Registries.BLOCK);
        copyTags(manager, BlockTags.NEEDS_IRON_TOOL, Registries.BLOCK);
        copyTags(manager, BlockTags.NEEDS_DIAMOND_TOOL, Registries.BLOCK);
        copyTags(manager, BlockTags.MINEABLE_WITH_AXE, Registries.BLOCK);
        copyTags(manager, BlockTags.MINEABLE_WITH_HOE, Registries.BLOCK);
        copyTags(manager, BlockTags.MINEABLE_WITH_PICKAXE, Registries.BLOCK);
        copyTags(manager, BlockTags.MINEABLE_WITH_SHOVEL, Registries.BLOCK);
        copyTags(manager, BlockTags.DRAGON_IMMUNE, Registries.BLOCK);
        copyTags(manager, BlockTags.DAMPENS_VIBRATIONS, Registries.BLOCK);
        copyTags(manager, BlockTags.GUARDED_BY_PIGLINS, Registries.BLOCK);
        copyTags(manager, ItemTags.PIGLIN_LOVED, Registries.ITEM);
    }

    private <T> void copyTags(ResourceManager manager, TagKey<T> tagKey, ResourceKey<Registry<T>> registry) {
        Set<String> tagValues = getTags(manager, tagKey);

        SimpleTagBuilder builer = SimpleTagBuilder.of(tagKey);
        for (var e : VSC.VERTICAL_SLABS_ITEMS.entrySet()) {
            ResourceLocation id = BuiltInRegistries.BLOCK.getKey(e.getKey().slab);
            if (tagValues.contains(id.toString())) {
                builer.addEntry(e.getValue());
            }
        }
        var b = builer.build();
        if (!b.isEmpty()) {
            dynamicPack.addTag(builer, registry);
        }
    }

    @NotNull
    private static <T> Set<String> getTags(ResourceManager manager, TagKey<T> tagKey) {
        var resources = manager.getResourceStack(ResType.TAGS.getPath(tagKey.location().withPrefix(tagKey.registry().location().getPath() + "s/")));
        Set<String> tagValues = new HashSet<>();
        Set<String> actualTags = new HashSet<>();
        for (var r : resources) {
            try (var res = r.open()) {
                RPUtils.deserializeJson(res).getAsJsonArray("values")
                        .asList().stream()
                        .filter(JsonElement::isJsonPrimitive).forEach(v -> tagValues.add(v.getAsString()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        for (var s : tagValues) {
            if (s.startsWith("#")) {
                var res = new ResourceLocation(s.substring(1));
                if (res.getPath().contains("slab")) {
                    TagKey<T> newKey = TagKey.create(tagKey.registry(), res);
                    actualTags.addAll(getTags(manager, newKey));
                }
            }else actualTags.add(s);
        }
        return actualTags;
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
        return true;
    }


}
