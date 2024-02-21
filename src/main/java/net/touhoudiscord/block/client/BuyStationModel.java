package net.touhoudiscord.block.client;

import net.minecraft.util.Identifier;
import net.touhoudiscord.HardcoreRedeploy;
import net.touhoudiscord.block.BuyStationEntity;
import software.bernie.geckolib.model.GeoModel;

public class BuyStationModel extends GeoModel<BuyStationEntity> {
    @Override
    public Identifier getModelResource(BuyStationEntity animatable) {
        return new Identifier(HardcoreRedeploy.MOD_ID, "geo/buy_station.geo.json");
    }

    @Override
    public Identifier getTextureResource(BuyStationEntity animatable) {
        return new Identifier(HardcoreRedeploy.MOD_ID, "textures/block/buy_station.png");
    }

    @Override
    public Identifier getAnimationResource(BuyStationEntity animatable) {
        return new Identifier(HardcoreRedeploy.MOD_ID, "animations/buy_station.animation.json");
    }
}
