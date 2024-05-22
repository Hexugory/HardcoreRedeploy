package net.touhoudiscord.block.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.touhoudiscord.block.BuyStationEntity;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

@Environment(EnvType.CLIENT)
public class BuyStationRenderer extends GeoBlockRenderer<BuyStationEntity> {
    public BuyStationRenderer(BlockEntityRendererProvider.Context context) {
        super(new BuyStationModel());
    }

    @Override
    public void preRender(PoseStack poseStack, BuyStationEntity animatable, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        Direction direction = animatable.getBlockState().getValue(HorizontalDirectionalBlock.FACING).getClockWise();
        poseStack.translate(direction.getStepX()/2., 0, direction.getStepZ()/2.);
        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
