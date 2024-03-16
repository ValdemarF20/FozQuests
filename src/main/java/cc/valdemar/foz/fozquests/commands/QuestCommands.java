package cc.valdemar.foz.fozquests.commands;

import cc.valdemar.foz.fozquests.config.Messages;
import cc.valdemar.foz.fozquests.menus.LeaderboardMenu;
import cc.valdemar.foz.fozquests.menus.MainCreationMenu;
import cc.valdemar.foz.fozquests.menus.QuestListMenu;
import cc.valdemar.foz.fozquests.quests.Quest;
import cc.valdemar.foz.fozquests.quests.QuestManager;
import cc.valdemar.foz.fozquests.quests.QuestRegistry;
import cc.valdemar.foz.fozquests.quests.QuestType;
import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("quest|quests|q")
@CommandPermission("fozquests.commands.quest")
@RequiredArgsConstructor
public class QuestCommands extends BaseCommand {
    private final QuestListMenu questListMenu;
    private final QuestManager manager;
    private final QuestRegistry registry;

    private final LeaderboardMenu leaderboardMenu;

    @Default
    @Description("Opens the quest menu")
    @CommandPermission("fozquests.commands.quest.menu")
    public void openMenu(Player player) {
        openMenu(player, new OnlinePlayer(player));
    }

    @Default
    @Description("Opens the quest menu for another player")
    @CommandPermission("fozquests.commands.quest.menu.other")
    public void openMenu(CommandSender sender, OnlinePlayer onlinePlayer) {
        questListMenu.open(onlinePlayer.player);
    }

    @Subcommand("create")
    @Description("Create a new quest with given type")
    @CommandPermission("fozquests.commands.quest.create")
    public void create(Player player,
                       @Name("icon") Material icon,
                       @Name("reward") Material reward,
                       @Name("quest-type") QuestType questType,
                       @Name("type") String type,
                       @Optional @Default("1") @Name("amount") int amount) {
        new MainCreationMenu(player, icon, reward, amount, questType, type, false).open();
    }

    @Subcommand("delete")
    @Description("Delete a quest made by given player with given identifier")
    @CommandPermission("fozquests.commands.quest.delete")
    public void delete(CommandSender sender,
                       @Name("player") OnlinePlayer onlinePlayer,
                       @Name("identifier") String identifier) {
        Quest quest = manager.getQuest(onlinePlayer.player, identifier);
        if(quest == null) {
            sender.sendMessage(Messages.getInstance().getInvalidQuestSettings());
            return;
        }
        manager.deleteQuest(quest);
    }

    @Subcommand("list")
    @Description("Sends a list of all registered quests")
    @CommandPermission("fozquests.commands.quest.list")
    public void list(CommandSender sender) {
        for (Quest quest : registry.getAll()) {
            sender.sendMessage(MiniMessage.miniMessage().deserialize(
                    "<blue>Name: <reset>" + quest.getName() +
                    " <blue>Type: <reset>" + quest.getQuestType() +
                    " <blue>Requirement: <reset>" + quest.getAmount() + " " + quest.getArgs())
            );
        }
    }

    @Subcommand("leaderboard|lb|l")
    @Description("Opens the current leaderboard for most (non-custom) quests completed")
    @CommandPermission("fozquests.commands.quest.leaderboard")
    public void leaderboard(Player player) {
        leaderboardMenu.open(player);
    }

    @HelpCommand
    public static void onHelp(CommandSender sender, CommandHelp help) {
        help.showHelp();
    }
}
