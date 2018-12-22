package net.theuniverscraft.GradesManager.Commands;

import net.milkbowl.vault.permission.Permission;
import net.theuniverscraft.GradesManager.PluginMain;
import net.theuniverscraft.GradesManager.Managers.DbManager;
import net.theuniverscraft.GradesManager.Managers.WorldManager;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandAddGroup implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!sender.isOp() || args.length != 3) return false;
		try {
			Permission permisson = PluginMain.getInstance().getPermission();
		
			OfflinePlayer player = Bukkit.getOfflinePlayer(args[0]);
			Long time = Long.parseLong(args[2]);
			DbManager.getInstance().addGroup(player, args[1], time);
			
			if(player.isOnline()) {
				Player online = player.getPlayer();
				if(!permisson.playerInGroup(WorldManager.getBaseWorld(), online.getName(), args[1]))
					permisson.playerAddGroup(WorldManager.getBaseWorld(), online.getName(), args[1]);
			}
			
		} catch(NumberFormatException e) { return false; }
		
		return true;
	}
}
