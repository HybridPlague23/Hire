package me.hybridplague.hire.commands;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.hybridplague.hire.Hire;

public class CommandRehire implements CommandExecutor {

	private Hire plugin;
	
	private boolean found;
	
	public CommandRehire(Hire plugin) {
		this.plugin = plugin;
		this.found = false;
	}
	
	
	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		// /fire <player> <job>
		
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Only players can execute this command.");
			return true;
		}
		
		Player p = (Player) sender;
		
		if (args.length <= 1) { // <-- Missing args | /fire (<player>)
			this.help(p);
			return true;
		}
		
		else if (args.length >= 2) { // <-- /fire <player> <job>
			
			// Check if arg-1 player exists
			
			OfflinePlayer t = Bukkit.getOfflinePlayer(args[0]); 
			
			if (t.hasPlayedBefore() || t.isOnline()) {
				
				// Exists
				String job = args[1].toLowerCase();
				
				plugin.getConfig().getConfigurationSection("Jobs").getKeys(false).forEach(key -> {
					
					if (key.equalsIgnoreCase(job)) {
						this.setFound(true);
						// no permission
						if (!p.hasPermission(plugin.getConfig().getString("Jobs." + key + ".permission"))) { 
							p.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("Jobs." + key + ".error-message")));
							return;
						}
						
						if (plugin.getConfig().getBoolean("Jobs." + key + ".hard-soft-fireable") == true) {
						
							Bukkit.dispatchCommand(Bukkit.getConsoleSender(), plugin.getConfig().getString("Jobs." + key + ".rehire-command").replace("$player", t.getName()));
							p.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("Jobs." + key + ".rehire-message").replace("$player", t.getName())));
							List<String> l = plugin.data.getConfig().getStringList("Jobs." + key);
							if (l.contains(t.getUniqueId().toString()))
								l.remove(t.getUniqueId().toString());
							plugin.data.getConfig().set("Jobs." + key, l);
							plugin.data.saveConfig();
							return;
						}
						this.noRehire(p);
						return;
					}
					
				});
				if(!getFound()) {
					this.help(p);
					return true;
				} else {
					this.setFound(false);
					return true;
				}
			}
			this.noPlayer(p);
			return true;
		}
		
		return false;
	}
	
	private void noRehire(Player p) {
		p.sendMessage(ChatColor.RED + "Invalid rehire job");
	}
	
	private void noPlayer(Player p) {
		p.sendMessage(ChatColor.RED + "Player not found");
	}
	
	private void help(Player p) {
		p.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("Help-header")).replace("$cmd", "rehire"));
		plugin.getConfig().getConfigurationSection("Jobs").getKeys(false).forEach(key -> {
			
			if (p.hasPermission(plugin.getConfig().getString("Jobs." + key + ".permission"))) {
				p.sendMessage(ChatColor.GRAY + key);
			}
			
		});
	}
	
	public void setFound(Boolean found) {
		this.found = found;
	}
	
	public boolean getFound() {
		return found;
	}
	
}
