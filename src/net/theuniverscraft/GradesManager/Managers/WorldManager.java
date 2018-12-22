package net.theuniverscraft.GradesManager.Managers;

import org.bukkit.Bukkit;
import org.bukkit.World;

public class WorldManager {
	public static World getBaseWorld() {
		return Bukkit.getWorld("world");
	}
}
