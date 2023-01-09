package net.mehvahdjukaar.vsc;

import com.google.common.base.Stopwatch;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.mehvahdjukaar.moonlight.api.events.AfterLanguageLoadEvent;
import net.mehvahdjukaar.moonlight.api.set.BlockTypeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlabBlock;
import org.jetbrains.annotations.Contract;

import java.util.Optional;

public class CutBlockTypeRegistry extends BlockTypeRegistry<CutBlockType> {
    public static final CutBlockType STONE_TYPE = new CutBlockType(new ResourceLocation("stone"), Blocks.STONE, Blocks.STONE_SLAB);

    protected CutBlockTypeRegistry(String name) {
        super(CutBlockType.class, name);
    }

    @Override
    public CutBlockType getDefaultType() {
        return STONE_TYPE;
    }

    @Override
    public Optional<CutBlockType> detectTypeFromBlock(Block block, ResourceLocation baseRes) {
        String name = null;
        String path = baseRes.getPath();
        if (path.endsWith("_slab") && !path.endsWith("_vertical_slab")) {
            name = path.substring(0, path.length() - "_slab".length());
        } else if (path.startsWith("slab_")) {
            name = path.substring("slab_".length());
        }
        String namespace = baseRes.getNamespace();
        if (name != null && block instanceof SlabBlock && !namespace.equals("securitycraft")) {
            ResourceLocation id = new ResourceLocation(namespace, name);
            var o = Registry.BLOCK.getOptional(id);
            if (o.isEmpty())
                o = Registry.BLOCK.getOptional(new ResourceLocation(namespace, name + "s"));
            if (o.isEmpty())
                o = Registry.BLOCK.getOptional(new ResourceLocation(namespace, name + "_planks"));
            if (o.isEmpty()) o = Registry.BLOCK.getOptional(new ResourceLocation(name));
            if (o.isPresent() && hasRightShapeHack(block)) {
                return Optional.of(new CutBlockType(id, o.get(), block));
            }
        }
        return Optional.empty();
    }

    @Override
    public void buildAll() {
        Stopwatch watch = Stopwatch.createStarted();
        super.buildAll();
        VSC.LOGGER.info("Initialized slab sets in: {} ms", watch.elapsed().toMillis());

    }

    @Contract
    @ExpectPlatform
    public static boolean hasRightShapeHack(Block block) {
        throw new AssertionError();
    }

    @Override
    public void addTypeTranslations(AfterLanguageLoadEvent language) {
        this.getValues().forEach((w) -> {
            if (language.isDefault()) language.addEntry(w.getTranslationKey(), w.getReadableName());
        });
    }
}
