package net.touhoudiscord.item.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.touhoudiscord.item.BuyStationItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;

@Environment(EnvType.CLIENT)
public class BuyStationItemRenderer extends GeoItemRenderer<BuyStationItem> {
    public BuyStationItemRenderer() {
        super(new BuyStationItemModel());
    }
}
