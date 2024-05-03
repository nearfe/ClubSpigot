package club.minemen.spigot.knockback;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import club.minemen.spigot.ClubSpigot;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.sugarcanemc.sugarcane.util.yaml.YamlCommenter;

import com.google.common.base.Throwables;

import dev.cobblesword.nachospigot.knockback.KnockbackProfile;

public class KnockbackConfig {
	private static final Logger LOGGER = LogManager.getLogger(KnockbackConfig.class);
	private static File CONFIG_FILE;
	protected static final YamlCommenter c = new YamlCommenter();
	private static final String HEADER = "This is the knockback configuration file for ClubSpigot.\n"
			+ "For configuration info see this: https://github.com/Wind-Development/ClubSpigot/wiki/Knockback-Configuration";
	static YamlConfiguration config;

	private static volatile KnockbackProfile currentKb;
	private static volatile Set<KnockbackProfile> kbProfiles = new HashSet<>();

	public static void init(File configFile) {
		CONFIG_FILE = configFile;
		config = new YamlConfiguration();
		try {
			ClubSpigot.LOGGER.info("Loading ClubSpigot knockback config from " + configFile.getName());
			config.load(CONFIG_FILE);
		} catch (IOException ignored) {
		} catch (InvalidConfigurationException ex) {
			LOGGER.log(Level.ERROR, "Could not load knockback.yml, please correct your syntax errors", ex);
			throw Throwables.propagate(ex);
		}
		config.options().copyDefaults(true);
		c.setHeader(HEADER);

		Set<String> keys = getKeys("knockback.profiles");

		if (!keys.contains("vanilla")) {
			final KnockbackProfile vanillaProfile = new CraftKnockbackProfile("vanilla");
			vanillaProfile.save(true);
		}
		
		// Reload keys
		keys = getKeys("knockback.profiles");
		
		for (String key : keys) {
			final String path = "knockback.profiles." + key;
			CraftKnockbackProfile profile = (CraftKnockbackProfile) getKbProfileByName(key);
			if (profile == null) {
				profile = new CraftKnockbackProfile(key);
				kbProfiles.add(profile);
			}
			profile.setVertical(getDouble(path + ".vertical", 0.9055D));
			profile.setHorizontal(getDouble(path + ".horizontal", 0.25635D));
			profile.setSprintMultiplier(getDouble(path + ".sprint-multiplier", 1.0D));
			profile.setRangeFactor(getDouble(path + ".range-factor", 0.025D));
			profile.setMaxRangeReduction(getDouble(path + ".max-range-reduction", 1.2D));
			profile.setStartRangeReduction(getDouble(path + ".start-range-reduction", 3.0D));
			profile.setMinRange(getDouble(path + ".min-range", 0.12D));
			profile.setVerticalLimit(getBoolean(path + ".vertical-limit", true));
			profile.setVerticalLimitValue(getDouble(path + ".vertical-limit-value", 4.0D));
		}
		currentKb = getKbProfileByName(getString("knockback.current", "mmc"));
		if (currentKb == null) {
			ClubSpigot.LOGGER.warn("Knockback profile selected was not found, using profile 'mmc' for now!");
			currentKb = getKbProfileByName("mmc");
			
			ClubSpigot.LOGGER.info("Setting default knockback as 'mmc'...");
			set("knockback.current", "mmc");
		}
		save();
	}

	public static KnockbackProfile getCurrentKb() {
		if (currentKb == null) {
			setCurrentKb(getKbProfileByName("vanilla"));
		}
		return currentKb;
	}

	public static void setCurrentKb(KnockbackProfile kb) {
		currentKb = kb;
	}

	public static KnockbackProfile getKbProfileByName(String name) {
		for (KnockbackProfile profile : kbProfiles) {
			if (profile.getName().equalsIgnoreCase(name)) {
				return profile;
			}
		}
		return null;
	}

	public static Set<KnockbackProfile> getKbProfiles() {
		return kbProfiles;
	}

	public static void save() {
		try {
			config.save(CONFIG_FILE);
		} catch (IOException ex) {
			LOGGER.log(Level.ERROR, "Could not save " + CONFIG_FILE, ex);
		}
	}

	public static void set(String path, Object val) {
		config.set(path, val);

		save();
	}

	public static Set<String> getKeys(String path) {
		if (!config.isConfigurationSection(path)) {
			config.createSection(path);
			return new HashSet<>();
		}

		return config.getConfigurationSection(path).getKeys(false);
	}

	private static boolean getBoolean(String path, boolean def) {
		config.addDefault(path, def);
		return config.getBoolean(path, config.getBoolean(path));
	}

	private static double getDouble(String path, double def) {
		config.addDefault(path, def);
		return config.getDouble(path, config.getDouble(path));
	}

	private static float getFloat(String path, float def) {
		config.addDefault(path, def);
		return config.getFloat(path, config.getFloat(path));
	}

	private static int getInt(String path, int def) {
		config.addDefault(path, def);
		return config.getInt(path, config.getInt(path));
	}

	private static <T> List getList(String path, T def) {
		config.addDefault(path, def);
		return config.getList(path, config.getList(path));
	}

	private static String getString(String path, String def) {
		config.addDefault(path, def);
		return config.getString(path, config.getString(path));
	}
}
