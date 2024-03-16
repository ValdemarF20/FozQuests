package cc.valdemar.foz.fozquests.commands;

import cc.valdemar.foz.fozquests.config.Messages;
import cc.valdemar.foz.fozquests.menus.MainCreationMenu;
import cc.valdemar.foz.fozquests.menus.approval.PendingMenu;
import cc.valdemar.foz.fozquests.quests.QuestManager;
import cc.valdemar.foz.fozquests.quests.QuestType;
import cc.valdemar.foz.fozquests.quests.Request;
import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("customquest|cq")
@CommandPermission("fozquests.commands.customquest")
@RequiredArgsConstructor
public class CustomQuestCommands extends BaseCommand {
    private final QuestManager questManager;
    private final PendingMenu pendingMenu;

    @Subcommand("create")
    @Description("Create a new quest with given type")
    @CommandPermission("fozquests.commands.customquest.create")
    public void create(Player player,
                       @Name("icon") Material icon,
                       @Name("reward") Material reward,
                       @Name("quest-type") QuestType questType,
                       @Name("type") String type,
                       @Optional @Default("1") @Name("amount") int amount) {
        if(questManager.hasRequest(player.getUniqueId())) {
            player.sendMessage(Messages.getInstance().getAlreadyPending());
            return;
        }

        new MainCreationMenu(player, icon, reward, amount, questType, type, true).open();
    }

    @Subcommand("approve")
    @Description("Opens the menu to approve custom quests")
    @CommandPermission("fozquests.commands.customquest.approve")
    public void approve(CommandSender sender, @Optional @Name("player") OnlinePlayer onlinePlayer) {
        if(onlinePlayer == null) {
            if(!(sender instanceof Player player)) {
                sender.sendMessage(Messages.getInstance().getPlayersOnlyCommand());
                return;
            }
            pendingMenu.open(player, questManager);
        } else {
            Request request = questManager.getRequest(onlinePlayer.getPlayer().getUniqueId());
            if(request == null) {
                sender.sendMessage(Messages.getInstance().getNoExistingRequest());
            } else {
                questManager.accept(sender, request);
            }
        }
    }

    @HelpCommand
    public static void onHelp(CommandSender sender, CommandHelp help) {
        help.showHelp();
    }
}
