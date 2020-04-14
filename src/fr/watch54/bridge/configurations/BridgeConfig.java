package fr.watch54.bridge.configurations;

import fr.watch54.bridge.Bridge;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class BridgeConfig {

    private Bridge bridge;
    private FileConfiguration config;

    public BridgeConfig(Bridge bridge){
        this.bridge = bridge;
        this.config = bridge.getConfig();
        bridge.saveDefaultConfig();

    }

    /*
     * BRIDGE (GLOBAL CONFIGURATION)
     */

    public String gameMode(){
        return config.getString("mode");

    }

    public int amountPlayers(){

        String mode = this.gameMode();

        if(mode.equalsIgnoreCase("1v1")) return 2;
        else if(mode.equalsIgnoreCase("2v2")) return 4;
        else if(mode.equalsIgnoreCase("3v3")) return 6;
        else if(mode.equalsIgnoreCase("4v4")) return 8;
        else return 0;

    }

    public List<String> mapList(){
        return config.getStringList("maps");

    }

    public String selectedMap(){
        return config.getString("map");

    }

    public int startTimer(){
        return config.getInt("start-timer");

    }

    public int loopTimer(){
        return config.getInt("loop-timer");

    }

    public int respawnTimer(){
        return config.getInt("respawn-timer");

    }

    public int winPoint(){
        return config.getInt("point");

    }

    public int blockLimit(){
        return config.getInt(bridge.getMapManager().getMapName().toLowerCase() + ".block-limit");

    }

    public String prefixGame(){
        return config.getString("prefix");

    }

    public String waitingSpawnName(){
        return config.getString("spawn-name");

    }

    public String waitingSpawnLocation(){
        return config.getString("spawn-location");

    }

    public String bedLocationCorner1(){
        return config.getString(bridge.getMapManager().getMapName().toLowerCase() + ".bed-location-corner-1");

    }

    public String bedLocationCorner2(){
        return config.getString(bridge.getMapManager().getMapName().toLowerCase() + ".bed-location-corner-2");

    }

    public String buildLocation(){
        return config.getString(bridge.getMapManager().getMapName().toLowerCase() + ".build-location");

    }

    public int buildRadius(){
        return config.getInt(bridge.getMapManager().getMapName().toLowerCase() + ".build-radius");

    }

    public ConfigurationSection teamList(){
        return config.getConfigurationSection(bridge.getMapManager().getMapName().toLowerCase() + ".teams");

    }
    
}
