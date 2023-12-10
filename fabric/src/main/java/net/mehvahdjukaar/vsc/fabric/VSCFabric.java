package net.mehvahdjukaar.vsc.fabric;

import net.fabricmc.api.ModInitializer;
import net.mehvahdjukaar.vsc.VSC;
import net.mehvahdjukaar.vsc.VSCClient;

public class VSCFabric implements ModInitializer {

    @Override
    public void onInitialize() {

        VSC.commonInit();

    }
}
