package cc.valdemar.foz.fozquests.utils.adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.Base64;

public class ItemAdapter extends TypeAdapter<ItemStack> {
    @Override
    public void write(JsonWriter jsonWriter, ItemStack item) throws IOException {
        if(item == null) {
            jsonWriter.nullValue();
            return;
        }

        jsonWriter.value(Base64.getEncoder().encodeToString(item.serializeAsBytes()));
    }

    @Override
    public ItemStack read(JsonReader jsonReader) throws IOException {
        if(jsonReader.peek() == JsonToken.NULL) {
            jsonReader.nextNull();
            return null;
        }

        return ItemStack.deserializeBytes(Base64.getDecoder().decode(jsonReader.nextString()));
    }
}
