package potatowolfie.earth_and_water;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.block.DispenserBlock;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ProjectileItem;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Position;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import potatowolfie.earth_and_water.block.ModBlocks;
import potatowolfie.earth_and_water.effect.ModEffects;
import potatowolfie.earth_and_water.entity.ModEntities;
import potatowolfie.earth_and_water.item.ModItems;
import potatowolfie.earth_and_water.recipe.ModRecipes;

public class EarthWater implements ModInitializer {
	public static final String MOD_ID = "earth-and-water";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final SimpleParticleType LIGHT_UP = FabricParticleTypes.simple();
	public static final SimpleParticleType SHOCK_WAVE = FabricParticleTypes.simple();

	@Override
	public void onInitialize() {
		ModItems.registerModItems();
		ModBlocks.registerModBlocks();
		ModEffects.registerEffects();
		ModRecipes.registerRecipes();
		ModEntities.registerModEntities();
		Registry.register(Registries.PARTICLE_TYPE, Identifier.of(MOD_ID, "light_up"), LIGHT_UP);
		Registry.register(Registries.PARTICLE_TYPE, Identifier.of(MOD_ID, "shock_wave"), SHOCK_WAVE);

		registerDispenserBehaviors();

		LOGGER.info("Earth and Water mod initialized!");
	}

	private void registerDispenserBehaviors() {
		registerProjectileDispenserBehavior(ModItems.WATER_CHARGE);
		registerProjectileDispenserBehavior(ModItems.EARTH_CHARGE);
	}

	private void registerProjectileDispenserBehavior(Item item) {
		DispenserBlock.registerBehavior(
				item,
				(pointer, stack) -> {
					World world = pointer.world();
					Position position = DispenserBlock.getOutputLocation(pointer);
					Direction direction = pointer.state().get(DispenserBlock.FACING);

					ProjectileItem projectileItem = (ProjectileItem)stack.getItem();
					ProjectileEntity projectileEntity = projectileItem.createEntity(
							world,
							position,
							stack,
							direction
					);

					projectileEntity.setVelocity(
							direction.getOffsetX(),
							direction.getOffsetY() + 0.1F,
							direction.getOffsetZ(),
							1.5F,
							0.1F
					);

					world.spawnEntity(projectileEntity);
					stack.decrement(1);
					return stack;
				}
		);
	}
}