package fr.watch54.bridge.managers;

import fr.watch54.bridge.Bridge;
import org.bukkit.WorldCreator;

import java.util.*;

public class MapManager {

    private Bridge bridge;
    private List<String> maps = new ArrayList<>();
    private String mapGame;

    public MapManager(Bridge bridge){
        this.bridge = bridge;

    }

    public void loadMap(){
        new WorldCreator(mapGame).createWorld();

    }

    public void addMap(String mapName){

        if(!maps.contains(mapName)){
            maps.add(mapName);

        }

    }

    public void setMap(String mapName){
        this.mapGame = mapName;

    }

    public void randomMap(){
        mapGame = maps.get(new Random().nextInt(maps.size()));

    }

    public String getMapName(){
        return mapGame;

    }

    public List<String> getMaps(){
        return maps;

    }

}
