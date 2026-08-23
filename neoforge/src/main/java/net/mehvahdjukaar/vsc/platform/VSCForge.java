package net.mehvahdjukaar.vsc.platform;

import net.mehvahdjukaar.vsc.VSC;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

/**
 * Author: MehVahdJukaar
 */
@Mod(VSC.MOD_ID)
public class VSCForge {

    public VSCForge(IEventBus bus) {
        VSC.commonInit();
    }

}
