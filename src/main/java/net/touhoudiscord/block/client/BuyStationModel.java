package net.touhoudiscord.block.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resources.ResourceLocation;
import net.touhoudiscord.HardcoreRedeploy;
import net.touhoudiscord.block.BuyStationEntity;
import software.bernie.geckolib.model.GeoModel;

@Environment(EnvType.CLIENT)
public class BuyStationModel extends GeoModel<BuyStationEntity> {
    @Override
    public ResourceLocation getModelResource(BuyStationEntity animatable) {
        return new ResourceLocation(HardcoreRedeploy.MOD_ID, "geo/buy_station.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(BuyStationEntity animatable) {
        return new ResourceLocation(HardcoreRedeploy.MOD_ID, "textures/block/buy_station.png");
    }

    @Override
    public ResourceLocation getAnimationResource(BuyStationEntity animatable) {
        return new ResourceLocation(HardcoreRedeploy.MOD_ID, "animations/buy_station.animation.json");
    }
}
