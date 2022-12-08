package net.mehvahdjukaar.vsc.forge;

import net.mehvahdjukaar.moonlight.api.platform.PlatformHelper;
import net.mehvahdjukaar.vsc.VSC;
import net.mehvahdjukaar.vsc.VSCClient;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

/**
 * Author: MehVahdJukaar
 */
@Mod(VSC.MOD_ID)
public class VSCForge {

    public VSCForge() {
        VSC.commonInit();

        if (PlatformHelper.getEnv().isClient()) {
            VSCClient.init();
        }
        MinecraftForge.EVENT_BUS.register(this);
    }
    
}

