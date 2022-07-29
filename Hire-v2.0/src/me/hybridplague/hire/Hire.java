package me.hybridplague.hire;

import org.bukkit.plugin.java.JavaPlugin;

import me.hybridplague.hire.commands.CommandFire;
import me.hybridplague.hire.commands.CommandFireList;
import me.hybridplague.hire.commands.CommandHire;
import me.hybridplague.hire.commands.CommandRehire;

public class Hire extends JavaPlugin {

	public DataManager data;
	
	@Override
	public void onEnable() {
		this.saveDefaultConfig();
		
		this.data = new DataManager(this);
		
		this.getCommand("hire").setExecutor(new CommandHire(this));
		this.getCommand("fire").setExecutor(new CommandFire(this));
		this.getCommand("rehire").setExecutor(new CommandRehire(this));
		this.getCommand("firelist").setExecutor(new CommandFireList(this));
		
	}
	
	@Override
	public void onDisable() {
	}
	
	
	
}
