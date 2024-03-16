package cc.valdemar.foz.fozquests.quests;

import lombok.Getter;
import org.bukkit.inventory.ItemStack;

@Getter
public class Reward {
    private final ItemStack itemStack;

    public Reward(ItemStack itemStack) {
        this.itemStack = itemStack;
    }
}
