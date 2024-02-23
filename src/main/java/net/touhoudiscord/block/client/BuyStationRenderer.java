package net.touhoudiscord.block.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import net.touhoudiscord.block.BuyStationEntity;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

@Environment(EnvType.CLIENT)
public class BuyStationRenderer extends GeoBlockRenderer<BuyStationEntity> {
    public BuyStationRenderer(BlockEntityRendererFactory.Context context) {
        super(new BuyStationModel());
    }

    @Override
    public void preRender(MatrixStack poseStack, BuyStationEntity animatable, BakedGeoModel model, VertexConsumerProvider bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        Direction direction = animatable.getCachedState().get(HorizontalFacingBlock.FACING).rotateYClockwise();
        poseStack.translate(direction.getOffsetX()/2., 0, direction.getOffsetZ()/2.);
        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
