package net.mehvahdjukaar.vsc;

import net.mehvahdjukaar.moonlight.api.misc.EventCalled;
import net.mehvahdjukaar.moonlight.api.platform.ClientPlatformHelper;

public class VSCClient {

    public static void init() {
        ClientPlatformHelper.addBlockColorsRegistration(VSCClient::registerBlockColors);

    }

    public static void setup() {
    }

    @EventCalled
    private static void registerBlockColors(ClientPlatformHelper.BlockColorEvent event) {
    }

}
