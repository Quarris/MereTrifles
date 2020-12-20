package quarris.meretrifles.registry;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import quarris.meretrifles.MereTrifles;
import quarris.meretrifles.recipe.IJsonRecipeLoader;
import quarris.meretrifles.recipe.RecipeDryingRack;
import quarris.meretrifles.recipe.RecipeStill;
import quarris.meretrifles.recipe.RecipeTanningTub;

import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

public class JsonRecipeLoader {

    private static File CONFIG_DIR;
    public static final JsonParser JSON_PARSER = new JsonParser();

    private static Map<String, IJsonRecipeLoader> RECIPES_FILES = new HashMap<>();

    public static void init(File modConfigDir) {
        CONFIG_DIR = modConfigDir;
        CONFIG_DIR.mkdir();

        RECIPES_FILES.put("drying_rack", new RecipeDryingRack.RecipeLoader());
        RECIPES_FILES.put("still", new RecipeStill.RecipeLoader());
        RECIPES_FILES.put("tanning_tub", new RecipeTanningTub.RecipeLoader());
    }

    public static File getModDir() {
        return CONFIG_DIR;
    }

    public static void loadRecipes() {
        MereTrifles.LOGGER.info("Loading recipes for {}", MereTrifles.NAME);
        File recipesDir = new File(getModDir(), "recipes");

        if (!recipesDir.exists()) {
            recipesDir.mkdir();
        }

        for (Map.Entry<String, IJsonRecipeLoader> entry : RECIPES_FILES.entrySet()) {
            String dirName = entry.getKey();
            File recipeDir = new File(recipesDir, dirName);
            if (!recipeDir.exists()) {
                recipeDir.mkdir();
                continue;
            }

            IJsonRecipeLoader loader = entry.getValue();
            int count = 0;
            for (File recipeFile : recipeDir.listFiles()) {
                String fileName = recipeFile.getName().replace(".json", "");
                try {
                    JsonObject json = (JsonObject) JSON_PARSER.parse(new FileReader(recipeFile));
                    loader.load(fileName, json);
                    count++;
                } catch (Exception e) {
                    MereTrifles.LOGGER.warn("Could not load recipe '" + fileName + "'", e);
                }
            }

            if (count > 0) {
                MereTrifles.LOGGER.info("   {}: {} recipes", dirName, count);
            }
        }
    }
}
