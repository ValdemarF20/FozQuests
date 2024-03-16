package cc.valdemar.foz.fozquests.quests.impl;

import cc.valdemar.foz.fozquests.quests.Quest;
import cc.valdemar.foz.fozquests.quests.QuestType;
import cc.valdemar.foz.fozquests.quests.Reward;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

@Getter
public class ExploreQuest extends Quest {
    private transient final Location location;
    public ExploreQuest(String identifier, ItemStack icon, Reward reward, int requirement, QuestType questType, Location location, boolean custom) {
        super(identifier, icon, reward, requirement, questType, location.getWorld().getName() + ":" + location.x() + ":" + location.y() + ":" + location.z(), custom);
        this.location = location;
    }
}
