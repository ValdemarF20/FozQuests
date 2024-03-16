package cc.valdemar.foz.fozquests.listeners.data;

import cc.valdemar.foz.fozquests.players.PlayerManager;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequiredArgsConstructor
public class PlayerJoinListener implements Listener {
    private static final Logger LOGGER = LoggerFactory.getLogger(PlayerJoinListener.class);
    private final PlayerManager playerManager;

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();

        playerManager.loadPlayer(player.getUniqueId()).whenComplete((input, exception) -> {
            if(exception != null) {
                LOGGER.error("Exception happened when loading player: " + player.getName(), exception);
            }
        });
    }
}
