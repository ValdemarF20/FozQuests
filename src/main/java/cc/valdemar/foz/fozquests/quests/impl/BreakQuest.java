package cc.valdemar.foz.fozquests.quests.impl;

import cc.valdemar.foz.fozquests.quests.Quest;
import cc.valdemar.foz.fozquests.quests.QuestType;
import cc.valdemar.foz.fozquests.quests.Reward;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@Getter
public class BreakQuest extends Quest {
    private transient final Material block;
    public BreakQuest(String identifier, ItemStack icon, Reward reward, int requirement, QuestType questType, Material block, boolean custom) {
        super(identifier, icon, reward, requirement, questType, block.toString(), custom);
        this.block = block;
    }
}
