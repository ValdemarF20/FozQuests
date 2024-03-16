package cc.valdemar.foz.fozquests.quests.impl;

import cc.valdemar.foz.fozquests.quests.Quest;
import cc.valdemar.foz.fozquests.quests.QuestType;
import cc.valdemar.foz.fozquests.quests.Reward;
import lombok.Getter;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

@Getter
public class KillQuest extends Quest {
    private transient final EntityType entityType;
    public KillQuest(String identifier, ItemStack icon, Reward reward, int requirement, QuestType questType, EntityType entityType, boolean custom) {
        super(identifier, icon, reward, requirement, questType, entityType.toString(), custom);
        this.entityType = entityType;
    }
}
