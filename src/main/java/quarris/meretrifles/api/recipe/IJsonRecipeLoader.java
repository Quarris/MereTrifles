package quarris.meretrifles.api.recipe;

import com.google.gson.JsonObject;

public interface IJsonRecipeLoader<T extends JsonRecipe> {

    T load(String fileName, JsonObject obj);

}
