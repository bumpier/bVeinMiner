package net.bumpier.bveinminer.utils;

import net.md_5.bungee.api.ChatColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatUtils {

    private static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");

    public static String colorize(String message) {
        if (message == null) {
            return "";
        }

        // Use BungeeCord's ChatColor API to translate modern hex codes
        Matcher matcher = HEX_PATTERN.matcher(message);
        StringBuffer buffer = new StringBuffer();

        while (matcher.find()) {
            matcher.appendReplacement(buffer, ChatColor.of("#" + matcher.group(1)).toString());
        }
        matcher.appendTail(buffer);

        // Then, translate legacy color codes using Bukkit's utility on the result
        return org.bukkit.ChatColor.translateAlternateColorCodes('&', buffer.toString());
    }
}