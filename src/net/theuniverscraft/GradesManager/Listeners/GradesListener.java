package net.theuniverscraft.GradesManager.Listeners;

import net.theuniverscraft.GradesManager.Managers.DbManager;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class GradesListener implements Listener {
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		DbManager.getInstance().restorePlayer(event.getPlayer());
	}
}
