package fr.watch54.bridge.utils;

import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class TitleBuilder {

    private Player player;
    private String title;
    private String subtitle;
    private ChatColor color;
    private int stay;
    private int fadeIn;
    private int fadeOut;

    /**
     * A constructor to create a custom title
     * @param player Set the player to send the title
     */
    public TitleBuilder(Player player){
        this.player = player;

    }

    /**
     * Define the title
     * @param title Middle screen title
     * @return {@link TitleBuilder}
     */
    public TitleBuilder setTitle(String title){
        this.title = title;
        return this;

    }

    /**
     * Define the subtitle
     * @param subtitle Bottom middle screen subtitle
     * @return {@link TitleBuilder}
     */
    public TitleBuilder setSubtitle(String subtitle){
        this.subtitle = subtitle;
        return this;

    }

    /**
     * Define the color
     * @param color Define the ChatColor
     * @return {@link TitleBuilder}
     */
    public TitleBuilder setColor(ChatColor color){
        this.color = color;
        return this;

    }

    /**
     * Define time to fade in the the title
     * @param fadeIn time in tick
     * @return {@link TitleBuilder}
     */
    public TitleBuilder fadeIn(int fadeIn){
        this.fadeIn = fadeIn;
        return this;

    }

    /**
     * Define the time to stay the title
     * @param stay Time in tick
     * @return {@link TitleBuilder}
     */
    public TitleBuilder stay(int stay){
        this.stay = stay;
        return this;

    }

    /**
     * Define the time to fade out the title
     * @param fadeOut Time in tick
     * @return {@link TitleBuilder}
     */
    public TitleBuilder fadeOut(int fadeOut){
        this.fadeOut = fadeOut;
        return this;

    }

    /**
     * Build the title
     */
    public void build(){

        CraftPlayer craftPlayer = (CraftPlayer) this.player;

        if (this.color == null)  this.color = ChatColor.WHITE;

        PacketPlayOutTitle tickPacket = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TIMES, null, this.fadeIn, this.stay, this.fadeOut);

        PacketPlayOutTitle titlePacket = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE,
                IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + this.color + this.title + "\"}"));

        PacketPlayOutTitle subtitlePacket = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE,
                IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + this.color + this.subtitle + "\"}"));

        craftPlayer.getHandle().playerConnection.sendPacket(tickPacket);

        if(this.title != null) craftPlayer.getHandle().playerConnection.sendPacket(titlePacket);
        if(this.subtitle != null) craftPlayer.getHandle().playerConnection.sendPacket(subtitlePacket);

    }

}
