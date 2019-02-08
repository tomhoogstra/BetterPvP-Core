package net.betterpvp.core.configs;

import java.io.File;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;


public class ConfigManager {

	private  Configs current;

	private  FileConfiguration fc;
	private Plugin inst;

	public ConfigManager(Plugin i){
		inst = i;
	

	}

	public void load() {
		inst.reloadConfig();
	}

	public void save() {
		try {
			fc.save(new File(inst.getDataFolder() + current.getPath()));
		} catch (Exception ex)  {
			ex.printStackTrace();
		}
	}

	public void check(String key, Object value) {
		if (!fc.isSet(key)) {
			fc.set(key, value);
			save();
		}
	}

	public boolean getBoolean(String key) {
		return fc.getBoolean(key);
	}

	public double getDouble(String key){

		return fc.getDouble(key);
	}

	public int getInt(String key) {
		return fc.getInt(key);
	}

	public Long getLong(String key){
		return fc.getLong(key);
	}

	public boolean getBool(String key){
		return fc.getBoolean(key);
	}

	public String getString(String key) {
		return fc.getString(key);
	}

	public List<?> getList(String key) {
		return fc.getList(key);
	}

	public List<String> getStringList(String key) {
		return fc.getStringList(key);
	}

	public ConfigurationSection getSection(String key) {
		return fc.getConfigurationSection(key);
	}

	public  void set(String key, Object value) {
		fc.set(key, value);
		save();
	}


	public ConfigManager get(Configs e){

		current = e;
		
		fc = YamlConfiguration.loadConfiguration(new File(inst.getDataFolder() + current.getPath()));
		return this;
	}


}