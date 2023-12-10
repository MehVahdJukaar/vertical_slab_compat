package net.mehvahdjukaar.vsc.dynamicpack;

import com.google.gson.JsonElement;
import net.mehvahdjukaar.moonlight.api.events.AfterLanguageLoadEvent;
import net.mehvahdjukaar.moonlight.api.resources.RPUtils;
import net.mehvahdjukaar.moonlight.api.resources.ResType;
import net.mehvahdjukaar.moonlight.api.resources.StaticResource;
import net.mehvahdjukaar.moonlight.api.resources.assets.LangBuilder;
import net.mehvahdjukaar.moonlight.api.resources.pack.DynClientResourcesGenerator;
import net.mehvahdjukaar.moonlight.api.resources.pack.DynamicTexturePack;
import net.mehvahdjukaar.moonlight.api.util.Utils;
import net.mehvahdjukaar.vsc.VSC;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.block.Block;
import org.apache.logging.log4j.Logger;

import java.io.FileNotFoundException;


public class ClientDynamicResourcesHandler extends DynClientResourcesGenerator {

    public static final ClientDynamicResourcesHandler INSTANCE = new ClientDynamicResourcesHandler();

    public ClientDynamicResourcesHandler() {
        super(new DynamicTexturePack(VSC.res("generated_pack")));
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
        var blockState = StaticResource.getOrFail(manager, ResType.BLOCKSTATES.getPath(VSC.res("vertical_slab_template")));
        var blockModel = StaticResource.getOrFail(manager, ResType.BLOCK_MODELS.getPath(VSC.res("vertical_slab_template")));
        var itemModel = StaticResource.getOrFail(manager, ResType.ITEM_MODELS.getPath(VSC.res("vertical_slab_template")));
        for (var e : VSC.VERTICAL_SLABS.entrySet()) {
            try {
                var type = e.getKey();
                var texture = RPUtils.findFirstBlockTextureLocation(manager, type.slab);
                var blockModelLocation = findFirstBlockModel(manager, type.base);
                ResourceLocation id = Utils.getID(e.getValue());
                String modelId = new ResourceLocation(id.getNamespace(), "block/" + id.getPath()).toString();

                this.addSimilarJsonResource(manager, blockModel,
                        text -> text.replace("$texture", texture.toString()),
                        name -> name.replace("vertical_slab_template", id.getPath()));
                this.addSimilarJsonResource(manager, blockState,
                        text -> {
                            text = text.replace("$v_slab", modelId);
                            text = text.replace("$block", blockModelLocation.toString());
                            return text;
                        },
                        name -> name.replace("vertical_slab_template", id.getPath()));
                this.addSimilarJsonResource(manager, itemModel,
                        text -> text.replace("$v_slab", modelId),
                        name -> name.replace("vertical_slab_template", id.getPath()));

            } catch (Exception ex) {
                VSC.LOGGER.error("Failed to generate assets for {}", e.getValue(), ex);
            }
        }

    }


    public static ResourceLocation findFirstBlockModel(ResourceManager manager, Block block) throws FileNotFoundException {

        ResourceLocation res = Utils.getID(block);
        var blockState = manager.getResource(ResType.BLOCKSTATES.getPath(res));
        try (var bsStream = blockState.get().open()) {
            JsonElement bsElement = RPUtils.deserializeJson(bsStream);

            //grabs the first resource location of a model
            return new ResourceLocation(RPUtils.findAllResourcesInJsonRecursive(bsElement.getAsJsonObject(), s -> s.equals("model"))
                    .stream().findAny().get());

        } catch (Exception ignored) {
        }
        throw new FileNotFoundException("Could not fine any model for block " + block);
    }

    @Override
    public void addDynamicTranslations(AfterLanguageLoadEvent lang) {
        VSC.VERTICAL_SLABS.forEach((w, b) -> {
            LangBuilder.addDynamicEntry(lang, "block_type.v_slab_compat.vertical_slab", w, b);

        });
    }

}
