package cc.valdemar.foz.fozquests.quests;

import lombok.Getter;
import org.bukkit.entity.Player;

public class ActiveQuest {
    private int progress = 0;
    @Getter
    private final Quest quest;

    public ActiveQuest(Quest quest, int progress) {
        this.quest = quest;
        this.progress = progress;
    }

    public boolean updateProgress() {
        return updateProgress(1);
    }

    public boolean updateProgress(int change) {
        int newProgress = progress + change;
        if(newProgress >= quest.getAmount()) {
            return true;
        }
        progress = newProgress;
        return false;
    }

    public void giveRewards(Player player) {
        quest.giveRewards(player);
    }
}
