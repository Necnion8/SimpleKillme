package com.gmail.necnionch.myplugin.simplekillme.bukkit;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.EntitySelectorArgument;
import dev.jorel.commandapi.wrappers.NativeProxyCommandSender;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TranslatableComponent;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;


public class CommandKill {
    private final SimpleKillme plugin;

    public CommandKill(SimpleKillme plugin) {
        this.plugin = plugin;
    }


    public void register() {
        new CommandAPICommand("kill")
                .withPermission("simplekillme.command.killme")
                .executesNative(this::execute)
                .override();

        new CommandAPICommand("kill")
                .withPermission("minecraft.command.kill")
                .withArguments(new EntitySelectorArgument("targets", EntitySelectorArgument.EntitySelector.MANY_ENTITIES)
                        .withPermission("minecraft.command.kill"))
                .executesNative(this::executeArgument)
                .register();
    }


    private int execute(NativeProxyCommandSender sender, Object[] args) {
        if (sender.getCallee() instanceof Entity)
            return executeKill(sender, Collections.singletonList((Entity) sender.getCallee()));
        return 0;
    }

    private int executeArgument(NativeProxyCommandSender sender, Object[] args) {
        if (args[0] instanceof Collection)
            //noinspection unchecked
            return executeKill(sender, (Collection<Entity>) args[0]);

        return executeKill(sender, Collections.singletonList((Entity) args[0]));
    }



    private int executeKill(NativeProxyCommandSender sender, Collection<Entity> targets) {
        if (sender.getCaller() instanceof Player) {
            Player p = (Player) sender.getCaller();

            int limit = plugin.getMainConfig().getKillWarningCount();
            if (limit >= -1 && targets.size() >= limit) {
                if (!plugin.isConfirmed(p)) {
                    plugin.setConfirm(p);
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getMainConfig().getKillWarningMessage().replaceAll("\\{count}", String.valueOf(targets.size()))));
                    return 0;
                }
            }

            plugin.removeConfirm(p);
        }


        for (Entity entity : targets) {
            if (entity instanceof Damageable) {
                ((Damageable) entity).setHealth(0);
            } else {
                entity.remove();
            }
        }

        BaseComponent message;
        if (targets.isEmpty()) {
            if (sender.getCaller() instanceof Player) {
                message = new TranslatableComponent("argument.entity.notfound.entity");
                message.setColor(ChatColor.RED);
                sender.getCaller().sendMessage(message);
            }
            return 0;

        } else if (targets.size() == 1) {
            Entity entity = targets.iterator().next();
            message = new TranslatableComponent("commands.kill.success.single", (entity.getCustomName() != null) ? entity.getCustomName() : entity.getName());
        } else {
            message = new TranslatableComponent("commands.kill.success.multiple", targets.size());
        }


        SimpleKillme.broadcastCommandMessage(sender.getCaller(), message, true);
        return targets.size();
    }



}
