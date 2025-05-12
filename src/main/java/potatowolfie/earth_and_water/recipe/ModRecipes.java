package potatowolfie.earth_and_water.recipe;

import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialRecipeSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import potatowolfie.earth_and_water.EarthWater;

public class ModRecipes {
    public static final RecipeSerializer<SpikedShieldDecorationRecipe> SPIKED_SHIELD_DECORATION = 
            Registry.register(Registries.RECIPE_SERIALIZER, 
                    Identifier.of(EarthWater.MOD_ID, "crafting_special_spikedshielddecoration"),
                    new SpecialRecipeSerializer<>(SpikedShieldDecorationRecipe::new));

    public static void registerRecipes() {
        EarthWater.LOGGER.info("Registering recipes for " + EarthWater.MOD_ID);
    }
}
