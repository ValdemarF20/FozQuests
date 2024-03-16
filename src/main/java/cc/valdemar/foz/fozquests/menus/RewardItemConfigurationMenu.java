package cc.valdemar.foz.fozquests.menus;

import cc.valdemar.foz.fozquests.FozQuests;
import cc.valdemar.foz.fozquests.config.menu.RewardMenuSettings;
import cc.valdemar.foz.fozquests.quests.Reward;
import cc.valdemar.foz.fozquests.utils.ChatUtil;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class RewardItemConfigurationMenu extends ItemConfigurationMenu {
    private final Component title;

    @Getter
    private final Reward reward;
    private final ItemStack configureName;
    private final ItemStack configureLore;
    private final ItemStack configureAmount;
    private final ItemStack finish;

    public RewardItemConfigurationMenu(Player player, Material rewardMaterial, MainCreationMenu mainCreationMenu) {
        super(player, mainCreationMenu);

        RewardMenuSettings settings = FozQuests.getInstance().getConfigManager().getConfig(RewardMenuSettings.class, "reward-menu-settings");
        reward = new Reward(settings.getTemplate().clone());
        item = reward.getItemStack();
        item.setType(rewardMaterial);
        title = settings.getTitle();
        configureName = settings.getCreationName().clone();
        configureLore = settings.getCreationLore().clone();
        configureAmount = settings.getCreationAmount().clone();
        finish = settings.getFinish().clone();
    }

    public void open() {
        Gui gui = Gui.gui()
                .rows(6)
                .title(title)
                .disableAllInteractions()
                .apply(consumer -> {
                    GuiItem iconItem = ItemBuilder.from(item).asGuiItem();
                    consumer.setItem(4, iconItem);

                    //Reward lore editor
                    GuiItem loreItem = ItemBuilder.from(configureLore).asGuiItem(event -> {
                        sign = getSign(player);
                        if(sign == null) return;

                        sign.setEditable(true);

                        Bukkit.getScheduler().runTaskLater(FozQuests.getInstance(), () -> player.openSign(sign), 3L);

                        Bukkit.getPluginManager().registerEvents(listener, FozQuests.getInstance());
                        registerLoreSignUpdater();
                    });
                    consumer.setItem(11, loreItem);

                    //Reward name editor
                    GuiItem nameItem = ItemBuilder.from(configureName).asGuiItem(event -> {
                        sign = getSign(player);
                        if(sign == null) return;

                        sign.setEditable(true);

                        Bukkit.getScheduler().runTaskLater(FozQuests.getInstance(), () -> player.openSign(sign), 3L);

                        Bukkit.getPluginManager().registerEvents(listener, FozQuests.getInstance());
                        registerNameSignUpdater();
                    });
                    consumer.setItem(15, nameItem);

                    //Reward amount editor
                    GuiItem amountItem = ItemBuilder.from(configureAmount).asGuiItem(event -> {
                        sign = getSign(player);
                        if(sign == null) return;

                        sign.setEditable(true);

                        sign.line(1, ChatUtil.deserialize("<blue>^^ Insert Amount ^^"));

                        sign.update(false, false);

                        Bukkit.getScheduler().runTaskLater(FozQuests.getInstance(), () -> player.openSign(sign), 3L);

                        Bukkit.getPluginManager().registerEvents(listener, FozQuests.getInstance());
                        registerAmountSignUpdater();
                    });
                    consumer.setItem(22, amountItem);


                    //Finish editing reward
                    GuiItem finishItem = ItemBuilder.from(finish).asGuiItem(event -> {
                        mainCreationMenu.open();
                    });
                    consumer.setItem(40, finishItem);

                }).create();

        gui.open(player);
    }
}
