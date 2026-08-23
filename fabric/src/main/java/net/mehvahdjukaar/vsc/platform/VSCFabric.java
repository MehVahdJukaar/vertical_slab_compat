package net.mehvahdjukaar.vsc.platform;

import net.fabricmc.api.ModInitializer;
import net.mehvahdjukaar.vsc.VSC;

public class VSCFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        VSC.commonInit();
    }
}
