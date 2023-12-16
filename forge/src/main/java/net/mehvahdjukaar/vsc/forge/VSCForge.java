package net.mehvahdjukaar.vsc.forge;

import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;
import net.mehvahdjukaar.vsc.VSC;
import net.mehvahdjukaar.vsc.VSCClient;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Author: MehVahdJukaar
 */
@Mod(VSC.MOD_ID)
public class VSCForge {

    public VSCForge() {
        VSC.commonInit();
    }

    public void onTagLoadEvent(TagsUpdatedEvent event){

    }

}

