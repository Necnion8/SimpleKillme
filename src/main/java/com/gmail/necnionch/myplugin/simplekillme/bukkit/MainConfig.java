package com.gmail.necnionch.myplugin.simplekillme.bukkit;

import com.gmail.necnionch.myplugin.simplekillme.common.BukkitConfigDriver;
import org.bukkit.plugin.java.JavaPlugin;

public class MainConfig extends BukkitConfigDriver {
    public MainConfig(JavaPlugin plugin) {
        super(plugin);
    }


    public int getKillWarningCount() {
        return config.getInt("kill-warning.count", 100);
    }

    public String getKillWarningMessage() {
        return config.getString("kill-warning.message", "&c&l続けるにはもう一度実行してください。 (対象のエンティティ: {count}体)");
    }


}
