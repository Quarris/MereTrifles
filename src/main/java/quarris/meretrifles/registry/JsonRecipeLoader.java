package quarris.meretrifles.registry;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import quarris.meretrifles.MereTrifles;
import quarris.meretrifles.api.recipe.IJsonRecipeLoader;
import quarris.meretrifles.api.recipe.RecipeDryingRack;

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
    }

    public static File getModDir() {
        return CONFIG_DIR;
    }

    public static void loadRecipes() {
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
            for (File recipeFile : recipeDir.listFiles()) {
                String fileName = recipeFile.getName().replace(".json", "");
                try {
                    JsonObject json = (JsonObject) JSON_PARSER.parse(new FileReader(recipeFile));
                    loader.load(fileName, json);
                } catch (Exception e) {
                    MereTrifles.LOGGER.warn("Could not load recipe '" + fileName + "'", e);
                }
            }
        }
    }
}
