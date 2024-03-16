package cc.valdemar.foz.fozquests.utils.adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.time.Instant;
import java.util.Base64;

public class InstantAdapter extends TypeAdapter<Instant> {
    @Override
    public void write(JsonWriter jsonWriter, Instant time) throws IOException {
        if(time == null) {
            jsonWriter.nullValue();
            return;
        }

        jsonWriter.value(time.getEpochSecond());
    }

    @Override
    public Instant read(JsonReader jsonReader) throws IOException {
        if(jsonReader.peek() == JsonToken.NULL) {
            jsonReader.nextNull();
            return null;
        }

        return Instant.ofEpochSecond(jsonReader.nextLong());
    }
}
