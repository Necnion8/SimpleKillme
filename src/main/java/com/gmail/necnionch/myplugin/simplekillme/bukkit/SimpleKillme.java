package com.gmail.necnionch.myplugin.simplekillme.bukkit;

import com.google.common.collect.Maps;
import dev.jorel.commandapi.CommandAPI;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TranslatableComponent;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.CommandMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.permissions.Permissible;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.Set;


public final class SimpleKillme extends JavaPlugin implements Listener {
    private final MainConfig mainConfig = new MainConfig(this);
    private final Map<Player, Integer> confirmed = Maps.newHashMap();

    @Override
    public void onEnable() {
        mainConfig.load();

        new CommandKill(this).register();

        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        CommandAPI.unregister("kill");
    }



    public static void broadcastCommandMessage(CommandSender source, BaseComponent message, boolean sendToSource) {
        BaseComponent result = new TranslatableComponent("chat.type.admin", source.getName(), message);
        result.setColor(ChatColor.GRAY);
        result.setItalic(true);

        if (source instanceof BlockCommandSender) {
            BlockCommandSender blockCommandSender = (BlockCommandSender)source;
            if (!(Boolean) Boolean.TRUE.equals(blockCommandSender.getBlock().getWorld().getGameRuleValue(GameRule.COMMAND_BLOCK_OUTPUT))) {
                Bukkit.getConsoleSender().sendMessage(result);
                return;
            }
        } else if (source instanceof CommandMinecart) {
            CommandMinecart commandMinecart = (CommandMinecart)source;
            if (!(Boolean) Boolean.TRUE.equals(commandMinecart.getWorld().getGameRuleValue(GameRule.COMMAND_BLOCK_OUTPUT))) {
                Bukkit.getConsoleSender().sendMessage(result);
                return;
            }
        }

        if (sendToSource && !(source instanceof ConsoleCommandSender)) {
            source.sendMessage(message);
        }


        Set<Permissible> users = Bukkit.getPluginManager().getPermissionSubscriptions("bukkit.broadcast.admin");

        for (Permissible user : users) {
            if (user instanceof CommandSender && user.hasPermission("bukkit.broadcast.admin")) {
                CommandSender target = (CommandSender) user;
                if (target instanceof ConsoleCommandSender) {
                    target.sendMessage(result);
                } else if (!target.equals(source)) {
                    target.sendMessage(result);
                }
            }
        }

    }


    public MainConfig getMainConfig() {
        return mainConfig;
    }


    public boolean isConfirmed(Player player) {
        return confirmed.containsKey(player);
    }

    public void setConfirm(Player player) {
        removeConfirm(player);
        confirmed.put(player, getServer().getScheduler().scheduleSyncDelayedTask(this, () -> confirmed.remove(player), 20 * 10));
    }

    public void removeConfirm(Player player) {
        Integer waitId = confirmed.remove(player);
        if (waitId != null && waitId != -1)
            getServer().getScheduler().cancelTask(waitId);
    }


    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        removeConfirm(event.getPlayer());
    }



}
