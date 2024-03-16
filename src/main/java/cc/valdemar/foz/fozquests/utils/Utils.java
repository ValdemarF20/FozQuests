package cc.valdemar.foz.fozquests.utils;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class Utils {
    public static ItemStack getPlayerSkull(Player paramPlayer) {
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);

        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        meta.setOwningPlayer(paramPlayer);
        meta.displayName(paramPlayer.displayName());
        skull.setItemMeta(meta);
        return skull;
    }
}
