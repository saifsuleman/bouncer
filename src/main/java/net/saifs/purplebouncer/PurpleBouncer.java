package net.saifs.purplebouncer;

import net.saifs.purplebouncer.bouncers.BadlionBouncer;
import net.saifs.purplebouncer.bouncers.LabyBouncer;
import net.saifs.purplebouncer.bouncers.LunarBouncer;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public final class PurpleBouncer extends JavaPlugin implements Listener {
    @Override
    public void onEnable() {
        // Plugin startup logic
        this.getServer().getPluginManager().registerEvents(this, this);
        LabyBouncer labyBouncer = new LabyBouncer(this);
        LunarBouncer lunarBouncer = new LunarBouncer();
        BadlionBouncer badlionBouncer = new BadlionBouncer(this);

        getServer().getPluginManager().registerEvents(lunarBouncer, this);
        getServer().getPluginManager().registerEvents(badlionBouncer, this);
        getServer().getMessenger().registerIncomingPluginChannel(this, "labymod3:main",
                labyBouncer);
        getServer().getMessenger().registerOutgoingPluginChannel(this, "labymod3:main");
        getServer().getMessenger().registerOutgoingPluginChannel(this, "badlion:mods");
    }

}
