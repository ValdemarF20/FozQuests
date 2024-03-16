package cc.valdemar.foz.fozquests.utils.adapters;

import cc.valdemar.foz.fozquests.quests.Quest;
import cc.valdemar.foz.fozquests.quests.QuestManager;
import cc.valdemar.foz.fozquests.quests.QuestType;
import cc.valdemar.foz.fozquests.quests.Reward;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import lombok.RequiredArgsConstructor;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.Base64;

@RequiredArgsConstructor
public class QuestAdapter extends TypeAdapter<Quest> {
    private final QuestManager questManager;

    @Override
    public void write(JsonWriter writer, Quest quest) throws IOException {
        if(quest == null) {
            writer.nullValue();
            return;
        }

        writer.beginObject();
        writer.name("identifier").value(quest.getIdentifier());
        writer.name("icon").value(itemToBase64(quest.getIcon()));
        writer.name("reward").value(itemToBase64(quest.getReward().getItemStack()));
        writer.name("amount").value(quest.getAmount());
        writer.name("quest-type").value(quest.getQuestType().toString());
        writer.name("args").value(quest.getArgs());
        writer.name("custom").value(quest.isCustom());
        writer.endObject();
    }

    @Override
    public Quest read(JsonReader reader) throws IOException {
        if(reader.peek() == JsonToken.NULL) {
            reader.nextNull();
            return null;
        }

        String identifier = null;
        QuestType questType = null;
        ItemStack icon = null;
        Integer amount = null;
        Reward reward = null;
        String args = null;
        Boolean custom = null;

        reader.beginObject();
        while(reader.hasNext()) {
            if(!reader.peek().equals(JsonToken.NAME)) {
                continue; // Field is not a json name
            }

            String field = reader.nextName();
            switch (field) {
                case "identifier" -> identifier = reader.nextString();
                case "icon" -> icon = base64ToItem(reader.nextString());
                case "reward" -> reward = new Reward(base64ToItem(reader.nextString()));
                case "amount" -> amount = reader.nextInt();
                case "quest-type" -> questType = QuestType.valueOf(reader.nextString());
                case "args" -> args = reader.nextString();
                case "custom" -> custom = reader.nextBoolean();
                default -> {
                    return null;
                }
            }
        }
        reader.endObject();

        // Check if any data is null
        if(identifier == null || icon == null || reward == null || amount == null || questType == null || args == null || custom == null) {
            throw new RuntimeException("Null data found when deserializing quest");
        }

        return questManager.createQuest(identifier, icon, reward, amount, questType, args, custom);
    }

    private String itemToBase64(ItemStack item) {
        return Base64.getEncoder().encodeToString(item.serializeAsBytes());
    }

    private ItemStack base64ToItem(String base64) {
        return ItemStack.deserializeBytes(Base64.getDecoder().decode(base64));
    }
}
