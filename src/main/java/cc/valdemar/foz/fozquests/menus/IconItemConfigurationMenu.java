package cc.valdemar.foz.fozquests.menus;

import cc.valdemar.foz.fozquests.FozQuests;
import cc.valdemar.foz.fozquests.config.menu.IconMenuSettings;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class IconItemConfigurationMenu extends ItemConfigurationMenu {
    private final Component title;
    private final ItemStack configureName;
    private final ItemStack configureLore;
    private final ItemStack finish;

    public IconItemConfigurationMenu(Player player, Material iconMaterial, MainCreationMenu mainCreationMenu) {
        super(player, mainCreationMenu);

        IconMenuSettings settings = FozQuests.getInstance().getConfigManager().getConfig(IconMenuSettings.class, "icon-menu-settings");
        title = settings.getTitle();
        item = settings.getTemplate().clone();
        item.setType(iconMaterial);
        configureName = settings.getCreationName().clone();
        configureLore = settings.getCreationLore().clone();
        finish = settings.getFinish().clone();
    }

    @Override
    public void open() {
        Gui gui = Gui.gui()
                .rows(6)
                .title(title)
                .disableAllInteractions()
                .apply(consumer -> {
                    GuiItem iconItem = ItemBuilder.from(item).asGuiItem();
                    consumer.setItem(4, iconItem);

                    //Icon lore editor
                    GuiItem loreItem = ItemBuilder.from(configureLore).asGuiItem(event -> {
                        sign = getSign(player);
                        if(sign == null) return;

                        sign.setEditable(true);

                        Bukkit.getScheduler().runTaskLater(FozQuests.getInstance(), () -> player.openSign(sign), 3L);

                        Bukkit.getPluginManager().registerEvents(listener, FozQuests.getInstance());
                        registerLoreSignUpdater();
                    });
                    consumer.setItem(11, loreItem);


                    //Icon name editor
                    GuiItem nameItem = ItemBuilder.from(configureName).asGuiItem(event -> {
                        sign = getSign(player);
                        if(sign == null) return;

                        sign.setEditable(true);

                        Bukkit.getScheduler().runTaskLater(FozQuests.getInstance(), () -> player.openSign(sign), 3L);

                        Bukkit.getPluginManager().registerEvents(listener, FozQuests.getInstance());
                        registerNameSignUpdater();
                    });
                    consumer.setItem(15, nameItem);

                    //Finish editing icon
                    GuiItem finishItem = ItemBuilder.from(finish).asGuiItem(event -> {
                        mainCreationMenu.open();
                    });
                    consumer.setItem(40, finishItem);
                }).create();

        gui.open(player);
    }
}
