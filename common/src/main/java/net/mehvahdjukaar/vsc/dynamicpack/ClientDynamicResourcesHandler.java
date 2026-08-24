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
import java.nio.charset.StandardCharsets;
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
        //everything we generate lands under our own namespace, which is added for us
        return List.of();
    }

    @Override
    protected void regenerateDynamicAssets(Consumer<ResourceGenTask> executor) {
        executor.accept((manager, sink) -> {
            var blockState = StaticResource.getOrThrow(manager, ResType.GENERIC.getPath(VSC.res("template/blockstate.json")));
            var blockModel = StaticResource.getOrThrow(manager, ResType.GENERIC.getPath(VSC.res("template/block_model.json")));
            var itemModel = StaticResource.getOrThrow(manager, ResType.GENERIC.getPath(VSC.res("template/item_model.json")));
            for (var e : VSC.VERTICAL_SLABS.entrySet()) {
                try {
                    var type = e.getKey();
                    var texture = RPUtils.findFirstBlockTextureLocation(manager, type.slab);
                    var blockModelLocation = findFirstBlockModel(manager, type.base);
                    ResourceLocation id = Utils.getID(e.getValue());
                    String modelId = ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "block/" + id.getPath()).toString();

                    sink.addBytes(id, blockModel.asString()
                            .replace("$texture", texture.toString())
                            .getBytes(StandardCharsets.UTF_8), ResType.BLOCK_MODELS);

                    sink.addBytes(id, blockState.asString()
                            .replace("$v_slab", modelId)
                            .replace("$block", blockModelLocation.toString())
                            .getBytes(StandardCharsets.UTF_8), ResType.BLOCKSTATES);

                    sink.addBytes(id, itemModel.asString()
                            .replace("$v_slab", modelId)
                            .getBytes(StandardCharsets.UTF_8), ResType.ITEM_MODELS);

                } catch (Exception ex) {
                    VSC.LOGGER.error("Failed to generate assets for {}", e.getValue(), ex);
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
