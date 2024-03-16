package cc.valdemar.foz.fozquests.listeners.data;

import cc.valdemar.foz.fozquests.FozQuests;
import cc.valdemar.foz.fozquests.players.PlayerManager;
import cc.valdemar.foz.fozquests.players.QPlayer;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;
@RequiredArgsConstructor
public class PlayerQuitListener implements Listener {

    private final PlayerManager playerManager;

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        run(e.getPlayer().getUniqueId());
    }

    private void run(UUID uuid){
        playerManager.getLeaveTasks().put(uuid, new BukkitRunnable(){
            @Override
            public void run(){
                QPlayer qPlayer = playerManager.getQPlayer(uuid);
                playerManager.removePlayer(qPlayer.getUuid());
            }
        }.runTaskLater(FozQuests.getInstance(), 300));
    }
}
