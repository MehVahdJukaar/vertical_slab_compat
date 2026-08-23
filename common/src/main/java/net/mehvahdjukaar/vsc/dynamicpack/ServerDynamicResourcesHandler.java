package net.mehvahdjukaar.vsc.dynamicpack;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.mehvahdjukaar.moonlight.api.resources.RPUtils;
import net.mehvahdjukaar.moonlight.api.resources.ResType;
import net.mehvahdjukaar.moonlight.api.resources.SimpleTagBuilder;
import net.mehvahdjukaar.moonlight.api.resources.StaticResource;
import net.mehvahdjukaar.moonlight.api.resources.pack.DynamicServerResourceProvider;
import net.mehvahdjukaar.moonlight.api.resources.pack.PackGenerationStrategy;
import net.mehvahdjukaar.moonlight.api.resources.pack.ResourceGenTask;
import net.mehvahdjukaar.moonlight.api.resources.pack.ResourceSink;
import net.mehvahdjukaar.moonlight.api.util.Utils;
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
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class ServerDynamicResourcesHandler extends DynamicServerResourceProvider {

    public static final ServerDynamicResourcesHandler INSTANCE = new ServerDynamicResourcesHandler();

    private static final List<String> RECIPE_TEMPLATES = List.of("recipe", "recipe_2", "recipe_stonecutter");

    public ServerDynamicResourcesHandler() {
        super(VSC.res("generated_pack"), PackGenerationStrategy.REGEN_ON_EVERY_RELOAD);
    }

    @Override
    protected Collection<String> gatherSupportedNamespaces() {
        //minecraft and quark for the tags we copy into, neoforge for the furnace fuel data map
        return List.of("minecraft", "quark", "neoforge");
    }

    @Override
    protected void regenerateDynamicAssets(Consumer<ResourceGenTask> executor) {
        executor.accept((manager, sink) -> {
            for (var res : RECIPE_TEMPLATES) {
                try {
                    addBlocksRecipes(manager, sink, res);
                } catch (Exception e) {
                    VSC.LOGGER.error("Failed to generate recipes for template at location {} ", res);
                }
            }
        });

        executor.accept(this::addBlocksLootTable);
        executor.accept(this::addTags);
        executor.accept((manager, sink) -> addFurnaceFuels(sink));
    }

    private void addTags(ResourceManager manager, ResourceSink sink) {
        SimpleTagBuilder tag = SimpleTagBuilder.of(VSC.res("vertical_slabs"));
        tag.addEntries(VSC.VERTICAL_SLABS_ITEMS.values());
        sink.addTag(tag, Registries.BLOCK);
        sink.addTag(tag, Registries.ITEM);
        SimpleTagBuilder quarkTag = SimpleTagBuilder.of(ResourceLocation.parse("quark:vertical_slabs"));
        SimpleTagBuilder quarkWoodenTag = SimpleTagBuilder.of(ResourceLocation.parse("quark:wooden_vertical_slabs"));
        quarkTag.addTag(tag);
        quarkWoodenTag.addEntries(woodenSlabItems());
        sink.addTag(quarkTag, Registries.BLOCK);
        sink.addTag(quarkTag, Registries.ITEM);
        sink.addTag(quarkWoodenTag, Registries.BLOCK);
        sink.addTag(quarkWoodenTag, Registries.ITEM);

        copyTags(manager, sink, BlockTags.NEEDS_STONE_TOOL, Registries.BLOCK);
        copyTags(manager, sink, BlockTags.NEEDS_IRON_TOOL, Registries.BLOCK);
        copyTags(manager, sink, BlockTags.NEEDS_DIAMOND_TOOL, Registries.BLOCK);
        copyTags(manager, sink, BlockTags.MINEABLE_WITH_AXE, Registries.BLOCK);
        copyTags(manager, sink, BlockTags.MINEABLE_WITH_HOE, Registries.BLOCK);
        copyTags(manager, sink, BlockTags.MINEABLE_WITH_PICKAXE, Registries.BLOCK);
        copyTags(manager, sink, BlockTags.MINEABLE_WITH_SHOVEL, Registries.BLOCK);
        copyTags(manager, sink, BlockTags.DRAGON_IMMUNE, Registries.BLOCK);
        copyTags(manager, sink, BlockTags.DAMPENS_VIBRATIONS, Registries.BLOCK);
        copyTags(manager, sink, BlockTags.GUARDED_BY_PIGLINS, Registries.BLOCK);
        copyTags(manager, sink, ItemTags.PIGLIN_LOVED, Registries.ITEM);
    }

    private <T> void copyTags(ResourceManager manager, ResourceSink sink, TagKey<T> tagKey, ResourceKey<Registry<T>> registry) {
        Set<String> tagValues = getTags(manager, tagKey);

        SimpleTagBuilder builder = SimpleTagBuilder.of(tagKey);
        for (var e : VSC.VERTICAL_SLABS_ITEMS.entrySet()) {
            ResourceLocation id = BuiltInRegistries.BLOCK.getKey(e.getKey().slab);
            if (tagValues.contains(id.toString())) {
                builder.addEntry(e.getValue());
            }
        }
        if (!builder.build().isEmpty()) {
            sink.addTag(builder, registry);
        }
    }

    @NotNull
    private static <T> Set<String> getTags(ResourceManager manager, TagKey<T> tagKey) {
        var resources = manager.getResourceStack(ResType.getTagPath(tagKey));
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
                var res = ResourceLocation.parse(s.substring(1));
                if (res.getPath().contains("slab")) {
                    TagKey<T> newKey = TagKey.create(tagKey.registry(), res);
                    actualTags.addAll(getTags(manager, newKey));
                }
            } else actualTags.add(s);
        }
        return actualTags;
    }

    //neoforge only. on fabric burn times are registered in code instead
    private void addFurnaceFuels(ResourceSink sink) {
        JsonObject values = new JsonObject();
        for (var i : woodenSlabItems()) {
            JsonObject entry = new JsonObject();
            entry.addProperty("burn_time", VSC.WOOD_BURN_TIME);
            values.add(Utils.getID(i).toString(), entry);
        }
        if (values.size() == 0) return;
        JsonObject json = new JsonObject();
        json.add("values", values);
        sink.addJson(ResourceLocation.fromNamespaceAndPath("neoforge", "data_maps/item/furnace_fuels"),
                json, ResType.JSON);
    }

    private void addBlocksLootTable(ResourceManager manager, ResourceSink sink) {
        var template = StaticResource.getOrThrow(manager, ResType.GENERIC.getPath(VSC.res("template/loot_table.json")));

        VSC.VERTICAL_SLABS.forEach((w, block) -> {
            String fullText = template.asString().replace("$v_slab", Utils.getID(block).toString());
            sink.addBytes(block.getLootTable().location(), fullText.getBytes(StandardCharsets.UTF_8), ResType.LOOT_TABLES);
        });
    }

    private void addBlocksRecipes(ResourceManager manager, ResourceSink sink, String templateName) {
        var template = StaticResource.getOrThrow(manager, ResType.GENERIC.getPath(VSC.res("template/" + templateName + ".json")));
        String recipeSuffix = templateName.substring("recipe".length());
        boolean isStonecutting = templateName.contains("stone");

        VSC.VERTICAL_SLABS_ITEMS.forEach((w, i) -> {
            if (VSC.BLACKLIST.get().contains(w.getNamespace())) return;
            if (isStonecutting && w.getWoodType() != null) return;

            String fullText = template.asString();
            fullText = fullText.replace("$slab", Utils.getID(w.slab).toString());
            fullText = fullText.replace("$v_slab", Utils.getID(w.getBlockOfThis("vertical_slab")).toString());
            fullText = fullText.replace("$block", Utils.getID(w.base).toString());

            sink.addBytes(VSC.res(w.getAppendableId() + recipeSuffix),
                    fullText.getBytes(StandardCharsets.UTF_8), ResType.RECIPES);
        });
    }

    private static Collection<Item> woodenSlabItems() {
        return VSC.VERTICAL_SLABS_ITEMS.entrySet().stream()
                .filter(t -> t.getKey().getWoodType() != null)
                .map(Map.Entry::getValue).toList();
    }

}
