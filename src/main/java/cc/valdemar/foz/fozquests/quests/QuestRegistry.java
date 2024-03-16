package cc.valdemar.foz.fozquests.quests;

import cc.valdemar.foz.fozquests.FozQuests;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class QuestRegistry {
    private final Map<NamespacedKey, Quest> quests = new HashMap<>();

    public void register(Quest quest) {
        register(getKey(quest), quest);
    }

    public void register(NamespacedKey key, Quest quest) {
        if(quests.containsKey(key)) {
                throw new IllegalArgumentException("Attempted to add duplicate key for quest with identifier: " + quest.getIdentifier());
            }

            quests.put(key, quest);
    }

    public void unregister(Quest quest) {
        unregister(getKey(quest));
    }

    public void unregister(NamespacedKey key) {
        quests.remove(key);
    }

    @Nullable
    public Quest lookup(String identifier) {
        return lookup(getKey(identifier));
    }

    @Nullable
    public Quest lookup(NamespacedKey key) {
        return quests.get(key);
    }

    private NamespacedKey getKey(Quest quest) {
        return getKey(quest.getIdentifier());
    }

    private NamespacedKey getKey(String identifier) {
        return new NamespacedKey(FozQuests.getInstance(), "quest-" + identifier);
    }

    public Set<Quest> getAll() {
        return new HashSet<>(quests.values());
    }
}
