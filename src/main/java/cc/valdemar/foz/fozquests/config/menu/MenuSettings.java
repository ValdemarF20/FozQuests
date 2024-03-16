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
public class MenuSettings implements LocaleReference {
    @Comment("Title used in creation menu")
    @Setting
    private final Component creationTitle = ChatUtil.deserialize("Quest Creation Menu");

    @Comment("The item used in quest creation menu for finishing building the quest")
    @Setting
    private final ItemStack creationFinish = ItemBuilder.from(Material.DIAMOND)
            .name(ChatUtil.deserialize("<green>Finish Creation")).build();
}
