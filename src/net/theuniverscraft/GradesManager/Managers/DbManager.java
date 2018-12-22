package net.theuniverscraft.GradesManager.Managers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import net.milkbowl.vault.permission.Permission;
import net.theuniverscraft.GradesManager.PluginMain;
import net.theuniverscraft.GradesManager.Utils.Utils;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class DbManager {
	private Connection connection;
	
	private final String bddTmpPermsName = YamlConfig.getInstance().getString("mysql.prefix") + "perms";
	private final String bddTmpGroupsName = YamlConfig.getInstance().getString("mysql.prefix") + "groups";
	
	private static DbManager dbManager = null;
	
	public static DbManager getInstance() {
		if(dbManager == null) dbManager = new DbManager();
		return dbManager;
	}
	public static void closeInstance() {
		if(dbManager != null) {
			try { dbManager.connection.close(); }
			catch (Exception e) { e.printStackTrace(); }
		}
	}
	
	private DbManager() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			String url = "jdbc:mysql://"+YamlConfig.getInstance().getString("mysql.host")+":"+
					YamlConfig.getInstance().getString("mysql.port")+
					"/"+YamlConfig.getInstance().getString("mysql.name");
			String user = YamlConfig.getInstance().getString("mysql.user");
			String password = YamlConfig.getInstance().getString("mysql.password");
			
			connection = DriverManager.getConnection(url, user, password);
			Statement state = connection.createStatement();
			
			String sql = new StringBuilder().append("CREATE TABLE IF NOT EXISTS `").append(bddTmpPermsName).append("` (")
					.append("`id` int(11) NOT NULL AUTO_INCREMENT,")
					.append("`pseudo` varchar(50) NOT NULL,")
					.append("`perm` varchar(50) NOT NULL,")
					.append("`timestamp` bigint(20) NOT NULL,")
					.append("PRIMARY KEY (`id`)")
					.append(") ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;").toString();
            
			state.executeUpdate(sql);
			
			sql = new StringBuilder().append("CREATE TABLE IF NOT EXISTS `").append(bddTmpGroupsName).append("` (")
					.append("`id` int(11) NOT NULL AUTO_INCREMENT,")
					.append("`pseudo` varchar(50) NOT NULL,")
					.append("`group` varchar(50) NOT NULL,")
					.append("`timestamp` bigint(20) NOT NULL,")
					.append("PRIMARY KEY (`id`)")
					.append(") ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;").toString();
            
			state.executeUpdate(sql);
			
			state.close();
		} catch (Exception e) { e.printStackTrace(); }
	}
	
	public void restorePlayer(Player player) {
		Permission perm = PluginMain.getInstance().getPermission();
		try {
			String sql = "SELECT * FROM "+bddTmpGroupsName+" WHERE pseudo=?";
			PreparedStatement state = connection.prepareStatement(sql);
			
			state.setString(1, player.getName());
			
			ResultSet result = state.executeQuery();
			while(result.next()) {
				if(result.getLong("timestamp") > Utils.currentTimeSeconds()) { // Il a la perm
					if(!perm.playerInGroup(WorldManager.getBaseWorld(), player.getName(), result.getString("group")))
						perm.playerAddGroup(WorldManager.getBaseWorld(), player.getName(), result.getString("group"));
				}
				else { // Il ne l'a plus
					if(perm.playerInGroup(WorldManager.getBaseWorld(), player.getName(), result.getString("group")))
						perm.playerRemoveGroup(WorldManager.getBaseWorld(), player.getName(), result.getString("group"));
				}
			}
			
			state.close();
		} catch(Exception e) { e.printStackTrace(); }
		
		try {
			String sql = "SELECT * FROM "+bddTmpPermsName+" WHERE pseudo=?";
			PreparedStatement state = connection.prepareStatement(sql);
			
			state.setString(1, player.getName());
			
			ResultSet result = state.executeQuery();
			while(result.next()) {
				if(result.getLong("timestamp") > Utils.currentTimeSeconds()) { // Il a la perm
					if(!perm.playerHas(WorldManager.getBaseWorld(), player.getName(), result.getString("perm")))
						perm.playerAdd(WorldManager.getBaseWorld(), player.getName(), result.getString("perm"));
				}
				else { // Il ne l'a plus
					if(perm.playerHas(WorldManager.getBaseWorld(), player.getName(), result.getString("perm")))
						perm.playerRemove(WorldManager.getBaseWorld(), player.getName(), result.getString("perm"));
				}
			}
			
			state.close();
		} catch(Exception e) { e.printStackTrace(); }
	}
	
	public void addPerm(OfflinePlayer player, String perm, Long time) {
		Long baseTime = Utils.currentTimeSeconds();
		try {
			String sql = "SELECT * FROM "+bddTmpPermsName+" WHERE pseudo=? AND perm=?";
			PreparedStatement state = connection.prepareStatement(sql);
			
			state.setString(1, player.getName());
			state.setString(2, perm);
			
			ResultSet result = state.executeQuery();
			if(result.next()) {
				if(result.getLong("timestamp") > baseTime) { // Il a la perm
					baseTime = result.getLong("timestamp");
				}
				// UPDATE
				sql = "UPDATE "+bddTmpPermsName+" SET timestamp=? WHERE pseudo=? AND perm=?";
				PreparedStatement stateInsert = connection.prepareStatement(sql);
				
				stateInsert.setLong(1, baseTime + time);
				stateInsert.setString(2, player.getName());
				stateInsert.setString(3, perm);				
				
				stateInsert.executeUpdate();
				
				stateInsert.close();
			}
			else {
				// INSERT
				sql = "INSERT INTO "+bddTmpPermsName+"(pseudo, perm, timestamp) VALUES(?, ?, ?)";
				PreparedStatement stateInsert = connection.prepareStatement(sql);
				
				stateInsert.setString(1, player.getName());
				stateInsert.setString(2, perm);
				stateInsert.setLong(3, baseTime + time);
				
				stateInsert.executeUpdate();
				
				stateInsert.close();
			}
			
			state.close();
		} catch(Exception e) { e.printStackTrace(); }
	}
	
	public void addGroup(OfflinePlayer player, String group, Long time) {
		Long baseTime = Utils.currentTimeSeconds();
		try {
			String sql = "SELECT * FROM "+bddTmpGroupsName+" WHERE pseudo=? AND group=?";
			PreparedStatement state = connection.prepareStatement(sql);
			
			state.setString(1, player.getName());
			state.setString(2, group);
			
			ResultSet result = state.executeQuery();
			if(result.next()) {
				if(result.getLong("timestamp") > baseTime) { // Il est dans le groupe
					baseTime = result.getLong("timestamp");
				}
				// UPDATE
				sql = "UPDATE "+bddTmpGroupsName+" SET timestamp=? WHERE pseudo=? AND group=?";
				PreparedStatement stateInsert = connection.prepareStatement(sql);
				
				stateInsert.setLong(1, baseTime + time);
				stateInsert.setString(2, player.getName());
				stateInsert.setString(3, group);				
				
				stateInsert.executeUpdate();
				
				stateInsert.close();
			}
			else {
				// INSERT
				sql = "INSERT INTO "+bddTmpGroupsName+"(pseudo, group, timestamp) VALUES(?, ?, ?)";
				PreparedStatement stateInsert = connection.prepareStatement(sql);
				
				stateInsert.setString(1, player.getName());
				stateInsert.setString(2, group);
				stateInsert.setLong(3, baseTime + time);
				
				stateInsert.executeUpdate();
				
				stateInsert.close();
			}
			
			state.close();
		} catch(Exception e) { e.printStackTrace(); }
	}
}
