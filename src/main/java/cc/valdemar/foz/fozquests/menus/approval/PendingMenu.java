package cc.valdemar.foz.fozquests.menus.approval;

import cc.valdemar.foz.fozquests.FozQuests;
import cc.valdemar.foz.fozquests.config.menu.ApprovalMenuSettings;
import cc.valdemar.foz.fozquests.players.QPlayer;
import cc.valdemar.foz.fozquests.quests.QuestManager;
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
public class PendingMenu implements LocaleReference {
    @Comment("The title for the menu")
    @Setting
    private final Component title = MiniMessage.miniMessage().deserialize("<blue>Approval Menu");

    public void open(Player player, QuestManager questManager) {
        ApprovalMenuSettings settings = FozQuests.getInstance().getConfigManager().getConfig(ApprovalMenuSettings.class, "approval-menu-settings");
        QPlayer qPlayer = FozQuests.getInstance().getPlayerManager().getQPlayer(player.getUniqueId());
        if(qPlayer == null) return;

        Gui gui = Gui.gui()
                .rows(3)
                .title(title)
                .disableAllInteractions()
                .apply(consumer -> {
                    consumer.getFiller().fillBetweenPoints(
                            1, 1,
                            3, 1,
                            new GuiItem(Material.BLACK_STAINED_GLASS)
                    );
                    consumer.getFiller().fillBetweenPoints(
                            1, 9,
                            3, 9,
                            new GuiItem(Material.BLACK_STAINED_GLASS)
                    );

                    questManager.getRequests().forEach(((request, instant) -> {
                        Player pendingPlayer = Bukkit.getPlayer(player.getUniqueId());
                        GuiItem playerItem = ItemBuilder.from(Utils.getPlayerSkull(pendingPlayer)).asGuiItem(event -> {
                            new QuestApprovalMenu(pendingPlayer, request, settings, questManager).open();
                        });
                        consumer.addItem(playerItem);
                    }));

                }).create();

        gui.open(player);
    }
}
