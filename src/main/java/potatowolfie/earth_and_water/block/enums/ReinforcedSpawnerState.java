package potatowolfie.earth_and_water.block.enums;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringIdentifiable;

/**
 * Enum for the state of a Reinforced Spawner.
 * Similar to TrialSpawnerState but customized for our needs without rewards.
 */
public enum ReinforcedSpawnerState implements StringIdentifiable {
    /**
     * The spawner is not active and won't spawn mobs.
     */
    INACTIVE("inactive"),
    
    /**
     * The spawner is active and can spawn mobs.
     */
    ACTIVE("active");

    public static final Codec<ReinforcedSpawnerState> CODEC = StringIdentifiable.createCodec(ReinforcedSpawnerState::values);
    private final String name;

    ReinforcedSpawnerState(String name) {
        this.name = name;
    }

    @Override
    public String asString() {
        return this.name;
    }

    /**
     * Check if this state allows the spawner to spawn mobs.
     *
     * @return true if the spawner can spawn mobs in this state
     */
    public boolean canSpawn() {
        return this == ACTIVE;
    }

    /**
     * Check if the spawner is currently active.
     *
     * @return true if the spawner is in the ACTIVE state
     */
    public boolean isActive() {
        return this == ACTIVE;
    }
}
