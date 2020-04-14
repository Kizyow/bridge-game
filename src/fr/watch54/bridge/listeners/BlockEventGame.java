package fr.watch54.bridge.listeners;

import fr.watch54.bridge.Bridge;
import fr.watch54.bridge.scoreboard.BridgeScoreboard;
import fr.watch54.bridge.state.GameState;
import fr.watch54.bridge.tasks.EndRunnable;
import fr.watch54.bridge.teams.Team;
import fr.watch54.bridge.utils.Cuboid;
import fr.watch54.bridge.utils.ParseLocation;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

public class BlockEventGame implements Listener {

    private Bridge bridge;

    public BlockEventGame(Bridge bridge){
        this.bridge = bridge;

    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event){

        Player player = event.getPlayer();
        Block block = event.getBlock();

        if(!GameState.isState(GameState.PLAYING)){
            event.setCancelled(true);
            return;

        }

        if(block.getLocation().getY() > bridge.getConfiguration().blockLimit()){
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "Vous ne pouvez pas construire plus haut !");
            return;

        }

        Location bedCorner1 = new ParseLocation(bridge).parseLocationWithMap(bridge.getConfiguration().bedLocationCorner1());
        Location bedCorner2 = new ParseLocation(bridge).parseLocationWithMap(bridge.getConfiguration().bedLocationCorner2());

        if(Cuboid.isInCube(block.getLocation(), bedCorner1, bedCorner2)){
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "Vous ne pouvez pas construire aussi près du lit !");
            return;

        }

        if(!Cuboid.isInCircle(block.getLocation(), new ParseLocation(bridge).parseLocationWithMap(bridge.getConfiguration().buildLocation()), bridge.getConfiguration().buildRadius())
                && bridge.getTeamManager().getTeam(player).getRole() == bridge.getTeamManager().getDefend()){

            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "Vous ne pouvez pas construire en dehors de votre base");
            return;

        }

        if(block.getType() == Material.SANDSTONE) player.getInventory().addItem(new ItemStack(Material.SANDSTONE, 1, (byte) 2));
        bridge.getBlockManager().addBlock(block);

    }

    @EventHandler
    public void onBreak(BlockBreakEvent event){

        Player player = event.getPlayer();
        Block block = event.getBlock();

        if(!GameState.isState(GameState.PLAYING)){
            event.setCancelled(true);
            return;

        }

        if(!bridge.getBlockManager().isPlacedByPlayer(block) && block.getType() != Material.BED_BLOCK){
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "Vous ne pouvez que casser les blocs posés par des joueurs.");

        }

        if(GameState.isState(GameState.PLAYING) && block.getType() == Material.BED_BLOCK){

            event.setCancelled(true);
            Team team = bridge.getTeamManager().getTeam(player);

            if(team.getRole() == bridge.getTeamManager().getAttack()){

                bridge.getLoopRunnable().cancel();
                team.givePoint();
                Bukkit.broadcastMessage(team.getColor() + player.getName() + ChatColor.GRAY + " marque un point en détruisant le lit !");
                bridge.getMemoryManager().addCoins(player, 20, "Destruction du lit");

                if(team.getPoints() >= bridge.getConfiguration().winPoint()){

                    GameState.setState(GameState.ENDING);
                    Bukkit.getScheduler().cancelTasks(bridge);

                    EndRunnable endRunnable = new EndRunnable(bridge, team);
                    endRunnable.runTaskTimer(bridge, 0, 20);

                    for (Player players : Bukkit.getOnlinePlayers()){

                        players.setGameMode(GameMode.SURVIVAL);
                        players.getInventory().clear();
                        players.getInventory().setArmorContents(null);
                        players.setAllowFlight(true);

                        bridge.getBlockManager().removeAllBlock();

                        BridgeScoreboard bridgeScoreboard = new BridgeScoreboard(bridge);
                        bridgeScoreboard.remove(players);
                        bridgeScoreboard.createEndingScoreboard(players, team);

                    }

                } else {

                    bridge.getTeamManager().switchRole();
                    bridge.resetGame();

                }

            } else {

                player.sendMessage(ChatColor.RED + "On ne casse pas son propre lit ! Défendez-le plutôt.");

            }

        }

    }

}
