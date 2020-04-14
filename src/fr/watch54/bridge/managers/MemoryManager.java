package fr.watch54.bridge.managers;

import fr.watch54.bridge.Bridge;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class MemoryManager {

    private Bridge bridge;
    private Map<Player, Integer> coins = new HashMap<>();
    private Map<Player, Integer> experiences = new HashMap<>();
    private Map<Player, Integer> kills = new HashMap<>();
    private Map<Player, Integer> deaths = new HashMap<>();

    public MemoryManager(Bridge bridge){
        this.bridge = bridge;

    }

    public void addCoins(Player player, int amount, String reason){

        if(!coins.containsKey(player)) coins.put(player, 0);
        int total = amount + coins.get(player);

        coins.remove(player);
        coins.put(player, total);

        player.sendMessage("§7Gain de Pièces §e+" + amount + " §7(" + reason +")");

    }

    public double totalCoins(Player player){
        if(coins.containsKey(player)) return coins.get(player);
        return 0;

    }

    public void addEXP(Player player, int amount, String reason){

        if(!experiences.containsKey(player)) experiences.put(player, 0);
        int total = amount + experiences.get(player);

        experiences.remove(player);
        experiences.put(player, total);

        player.sendMessage("§7Gain d'EXP §3+" + amount + " §7(" + reason +")");

    }

    public double totalEXP(Player player){
        if(experiences.containsKey(player)) return experiences.get(player);
        return 0;

    }

    public void addKill(Player player){

        if(!kills.containsKey(player)) kills.put(player, 0);
        int total = kills.get(player)+1;

        kills.remove(player);
        kills.put(player, total);

    }

    public double totalKills(Player player){
        if(kills.containsKey(player)) return kills.get(player);
        return 0;

    }

    public void addDeath(Player player){

        if(!deaths.containsKey(player)) deaths.put(player, 0);
        int total = deaths.get(player)+1;

        deaths.remove(player);
        deaths.put(player, total);

    }

    public double totalDeaths(Player player){
        if(deaths.containsKey(player)) return deaths.get(player);
        return 0;

    }

}
