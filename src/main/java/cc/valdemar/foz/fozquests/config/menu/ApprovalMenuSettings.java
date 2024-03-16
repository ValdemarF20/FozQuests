package cc.valdemar.foz.fozquests.config.menu;

import cc.valdemar.foz.fozquests.utils.ChatUtil;
import cc.valdemar.foz.fozquests.utils.config.LocaleReference;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@Getter
@ConfigSerializable
public class ApprovalMenuSettings implements LocaleReference {
    @Comment("The title used for the menu")
    @Setting
    private final String title = "<blue>Approval for <player>";

    @Comment("The button for approving the request")
    @Setting
    private final ItemStack approve = ItemBuilder.from(Material.GREEN_WOOL)
            .name(ChatUtil.deserialize("<green>Approve")).build();

    @Comment("The button for approving the request")
    @Setting
    private final ItemStack deny = ItemBuilder.from(Material.RED_WOOL)
            .name(ChatUtil.deserialize("<green>Deny")).build();
}
