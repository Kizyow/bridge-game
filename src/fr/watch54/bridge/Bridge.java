package fr.watch54.bridge;

import fr.watch54.bridge.commands.BridgeCommand;
import fr.watch54.bridge.configurations.BridgeConfig;
import fr.watch54.bridge.listeners.*;
import fr.watch54.bridge.managers.BlockManager;
import fr.watch54.bridge.managers.MemoryManager;
import fr.watch54.bridge.managers.MapManager;
import fr.watch54.bridge.managers.TeamManager;
import fr.watch54.bridge.state.GameState;
import fr.watch54.bridge.tasks.LoopRunnable;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Bridge extends JavaPlugin {

    private BridgeConfig bridgeConfig = new BridgeConfig(this);
    private TeamManager teamManager;
    private BlockManager blockManager;
    private MapManager mapManager = new MapManager(this);
    private MemoryManager memoryManager;
    private LoopRunnable loopRunnable;

    @Override
    public void onLoad(){

        for(String map : bridgeConfig.mapList()) mapManager.addMap(map);
        if(bridgeConfig.selectedMap().equalsIgnoreCase("none")) mapManager.randomMap();
        else mapManager.setMap(bridgeConfig.selectedMap());

    }

    @Override
    public void onEnable(){

        GameState.setState(GameState.WAITING);

        teamManager = new TeamManager(this);
        blockManager = new BlockManager(this);
        memoryManager = new MemoryManager(this);

        mapManager.loadMap();
        teamManager.registerTeams();

        Bukkit.getPluginManager().registerEvents(new BlockEventGame(this), this);
        Bukkit.getPluginManager().registerEvents(new ChatEventGame(this), this);
        Bukkit.getPluginManager().registerEvents(new DamageEventGame(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerEventGame(this), this);
        Bukkit.getPluginManager().registerEvents(new WorldEventGame(), this);

        getCommand("bridge").setExecutor(new BridgeCommand(this));

        for(World world : Bukkit.getWorlds()){
            world.setTime(6000);
            world.setDifficulty(Difficulty.EASY);
            world.setGameRuleValue("doDaylightCycle", "false");
            world.setGameRuleValue("doMobSpawning", "false");
            world.setAutoSave(false);

        }

    }

    @Override
    public void onDisable(){
        blockManager.removeAllBlock();
        Bukkit.getWorld(mapManager.getMapName()).save();

    }

    public void resetGame(){

        blockManager.removeAllBlock();

        if(GameState.isState(GameState.PLAYING)){
            loopRunnable = new LoopRunnable(this);
            loopRunnable.runTaskTimer(this, 0, 20);

        }

        for(Player player : Bukkit.getOnlinePlayers())
            if(teamManager.getTeamMap().containsKey(player))
                teamManager.spawnPlayer(player);

    }

    public BridgeConfig getConfiguration(){
        return bridgeConfig;

    }

    public TeamManager getTeamManager(){
        return teamManager;

    }

    public BlockManager getBlockManager(){
        return blockManager;

    }

    public MapManager getMapManager(){
        return mapManager;

    }

    public MemoryManager getMemoryManager(){
        return memoryManager;

    }

    public LoopRunnable getLoopRunnable(){
        return loopRunnable;

    }

}
