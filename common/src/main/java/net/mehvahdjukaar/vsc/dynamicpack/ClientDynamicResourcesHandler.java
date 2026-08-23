package net.mehvahdjukaar.vsc.dynamicpack;

import com.google.gson.JsonElement;
import net.mehvahdjukaar.moonlight.api.events.AfterLanguageLoadEvent;
import net.mehvahdjukaar.moonlight.api.resources.RPUtils;
import net.mehvahdjukaar.moonlight.api.resources.ResType;
import net.mehvahdjukaar.moonlight.api.resources.StaticResource;
import net.mehvahdjukaar.moonlight.api.resources.assets.LangBuilder;
import net.mehvahdjukaar.moonlight.api.resources.pack.DynamicClientResourceProvider;
import net.mehvahdjukaar.moonlight.api.resources.pack.PackGenerationStrategy;
import net.mehvahdjukaar.moonlight.api.resources.pack.ResourceGenTask;
import net.mehvahdjukaar.moonlight.api.util.Utils;
import net.mehvahdjukaar.vsc.VSC;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.block.Block;

import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;


public class ClientDynamicResourcesHandler extends DynamicClientResourceProvider {

    public static final ClientDynamicResourcesHandler INSTANCE = new ClientDynamicResourcesHandler();

    public ClientDynamicResourcesHandler() {
        super(VSC.res("generated_pack"), PackGenerationStrategy.REGEN_ON_EVERY_RELOAD);
    }

    @Override
    protected Collection<String> gatherSupportedNamespaces() {
        return List.of("minecraft");
    }

    @Override
    protected void regenerateDynamicAssets(Consumer<ResourceGenTask> executor) {
        executor.accept((manager, sink) -> {
            var blockState = StaticResource.getOrFail(manager, ResType.BLOCKSTATES.getPath(VSC.res("vertical_slab_template")));
            var blockModel = StaticResource.getOrFail(manager, ResType.BLOCK_MODELS.getPath(VSC.res("vertical_slab_template")));
            var itemModel = StaticResource.getOrFail(manager, ResType.ITEM_MODELS.getPath(VSC.res("vertical_slab_template")));
            for (var e : VSC.VERTICAL_SLABS.entrySet()) {
                try {
                    var type = e.getKey();
                    var texture = RPUtils.findFirstBlockTextureLocation(manager, type.slab);
                    var blockModelLocation = findFirstBlockModel(manager, type.base);
                    ResourceLocation id = Utils.getID(e.getValue());
                    String modelId = ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "block/" + id.getPath()).toString();

                    sink.addSimilarJsonResource(manager, blockModel,
                            text -> text.replace("$texture", texture.toString()),
                            name -> name.replace("vertical_slab_template", id.getPath()));
                    sink.addSimilarJsonResource(manager, blockState,
                            text -> {
                                text = text.replace("$v_slab", modelId);
                                text = text.replace("$block", blockModelLocation.toString());
                                return text;
                            },
                            name -> name.replace("vertical_slab_template", id.getPath()));
                    sink.addSimilarJsonResource(manager, itemModel,
                            text -> text.replace("$v_slab", modelId),
                            name -> name.replace("vertical_slab_template", id.getPath()));

                } catch (Exception ex) {
                    VSC.LOGGER.error("Failed to generate assets for {}", e.getValue());
                }
            }
        });
    }


    public static ResourceLocation findFirstBlockModel(ResourceManager manager, Block block) throws FileNotFoundException {

        ResourceLocation res = Utils.getID(block);
        var blockState = manager.getResource(ResType.BLOCKSTATES.getPath(res));
        try (var bsStream = blockState.get().open()) {
            JsonElement bsElement = RPUtils.deserializeJson(bsStream);

            //grabs the first resource location of a model
            return ResourceLocation.parse(RPUtils.findAllResourcesInJsonRecursive(bsElement.getAsJsonObject(), s -> s.equals("model"))
                    .stream().findAny().get());

        } catch (Exception ignored) {
        }
        throw new FileNotFoundException("Could not fine any model for block " + block);
    }

    @Override
    protected void addDynamicTranslations(AfterLanguageLoadEvent lang) {
        VSC.VERTICAL_SLABS.forEach((w, b) ->
                LangBuilder.addDynamicEntry(lang, "block_type.v_slab_compat.vertical_slab", w, b));
    }

}
