package me.hybridplague.hire.commands;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.hybridplague.hire.Hire;

public class CommandHire implements CommandExecutor {

	private Hire plugin;
	
	public boolean found;
	
	public CommandHire(Hire plugin) {
		this.plugin = plugin;
		this.found = false;
	}
	
	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		// /hire <player> <job>
		
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Only players can execute this command.");
			return true;
		}
		
		Player p = (Player) sender;
		
		if (args.length <= 1) { // <-- Missing args | /hire (<player>)
			this.help(p);
			return true;
		}
		
		else if (args.length >= 2) { // <-- /hire <player> <job>
			
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
						
						Date now = new Date();
					    SimpleDateFormat format = new SimpleDateFormat(plugin.getConfig().getString("Date-Format"));
					    String date = format.format(now);
						
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), plugin.getConfig().getString("Jobs." + key + ".hire-command").replace("$player", t.getName()));
						if (plugin.getConfig().getBoolean("Jobs." + key + ".hire-mail.send") == true) {
							Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mail send " + t.getName() + " " + plugin.getConfig().getString("Jobs." + key + ".hire-mail.message")
									.replace("$sender", p.getName())
									.replace("$date", date)
									.replace("$player", t.getName()));
						}
						
						Bukkit.broadcast(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("Jobs." + key + ".minister-hire-broadcast")
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
			this.noPlayer(p, args[1]);
			return true;
		}
		
		return false;
	}
	
	private void noPlayer(Player p, String p2) {
		p.sendMessage(ChatColor.RED + "Player \"" + p2 + "\" not found");
	}
	
	private void help(Player p) {
		p.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("Help-header")).replace("$cmd", "hire"));
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
