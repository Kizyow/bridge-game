package fr.watch54.bridge.tasks;

import fr.watch54.bridge.Bridge;
import fr.watch54.bridge.state.GameState;
import fr.watch54.bridge.utils.TitleBuilder;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class RespawnRunnable extends BukkitRunnable {

    private Player player;
    private Bridge bridge;
    private int timer;

    public RespawnRunnable(Player player, Bridge bridge){
        this.player = player;
        this.bridge = bridge;
        this.timer = bridge.getConfiguration().respawnTimer();

    }

    @Override
    public void run(){

        String seconds = timer > 1 ? " secondes" : " seconde";

        TitleBuilder titleBuilder = new TitleBuilder(player)
                .setColor(ChatColor.AQUA)
                .setTitle("Vous êtes mort")
                .setSubtitle(ChatColor.GRAY + "Réapparition dans " + ChatColor.GOLD + timer + seconds)
                .fadeIn(5)
                .stay(20)
                .fadeOut(5);
        titleBuilder.build();

        if(!GameState.isState(GameState.PLAYING)) cancel();

        if(timer == 0){

            cancel();
            bridge.getTeamManager().spawnPlayer(player);

        }

        timer--;

    }

}
