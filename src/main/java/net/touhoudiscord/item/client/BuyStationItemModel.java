package net.touhoudiscord.item.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resources.ResourceLocation;
import net.touhoudiscord.HardcoreRedeploy;
import net.touhoudiscord.item.BuyStationItem;
import software.bernie.geckolib.model.GeoModel;

@Environment(EnvType.CLIENT)
public class BuyStationItemModel extends GeoModel<BuyStationItem> {
    @Override
    public ResourceLocation getModelResource(BuyStationItem animatable) {
        return new ResourceLocation(HardcoreRedeploy.MOD_ID, "geo/buy_station.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(BuyStationItem animatable) {
        return new ResourceLocation(HardcoreRedeploy.MOD_ID, "textures/block/buy_station.png");
    }

    @Override
    public ResourceLocation getAnimationResource(BuyStationItem animatable) {
        return new ResourceLocation(HardcoreRedeploy.MOD_ID, "animations/buy_station.animation.json");
    }
}
