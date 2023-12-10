package net.mehvahdjukaar.vsc;

import net.mehvahdjukaar.moonlight.api.misc.EventCalled;
import net.mehvahdjukaar.moonlight.api.platform.ClientHelper;

public class VSCClient {

    public static void init() {
        ClientHelper.addBlockColorsRegistration(VSCClient::registerBlockColors);

    }

    public static void setup() {
    }

    @EventCalled
    private static void registerBlockColors(ClientHelper.BlockColorEvent event) {
    }

}
