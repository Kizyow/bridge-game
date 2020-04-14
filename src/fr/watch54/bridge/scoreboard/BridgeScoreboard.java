package fr.watch54.bridge.scoreboard;

import fr.minuskube.netherboard.Netherboard;
import fr.minuskube.netherboard.bukkit.BPlayerBoard;
import fr.watch54.bridge.Bridge;
import fr.watch54.bridge.teams.Team;
import org.apache.commons.lang.time.DateFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class BridgeScoreboard {

    private Bridge bridge;
    private static Map<Player, BukkitTask> bukkitTask = new HashMap<>();

    public BridgeScoreboard(Bridge bridge){
        this.bridge = bridge;

    }

    public void createWaitingScoreboard(Player player){

        BPlayerBoard board;

        if(Netherboard.instance().getBoard(player) != null) board = Netherboard.instance().getBoard(player);
        else board = Netherboard.instance().createBoard(player, "§8- §e§lBRIDGE §8-");

        String line = "§cEn attente...";
        if(Bukkit.getOnlinePlayers().size() >= bridge.getConfiguration().amountPlayers()) line = "§6Début de la partie...";

        String dateString = DateFormatUtils.format(Date.from(Instant.now()), "dd/MM/yy");

        board.set(ChatColor.GRAY + dateString + " " + ChatColor.DARK_GRAY + Bukkit.getServerName(), 10);
        board.set("§a", 9);
        board.set("Carte: " + ChatColor.GREEN + bridge.getMapManager().getMapName(), 8);
        board.set("Mode: " + ChatColor.GREEN + bridge.getConfiguration().gameMode(), 7);
        board.set("§b", 6);
        board.set("Joueurs: " + ChatColor.GREEN + Bukkit.getOnlinePlayers().size() + "/" + bridge.getConfiguration().amountPlayers(), 5);
        board.set(line, 4);
        board.set("§d", 2);
        board.set(ChatColor.YELLOW + "play.hypemc.fr", 1);

    }

    public void updateWaitingScoreboard(Player player, int delay, int tick){
        BukkitTask task = Bukkit.getScheduler().runTaskTimer(bridge, () -> createWaitingScoreboard(player), delay, tick);
        bukkitTask.put(player, task);

    }

    public void createPlayingScoreboard(Player player){

        BPlayerBoard board;

        if(Netherboard.instance().getBoard(player) != null) board = Netherboard.instance().getBoard(player);
        else board = Netherboard.instance().createBoard(player, "§8- §e§lBRIDGE §8-");

        Team playerTeam = bridge.getTeamManager().getTeam(player);
        String dateString = DateFormatUtils.format(Date.from(Instant.now()), "dd/MM/yy");

        Team redTeam = bridge.getTeamManager().getRedTeam();
        Team blueTeam = bridge.getTeamManager().getBlueTeam();

        int timer = bridge.getLoopRunnable().getTimer()+1;

        String roleString = "Changement";
        if(playerTeam.getRole() == bridge.getTeamManager().getAttack()) roleString = "Défaite";
        if(playerTeam.getRole() == bridge.getTeamManager().getDefend()) roleString = "Victoire";

        String lineTime = roleString + " dans §a" + DateFormatUtils.format(timer*1000, "m:ss");;
        if(timer > bridge.getLoopRunnable().getMinus()/2)
            lineTime = "Bonus II dans §a" + DateFormatUtils.format((timer-bridge.getLoopRunnable().getMinus()/2)*1000, "m:ss");
        if(timer > bridge.getLoopRunnable().getMinus())
            lineTime = "Bonus dans §a" + DateFormatUtils.format((timer-bridge.getLoopRunnable().getMinus())*1000, "m:ss");

        board.set(ChatColor.GRAY + dateString + " " + ChatColor.DARK_GRAY + Bukkit.getServerName(), 9);
        board.set("§a", 8);
        board.set(lineTime, 7);
        board.set("§b", 6);
        board.set(redTeam.getColor() + redTeam.getName() + ": " + ChatColor.WHITE + redTeam.getPoints() , 5);
        board.set(blueTeam.getColor() + blueTeam.getName() + ": " + ChatColor.WHITE + blueTeam.getPoints() , 4);
        board.set("§c", 3);
        board.set("§7Objectif: " + bridge.getConfiguration().winPoint() + " points", 2);
        board.set(ChatColor.YELLOW + "play.hypemc.fr", 1);

    }

    public void updatePlayingScoreboard(Player player, int delay, int tick){
        BukkitTask task = Bukkit.getScheduler().runTaskTimer(bridge, () -> createPlayingScoreboard(player), delay, tick);
        bukkitTask.put(player, task);

    }

    public void createEndingScoreboard(Player player, Team winnerTeam){

        BPlayerBoard board;

        if(Netherboard.instance().getBoard(player) != null) {
            board = Netherboard.instance().getBoard(player);
            board.delete();

        }

        board = Netherboard.instance().createBoard(player, "§8- §e§lBRIDGE §8-");

        String dateString = DateFormatUtils.format(Date.from(Instant.now()), "dd/MM/yy");

        board.set(ChatColor.GRAY + dateString + " " + ChatColor.DARK_GRAY + Bukkit.getServerName(), 5);
        board.set("§a", 4);
        board.set("§fVictoire de l'équipe " + winnerTeam.getColor() + winnerTeam.getName(), 3);
        board.set("§b", 2);
        board.set(ChatColor.YELLOW + "play.hypemc.fr", 1);

    }

    public void remove(Player player){

        if(bukkitTask.containsKey(player)){
            bukkitTask.get(player).cancel();
            bukkitTask.remove(player);

        }

    }

}
