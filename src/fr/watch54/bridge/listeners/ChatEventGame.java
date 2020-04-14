package fr.watch54.bridge.listeners;

import fr.watch54.bridge.Bridge;
import fr.watch54.bridge.state.GameState;
import fr.watch54.bridge.teams.Team;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatEventGame implements Listener {

    private Bridge bridge;

    public ChatEventGame(Bridge bridge){
        this.bridge = bridge;

    }

    @EventHandler
    public void onAsyncChat(AsyncPlayerChatEvent event){

        Player player = event.getPlayer();
        event.setFormat(ChatColor.WHITE + "(Global) §7" + player.getName() + "§f: %2$s");

        if(GameState.isState(GameState.PLAYING) && !(player.getGameMode() == GameMode.SPECTATOR) && event.getMessage().startsWith("@")){

            Team team = bridge.getTeamManager().getTeam(player);
            event.setFormat("§f(Global) " + team.getColor() + team.getName() + " " + player.getName() + "§f: %2$s");

        } else if(GameState.isState(GameState.PLAYING) && !(player.getGameMode() == GameMode.SPECTATOR) && bridge.getTeamManager().getTeam(player) != null){

            Team team = bridge.getTeamManager().getTeam(player);
            event.setFormat("§f(Global) " + team.getColor() + team.getName() + " " + player.getName() + "§f: %2$s");

            if(bridge.getTeamManager().getTeam(player).getPlayers().size() > 1){

                team.getPlayers().forEach(playerTeam -> playerTeam.sendMessage("§e(Équipe) " + team.getColor() + team.getName() + " " + player.getName() + "§f: " + event.getMessage()));
                event.setCancelled(true);

            }

        }

    }

}
