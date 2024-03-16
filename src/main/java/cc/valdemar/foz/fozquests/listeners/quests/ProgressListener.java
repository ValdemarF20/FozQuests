package cc.valdemar.foz.fozquests.listeners.quests;

import cc.valdemar.foz.fozquests.config.Messages;
import cc.valdemar.foz.fozquests.players.PlayerManager;
import cc.valdemar.foz.fozquests.players.QPlayer;
import cc.valdemar.foz.fozquests.quests.ActiveQuest;
import cc.valdemar.foz.fozquests.quests.impl.BreakQuest;
import cc.valdemar.foz.fozquests.quests.impl.ExploreQuest;
import cc.valdemar.foz.fozquests.quests.impl.KillQuest;
import cc.valdemar.foz.fozquests.utils.ChatUtil;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;

@RequiredArgsConstructor
public class ProgressListener implements Listener { //TODO collecting items
    private final PlayerManager playerManager;

    @EventHandler
    public void onKill(EntityDamageByEntityEvent event) {
        if(!(event.getEntity() instanceof LivingEntity defender)) return;
        double damage = event.getDamage();
        double health = defender.getHealth();
        if(health - damage > 0) return;
        if(!(event.getDamager() instanceof Player player)) return;

        // Entity was killed by a player
        QPlayer qPlayer = playerManager.getQPlayer(player.getUniqueId());
        if(qPlayer == null) return;
        ActiveQuest activeQuest = qPlayer.getActiveQuest();

        if(activeQuest == null || !(activeQuest.getQuest() instanceof KillQuest killQuest)) {
            return;
        }
        if(killQuest.getEntityType() != defender.getType()) return;

        progress(qPlayer, player, activeQuest);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        QPlayer qPlayer = playerManager.getQPlayer(player.getUniqueId());
        if(qPlayer == null) return;

        ActiveQuest activeQuest = qPlayer.getActiveQuest();
        if(activeQuest == null || !(activeQuest.getQuest() instanceof ExploreQuest exploreQuest)) {
            return;
        }

        // Player has to be within 5 blocks
        if(player.getLocation().distance(exploreQuest.getLocation()) > 5) return;

        progress(qPlayer, player, activeQuest);
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        QPlayer qPlayer = playerManager.getQPlayer(player.getUniqueId());
        if(qPlayer == null) return;

        ActiveQuest activeQuest = qPlayer.getActiveQuest();
        if(activeQuest == null || !(activeQuest.getQuest() instanceof BreakQuest breakQuest)) {
            return;
        }

        // Block type must match quest block type
        Material block = breakQuest.getBlock();
        if(!block.isBlock() || block != event.getBlock().getType()) return;

        progress(qPlayer, player, activeQuest);
    }

    private void progress(QPlayer qPlayer, Player player, ActiveQuest activeQuest) {
        if(qPlayer.updateProgress()) {
            player.sendMessage(ChatUtil.deserialize(
                    Messages.getInstance().getQuestCompleted(),
                    Placeholder.parsed("quest", activeQuest.getQuest().getName()))
            );
        }
    }
}
