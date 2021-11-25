package com.gmail.necnionch.myplugin.simplekillme.bukkit;

import com.gmail.necnionch.myplugin.simplekillme.common.BukkitConfigDriver;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class MainConfig extends BukkitConfigDriver {

    private boolean hideKillLogConsole;

    public MainConfig(JavaPlugin plugin) {
        super(plugin);
    }


    public int getKillWarningCount() {
        return config.getInt("kill-warning.count", 100);
    }

    public String getKillWarningMessage() {
        return config.getString("kill-warning.message", "&c&l続けるにはもう一度実行してください。 (対象のエンティティ: {count}体)");
    }

    public boolean isHideKillLogExecutedByConsole() {
        return hideKillLogConsole;
    }

    @Override
    public boolean onLoaded(FileConfiguration config) {
        if (super.onLoaded(config)) {
            hideKillLogConsole = config.getBoolean("hide-kill-log-executed-by-console", true);
            return true;
        }
        return false;
    }

}
