package cc.valdemar.foz.fozquests.config;

import cc.valdemar.foz.fozquests.FozQuests;
import cc.valdemar.foz.fozquests.config.menu.ApprovalMenuSettings;
import cc.valdemar.foz.fozquests.config.menu.IconMenuSettings;
import cc.valdemar.foz.fozquests.config.menu.MenuSettings;
import cc.valdemar.foz.fozquests.config.menu.RewardMenuSettings;
import cc.valdemar.foz.fozquests.database.impl.SQLiteDatabase;
import cc.valdemar.foz.fozquests.menus.approval.PendingMenu;
import cc.valdemar.foz.fozquests.menus.LeaderboardMenu;
import cc.valdemar.foz.fozquests.menus.QuestListMenu;
import cc.valdemar.foz.fozquests.utils.config.ConfigurationWrapper;
import cc.valdemar.foz.fozquests.utils.config.LocaleReference;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class ConfigManager {
    private final Map<String, ConfigurationWrapper> configs = new HashMap<>();
    private final ClassLoader classLoader;
    private final Path dataFolder;

    public ConfigManager(FozQuests fozQuests) {
        classLoader = fozQuests.getClass().getClassLoader();
        dataFolder = fozQuests.getDataFolder().toPath();
        load();
    }

    private void load() {
        addConfig(SQLiteDatabase.class, "sqlite", "database/sqlite.yml");
        addConfig(QuestListMenu.class, "quest-menu", "quests/menu.yml");
        addConfig(Messages.class, "messages", "messages.yml");
        addConfig(MenuSettings.class, "menu-settings", "menus/settings.yml");
        addConfig(RewardMenuSettings.class, "reward-menu-settings", "menus/reward-settings.yml");
        addConfig(IconMenuSettings.class, "icon-menu-settings", "menus/icon-settings.yml");
        addConfig(LeaderboardMenu.class, "leaderboard-menu", "menus/leaderboard.yml");
        addConfig(ApprovalMenuSettings.class, "approval-menu-settings", "menus/approval-menu-settings.yml");
        addConfig(PendingMenu.class, "pending-menu", "menus/pending.yml");
    }

    public void save() {
        configs.values().forEach(ConfigurationWrapper::saveConfig);
    }

    @SuppressWarnings("unchecked")
    private <T extends LocaleReference> T addConfig(Class<T> reference, String name, String resourcePath) {
        ConfigurationWrapper wrapper = new ConfigurationWrapper(reference, classLoader, dataFolder, resourcePath);
        configs.put(name, wrapper);
        Object result = wrapper.get();
        if(getConfig(reference, name) == null) {
            throw new RuntimeException("Invalid configuration class reference (must implement LocaleReference)");
        }
        return (T) result;
    }

    private void addConfig(String name, String resourcePath) {
        configs.put(name, new ConfigurationWrapper(classLoader, dataFolder, resourcePath));
    }

    public void saveConfig(String name) {
        ConfigurationWrapper config = getConfig(name);
        config.saveConfig();
    }

    public ConfigurationWrapper getConfig(String name) {
        return configs.get(name);
    }

    @SuppressWarnings("unchecked")
    public <T extends LocaleReference> T getConfig(Class<T> reference, String name) {
        Object result = configs.get(name).get();
        if(!reference.isInstance(result)) {
            return null;
        }
        return (T) result;
    }
}
