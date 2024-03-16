package cc.valdemar.foz.fozquests.menus;

import cc.valdemar.foz.fozquests.FozQuests;
import cc.valdemar.foz.fozquests.config.menu.MenuSettings;
import cc.valdemar.foz.fozquests.config.Messages;
import cc.valdemar.foz.fozquests.quests.Quest;
import cc.valdemar.foz.fozquests.quests.QuestType;
import cc.valdemar.foz.fozquests.utils.ChatUtil;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class MainCreationMenu {
    private final RewardItemConfigurationMenu rewardCreationMenu;
    private final IconItemConfigurationMenu iconCreationMenu;

    private final Component title;
    private final ItemStack finish;

    private final Player player;
    private final int amount;
    private final QuestType questType;
    private final String type;
    private final boolean custom;

    public MainCreationMenu(Player player, Material iconMaterial, Material rewardMaterial, int amount, QuestType questType, String type, boolean custom) {
        this.player = player;
        this.amount = amount;
        this.questType = questType;
        this.type = type;
        this.custom = custom;

        rewardCreationMenu = new RewardItemConfigurationMenu(player, rewardMaterial, this);
        iconCreationMenu = new IconItemConfigurationMenu(player, iconMaterial, this);

        MenuSettings menuSettings = FozQuests.getInstance().getConfigManager().getConfig(MenuSettings.class, "menu-settings");
        title = menuSettings.getCreationTitle();
        finish = menuSettings.getCreationFinish().clone();
    }

    public void open() {
        Gui gui = Gui.gui()
                .rows(6)
                .title(title)
                .disableAllInteractions()
                .apply(consumer -> {
                    //Configure icon
                    GuiItem iconItem = ItemBuilder.from(iconCreationMenu.getItem()).asGuiItem(event -> {
                        iconCreationMenu.open();
                    });
                    consumer.setItem(11, iconItem);

                    //Configure icon
                    GuiItem rewardItem = ItemBuilder.from(rewardCreationMenu.getReward().getItemStack()).asGuiItem(event -> {
                        rewardCreationMenu.open();
                    });
                    consumer.setItem(15, rewardItem);

                    //Finish quest
                    GuiItem finishItem = ItemBuilder.from(finish).asGuiItem(event -> {
                        String identifier = ChatUtil.serializePlain(iconCreationMenu.getItem().displayName());
                        identifier = identifier.replace(" ", "-");
                        Quest newQuest = FozQuests.getInstance().getQuestManager().createQuest(
                                player.getUniqueId() + "-" + identifier,
                                iconCreationMenu.getItem(),
                                rewardCreationMenu.getReward(),
                                amount,
                                questType,
                                type,
                                custom
                        );

                        try {
                            if(custom) {
                                FozQuests.getInstance().getQuestManager().addRequest(player.getUniqueId(), newQuest);
                            } else {
                                FozQuests.getInstance().getQuestRegistry().register(newQuest);
                            }
                        } catch (IllegalArgumentException exception) {
                            player.sendMessage(Messages.getInstance().getQuestAlreadyExists());
                        }
                        consumer.close(player);
                    });
                    consumer.setItem(31, finishItem);

                    consumer.getFiller().fill(new GuiItem(Material.BLACK_STAINED_GLASS));
                }).create();

        gui.open(player);
    }
}
