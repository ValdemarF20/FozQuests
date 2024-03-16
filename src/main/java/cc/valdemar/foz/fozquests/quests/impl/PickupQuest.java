package cc.valdemar.foz.fozquests.quests.impl;

import cc.valdemar.foz.fozquests.quests.Quest;
import cc.valdemar.foz.fozquests.quests.QuestType;
import cc.valdemar.foz.fozquests.quests.Reward;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@Getter
public class PickupQuest extends Quest {
    private transient final Material pickup;
    public PickupQuest(String identifier, ItemStack icon, Reward reward, int requirement, QuestType questType, Material pickup, boolean custom) {
        super(identifier, icon, reward, requirement, questType, pickup.toString(), custom);
        this.pickup = pickup;
    }
}
