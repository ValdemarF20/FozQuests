package cc.valdemar.foz.fozquests.players;

import cc.valdemar.foz.fozquests.FozQuests;
import cc.valdemar.foz.fozquests.quests.ActiveQuest;
import cc.valdemar.foz.fozquests.quests.Quest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class QPlayer {
    private final UUID uuid;
    @Setter @Nullable
    private ActiveQuest activeQuest;
    private final int completions;

    public boolean updateProgress() {
        return updateProgress(1);
    }

    public boolean updateProgress(int progress) {
        if(activeQuest == null) return false;
        if(activeQuest.updateProgress(progress)) {
            activeQuest.giveRewards(getPlayer());
            FozQuests.getInstance().getQuestManager().addCompleted(uuid);
            activeQuest = null;
            return true;
        }
        return false;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    public Collection<Quest> getAvailableQuests() {
        return FozQuests.getInstance().getQuestRegistry().getAll();
    }
}
