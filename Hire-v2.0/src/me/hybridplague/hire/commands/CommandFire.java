package me.hybridplague.hire.commands;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.hybridplague.hire.Hire;

public class CommandFire implements CommandExecutor {

	private Hire plugin;
	
	public boolean found;
	
	public CommandFire(Hire plugin) {
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
							if (args.length < 3) {
								this.requiresInput(p);
								return;
							}
							if (!args[2].equalsIgnoreCase("-s") && !args[2].equalsIgnoreCase("-h")) {
								this.requiresInput(p);
								return;
							}
							if (args[2].equalsIgnoreCase("-h")) {
								Bukkit.dispatchCommand(Bukkit.getConsoleSender(), plugin.getConfig().getString("Jobs." + key + ".hard-fire-command").replace("$player", t.getName()));
								List<String> l = plugin.data.getConfig().getStringList("Jobs." + key);
								if (!l.contains(t.getUniqueId().toString()))
									l.add(t.getUniqueId().toString());
								plugin.data.getConfig().set("Jobs." + key, l);
								plugin.data.saveConfig();
							}
						}
						
						Date now = new Date();
					    SimpleDateFormat format = new SimpleDateFormat(plugin.getConfig().getString("Date-Format"));
					    String date = format.format(now);
						
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), plugin.getConfig().getString("Jobs." + key + ".fire-command").replace("$player", t.getName()));
						if (plugin.getConfig().getBoolean("Jobs." + key + ".fire-mail.send") == true) {
							Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mail send " + t.getName() + " " + plugin.getConfig().getString("Jobs." + key + ".fire-mail.message")
									.replace("$sender", p.getName())
									.replace("$date", date)
									.replace("$player", t.getName()));
						}
						
						Bukkit.broadcast(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("Jobs." + key + ".minister-fire-broadcast")
									.replace("$sender", p.getName())
									.replace("$date", date)
									.replace("$player", t.getName())), "businesscraft.minister");
						
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
	
	private void requiresInput(Player p) {
		p.sendMessage(ChatColor.RED + "This job requires -s or -h");
	}
	
	private void noPlayer(Player p) {
		p.sendMessage(ChatColor.RED + "Player not found");
	}
	
	private void help(Player p) {
		p.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("Help-header")).replace("$cmd", "fire"));
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
