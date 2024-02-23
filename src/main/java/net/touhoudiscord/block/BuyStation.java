package net.touhoudiscord.block;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import net.minecraft.world.WorldView;
import net.touhoudiscord.BuyStationCapable;
import org.jetbrains.annotations.Nullable;

public class BuyStation extends BlockWithEntity {
    public static final EnumProperty<BuyStationPart> BUY_STATION_PART = EnumProperty.of("part", BuyStationPart.class);

    protected static final VoxelShape NORTH_SHAPE = Block.createCuboidShape(1.0, 0.0, 1.0, 16.0, 12.0, 15.0);
    protected static final VoxelShape SOUTH_SHAPE = Block.createCuboidShape(0.0, 0.0, 1.0, 15.0, 12.0, 15.0);
    protected static final VoxelShape WEST_SHAPE = Block.createCuboidShape(1.0, 0.0, 0.0, 15.0, 12.0, 15.0);
    protected static final VoxelShape EAST_SHAPE = Block.createCuboidShape(1.0, 0.0, 1.0, 15.0, 12.0, 16.0);

    public BuyStation(Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState()
                .with(HorizontalFacingBlock.FACING, Direction.NORTH)
                .with(BUY_STATION_PART, BuyStationPart.MAIN));
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(HorizontalFacingBlock.FACING, ctx.getHorizontalPlayerFacing());
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        if (!world.isClient) {
            BlockPos blockPos = pos.offset(state.get(HorizontalFacingBlock.FACING).rotateYClockwise());
            world.setBlockState(blockPos, state.with(BUY_STATION_PART, BuyStationPart.AUX), Block.NOTIFY_ALL);
            world.updateNeighbors(pos, Blocks.AIR);
            state.updateNeighbors(world, pos, Block.NOTIFY_ALL);
        }
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (world.isClient()) {
            super.onBreak(world, pos, state, player);
        }

        BuyStationPart part = state.get(BUY_STATION_PART);
        if (part == BuyStationPart.MAIN) {
            BlockPos otherpos = pos.offset(state.get(HorizontalFacingBlock.FACING).rotateYClockwise());
            BlockState otherstate = world.getBlockState(otherpos);
            if (otherstate.getBlock() == this) {
                world.setBlockState(otherpos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);
                world.syncWorldEvent(player, WorldEvents.BLOCK_BROKEN, otherpos, Block.getRawIdFromState(otherstate));
            }
        }
        else if (part == BuyStationPart.AUX) {
            BlockPos otherpos = pos.offset(state.get(HorizontalFacingBlock.FACING).rotateYCounterclockwise());
            BlockState otherstate = world.getBlockState(otherpos);
            if (otherstate.getBlock() == this) {
                world.setBlockState(otherpos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);
                world.syncWorldEvent(player, WorldEvents.BLOCK_BROKEN, otherpos, Block.getRawIdFromState(otherstate));
            }
        }
    }

    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        Direction direction = state.get(HorizontalFacingBlock.FACING);
        BuyStationPart part = state.get(BUY_STATION_PART);
        if (part == BuyStationPart.AUX) direction = direction.getOpposite();
        return switch (direction) {
            default -> NORTH_SHAPE;
            case SOUTH -> SOUTH_SHAPE;
            case WEST -> WEST_SHAPE;
            case EAST -> EAST_SHAPE;
        };
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        Block block = world.getBlockState(pos).getBlock();
        if (block instanceof BuyStation) {
            ((BuyStationCapable) player).hardcoreredeploy_openBuyStationScreen(pos);
            return ActionResult.success(world.isClient);
        } else {
            return ActionResult.PASS;
        }
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(HorizontalFacingBlock.FACING, BUY_STATION_PART);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return state.get(BUY_STATION_PART) == BuyStationPart.MAIN ? new BuyStationEntity(pos, state) : null;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return state.get(BUY_STATION_PART) == BuyStationPart.MAIN ? BlockRenderType.MODEL : BlockRenderType.INVISIBLE;
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return world.getBlockState(pos.offset(state.get(HorizontalFacingBlock.FACING).rotateYClockwise())).isReplaceable() && super.canPlaceAt(state, world, pos);
    }

    public enum BuyStationPart implements StringIdentifiable {
        MAIN("main"),
        AUX("aux");

        private final String name;

        BuyStationPart(String name) {
            this.name = name;
        }

        public String toString() {
            return this.name;
        }

        public String asString() {
            return this.name;
        }
    }
}
