package cc.valdemar.foz.fozquests.quests;

import java.util.Locale;

public enum QuestType {
    BLOCK_BREAK,
    KILL_ENTITY,
    EXPLORE,
    PICKUP;

    public String displayName() {
        return toString().replace("_", " ").toLowerCase(Locale.ENGLISH);
    }
}
