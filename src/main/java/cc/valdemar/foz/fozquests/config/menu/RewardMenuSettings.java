package cc.valdemar.foz.fozquests.config.menu;

import cc.valdemar.foz.fozquests.utils.ChatUtil;
import cc.valdemar.foz.fozquests.utils.config.LocaleReference;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@Getter
@ConfigSerializable
public class RewardMenuSettings implements LocaleReference {
    @Comment("The title used for the menu")
    @Setting
    private final Component title = ChatUtil.deserialize("<blue>Quest Reward Configuration Menu");

    @Comment("The item used in \"/q create\" as button for reward")
    @Setting
    private final ItemStack template = ItemBuilder.from(Material.PAPER)
            .name(ChatUtil.deserialize("Change reward item"))
            .lore(
                    ChatUtil.deserialize(""),
                    ChatUtil.deserialize("<green>Configure the reward")
            ).build();

    @Comment("The name configurator for the reward")
    @Setting
    private final ItemStack creationName = ItemBuilder.from(Material.PAPER)
            .name(ChatUtil.deserialize("Change name"))
            .lore(
                    ChatUtil.deserialize(""),
                    ChatUtil.deserialize("<green>Configure the name")
            ).build();

    @Comment("The lore configurator for the reward")
    @Setting
    private final ItemStack creationLore = ItemBuilder.from(Material.PAPER)
            .name(ChatUtil.deserialize("Change lore"))
            .lore(
                    ChatUtil.deserialize(""),
                    ChatUtil.deserialize("<green>Configure the lore")
            ).build();

    @Comment("The lore configurator for the reward")
    @Setting
    private final ItemStack creationAmount = ItemBuilder.from(Material.PAPER)
            .name(ChatUtil.deserialize("Change amount"))
            .lore(
                    ChatUtil.deserialize(""),
                    ChatUtil.deserialize("<green>Configure the amount for the item")
            ).build();

    @Comment("The button for finishing configuration")
    @Setting
    private final ItemStack finish = ItemBuilder.from(Material.ENCHANTED_BOOK)
            .name(ChatUtil.deserialize("Finish configuration")).build();
}
