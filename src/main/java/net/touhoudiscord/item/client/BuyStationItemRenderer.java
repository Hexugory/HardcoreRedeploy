package net.touhoudiscord.item.client;

import net.touhoudiscord.item.BuyStationItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class BuyStationItemRenderer extends GeoItemRenderer<BuyStationItem> {
    public BuyStationItemRenderer() {
        super(new BuyStationItemModel());
    }
}
