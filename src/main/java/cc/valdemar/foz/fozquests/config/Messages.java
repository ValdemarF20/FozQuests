package cc.valdemar.foz.fozquests.config;

import cc.valdemar.foz.fozquests.utils.ChatUtil;
import cc.valdemar.foz.fozquests.utils.config.LocaleReference;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@Getter
@ConfigSerializable
public class Messages implements LocaleReference {
    @Getter
    private static Messages instance;

    private Messages() {
        instance = this;
    }

    @Comment("Message to be sent if player tries to create a quest that already exists")
    @Setting
    private final Component questAlreadyExists = ChatUtil.deserialize("<red>A quest with this identifier already exists");

    @Comment("Sends when a player has activated a quest")
    @Setting
    private final String questActivated = "<green>Activated quest: <quest>";

    @Comment("Sends when player has completed a quest")
    @Setting
    private final String questCompleted = "<green>You have completed <quest>!";

    @Comment("Sends when player attempts to create invalid quest (name includes special chars)")
    @Setting
    private final Component invalidQuestSettings = ChatUtil.deserialize("<red>Invalid quest settings - /quest help");

    @Comment("Sends when player attempts to delete invalid quest (wrong identifier)")
    @Setting
    private final Component invalidQuest = ChatUtil.deserialize("<red>Invalid quest identifier - must match the exact name of the quest");

    @Comment("Sends when a player attempts to create a new custom quest while another is pending approval")
    @Setting
    private final Component alreadyPending = ChatUtil.deserialize("<red>You already have a pending quest. Please wait for an admin to approve");

    @Comment("Sends when the console attempts to send a player-only command")
    @Setting
    private final Component playersOnlyCommand = ChatUtil.deserialize("<red>Only players can execute this command");

    @Comment("Sends when an admin attempts to approve request by a player that hasn't created one")
    @Setting
    private final Component noExistingRequest = ChatUtil.deserialize("<red>Player does not have an active request");

    @Comment("Sends when a player's request has been approved (if player is online)")
    @Setting
    private final Component requestApproved = ChatUtil.deserialize("<red>Your quest request has been approved!");

    @Comment("Sends when a player's request has been denied (if player is online)")
    @Setting
    private final Component requestDenied = ChatUtil.deserialize("<red>Your quest request has been denied");
}
