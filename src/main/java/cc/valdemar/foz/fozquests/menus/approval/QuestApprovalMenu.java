package cc.valdemar.foz.fozquests.menus.approval;

import cc.valdemar.foz.fozquests.config.Messages;
import cc.valdemar.foz.fozquests.config.menu.ApprovalMenuSettings;
import cc.valdemar.foz.fozquests.quests.Quest;
import cc.valdemar.foz.fozquests.quests.QuestManager;
import cc.valdemar.foz.fozquests.quests.Request;
import cc.valdemar.foz.fozquests.utils.ChatUtil;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
public class QuestApprovalMenu {
    private final Player player;
    private final Request request;
    private final ApprovalMenuSettings settings;
    private final QuestManager questManager;

    public void open() {
        OfflinePlayer creator = Bukkit.getOfflinePlayer(request.uuid());
        Player onlineCreator = creator.getPlayer();
        Quest quest = request.quest();

        Gui gui = Gui.gui()
                .rows(6)
                .title(ChatUtil.deserialize(settings.getTitle(), Placeholder.parsed("player", Objects.requireNonNull(creator.getName()))))
                .disableAllInteractions()
                .apply(consumer -> {
                    GuiItem iconItem = ItemBuilder.from(quest.getIcon()).asGuiItem();
                    consumer.setItem(11, iconItem);

                    GuiItem rewardItem = ItemBuilder.from(quest.getReward().getItemStack()).asGuiItem();
                    consumer.setItem(15, rewardItem);

                    ItemStack requirementsItemStack = new ItemStack(Material.PAPER);
                    requirementsItemStack.editMeta(meta -> {
                        meta.displayName(ChatUtil.deserialize("<green>Requirements"));

                        List<Component> lore = new ArrayList<>();
                        lore.add(ChatUtil.deserialize(""));
                        lore.add(ChatUtil.deserialize("<blue>Type: " + quest.getQuestType().displayName()));
                        lore.add(ChatUtil.deserialize("<blue>Requirement: " + quest.getArgs()));
                        if(quest.getAmount() > 1) lore.add(ChatUtil.deserialize("<blue>Amount: " + quest.getAmount()));
                        meta.lore(lore);
                    });
                    GuiItem requirementsItem = ItemBuilder.from(requirementsItemStack).asGuiItem();
                    consumer.setItem(22, requirementsItem);

                    GuiItem approveItem = ItemBuilder.from(settings.getApprove()).asGuiItem(event -> {
                        questManager.accept(player, request);
                        if(onlineCreator != null) {
                            if(questManager.getRequest(onlineCreator.getUniqueId()) != null) {
                                onlineCreator.sendMessage(Messages.getInstance().getRequestApproved());
                            } else {
                                onlineCreator.sendMessage(Messages.getInstance().getRequestDenied());
                            }
                            consumer.close(player);
                        }
                    });
                    consumer.setItem(30, approveItem);

                    GuiItem denyItem = ItemBuilder.from(settings.getDeny()).asGuiItem(event -> {
                        questManager.deny(request);
                        if(onlineCreator != null) {
                            onlineCreator.sendMessage(Messages.getInstance().getRequestDenied());
                            consumer.close(player);
                        }
                    });
                    consumer.setItem(32, denyItem);

                }).create();

        gui.open(player);
    }
}
