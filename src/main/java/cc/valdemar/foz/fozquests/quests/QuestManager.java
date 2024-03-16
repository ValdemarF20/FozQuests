package cc.valdemar.foz.fozquests.quests;

import cc.valdemar.foz.fozquests.config.Messages;
import cc.valdemar.foz.fozquests.players.QPlayer;
import cc.valdemar.foz.fozquests.quests.impl.BreakQuest;
import cc.valdemar.foz.fozquests.quests.impl.ExploreQuest;
import cc.valdemar.foz.fozquests.quests.impl.KillQuest;
import cc.valdemar.foz.fozquests.quests.impl.PickupQuest;
import cc.valdemar.foz.fozquests.utils.MapUtil;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class QuestManager {
    private final QuestRegistry registry;
    private final Map<Request, Instant> requests = new HashMap<>();
    private final Map<UUID, Integer> completions = new TreeMap<>();
    public void addCompleted(UUID uuid) {
        int current = completions.get(uuid) == null ? 0 : completions.get(uuid);
        completions.put(uuid, current + 1);
    }

    @NotNull
    public Quest createQuest(String identifier, ItemStack icon, Reward reward, int amount, QuestType questType, String args, boolean custom) {
        Quest newQuest;
        switch(questType) {
            case KILL_ENTITY -> {
                newQuest = new KillQuest(identifier, icon, reward, amount, questType, EntityType.valueOf(args.toUpperCase()), custom);
            }
            case BLOCK_BREAK -> {
                newQuest = new BreakQuest(identifier, icon, reward, amount, questType, Material.valueOf(args.toUpperCase()), custom);
            }
            case EXPLORE -> {
                String[] serializedLocation = args.split(":");
                Location location;
                try {
                    location = new Location(
                            Bukkit.getWorld(serializedLocation[0]),
                            Double.parseDouble(serializedLocation[1]),
                            Double.parseDouble(serializedLocation[2]),
                            Double.parseDouble(serializedLocation[3])
                    );
                } catch (Exception ignored) {
                    throw new RuntimeException("Exception happened while deserializing location for explore quest: " + identifier);
                }

                newQuest = new ExploreQuest(identifier, icon, reward, amount, questType, location, custom);
            }
            case PICKUP -> {
                newQuest = new PickupQuest(identifier, icon, reward, amount, questType, Material.valueOf(args.toUpperCase()), custom);
            }
            default -> {
                throw new RuntimeException("No quest type found for quest with identifier: " + identifier);
            }
        }

        return newQuest;
    }

    public Quest getQuest(Player player, String identifier) {
        return getQuest(player.getUniqueId() + "-" + identifier);
    }

    public Quest getQuest(String identifier) {
        return registry.lookup(identifier);
    }

    public void deleteQuest(Quest quest) {
        registry.unregister(quest);
    }

    public Map<UUID, Integer> getLeaderboard() {
        Map<UUID, Integer> sorted = new TreeMap<>(completions);
        MapUtil.entriesSortedByValues(sorted);
        return sorted;
    }

    public Request getRequest(UUID uuid) {
        for (Request request : requests.keySet()) {
            if(request.uuid().equals(uuid)) return request;
        }
        return null;
    }

    public boolean hasRequest(UUID uuid) {
        return getRequest(uuid) != null;
    }

    public void addRequest(UUID uuid, Quest quest) {
        addRequest(uuid, quest, Instant.now());
    }
    public void addRequest(UUID uuid, Quest quest, Instant time) {
        requests.put(new Request(uuid, quest), time);
    }

    public void removeRequest(Request request) {
        requests.remove(request);
    }

    public Map<Request, Instant> getRequests() {
        return requests.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1, HashMap::new)
        );
    }

    public void accept(CommandSender sender, Request request) {
        try {
            registry.register(request.quest());
        } catch (IllegalArgumentException ignored) {
            // Request couldn't be accepted
            sender.sendMessage(Messages.getInstance().getQuestAlreadyExists());
        }
        removeRequest(request);
    }

    public void deny(Request request) {
        requests.remove(request);
    }

    public int getCompletions(UUID uuid) {
        return completions.get(uuid);
    }

    public void setCompletions(QPlayer data) {
        completions.put(data.getUuid(), data.getCompletions());
    }
}
