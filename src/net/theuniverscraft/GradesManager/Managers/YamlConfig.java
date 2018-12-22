package net.theuniverscraft.GradesManager.Managers;

import java.util.HashMap;
import java.util.Iterator;

import net.theuniverscraft.GradesManager.PluginMain;

import org.bukkit.configuration.file.FileConfiguration;

public class YamlConfig {
	private FileConfiguration config;
	private HashMap<String, Object> values = new HashMap<String, Object>();
	
	private static YamlConfig instance;
	public static YamlConfig getInstance() {
		if(instance == null) instance = new YamlConfig();
		return instance;
	}
	
	private YamlConfig() {
		config = PluginMain.getInstance().getConfig();
		
		HashMap<String, Object> default_values = new HashMap<String, Object>();
		default_values.put("mysql.host", "127.0.0.1");
		default_values.put("mysql.port", 3306);
		default_values.put("mysql.name", "universcraft");
		default_values.put("mysql.user", "root");
		default_values.put("mysql.password", "");
		default_values.put("mysql.prefix", "temp_");
		
		Iterator<String> it = default_values.keySet().iterator();
		while(it.hasNext()) {
			String key = it.next();
			Object value = default_values.get(key);
			
			if(config.contains(key)) {
				values.put(key, config.get(key));
			}
			else {
				values.put(key, value);
				config.set(key, value);
			}
		}
		PluginMain.getInstance().saveConfig();
	}
	
	public String getString(String key) {
		return values.containsKey(key) ? values.get(key).toString() : null;
	}
	
	public Integer getInt(String key) {
		return values.containsKey(key) ? (Integer) values.get(key) : null;
	}
}
