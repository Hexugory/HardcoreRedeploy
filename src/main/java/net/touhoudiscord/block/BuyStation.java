package net.touhoudiscord.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.touhoudiscord.BuyStationCapable;
import org.jetbrains.annotations.Nullable;

public class BuyStation extends BaseEntityBlock {
    public static final EnumProperty<BuyStationPart> BUY_STATION_PART = EnumProperty.create("part", BuyStationPart.class);

    protected static final VoxelShape NORTH_SHAPE = Block.box(1.0, 0.0, 1.0, 16.0, 12.0, 15.0);
    protected static final VoxelShape SOUTH_SHAPE = Block.box(0.0, 0.0, 1.0, 15.0, 12.0, 15.0);
    protected static final VoxelShape WEST_SHAPE = Block.box(1.0, 0.0, 0.0, 15.0, 12.0, 15.0);
    protected static final VoxelShape EAST_SHAPE = Block.box(1.0, 0.0, 1.0, 15.0, 12.0, 16.0);

    public BuyStation(Properties settings) {
        super(settings);
        this.registerDefaultState(this.defaultBlockState()
                .setValue(HorizontalDirectionalBlock.FACING, Direction.NORTH)
                .setValue(BUY_STATION_PART, BuyStationPart.MAIN));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState().setValue(HorizontalDirectionalBlock.FACING, ctx.getHorizontalDirection());
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.setPlacedBy(world, pos, state, placer, itemStack);
        if (!world.isClientSide) {
            BlockPos blockPos = pos.relative(state.getValue(HorizontalDirectionalBlock.FACING).getClockWise());
            world.setBlock(blockPos, state.setValue(BUY_STATION_PART, BuyStationPart.AUX), Block.UPDATE_ALL);
            world.blockUpdated(pos, Blocks.AIR);
            state.updateNeighbourShapes(world, pos, Block.UPDATE_ALL);
        }
    }

    @Override
    public void playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player) {
        if (world.isClientSide()) {
            super.playerWillDestroy(world, pos, state, player);
        }

        BuyStationPart part = state.getValue(BUY_STATION_PART);
        if (part == BuyStationPart.MAIN) {
            BlockPos otherpos = pos.relative(state.getValue(HorizontalDirectionalBlock.FACING).getClockWise());
            BlockState otherstate = world.getBlockState(otherpos);
            if (otherstate.getBlock() == this) {
                world.setBlock(otherpos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
                world.levelEvent(player, LevelEvent.PARTICLES_DESTROY_BLOCK, otherpos, Block.getId(otherstate));
            }
        }
        else if (part == BuyStationPart.AUX) {
            BlockPos otherpos = pos.relative(state.getValue(HorizontalDirectionalBlock.FACING).getCounterClockWise());
            BlockState otherstate = world.getBlockState(otherpos);
            if (otherstate.getBlock() == this) {
                world.setBlock(otherpos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
                world.levelEvent(player, LevelEvent.PARTICLES_DESTROY_BLOCK, otherpos, Block.getId(otherstate));
            }
        }
    }

    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        Direction direction = state.getValue(HorizontalDirectionalBlock.FACING);
        BuyStationPart part = state.getValue(BUY_STATION_PART);
        if (part == BuyStationPart.AUX) direction = direction.getOpposite();
        return switch (direction) {
            default -> NORTH_SHAPE;
            case SOUTH -> SOUTH_SHAPE;
            case WEST -> WEST_SHAPE;
            case EAST -> EAST_SHAPE;
        };
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        Block block = world.getBlockState(pos).getBlock();
        if (block instanceof BuyStation) {
            ((BuyStationCapable) player).hardcoreredeploy_openBuyStationScreen(pos);
            return InteractionResult.sidedSuccess(world.isClientSide);
        } else {
            return InteractionResult.PASS;
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HorizontalDirectionalBlock.FACING, BUY_STATION_PART);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return state.getValue(BUY_STATION_PART) == BuyStationPart.MAIN ? new BuyStationEntity(pos, state) : null;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return state.getValue(BUY_STATION_PART) == BuyStationPart.MAIN ? RenderShape.MODEL : RenderShape.INVISIBLE;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        return world.getBlockState(pos.relative(state.getValue(HorizontalDirectionalBlock.FACING).getClockWise())).canBeReplaced() && super.canSurvive(state, world, pos);
    }

    public enum BuyStationPart implements StringRepresentable {
        MAIN("main"),
        AUX("aux");

        private final String name;

        BuyStationPart(String name) {
            this.name = name;
        }

        public String toString() {
            return this.name;
        }

        public String getSerializedName() {
            return this.name;
        }
    }
}
