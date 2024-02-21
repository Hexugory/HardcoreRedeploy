package net.touhoudiscord.item.client;

import net.minecraft.util.Identifier;
import net.touhoudiscord.HardcoreRedeploy;
import net.touhoudiscord.item.BuyStationItem;
import software.bernie.geckolib.model.GeoModel;

public class BuyStationItemModel extends GeoModel<BuyStationItem> {
    @Override
    public Identifier getModelResource(BuyStationItem animatable) {
        return new Identifier(HardcoreRedeploy.MOD_ID, "geo/buy_station.geo.json");
    }

    @Override
    public Identifier getTextureResource(BuyStationItem animatable) {
        return new Identifier(HardcoreRedeploy.MOD_ID, "textures/block/buy_station.png");
    }

    @Override
    public Identifier getAnimationResource(BuyStationItem animatable) {
        return new Identifier(HardcoreRedeploy.MOD_ID, "animations/buy_station.animation.json");
    }
}
