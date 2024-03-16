package cc.valdemar.foz.fozquests.menus;

import cc.valdemar.foz.fozquests.FozQuests;
import cc.valdemar.foz.fozquests.config.Messages;
import cc.valdemar.foz.fozquests.players.QPlayer;
import cc.valdemar.foz.fozquests.quests.ActiveQuest;
import cc.valdemar.foz.fozquests.quests.Quest;
import cc.valdemar.foz.fozquests.utils.ChatUtil;
import cc.valdemar.foz.fozquests.utils.config.LocaleReference;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@ConfigSerializable
public class QuestListMenu implements LocaleReference {
    @Comment("The title for the menu")
    @Setting
    private final Component title = MiniMessage.miniMessage().deserialize("<blue>Your quests");

    public void open(Player player) {
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

                    for (Quest quest : qPlayer.getAvailableQuests()) {
                        GuiItem guiItem = ItemBuilder.from(quest.getIcon()).asGuiItem(event -> {
                            // Set active quest
                            if(qPlayer.getActiveQuest() == null || !qPlayer.getActiveQuest().getQuest().equals(quest)) {
                                // Player doesn't have an active quest or player's active quest is not equal to new quest
                                qPlayer.setActiveQuest(new ActiveQuest(quest, 0));
                            }
                            consumer.close(player);
                            player.sendMessage(ChatUtil.deserialize(
                                    Messages.getInstance().getQuestActivated(),
                                    Placeholder.parsed("quest", quest.getName())
                            ));
                        });
                        consumer.addItem(guiItem);
                    }
                }).create();

        gui.open(player);
    }
}
