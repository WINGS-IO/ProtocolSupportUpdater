package io.WINGS.PluginUpdater.ProtocolSupport;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import io.WINGS.ProtocolSupportUpdater.storage.SS;
import io.WINGS.ProtocolSupportUpdater.storage.UpdateData;
import net.md_5.bungee.api.ChatColor;

public class Update {

	Plugin pl = Bukkit.getPluginManager().getPlugin(SS.PluginName);
	FileConfiguration config = pl.getConfig();
	public Boolean updateInProgress = false;
	
    public Update(CommandSender s) {
        if(updateInProgress) {
            s.sendMessage(SS.prefix + ChatColor.RED + "Update already in progress!");
            return;
        }
        
        s.sendMessage(SS.prefix + ChatColor.RED + "Downloading " + SS.PSName + "...");
        
        updateInProgress = true;

        try {
        	Method getFile = JavaPlugin.class.getDeclaredMethod("getFile");
            getFile.setAccessible(true);
            File dest = new File("plugins/" + SS.PSName + UpdateData.ext);

            //Connect
            URL url =
            new URL(UpdateData.JenkinsURL +
                    SS.PSName +
                    UpdateData.ext);
            
            // Creating con
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestProperty("User-Agent", "WINGS07/ProtocolSupportUpdater");

            // Get input stream
            try (InputStream input = con.getInputStream()) {
            	Files.copy(input, dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
              	}
            
            int uc = config.getInt("UpdateCounter");
            uc++;
            config.set("UpdateCounter", uc);
            pl.saveConfig();
            
            s.sendMessage(SS.prefix + ChatColor.RED + "Update success!");
        } catch (Exception ex) {
        	ex.printStackTrace();
            s.sendMessage(SS.prefix + ChatColor.RED + "Update failed, " + ex.getMessage());
            if(config.getBoolean("UseBackupServer")) {
        		new UpdateFromBackupServer(s);
        	} else {
        		s.sendMessage(SS.prefix + ChatColor.RED + "Update from backup server canceled by config.");
        	}
        } finally {
        	updateInProgress = false;
        }
    }
}
