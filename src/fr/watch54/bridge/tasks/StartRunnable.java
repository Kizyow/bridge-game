package fr.watch54.bridge.tasks;

import fr.watch54.bridge.Bridge;
import fr.watch54.bridge.listeners.TntFlyGame;
import fr.watch54.bridge.scoreboard.BridgeScoreboard;
import fr.watch54.bridge.state.GameState;
import fr.watch54.bridge.utils.BarBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class StartRunnable extends BukkitRunnable {

    private Bridge bridge;
    private int timer;
    private boolean forceStart;

    public StartRunnable(Bridge bridge, boolean forceStart){
        this.bridge = bridge;
        this.timer = bridge.getConfiguration().startTimer();
        this.forceStart = forceStart;

        if(forceStart) this.timer = 10;
        Bukkit.broadcastMessage(ChatColor.GOLD + "Démarrage de la partie dans " + ChatColor.YELLOW + timer + " secondes");

    }

    @Override
    public void run(){

        String seconds = timer > 1 ? " secondes" : " seconde";

        Bukkit.getOnlinePlayers().forEach(player -> player.setLevel(timer));

        if(Bukkit.getOnlinePlayers().size() < bridge.getConfiguration().amountPlayers() && !forceStart){

            cancel();
            GameState.setState(GameState.WAITING);

            for(Player player : Bukkit.getOnlinePlayers()) {

                BarBuilder barBuilder = new BarBuilder(player)
                        .setColor(ChatColor.RED)
                        .setTitle("Lancement annulé, il n'y a pas assez de joueurs !");
                barBuilder.build();
                player.playSound(player.getLocation(), Sound.VILLAGER_DEATH, 1.0F, 1.0F);

            }


        }

        if(timer == bridge.getConfiguration().startTimer()/2) Bukkit.broadcastMessage(ChatColor.GOLD + "Démarrage de la partie dans " + ChatColor.YELLOW + timer + seconds);

        if(timer <= 5 && timer > 0) Bukkit.broadcastMessage(ChatColor.GOLD + "Démarrage de la partie dans " + ChatColor.YELLOW + timer + seconds);

        if(timer < 1){

            cancel();

            for(Player player : Bukkit.getOnlinePlayers()){

                if(!bridge.getTeamManager().isPlayerOnTeam(player) && !(player.getGameMode() == GameMode.SPECTATOR)) bridge.getTeamManager().randomTeam(player);

                BridgeScoreboard scoreboard = new BridgeScoreboard(bridge);
                scoreboard.remove(player);
                scoreboard.updatePlayingScoreboard(player, 0, 1);

                player.playSound(player.getLocation(), Sound.EXPLODE, 1.0F, 1.0F);
                Bukkit.getPluginManager().registerEvents(new TntFlyGame(bridge), bridge);

            }

            GameState.setState(GameState.PLAYING);
            bridge.getTeamManager().loadTeams();
            bridge.getTeamManager().switchRole();
            bridge.resetGame();

        }

        timer--;

    }

}
