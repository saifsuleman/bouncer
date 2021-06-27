package net.saifs.purplebouncer.bouncers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.EncoderException;
import net.minecraft.server.v1_12_R1.PacketDataSerializer;
import net.minecraft.server.v1_12_R1.PacketPlayOutCustomPayload;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.scheduler.BukkitScheduler;

import java.nio.charset.StandardCharsets;

public class LabyBouncer implements PluginMessageListener {
    private JavaPlugin plugin;

    public LabyBouncer(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    private String readString(ByteBuf buf) {
        int i = readVarIntFromBuffer(buf);
        byte[] bytes = new byte[i];
        buf.readBytes(bytes);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    private int readVarIntFromBuffer(ByteBuf buf) {
        int i = 0;
        int j = 0;

        byte b0;
        do {
            b0 = buf.readByte();
            i |= (b0 & 127) << j++ * 7;
            if (j > 5) {
                throw new RuntimeException("VarInt too big");
            }
        } while ((b0 & 128) == 128);

        return i;
    }

    public void sendLabyModMessage(Player player, String key, JsonElement messageContent) {
        byte[] bytes = getBytesToSend(key, messageContent.toString());
        PacketDataSerializer pds = new PacketDataSerializer(Unpooled.wrappedBuffer(bytes));
        PacketPlayOutCustomPayload payloadPacket = new PacketPlayOutCustomPayload("labymod3:main", pds);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(payloadPacket);
    }

    private void sendBlockedAddons(Player player) {
        JsonObject object = new JsonObject();
        object.addProperty("CHAT", false);
        plugin.getServer().getScheduler().runTask(plugin, () ->
                sendLabyModMessage(player, "PERMISSIONS", object));
    }

    private byte[] getBytesToSend(String messageKey, String messageContents) {
        ByteBuf byteBuf = Unpooled.buffer();

        writeString(byteBuf, messageKey);

        writeString(byteBuf, messageContents);

        byte[] bytes = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(bytes);

        byteBuf.release();

        return bytes;
    }

    private void writeString(ByteBuf buf, String string) {
        byte[] abyte = string.getBytes(StandardCharsets.UTF_8);

        if (abyte.length > Short.MAX_VALUE) {
            throw new EncoderException("String too big (was " + string.length() + " bytes encoded, max " + Short.MAX_VALUE + ")");
        } else {
            writeVarIntToBuffer(buf, abyte.length);
            buf.writeBytes(abyte);
        }
    }

    private void writeVarIntToBuffer(ByteBuf buf, int input) {
        while ((input & -128) != 0) {
            buf.writeByte(input & 127 | 128);
            input >>>= 7;
        }

        buf.writeByte(input);
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!channel.equals("labymod3:main")) {
            return;
        }

        BukkitScheduler scheduler = this.plugin.getServer().getScheduler();
        scheduler.runTaskAsynchronously(plugin, () -> {
            ByteBuf buf = Unpooled.wrappedBuffer(message);

            String key = this.readString(buf);
            if (key.equals("INFO")) {
                this.sendBlockedAddons(player);
            }
        });
    }
}
