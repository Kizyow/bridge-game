package fr.watch54.bridge.teams;

import org.bukkit.Location;

public class Role {

    private String name;
    private String description;
    private Location location;

    public Role(String name, String description, Location location){
        this.name = name;
        this.description = description;
        this.location = location;

    }

    public String getName(){
        return name;

    }

    public String getDescription(){
        return description;

    }

    public Location getLocation(){
        return location;

    }

}
