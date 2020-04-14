package fr.watch54.bridge.teams;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Team {

    private String name;
    private ChatColor color;
    private Role role;
    private int points;

    private List<Player> players = new ArrayList<>();

    public Team(String name, ChatColor color, Role role){
        this.name = name;
        this.color = color;
        this.role = role;
        this.points = 0;

    }

    public void addPlayer(Player player){
        players.add(player);

    }

    public void removePlayer(Player player){
        players.remove(player);

    }

    public void givePoint(){
        points++;

    }

    public String getName(){
        return name;

    }

    public ChatColor getColor(){
        return color;

    }

    public Role getRole(){
        return role;

    }

    public void setRole(Role role){
        this.role = role;

    }

    public int getPoints(){
        return points;

    }

    public List<Player> getPlayers(){
        return players;

    }

}
