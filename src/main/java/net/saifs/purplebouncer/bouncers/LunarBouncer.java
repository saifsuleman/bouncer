package net.saifs.purplebouncer.bouncers;

import com.lunarclient.bukkitapi.LunarClientAPI;
import com.lunarclient.bukkitapi.nethandler.client.LCPacketModSettings;
import com.lunarclient.bukkitapi.nethandler.client.obj.ModSettings;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.HashMap;

public class LunarBouncer implements Listener {
    private LCPacketModSettings packet;

    public LunarBouncer() {
        ModSettings.ModSetting disabled = new ModSettings.ModSetting(false, new HashMap<>());
        this.packet = new LCPacketModSettings(new ModSettings().addModSetting("textHotKey", disabled));
    }

    public void sendPolicy(Player player) {
        Bukkit.broadcastMessage("sent");
        LunarClientAPI.getInstance().sendPacket(player, this.packet);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        Bukkit.broadcastMessage(String.valueOf(LunarClientAPI.getInstance().isRunningLunarClient(player)));
        sendPolicy(player);
    }
}
