package fr.watch54.bridge.utils;

import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class BarBuilder {

    private Player player;
    private String title;
    private ChatColor color;

    /**
     * A constructor to create custom action bar
     * @param player Define the player to send the bar
     */
    public BarBuilder(Player player){
        this.player = player;

    }

    /**
     * Define the title
     * @param title Bottom of subtitle
     * @return {@link BarBuilder}
     */
    public BarBuilder setTitle(String title){
        this.title = title;
        return this;

    }

    /**
     * Define the color (optional)
     * @param color The color of the bar
     * @return {@link BarBuilder}
     */
    public BarBuilder setColor(ChatColor color){
        this.color = color;
        return this;

    }

    /**
     * Build the action bar
     */
    public void build(){

        CraftPlayer craftPlayer = (CraftPlayer) this.player;

        if (this.color == null)  this.color = ChatColor.WHITE;

        IChatBaseComponent iChatBaseComponent = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + this.color + this.title + "\"}");
        PacketPlayOutChat packetPlayOutChat = new PacketPlayOutChat(iChatBaseComponent, (byte) 2);

        craftPlayer.getHandle().playerConnection.sendPacket(packetPlayOutChat);

    }

}
