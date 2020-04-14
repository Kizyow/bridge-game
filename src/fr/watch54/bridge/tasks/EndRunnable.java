package fr.watch54.bridge.tasks;

import fr.watch54.bridge.Bridge;
import fr.watch54.bridge.teams.Team;
import fr.watch54.bridge.utils.TitleBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;

public class EndRunnable extends BukkitRunnable {

    private Bridge bridge;
    private Team team;
    private int timer;

    public EndRunnable(Bridge bridge, Team team){
        this.bridge = bridge;
        this.team = team;
        this.timer = 10;

    }

    @Override
    public void run(){

        if(timer == 10){

            for(Player player : Bukkit.getOnlinePlayers()){

                TitleBuilder titleBuilder = new TitleBuilder(player)
                        .setColor(ChatColor.GREEN)
                        .setTitle("§eFin de partie !")
                        .setSubtitle("§e§k|§b§k|§c§k|§d§k|§r §6Victoire de l'équipe §e" + team.getName() + " §d§k|§c§k|§b§k|§e§k|")
                        .fadeIn(5)
                        .stay(60)
                        .fadeOut(10);
                titleBuilder.build();

            }

        }

        if(timer == 9){

            for(Player player : Bukkit.getOnlinePlayers()){

                player.sendMessage("§6--------------------------------------------------");
                player.sendMessage("§7Fin partie sur §e" + Bukkit.getServerName());
                player.sendMessage("§7Gain de §6Pièces §7sur la partie : §e" + bridge.getMemoryManager().totalCoins(player));
                player.sendMessage("§7Gain d'§3Expèriences §7sur la partie : §e" + bridge.getMemoryManager().totalEXP(player));
                player.sendMessage("§6--------------------------------------------------");
                player.sendMessage("§a➟ §7Rejouer immédiatement sur : §e[ce jeu] §a[cette carte]");
                player.sendMessage("§a➟ §7Vous allez être transféré dans un hub dans §e8 secondes§f.");

                bridge.getMemoryManager().addEXP(player, 50, "Fin de la partie");

            }

        }

        if(timer == 8 || timer == 7 || timer == 6 || timer == 5) fireworkWinner();

        if(timer == 0) Bukkit.shutdown();

        timer--;

    }

    private void fireworkWinner(){

        for(Player player : team.getPlayers()){

            Firework firework = (Firework) player.getWorld().spawnEntity(player.getLocation(), EntityType.FIREWORK);
            firework.detonate();
            FireworkMeta fireworkMeta = firework.getFireworkMeta();

            FireworkEffect effect = FireworkEffect.builder()
                        .flicker(true)
                        .withColor(Color.ORANGE)
                        .withFade(Color.YELLOW)
                        .with(FireworkEffect.Type.BALL_LARGE)
                        .trail(true)
                        .build();

            fireworkMeta.setPower(0);
            fireworkMeta.addEffect(effect);
            firework.setFireworkMeta(fireworkMeta);

        }

    }

}
