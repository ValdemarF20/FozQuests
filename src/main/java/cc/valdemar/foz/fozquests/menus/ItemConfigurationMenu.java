package cc.valdemar.foz.fozquests.menus;

import cc.valdemar.foz.fozquests.FozQuests;
import cc.valdemar.foz.fozquests.utils.ChatUtil;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public abstract class ItemConfigurationMenu {
    protected final MainCreationMenu mainCreationMenu;
    protected PacketAdapter packetListener;
    protected final LeaveListener listener = new LeaveListener();
    protected Sign sign;

    protected final Player player;
    @Getter
    protected ItemStack item;

    public ItemConfigurationMenu(Player player, MainCreationMenu mainCreationMenu) {
        this.player = player;
        this.mainCreationMenu = mainCreationMenu;
    }

    public abstract void open();

    /**
     * This method places a sign above the player (far away) to act as a virtual sign
     * @param player Player to get a sign for
     * @return The Sign instance, null if it could not be placed at given location (no available block)
     */
    protected Sign getSign(Player player) {
        int x_start = player.getLocation().getBlockX();
        int y_start = 255;
        int z_start = player.getLocation().getBlockZ();

        Material material = Material.getMaterial("WALL_SIGN");
        if (material == null)
            material = Material.OAK_WALL_SIGN;

        while (!player.getWorld().getBlockAt(x_start, y_start, z_start).getType().equals(Material.AIR) && !player.getWorld().getBlockAt(x_start, y_start, z_start).getType().equals(material)) {
            y_start--;
            if (y_start == 1)
                return null;
        }

        player.getWorld().getBlockAt(x_start, y_start, z_start).setType(material);
        return (Sign) player.getWorld().getBlockAt(x_start, y_start, z_start).getState();
    }

    protected class LeaveListener implements Listener {
        @EventHandler
        public void onLeave(PlayerQuitEvent e){
            if(e.getPlayer().equals(player)){
                ProtocolLibrary.getProtocolManager().removePacketListener(packetListener);
                HandlerList.unregisterAll(this);

                sign.getBlock().setType(Material.AIR);
            }
        }
    }

    protected void registerLoreSignUpdater() {
        ProtocolManager manager = ProtocolLibrary.getProtocolManager();
        packetListener = new PacketAdapter(FozQuests.getInstance(), PacketType.Play.Client.UPDATE_SIGN) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                if(event.getPlayer().equals(player)) {
                    String[] input = event.getPacket().getStringArrays().read(0);

                    Bukkit.getScheduler().runTask(FozQuests.getInstance(), () -> {
                        List<Component> lore = new ArrayList<>(Arrays.stream(input).map(ChatUtil::deserialize).toList());

                        // Remove last blank lines
                        Collections.reverse(lore); // Ignore null
                        for (Component component : new ArrayList<>(lore)) {
                            if(ChatUtil.serializePlain(component).isBlank()) {
                                lore.remove(component);
                            } else {
                                break;
                            }
                        }
                        Collections.reverse(lore);

                        item.editMeta(meta -> {
                            meta.lore(lore);
                        });


                        manager.removePacketListener(this);
                        HandlerList.unregisterAll(listener);

                        sign.getBlock().setType(Material.AIR);

                        open();
                    });
                }
            }
        };

        manager.addPacketListener(packetListener);
    }

    protected void registerNameSignUpdater() {
        ProtocolManager manager = ProtocolLibrary.getProtocolManager();
        packetListener = new PacketAdapter(FozQuests.getInstance(), PacketType.Play.Client.UPDATE_SIGN) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                if(event.getPlayer().equals(player)) {
                    String[] input = event.getPacket().getStringArrays().read(0);

                    Bukkit.getScheduler().runTask(FozQuests.getInstance(), () -> {

                        item.editMeta(meta -> {
                            meta.displayName(ChatUtil.deserialize(String.join(" ", input)));
                        });

                        manager.removePacketListener(this);
                        HandlerList.unregisterAll(listener);

                        sign.getBlock().setType(Material.AIR);

                        open();
                    });
                }
            }
        };

        manager.addPacketListener(packetListener);
    }

    protected void registerAmountSignUpdater() {
        ProtocolManager manager = ProtocolLibrary.getProtocolManager();
        packetListener = new PacketAdapter(FozQuests.getInstance(), PacketType.Play.Client.UPDATE_SIGN) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                if(event.getPlayer().equals(player)) {
                    String[] input = event.getPacket().getStringArrays().read(0);

                    Bukkit.getScheduler().runTask(FozQuests.getInstance(), () -> {
                        try {
                            item.setAmount(Integer.parseInt(input[0]));
                        } catch (NumberFormatException exception) {
                            player.sendMessage(ChatUtil.deserialize("<red>Invalid Number"));
                        }

                        manager.removePacketListener(this);
                        HandlerList.unregisterAll(listener);

                        sign.getBlock().setType(Material.AIR);

                        open();
                    });
                }
            }
        };

        manager.addPacketListener(packetListener);
    }
}
