package quarris.meretrifles.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.JsonUtils;
import quarris.meretrifles.MereTrifles;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface IJsonRecipeLoader<T extends JsonRecipe> {

    Pattern ITEM_REGEX = Pattern.compile("([\\w\\d_]+(?::[\\w\\d_]+)?)(?:#(\\d*))?(?:%(\\d*))?(?:@(\\{.*}))?");

    T load(String fileName, JsonObject obj) throws Exception;

    static ItemStack getItemStack(JsonObject json, String key) throws Exception {
        String itemString = JsonUtils.getString(json, key);

        Matcher matcher = ITEM_REGEX.matcher(itemString);

        if (!matcher.matches()) {
            MereTrifles.LOGGER.warn("Could not parse item string (" + itemString + "); using empty stack instead.");
            return ItemStack.EMPTY;
        }

        String itemName = matcher.group(1);
        int amount = matcher.start(2) > 0 ? Integer.parseInt(matcher.group(2)) : 1;
        int meta = matcher.start(3) > 0 ? Integer.parseInt(matcher.group(3)) : 0;
        NBTTagCompound nbt = matcher.start(4) > 0 ? JsonToNBT.getTagFromJson(matcher.group(4)) : new NBTTagCompound();

        Item item = Item.getByNameOrId(itemName);

        if (item == null) {
            MereTrifles.LOGGER.warn("Could not find item (" + itemName + ") for key (" + key + ") in json recipe (" + json + "); using empty stack instead.");
            return ItemStack.EMPTY;
        }

        return new ItemStack(item, amount, meta, nbt);
    }

    static Set<String> getStringSet(JsonObject json, String key) {
        if (!json.has(key) || json.get(key).isJsonObject()) {
            return Collections.emptySet();
        }

        JsonElement jsonElement = json.get(key);

        if (jsonElement.isJsonArray()) {
            Set<String> set = new HashSet<>();
            for (JsonElement element : jsonElement.getAsJsonArray()) {
                if (element.isJsonPrimitive()) {
                    set.add(element.getAsString());
                }
            }
            return set;
        }

        return Collections.singleton(jsonElement.getAsString());
    }

    static Set<Integer> getIntegerSet(JsonObject json, String key) {
        if (!json.has(key) || json.get(key).isJsonObject()) {
            return Collections.emptySet();
        }

        JsonElement jsonElement = json.get(key);

        if (jsonElement.isJsonArray()) {
            Set<Integer> set = new HashSet<>();
            for (JsonElement element : jsonElement.getAsJsonArray()) {
                if (element.isJsonPrimitive() && ((JsonPrimitive) element).isNumber()) {
                    set.add(element.getAsInt());
                }
            }
            return set;
        }

        return Collections.singleton(jsonElement.getAsInt());
    }
}
