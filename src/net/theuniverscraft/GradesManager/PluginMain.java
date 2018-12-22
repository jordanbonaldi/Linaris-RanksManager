package net.theuniverscraft.GradesManager;

import net.milkbowl.vault.permission.Permission;
import net.theuniverscraft.GradesManager.Commands.CommandAddGroup;
import net.theuniverscraft.GradesManager.Commands.CommandAddPerm;
import net.theuniverscraft.GradesManager.Listeners.GradesListener;
import net.theuniverscraft.GradesManager.Managers.DbManager;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class PluginMain extends JavaPlugin {
	public Permission m_permission = null;
	
	private static PluginMain instance;
	public static PluginMain getInstance() {
		return instance;
	}
	
	public void onEnable() {
		instance = this;
		
		PluginManager pm = getServer().getPluginManager();
		if(!setupPermissions()) {
			pm.disablePlugin(this);
		}
		
		DbManager.getInstance();
		
		getCommand("addgroup").setExecutor(new CommandAddGroup());
		getCommand("addperm").setExecutor(new CommandAddPerm());
		
		pm.registerEvents(new GradesListener(), this);
	}
	
	private boolean setupPermissions()
    {
		RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
		if (permissionProvider != null) {
			m_permission = permissionProvider.getProvider();
		}
		return (m_permission != null);
    }
	
	public Permission getPermission() { return m_permission; }
}
