package me.hybridplague.hire.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.hybridplague.hire.Hire;

public class CommandFireList implements CommandExecutor {

	private Hire plugin;
	public boolean found;
	
	public CommandFireList(Hire plugin) {
		this.found = false;
		this.plugin = plugin;
	}
	
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Only players can execute this command.");
			return true;
		}
		
		Player p = (Player) sender;
		
		if (args.length == 0) {
			this.help(p);
			return true;
		}
		
		if (args.length > 0) {
			String job = args[0].toLowerCase();
			
			plugin.data.getConfig().getConfigurationSection("Jobs").getKeys(false).forEach(key -> {
				
				if (key.equalsIgnoreCase(job)) {
					this.setFound(true);
					
					if (plugin.data.getConfig().getStringList("Jobs." + key).isEmpty()) {
						this.noPlayers(p);
						return;
					}
					
					List<String> l = plugin.data.getConfig().getStringList("Jobs." + key);
					List<String> players = new ArrayList<String>();
					for (int i = 0; i < l.size(); i++) {
						UUID id = UUID.fromString(l.toArray()[i].toString());
						players.add(ChatColor.RED + Bukkit.getOfflinePlayer(id).getName());
						
					}
					String result = String.join("&f, &c", players);
					p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&l" + key + "\n "));
					p.sendMessage(ChatColor.translateAlternateColorCodes('&', result));
					return;
				}
				return;
			});
			if(!getFound()) {
				this.help(p);
				return true;
			} else {
				this.setFound(false);
				return true;
			}
		}
		
		return true;
	}
	
	private void noPlayers(Player p) {
		p.sendMessage(ChatColor.RED + "No players are hard-fired for this job.");
	}
	
	private void help(Player p) {
		p.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("Help-header"))
				.replace("$cmd", "firelist")
				.replace("<player>", "<job>"));
		plugin.data.getConfig().getConfigurationSection("Jobs").getKeys(false).forEach(key -> {
			
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
