package cc.valdemar.foz.fozquests;

import cc.valdemar.foz.fozquests.commands.CustomQuestCommands;
import cc.valdemar.foz.fozquests.commands.QuestCommands;
import cc.valdemar.foz.fozquests.config.ConfigManager;
import cc.valdemar.foz.fozquests.database.Database;
import cc.valdemar.foz.fozquests.database.impl.SQLiteDatabase;
import cc.valdemar.foz.fozquests.listeners.data.PlayerJoinListener;
import cc.valdemar.foz.fozquests.listeners.data.PlayerQuitListener;
import cc.valdemar.foz.fozquests.listeners.quests.ProgressListener;
import cc.valdemar.foz.fozquests.menus.approval.PendingMenu;
import cc.valdemar.foz.fozquests.menus.LeaderboardMenu;
import cc.valdemar.foz.fozquests.menus.QuestListMenu;
import cc.valdemar.foz.fozquests.players.PlayerManager;
import cc.valdemar.foz.fozquests.quests.QuestManager;
import cc.valdemar.foz.fozquests.quests.QuestRegistry;
import co.aikar.commands.PaperCommandManager;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class FozQuests extends JavaPlugin {
    private static final Logger LOGGER = LoggerFactory.getLogger(FozQuests.class);
    @Getter
    private static FozQuests instance;
    @Getter
    private QuestRegistry questRegistry;
    private PaperCommandManager commandManager;
    @Getter
    private QuestManager questManager;
    @Getter
    private ConfigManager configManager;
    private Database database;
    @Getter
    private PlayerManager playerManager;

    @Override
    public void onEnable() {
        LOGGER.info("Enabling FozQuests");
        instance = this;

        /* Registries */
        questRegistry = new QuestRegistry();

        commandManager = new PaperCommandManager(this);
        commandManager.enableUnstableAPI("help");
        questManager = new QuestManager(questRegistry);

        /* Data */
        configManager = new ConfigManager(this);
        setupDatabase();

        playerManager = new PlayerManager(database, questManager);

        /* Register listeners */
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(playerManager), this);
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(playerManager), this);
        getServer().getPluginManager().registerEvents(new ProgressListener(playerManager), this);

        /* Register commands */
        registerCommands();
    }

    @Getter
    private CustomQuestCommands customQuestCommands;
    private void registerCommands() {
        commandManager.registerCommand(new QuestCommands(
                configManager.getConfig(QuestListMenu.class, "quest-menu"),
                questManager,
                questRegistry,
                configManager.getConfig(LeaderboardMenu.class, "leaderboard-menu")
        ));

        customQuestCommands = new CustomQuestCommands(questManager, configManager.getConfig(PendingMenu.class, "pending-menu"));
        commandManager.registerCommand(customQuestCommands);
    }

    private void setupDatabase() {
        database = configManager.getConfig(SQLiteDatabase.class, "sqlite");
        database.connect();
        database.createQuestsTable()
                .thenRun(() -> database.createPlayersTable())
                .thenRun(() -> database.createRequestsTable())
                .thenRun(() -> database.load())
                .whenComplete((input, exception) -> {
                    if(exception != null) {
                        LOGGER.error("Exception happened while loading database", exception);
                    } else {
                        LOGGER.info("Database loaded successfully");
                    }
                });
    }

    @Override
    public void onDisable() {
        database.save();
        database.disconnect();
    }
}
