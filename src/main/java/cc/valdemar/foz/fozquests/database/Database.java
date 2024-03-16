package cc.valdemar.foz.fozquests.database;

import cc.valdemar.foz.fozquests.FozQuests;
import cc.valdemar.foz.fozquests.database.impl.SQLiteDatabase;
import cc.valdemar.foz.fozquests.players.QPlayer;
import cc.valdemar.foz.fozquests.quests.Quest;
import cc.valdemar.foz.fozquests.quests.QuestRegistry;
import cc.valdemar.foz.fozquests.utils.adapters.InstantAdapter;
import cc.valdemar.foz.fozquests.utils.adapters.ItemAdapter;
import cc.valdemar.foz.fozquests.utils.adapters.QuestAdapter;
import cc.valdemar.foz.fozquests.utils.lambda.SafeConsumer;
import cc.valdemar.foz.fozquests.utils.lambda.SafeFunction;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * This abstract database class should define what methods and properties database implementations should contain
 */
public abstract class Database {
    protected final static Logger LOGGER = LoggerFactory.getLogger(SQLiteDatabase.class);
    protected transient HikariDataSource dataSource;
    public static Gson GSON = createGsonInstance();
    protected transient final QuestRegistry registry = FozQuests.getInstance().getQuestRegistry();

    /**
     * @return the instance of gson that should be used everywhere in the plugin
     */
    private static Gson createGsonInstance() {
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        builder.enableComplexMapKeySerialization();
        builder.registerTypeAdapter(ItemStack.class, new ItemAdapter());
        builder.registerTypeAdapter(Quest.class, new QuestAdapter(FozQuests.getInstance().getQuestManager()));
        builder.registerTypeAdapter(Instant.class, new InstantAdapter());
        return builder.create();
    }

    public abstract void connect();

    protected abstract <T> CompletableFuture<T> query(String sql, @NotNull SafeConsumer<PreparedStatement> initializer, @NotNull SafeFunction<ResultSet, T> process);
    protected abstract CompletableFuture<Void> update(String sql, @NotNull SafeConsumer<PreparedStatement> initializer);
    protected abstract CompletableFuture<Void> updateBatch(String sql, @NotNull SafeConsumer<PreparedStatement> initializer);

    public abstract CompletableFuture<Boolean> doesExist(UUID uuid);

    public boolean isConnected() { return dataSource != null; }

    /**
     * Loads the database into the cache
     *
     * @return
     */
    public abstract CompletableFuture<Void> load();

    /**
     * Saves the cached data to the database
     */
    public abstract void save();

    /**
     * Disconnects and closes the database
     */
    public void disconnect() {
        if(isConnected()) {
            dataSource.close();
        }
    }

    /**
     * Loads variables used in config
     * @return The HikariConfig to use for setting up the database
     */
    @NotNull
    protected abstract HikariConfig getHikariConfig();

    /**
     * Sets up the quests table in database
     *
     * @return
     */
    public abstract CompletableFuture<Void> createQuestsTable();

    /**
     * Sets up the players table in database
     *
     * @return
     */
    public abstract CompletableFuture<Void> createPlayersTable();

    public abstract CompletableFuture<Void> createRequestsTable();

    public abstract CompletableFuture<Void> serializePlayerData(QPlayer qPlayer);

    public abstract CompletableFuture<QPlayer> deserializePlayerData(UUID uuid);
}
