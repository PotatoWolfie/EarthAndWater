package potatowolfie.earth_and_water.block.custom;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import java.util.List;

public class ChiseledDripstoneBricksBlock extends Block {
    public static final BooleanProperty POWERED = BooleanProperty.of("powered");

    public ChiseledDripstoneBricksBlock(Settings settings) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState().with(POWERED, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(POWERED);
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (!world.isClient && !state.isOf(oldState.getBlock())) {
            world.scheduleBlockTick(pos, this, 2);
        }
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, net.minecraft.util.math.random.Random random) {
        if (!world.isClient) {
            double x = pos.getX();
            double y = pos.getY();
            double z = pos.getZ();
            Box box = new Box(x - 5, y - 5, z - 5, x + 5, y + 5, z + 5);
            List<PlayerEntity> players = world.getEntitiesByType(EntityType.PLAYER, box, 
                player -> !player.isSpectator());
            boolean playerNearby = !players.isEmpty();

            if (playerNearby != state.get(POWERED)) {
                world.setBlockState(pos, state.with(POWERED, playerNearby));
            }
            
            world.scheduleBlockTick(pos, this, 2); // Schedule next check
        }
    }
}
