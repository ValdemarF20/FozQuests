package cc.valdemar.foz.fozquests.database.impl;

import cc.valdemar.foz.fozquests.FozQuests;
import cc.valdemar.foz.fozquests.database.Database;
import cc.valdemar.foz.fozquests.players.PlayerManager;
import cc.valdemar.foz.fozquests.players.QPlayer;
import cc.valdemar.foz.fozquests.quests.ActiveQuest;
import cc.valdemar.foz.fozquests.quests.Quest;
import cc.valdemar.foz.fozquests.quests.Request;
import cc.valdemar.foz.fozquests.utils.Errors;
import cc.valdemar.foz.fozquests.utils.config.LocaleReference;
import cc.valdemar.foz.fozquests.utils.lambda.SafeConsumer;
import cc.valdemar.foz.fozquests.utils.lambda.SafeFunction;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;

@ConfigSerializable
public class SQLiteDatabase extends Database implements LocaleReference {
    @Comment("The path to the sqlite database file")
    @Setting
    private final String path = "plugins/FozQuests/database/sqlite.db";

    @Override
    public void connect()  {
        // create the hikari config
        HikariConfig hikari = getHikariConfig();

        LOGGER.info("Trying to connect to the SQLite database...");

        // create the file if it doesn't exist
        try {
            File file = new File(path);
            if (!file.exists()) {
                file.createNewFile();
            }
            this.dataSource = new HikariDataSource(hikari);
            LOGGER.info("Successfully established connection with SQLite database!");
        } catch (IOException e) {
            throw new RuntimeException("Failed to create the database file, stopping the server!", e);
        }

        try(Connection connection = dataSource.getConnection()) {
            if(!connection.isValid(5)) {
                LOGGER.error("SQLiteDatabase connection not valid");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    protected @NotNull HikariConfig getHikariConfig() {
        HikariConfig hikari = new HikariConfig();
        hikari.setPoolName("SummitPrison SQLiteDatabase Pool");
        hikari.setConnectionTimeout(60000);
        hikari.setIdleTimeout(600000);
        hikari.setLeakDetectionThreshold(180000);
        hikari.addDataSourceProperty("characterEncoding", "utf8");
        hikari.addDataSourceProperty("useUnicode", true);

        hikari.setDriverClassName("org.sqlite.JDBC");
        hikari.setJdbcUrl("jdbc:sqlite:" + path);
        hikari.setMaximumPoolSize(50);
        return hikari;
    }

    @Override
    public CompletableFuture<Void> createQuestsTable() {
        return this.update("CREATE TABLE IF NOT EXISTS quests (" +
                        "identifier VARCHAR PRIMARY KEY, " +
                        "quest VARCHAR" +
                        ");",
                statement -> {});
    }

    @Override
    public CompletableFuture<Void> createPlayersTable() {
        return this.update("CREATE TABLE IF NOT EXISTS players (" +
                        "uuid VARCHAR(36) PRIMARY KEY, " +
                        "activequest VARCHAR, " +
                        "completions INTEGER" +
                        ");",
                statement -> {});
    }

    @Override
    public CompletableFuture<Void> createRequestsTable() {
        return this.update("CREATE TABLE IF NOT EXISTS requests (" +
                        "uuid VARCHAR(36) PRIMARY KEY, " +
                        "quest VARCHAR, " +
                        "time VARCHAR" +
                        ");",
                statement -> {});
    }

    /**
     * Executes a query to database
     *
     * @param sql       A String query to send to the database
     * @param initializer A {@link Consumer} that can throw a checked exception.
     * @param process     A {@link Function} that can throw a checked exception.
     * @param <T>         The type of the result for {@link SafeFunction}
     * @return A {@link CompletableFuture<T>} for handling exceptions
     */
    @Override
    protected <T> CompletableFuture<T> query(String sql, @NotNull SafeConsumer<PreparedStatement> initializer, @NotNull SafeFunction<ResultSet, T> process) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement(sql)) {
                initializer.consume(statement);
                ResultSet resultSet = statement.executeQuery();
                return process.apply(resultSet);
            } catch (Exception e) {
                LOGGER.error("Error when preparing statement for query: " + sql);
                Errors.sneakyThrow(e);
                return null;
            }
        });
    }

    /**
     * Updates query to database
     * Must be a SQL Data Manipulation Language (DML) statement, such as INSERT, UPDATE or DELETE
     *
     * @param sql       A String query to send to the database
     * @param initializer A {@link Consumer} that can throw a checked exception.
     * @return A {@link CompletableFuture<Void>} for handling exceptions
     */
    @Override
    protected CompletableFuture<Void> update(String sql, @NotNull SafeConsumer<PreparedStatement> initializer) {
        return CompletableFuture.runAsync(() -> {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement(sql)) {
                initializer.consume(statement);
                statement.executeUpdate();
            } catch (Exception e) {
                LOGGER.error("Error when preparing statement for query: " + sql);
                Errors.sneakyThrow(e);
            }
        });
    }

    /**
     * Updates query batch to database (sync)
     * Must be a SQL Data Manipulation Language (DML) statement, such as INSERT, UPDATE or DELETE
     *
     * @param sql       A String query to send to the database
     * @param initializer A {@link Consumer} that can throw a checked exception.
     */
    protected void updateBatchSync(String sql, @NotNull SafeConsumer<PreparedStatement> initializer) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            initializer.consume(statement);
            statement.executeBatch();
        } catch (Exception e) {
            LOGGER.error("Error when preparing statement for query: " + sql);
            Errors.sneakyThrow(e);
        }
    }

    /**
     * Updates query batch to database
     * Must be a SQL Data Manipulation Language (DML) statement, such as INSERT, UPDATE or DELETE
     *
     * @param sql       A String query to send to the database
     * @param initializer A {@link Consumer} that can throw a checked exception.
     * @return A {@link CompletableFuture<Void>} for handling exceptions
     */
    @Override
    protected CompletableFuture<Void> updateBatch(String sql, @NotNull SafeConsumer<PreparedStatement> initializer) {
        return CompletableFuture.runAsync(() -> {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement(sql)) {
                initializer.consume(statement);
                statement.executeBatch();
            } catch (Exception e) {
                LOGGER.error("Error when preparing statement for query: " + sql);
                Errors.sneakyThrow(e);
            }
        });
    }

    @Override
    public CompletableFuture<Void> serializePlayerData(QPlayer qPlayer) {
        UUID uuid = qPlayer.getUuid();
        int completions = FozQuests.getInstance().getQuestManager().getCompletions(uuid);

        return this.update("INSERT INTO players (uuid, activequest, completions) VALUES (?, ?, ?)" +
                        "ON CONFLICT(uuid) DO UPDATE SET activequest = ?, completions = ?",
                statement -> {
                    statement.setString(1, uuid.toString());
                    statement.setString(2, GSON.toJson(qPlayer.getActiveQuest()));
                    statement.setInt(3, completions);
                    statement.setString(4, GSON.toJson(qPlayer.getActiveQuest()));
                    statement.setInt(5, completions);
                }).whenComplete((input, exception) -> {
            throw new RuntimeException("Exception happened when saving data for: " + qPlayer.getPlayer());
        });
    }

    @Override
    public CompletableFuture<QPlayer> deserializePlayerData(UUID uuid) {
        return this.query("SELECT * FROM players WHERE uuid = ?",
                statement -> {
                    statement.setString(1, uuid.toString());
                },
                resultSet -> {
                    if(resultSet.next()) {
                        ActiveQuest activeQuest = GSON.fromJson(resultSet.getString("activequest"), ActiveQuest.class);
                        int completions = resultSet.getInt("completions");
                        return new QPlayer(uuid, activeQuest, completions);
                    } else {
                        return new QPlayer(uuid, null, 0);
                    }
                });
    }

    public CompletableFuture<Boolean> doesExist(UUID uuid) {
        return this.query("SELECT 1 FROM players WHERE uuid = ?",
                        statement -> {
                            statement.setString(1, uuid.toString());
                        }, ResultSet::next)
                .whenComplete((input, exception) -> {
                    if (exception != null) {
                        LOGGER.error("Exception happened when checking if player exists for: " + uuid, exception);
                    }
                });
    }

    @Override
    public CompletableFuture<Void> load() {
        return this.query("SELECT quest FROM quests",
                statement -> {},
                resultSet -> {
                    while(resultSet.next()) {
                        registry.register(GSON.fromJson(resultSet.getString("quest"), Quest.class));
                    }
                    return null;
                }
        ).thenRun(() -> {
            this.query("SELECT * FROM requests",
                    statement -> {},
                    resultSet -> {
                        while (resultSet.next()) {
                            UUID uuid = UUID.fromString(resultSet.getString("uuid"));
                            Quest quest = GSON.fromJson(resultSet.getString("quest"), Quest.class);
                            Instant time = GSON.fromJson(resultSet.getString("time"), Instant.class);
                            FozQuests.getInstance().getQuestManager().addRequest(uuid, quest, time);
                        }
                        return null;
                    });
        }).whenComplete((input, exception) -> {
            if(exception != null) {
                LOGGER.error("Exception happened while loading quests and requests from database", exception);
            }
        });
    }

    @Override
    public void save() {
        updateBatchSync("INSERT INTO players (uuid, activequest, completions) VALUES (?, ?, ?)" +
                "ON CONFLICT(uuid) DO UPDATE SET activequest = ?, completions = ?",
                statement -> {
                    PlayerManager playerManager = FozQuests.getInstance().getPlayerManager();
                    Map<UUID, BukkitTask> leaveTasks = playerManager.getLeaveTasks();
                    Set<UUID> players = new HashSet<>(leaveTasks.keySet());
                    players.addAll(Bukkit.getOnlinePlayers().stream().map(Player::getUniqueId).toList());

                    for (UUID uuid : players) {
                        QPlayer qPlayer = playerManager.getQPlayer(uuid);
                        if(qPlayer == null) continue;
                        int completions = FozQuests.getInstance().getQuestManager().getCompletions(uuid);

                        statement.setString(1, uuid.toString());
                        statement.setString(2, GSON.toJson(qPlayer.getActiveQuest()));
                        statement.setInt(3, completions);
                        statement.setString(4, GSON.toJson(qPlayer.getActiveQuest()));
                        statement.setInt(5, completions);
                        statement.addBatch();
                    }
                });
        updateBatchSync("INSERT OR REPLACE INTO quests(identifier, quest) VALUES(?, ?)",
                statement -> {
                    List<Quest> quests = List.copyOf(registry.getAll());
                    for (Quest quest : quests) {
                        statement.setString(1, quest.getIdentifier());
                        statement.setString(2, GSON.toJson(quest, Quest.class));
                        statement.addBatch();
                    }
                }
        );
        updateBatchSync("INSERT OR REPLACE INTO requests(uuid, quest, time) VALUES(?, ?, ?)",
                statement -> {
                    Map<Request, Instant> requests = FozQuests.getInstance().getQuestManager().getRequests();
                    for (Map.Entry<Request, Instant> entry : requests.entrySet()) {
                        UUID uuid = entry.getKey().uuid();
                        Quest quest = entry.getKey().quest();
                        Instant instant = entry.getValue();
                        statement.setString(1, uuid.toString());
                        statement.setString(2, GSON.toJson(quest, Quest.class));
                        statement.setString(3, GSON.toJson(instant));
                        statement.addBatch();
                    }
                });
    }
}
