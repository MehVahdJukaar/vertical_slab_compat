package net.mehvahdjukaar.vsc.fabric;

import net.fabricmc.api.ModInitializer;
import net.mehvahdjukaar.vsc.VSC;
import net.mehvahdjukaar.vsc.VSCClient;
import net.mehvahdjukaar.moonlight.api.platform.PlatformHelper;
import net.mehvahdjukaar.moonlight.fabric.FabricSetupCallbacks;

public class VSCFabric implements ModInitializer {

    @Override
    public void onInitialize() {

        VSC.commonInit();

        if (PlatformHelper.getEnv().isClient()) {
            FabricSetupCallbacks.CLIENT_SETUP.add(VSCClient::init);
        }

    }
}
