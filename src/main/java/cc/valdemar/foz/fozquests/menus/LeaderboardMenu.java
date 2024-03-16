package cc.valdemar.foz.fozquests.menus;

import cc.valdemar.foz.fozquests.FozQuests;
import cc.valdemar.foz.fozquests.players.QPlayer;
import cc.valdemar.foz.fozquests.quests.QuestManager;
import cc.valdemar.foz.fozquests.utils.ChatUtil;
import cc.valdemar.foz.fozquests.utils.Utils;
import cc.valdemar.foz.fozquests.utils.config.LocaleReference;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@ConfigSerializable
public class LeaderboardMenu implements LocaleReference {
    @Comment("The title for the menu")
    @Setting
    private final Component title = MiniMessage.miniMessage().deserialize("<blue>Current Quest Completion Leaderboard");

    private transient final QuestManager manager;

    private LeaderboardMenu() {
        manager = FozQuests.getInstance().getQuestManager();
    }

    public void open(Player player) {
        QPlayer qPlayer = FozQuests.getInstance().getPlayerManager().getQPlayer(player.getUniqueId());
        if(qPlayer == null) return;

        Gui gui = Gui.gui()
                .rows(4)
                .title(title)
                .disableAllInteractions()
                .apply(consumer -> {
                    consumer.getFiller().fillBetweenPoints(
                            1, 1,
                            4, 1,
                            new GuiItem(Material.BLACK_STAINED_GLASS)
                    );
                    consumer.getFiller().fillBetweenPoints(
                            1, 9,
                            4, 9,
                            new GuiItem(Material.BLACK_STAINED_GLASS)
                    );

                    manager.getLeaderboard().forEach((uuid, completed) -> {
                        Player leaderboardPlayer = Bukkit.getPlayer(uuid);
                        GuiItem playerItem = ItemBuilder.from(Utils.getPlayerSkull(leaderboardPlayer))
                                .lore(
                                        ChatUtil.deserialize(""),
                                        ChatUtil.deserialize("<green>Completions: " + completed)
                                )
                                .asGuiItem();
                        consumer.addItem(playerItem);
                    });
                }).create();

        gui.open(player);
    }
}
