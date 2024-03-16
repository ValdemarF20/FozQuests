package cc.valdemar.foz.fozquests.quests;

import cc.valdemar.foz.fozquests.utils.ChatUtil;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@Getter
public class Quest {
    private final String identifier;
    private final QuestType questType;
    private final ItemStack icon;
    private final String name;
    private final int amount;
    private final Reward reward;
    private final String args;
    private final boolean custom;

    public Quest(String identifier, ItemStack icon, Reward reward, int amount, QuestType questType, String args, boolean custom) {
        this.identifier = identifier;
        this.icon = icon;
        name = ChatUtil.serializePlain(icon.displayName());
        this.reward = reward;
        this.amount = amount;
        this.questType = questType;
        this.args = args;
        this.custom = custom;
    }


    @Override
    public String toString() {
        return identifier;
    }

    public void giveRewards(Player player) {
        ItemStack rewardItem = reward.getItemStack().clone();
        ItemStack newRewardItem = new ItemStack(rewardItem.getType());
        newRewardItem.setAmount(rewardItem.getAmount());
        newRewardItem.editMeta(meta -> {
            meta.displayName(rewardItem.displayName());
            meta.lore(rewardItem.lore());
        });

        player.getInventory().addItem(newRewardItem);
    }
}
