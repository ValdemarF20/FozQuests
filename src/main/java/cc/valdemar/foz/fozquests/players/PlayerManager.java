package cc.valdemar.foz.fozquests.players;

import cc.valdemar.foz.fozquests.database.Database;
import cc.valdemar.foz.fozquests.quests.QuestManager;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
public class PlayerManager {
    private final static Logger LOGGER = LoggerFactory.getLogger(PlayerManager.class);
    private final Map<UUID, QPlayer> qPlayers = new HashMap<>();

    private final Database database;
    private final QuestManager manager;

    /**
     *  Gets a map of all leave tasks with uuid as key
     *
     * @return Map with {@link UUID} as key and {@link BukkitTask} as value
     */
    @Getter
    private final Map<UUID, BukkitTask> leaveTasks = new HashMap<>();

    /**
     * Runs 300 ticks after player quits
     *
     * @param uuid {@link UUID} of player to remove
     */
    public void removePlayer(UUID uuid) {
        leaveTasks.remove(uuid);
        //Save player data here
        database.serializePlayerData(qPlayers.get(uuid));
        qPlayers.remove(uuid);
    }

    /**
     * Loads a player after joining
     * Should only be called on player join
     * @param uuid {@link UUID} of the player to load
     */
    public CompletableFuture<QPlayer> loadPlayer(UUID uuid) {
        // Cancel tasks that removes player 300 ticks after leave, if task is active
        BukkitTask task = leaveTasks.get(uuid);
        if (task != null && !task.isCancelled()) {
            task.cancel();
            leaveTasks.remove(uuid);
            return null;
        }

        /* Create default data if player is not in database */
        return database.deserializePlayerData(uuid).thenApply((QPlayer data) -> {
            qPlayers.put(data.getUuid(), data);
            manager.setCompletions(data);
            return data;
        }).whenComplete((input, exception) -> {
            if (exception != null) {
                LOGGER.error("Exception when deserializing player data for " + uuid, exception);
            }
        });
    }

    /**
     * Gets a map of all QPlayers
     * @return Map with {@link UUID} as key and {@link QPlayer} as value
     */
    public Map<UUID, QPlayer> getQPlayers() {
        return qPlayers;
    }

    /**
     * Gets a QPlayer by given uuid
     * @param uuid {@link UUID} of player
     * @return The {@link QPlayer} instance
     */
    @Nullable
    public QPlayer getQPlayer(UUID uuid) {
        return qPlayers.get(uuid);
    }

    /**
     * Check if the player has previously played
     *
     * @param uuid Unique ID of the player
     * @return A CompletableFuture with the value true if the player has played before - false if not
     */
    public CompletableFuture<Boolean> doesExist(UUID uuid) {
        return database.doesExist(uuid);
    }
}
