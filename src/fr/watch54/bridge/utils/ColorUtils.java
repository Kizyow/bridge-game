package fr.watch54.bridge.utils;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;

import java.util.HashMap;
import java.util.Map;

public enum ColorUtils {

    BLACK(ChatColor.BLACK, DyeColor.BLACK),
    DARK_BLUE(ChatColor.DARK_BLUE, DyeColor.BLUE),
    DARK_GREEN(ChatColor.DARK_GREEN, DyeColor.GREEN),
    DARK_AQUA(ChatColor.DARK_AQUA, DyeColor.LIGHT_BLUE),
    DARK_RED(ChatColor.DARK_RED, DyeColor.RED),
    DARK_PURPLE(ChatColor.DARK_PURPLE, DyeColor.PURPLE),
    GOLD(ChatColor.GOLD, DyeColor.ORANGE),
    GRAY(ChatColor.GREEN, DyeColor.GRAY),
    DARK_GRAY(ChatColor.DARK_GRAY, DyeColor.GRAY),
    BLUE(ChatColor.BLUE, DyeColor.BLUE),
    GREEN(ChatColor.GREEN, DyeColor.LIME),
    AQUA(ChatColor.AQUA, DyeColor.CYAN),
    RED(ChatColor.RED, DyeColor.RED),
    LIGHT_PURPLE(ChatColor.LIGHT_PURPLE, DyeColor.PINK),
    YELLOW(ChatColor.YELLOW, DyeColor.YELLOW),
    WHITE(ChatColor.WHITE, DyeColor.WHITE);

    private ChatColor chatColor;
    private DyeColor dyeColor;

    private static Map<ChatColor, DyeColor> colorMap = new HashMap<>();

    ColorUtils(ChatColor chatColor, DyeColor dyeColor){
        this.chatColor = chatColor;
        this.dyeColor = dyeColor;

    }

    public static DyeColor getbyChatColor(ChatColor chatColor){
        return colorMap.get(chatColor);

    }

    static {

        for(ColorUtils colorUtils : values()) colorMap.put(colorUtils.chatColor, colorUtils.dyeColor);

    }

}
