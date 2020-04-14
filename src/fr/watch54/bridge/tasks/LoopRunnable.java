package fr.watch54.bridge.tasks;

import fr.watch54.bridge.Bridge;
import fr.watch54.bridge.scoreboard.BridgeScoreboard;
import fr.watch54.bridge.state.GameState;
import fr.watch54.bridge.teams.Team;
import fr.watch54.bridge.utils.ParseLocation;
import fr.watch54.bridge.utils.TitleBuilder;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LoopRunnable extends BukkitRunnable {

    private Bridge bridge;
    private int timer;
    private int minus;


    public LoopRunnable(Bridge bridge){
        this.bridge = bridge;
        this.timer = bridge.getConfiguration().loopTimer();
        this.minus = timer/2;

    }

    @Override
    public void run(){

        if(timer == minus){

            Bukkit.getOnlinePlayers().forEach(player -> player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F));
            Bukkit.broadcastMessage("§7Dans §e" + minus + " secondes§7, les défenseurs remportent le point.");
            Bukkit.broadcastMessage("§6L'équipe attaquante reçoit un bonus §e(+TNTFly)");

            for(Team team : bridge.getTeamManager().getTeamList()){

                if(team.getRole().equals(bridge.getTeamManager().getAttack())){

                    for(Player player : team.getPlayers()){

                        ItemStack tnt = new ItemStack(Material.TNT, 10);
                        ItemMeta tntMeta = tnt.getItemMeta();
                        tntMeta.setDisplayName("§eTNTFly");
                        tntMeta.setLore(Collections.singletonList("§7Propulsez-vous dans les airs"));
                        tnt.setItemMeta(tntMeta);

                        player.getInventory().setItem(3, tnt);
                        player.getInventory().setItem(4, new ItemStack(Material.FLINT_AND_STEEL));

                    }

                }

            }

        }

        if(timer == minus/2){

            Bukkit.getOnlinePlayers().forEach(player -> player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F));
            Bukkit.broadcastMessage("§7Dans §e" + minus/2 + " secondes§7, les défenseurs remportent le point.");
            Bukkit.broadcastMessage("§6L'équipe attaquante reçoit un bonus §e(+Snowfly)");

            for(Team team : bridge.getTeamManager().getTeamList()){

                if(team.getRole().equals(bridge.getTeamManager().getAttack())){

                    for(Player player : team.getPlayers()){

                        ItemStack snowBall = new ItemStack(Material.SNOW_BALL);
                        ItemMeta snowMeta = snowBall.getItemMeta();
                        snowMeta.setDisplayName("§eSnowfly");
                        snowMeta.setLore(Collections.singletonList("§7Envolez-vous tel un oiseau"));
                        snowBall.setItemMeta(snowMeta);

                        player.getInventory().setItem(5, snowBall);

                    }

                }

            }

        }

        if(timer <= 5){

            for(Player player : Bukkit.getOnlinePlayers()){

                ChatColor color = ChatColor.GREEN;
                if(timer <= 3) color = ChatColor.YELLOW;
                if(timer <= 1) color = ChatColor.RED;

                TitleBuilder titleBuilder = new TitleBuilder(player)
                        .setColor(ChatColor.GRAY)
                        .setTitle(color + "" + timer)
                        .setSubtitle("Victoire des défenseurs")
                        .fadeIn(5)
                        .stay(40)
                        .fadeOut(5);
                titleBuilder.build();

            }

        }

        if(timer == 0){

            cancel();

            for(Team team : bridge.getTeamManager().getTeamList()){

                if(team.getRole() == bridge.getTeamManager().getDefend()){

                    team.givePoint();
                    Bukkit.broadcastMessage(ChatColor.GRAY + "L'équipe " + team.getColor() + team.getName() + ChatColor.GRAY + " marque un point en défendant le lit !");
                    team.getPlayers().forEach(player ->  bridge.getMemoryManager().addCoins(player, 20, "Défense du lit"));

                    if(team.getPoints() >= bridge.getConfiguration().winPoint()){

                        GameState.setState(GameState.ENDING);
                        Bukkit.getScheduler().cancelTasks(bridge);

                        EndRunnable endRunnable = new EndRunnable(bridge, team);
                        endRunnable.runTaskTimer(bridge, 0, 20);

                        for (Player player : Bukkit.getOnlinePlayers()){

                            player.setGameMode(GameMode.SURVIVAL);
                            player.getInventory().clear();
                            player.getInventory().setArmorContents(null);
                            player.setAllowFlight(true);

                            bridge.getBlockManager().removeAllBlock();

                            BridgeScoreboard bridgeScoreboard = new BridgeScoreboard(bridge);
                            bridgeScoreboard.remove(player);
                            bridgeScoreboard.createEndingScoreboard(player, team);

                        }

                    } else {

                        bridge.getTeamManager().switchRole();
                        bridge.resetGame();

                    }

                    break;

                }

            }

        }

        timer--;

    }

    public int getTimer(){
        return timer;

    }

    public int getMinus(){
        return minus;

    }

}
