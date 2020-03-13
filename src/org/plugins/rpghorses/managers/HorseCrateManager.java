package org.plugins.rpghorses.managers;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.plugins.rpghorses.RPGHorsesMain;
import org.plugins.rpghorses.horses.HorseCrate;

import java.util.HashSet;
import java.util.logging.Level;

public class HorseCrateManager {

    private final RPGHorsesMain plugin;

    private HashSet<HorseCrate> horseCrates = new HashSet<>();

    public HorseCrateManager(RPGHorsesMain plugin) {
        this.plugin = plugin;

        this.loadHorseCrates();
    }

    public void loadHorseCrates() {
        this.horseCrates.clear();
        FileConfiguration config = this.plugin.getConfig();
        for (String crateName : config.getConfigurationSection("horse-crates").getKeys(false)) {
            String path = "horse-crates." + crateName + ".horse-info.";
            double[] healthValues = this.getMinAndMaxValues(config.getString(path + "health"));
            double minHealth = healthValues[0];
            double maxHealth = healthValues[1];
            double[] movementSpeedValues = this.getMinAndMaxValues(config.getString(path + "movement-speed"));
            double minMovementSpeed = movementSpeedValues[0];
            double maxMovementSpeed = movementSpeedValues[1];
            double[] jumpStrengthValues = this.getMinAndMaxValues(config.getString(path + "jump-strength"));
            double minJumpStrength = jumpStrengthValues[0];
            double maxJumpStrength = jumpStrengthValues[1];
            double movementSpeed = config.getDouble(path + "movement-speed");
            double jumpStrength = config.getDouble(path + "jump-strength");
            int tier = config.getInt(path + "tier");
            EntityType entityType = EntityType.HORSE;
            Horse.Color color = Horse.Color.BROWN;
            Horse.Style style = Horse.Style.NONE;
            try {
                entityType = EntityType.valueOf(config.getString(path + "type", "HORSE"));
            } catch (IllegalArgumentException e) {
                Bukkit.getLogger().log(Level.SEVERE, "[RPGHorses] Failed to load " + crateName + "( " + config.getString(path + "type") + " is not a valid entityType )");
            }
            try {
                color = Horse.Color.valueOf(config.getString(path + "color", "BROWN"));
            } catch (IllegalArgumentException e) {
                Bukkit.getLogger().log(Level.SEVERE, "[RPGHorses] Failed to load " + crateName + " ( " + config.getString(path + "color") + " is not a valid color )");
            }
            try {
                style = Horse.Style.valueOf(config.getString(path + "style", "NONE"));
            } catch (IllegalArgumentException e) {
                Bukkit.getLogger().log(Level.SEVERE, "[RPGHorses] Failed to load " + crateName + " ( " + config.getString(path + "style") + " is not a valid style )");
            }
            HorseCrate horseCrate = new HorseCrate(crateName, config.getDouble("horse-crates." + crateName + ".price"), minHealth, maxHealth, minMovementSpeed, maxMovementSpeed, minJumpStrength, maxJumpStrength, entityType, color, style, tier);
            this.horseCrates.add(horseCrate);
        }
    }

    public HorseCrate getHorseCrate(String name) {
        for (HorseCrate horseCrate : this.horseCrates) {
            if (horseCrate.getName().equalsIgnoreCase(name)) {
                return horseCrate;
            }
        }
        return null;
    }

    public String getHorseCrateList() {
        String list = "";
        for (HorseCrate horseCrate : this.horseCrates) {
            list += horseCrate.getName() + ", ";
        }
        return list.substring(0, list.length() - 2);
    }

    public double[] getMinAndMaxValues(String info) {
        double[] values = new double[2];
        values[0] = 0;
        values[1] = 0;
        int index = info.indexOf("-");
        if (index > -1) {
            double min = Double.parseDouble(info.substring(0, index));
            values[0] = min;
            double max = Double.parseDouble(info.substring(index + 1));
            values[1] = max;
        }
        return values;
    }

}
