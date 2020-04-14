package fr.watch54.bridge.listeners;

import fr.watch54.bridge.Bridge;
import fr.watch54.bridge.state.GameState;
import fr.watch54.bridge.teams.Team;
import fr.watch54.bridge.utils.ParseLocation;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class DamageEventGame implements Listener {

    private Bridge bridge;

    public DamageEventGame(Bridge bridge){
        this.bridge = bridge;

    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event){

        if(!GameState.isState(GameState.PLAYING)){
            event.setCancelled(true);
            return;

        }

        if(event.getEntity() instanceof Player && event.getCause() == EntityDamageEvent.DamageCause.FALL){

            Player player = (Player) event.getEntity();
            event.setDamage(event.getDamage() * 0.25);

            if(player.getHealth() <= event.getDamage()){

                Team team = bridge.getTeamManager().getTeam(player);

                event.setDamage(0);
                bridge.getTeamManager().respawnPlayer(player);
                Bukkit.broadcastMessage(team.getColor() + player.getName() + ChatColor.GRAY + " a été tué par une chute mortelle.");
                bridge.getMemoryManager().addDeath(player);

            }

        }

    }

    @EventHandler
    public void onEntityDamageEntity(EntityDamageByEntityEvent event){

        if(GameState.isState(GameState.PLAYING)){

            if(event.getEntity() instanceof Player){

                Player player = (Player) event.getEntity();

                if(event.getDamager() instanceof TNTPrimed){

                    TNTPrimed tntPrimed = (TNTPrimed) event.getDamager();
                    double damage = event.getDamage() * 0.25;

                    event.setDamage(damage);

                    for(Entity entity : tntPrimed.getNearbyEntities(1.5, 1.5, 1.5)){

                        if(entity instanceof Player){

                            Player playerAround = (Player) entity;
                            if(player.equals(playerAround)) player.setVelocity(player.getLocation().getDirection().multiply(damage).setY(1));

                        }

                    }

                    if(player.getHealth() <= event.getDamage()){

                        event.setDamage(0);
                        bridge.getTeamManager().respawnPlayer(player);

                        Team team = bridge.getTeamManager().getTeam(player);

                        Bukkit.broadcastMessage(team.getColor() + player.getName() + ChatColor.GRAY + " a été tué par une explosion.");
                        bridge.getMemoryManager().addDeath(player);


                    }

                }

                if(event.getDamager() instanceof Player){

                    Player target = (Player) event.getDamager();

                    if (bridge.getTeamManager().getTeam(target).equals(bridge.getTeamManager().getTeam(player))) {
                        event.setCancelled(true);
                        return;

                    }

                    if(player.getHealth() <= event.getDamage()){

                        event.setDamage(0);
                        bridge.getTeamManager().respawnPlayer(player);

                        Player killer = player.getKiller();

                        Team team = bridge.getTeamManager().getTeam(player);
                        Team kTeam = bridge.getTeamManager().getTeam(killer);

                        try {

                            Bukkit.broadcastMessage(team.getColor() + player.getName() + ChatColor.GRAY + " a été tué par " + kTeam.getColor() + killer.getName() + ".");
                            bridge.getMemoryManager().addCoins(killer, 5, "Tué un joueur");
                            bridge.getMemoryManager().addKill(killer);
                            bridge.getMemoryManager().addDeath(player);

                        } catch (NullPointerException e){

                            System.out.println("Can't broadcast the death of this player.");

                        }

                    }

                }

            }

        }

    }

    @EventHandler
    public void onVoid(PlayerMoveEvent event){

        Player player = event.getPlayer();
        Location location = player.getLocation();

        if(location.getY() < 0){

            if(player.getGameMode() != GameMode.SPECTATOR && GameState.isState(GameState.PLAYING)){

                if(player.getLastDamageCause() != null && player.getLastDamageCause().getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK){

                    EntityDamageByEntityEvent entityEvent = (EntityDamageByEntityEvent) player.getLastDamageCause();

                    if(entityEvent.getDamager() instanceof Player){

                        Player killer = (Player) entityEvent.getDamager();
                        Team team = bridge.getTeamManager().getTeam(player);
                        Team teamKiller = bridge.getTeamManager().getTeam(killer);

                        bridge.getTeamManager().respawnPlayer(player);
                        Bukkit.broadcastMessage(team.getColor() + player.getName() + ChatColor.GRAY + " est mort par le vide et " + teamKiller.getColor() + killer.getName());
                        bridge.getMemoryManager().addDeath(player);
                        bridge.getMemoryManager().addKill(killer);
                        bridge.getMemoryManager().addCoins(killer, 1, "Poussé dans le vide");
                        player.setLastDamageCause(new EntityDamageEvent(player, EntityDamageEvent.DamageCause.VOID, 0));

                    }

                } else {

                    Team team = bridge.getTeamManager().getTeam(player);
                    bridge.getTeamManager().respawnPlayer(player);
                    Bukkit.broadcastMessage(team.getColor() + player.getName() + ChatColor.GRAY + " est mort par le vide");
                    bridge.getMemoryManager().addDeath(player);
                    player.setLastDamageCause(new EntityDamageEvent(player, EntityDamageEvent.DamageCause.VOID, 0));

                }

            }

            if(!GameState.isState(GameState.PLAYING)){

                Location spawnLocation = new ParseLocation(bridge).parseLocation(bridge.getConfiguration().waitingSpawnName(), bridge.getConfiguration().waitingSpawnLocation());
                player.teleport(spawnLocation);
                player.sendMessage(ChatColor.GRAY + "Wooosh, c'était moins une, attention où vous marchez ! :)");

            }

        }

    }

}
