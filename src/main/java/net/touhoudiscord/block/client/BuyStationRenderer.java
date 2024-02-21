package net.touhoudiscord.block.client;

import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.touhoudiscord.block.BuyStationEntity;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class BuyStationRenderer extends GeoBlockRenderer<BuyStationEntity> {
    public BuyStationRenderer(BlockEntityRendererFactory.Context context) {
        super(new BuyStationModel());
    }
}
