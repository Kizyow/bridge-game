package fr.watch54.bridge.managers;

import fr.watch54.bridge.Bridge;
import fr.watch54.bridge.state.GameState;
import fr.watch54.bridge.tasks.RespawnRunnable;
import fr.watch54.bridge.teams.Role;
import fr.watch54.bridge.teams.Team;
import fr.watch54.bridge.utils.ParseLocation;
import fr.watch54.bridge.utils.TitleBuilder;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.*;

public class TeamManager {

    private Bridge bridge;
    private List<Team> teamList = new ArrayList<>();
    private List<Role> roleList = new ArrayList<>();
    private Map<Player, Team> teamMap = new HashMap<>();

    public TeamManager(Bridge bridge){
        this.bridge = bridge;

    }

    public void addPlayer(Player player, Team team){

        if(team.getPlayers().contains(player)){
            player.sendMessage(ChatColor.RED + "Vous êtes déjà dans cette équipe.");
            return;

        }

        if(team.getPlayers().size() >= (bridge.getConfiguration().amountPlayers()/2)){
            player.sendMessage(ChatColor.RED + "Cette équipe est pleine.");
            return;

        }

        for(Team teams : teamList) if(teams.getPlayers().contains(player) && teams != team) teams.removePlayer(player);

        team.addPlayer(player);
        player.setPlayerListName(team.getColor() + player.getName());
        player.sendMessage(ChatColor.GRAY + "Vous rejoignez l'équipe " + team.getColor() + team.getName());

    }

    public void removePlayer(Player player){

        if(!teamMap.containsKey(player)){
            player.sendMessage(ChatColor.RED + "Vous n'êtes dans aucune équipe.");
            return;

        }

        Team team = teamMap.get(player);
        teamMap.remove(player);

        team.removePlayer(player);
        player.sendMessage(ChatColor.GRAY + "Vous avez quitté l'équipe " + team.getColor() + team.getName());
        player.setPlayerListName(player.getName());

    }

    public void registerTeams(){

        ConfigurationSection section = bridge.getConfiguration().teamList();

        for(String teamName : section.getKeys(false)){
            String name = ChatColor.translateAlternateColorCodes('&', section.getString(teamName + ".name"));
            ChatColor color = ChatColor.getByChar(section.getString(teamName + ".color"));
            String roleName = section.getString(teamName + ".role");
            String roleDesc = section.getString(teamName + ".desc");
            Location location = new ParseLocation(bridge).parseLocationWithMap(section.getString(teamName + ".location"));

            Role role = new Role(roleName, roleDesc, location);
            roleList.add(role);
            Team team = new Team(name, color, role);
            teamList.add(team);

        }

    }

    public void loadTeams(){

        for(Team team : teamList){

            for(Player player : team.getPlayers()){

                teamMap.put(player, team);

            }

        }

    }

    public boolean isPlayerOnTeam(Player player){

        for(Team team : teamList) if(team.getPlayers().contains(player)) return true;
        return false;

    }

    public void randomTeam(Player player){

        if(!teamMap.containsKey(player)){

            for (Team team : teamList){

                if(team.getPlayers().size() < (bridge.getConfiguration().amountPlayers()/2)){

                    this.addPlayer(player, team);
                    break;

                }

            }

        }

    }

    public void switchRole(){

        Role redRole = getRedTeam().getRole() == getAttack() ? getDefend() : getAttack();
        Role blueRole = getBlueTeam().getRole() == getAttack() ? getDefend() : getAttack();

        getRedTeam().setRole(redRole);
        getBlueTeam().setRole(blueRole);

        for(Player player : getRedTeam().getPlayers()){

            TitleBuilder titleBuilder = new TitleBuilder(player)
                    .setColor(ChatColor.GREEN)
                    .fadeIn(5)
                    .stay(60)
                    .fadeOut(10)
                    .setTitle(redRole.getName())
                    .setSubtitle(ChatColor.GRAY + redRole.getDescription());
            titleBuilder.build();

        }

        for(Player player : getBlueTeam().getPlayers()){

            TitleBuilder titleBuilder = new TitleBuilder(player)
                    .setColor(ChatColor.GREEN)
                    .fadeIn(5)
                    .stay(60)
                    .fadeOut(10)
                    .setTitle(blueRole.getName())
                    .setSubtitle(ChatColor.GRAY + blueRole.getDescription());
            titleBuilder.build();

        }

    }

    public void spawnPlayer(Player player){

        Location location = getTeam(player).getRole().getLocation();

        player.setGameMode(GameMode.SURVIVAL);
        player.setHealth(20D);
        player.getActivePotionEffects().forEach(potionEffect -> player.removePotionEffect(potionEffect.getType()));
        player.teleport(location);

        player.getInventory().clear();

        ItemStack sword = new ItemStack(Material.IRON_SWORD);
        ItemMeta sEnchant = sword.getItemMeta();
        sEnchant.addEnchant(Enchantment.DAMAGE_ALL, 1, true);
        sword.setItemMeta(sEnchant);

        player.getInventory().setItem(0, sword);
        player.getInventory().setItem(1, new ItemStack(Material.IRON_PICKAXE, 1));
        player.getInventory().setItem(2, new ItemStack(Material.GOLDEN_APPLE, 64));
        player.getInventory().addItem(new ItemStack(Material.SANDSTONE, 2112, (byte) 2));

        if(getTeam(player).getRole().equals(getAttack()) && bridge.getLoopRunnable() != null){

            if(bridge.getLoopRunnable().getTimer() <= bridge.getLoopRunnable().getMinus()){

                ItemStack tnt = new ItemStack(Material.TNT, 10);
                ItemMeta tntMeta = tnt.getItemMeta();
                tntMeta.setDisplayName("§eTNTFly");
                tntMeta.setLore(Collections.singletonList("§7Propulsez-vous dans les airs"));
                tnt.setItemMeta(tntMeta);

                player.getInventory().setItem(3, tnt);
                player.getInventory().setItem(4, new ItemStack(Material.FLINT_AND_STEEL));
            }

            if(bridge.getLoopRunnable().getTimer() <= bridge.getLoopRunnable().getMinus()/2){

                ItemStack snowBall = new ItemStack(Material.SNOW_BALL);
                ItemMeta snowMeta = snowBall.getItemMeta();
                snowMeta.setDisplayName("§eSnowfly");
                snowMeta.setLore(Collections.singletonList("§7Envolez-vous tel un oiseau"));
                snowBall.setItemMeta(snowMeta);

                player.getInventory().setItem(5, snowBall);

            }

        }

        ItemStack helmet = new ItemStack(Material.LEATHER_HELMET);
        ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
        ItemStack legs = new ItemStack(Material.LEATHER_LEGGINGS);
        ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);

        ItemMeta enchant = helmet.getItemMeta();
        enchant.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1, true);

        helmet.setItemMeta(enchant);
        chestplate.setItemMeta(enchant);
        legs.setItemMeta(enchant);
        boots.setItemMeta(enchant);

        if(getTeam(player).getColor().equals(ChatColor.RED)) setArmorColor(helmet, Color.RED);
        else setArmorColor(helmet, Color.AQUA);

        if(getTeam(player).getColor().equals(ChatColor.RED)) setArmorColor(chestplate, Color.RED);
        else setArmorColor(chestplate, Color.AQUA);

        if(getTeam(player).getColor().equals(ChatColor.RED)) setArmorColor(legs, Color.RED);
        else setArmorColor(legs, Color.AQUA);

        if(getTeam(player).getColor().equals(ChatColor.RED)) setArmorColor(boots, Color.RED);
        else setArmorColor(boots, Color.AQUA);

        player.getInventory().setArmorContents(null);

        player.getInventory().setHelmet(helmet);
        player.getInventory().setChestplate(chestplate);
        player.getInventory().setLeggings(legs);
        player.getInventory().setBoots(boots);

    }

    public void respawnPlayer(Player player){

        if(GameState.isState(GameState.PLAYING)){

            player.setGameMode(GameMode.SPECTATOR);
            player.setHealth(20D);
            player.teleport(getTeam(player).getRole().getLocation());
            player.getInventory().clear();

            RespawnRunnable task = new RespawnRunnable(player, bridge);
            task.runTaskTimer(bridge, 0, 20);

        }

    }

    private void setArmorColor(ItemStack itemStack, Color color){
        LeatherArmorMeta itemMeta = (LeatherArmorMeta) itemStack.getItemMeta();
        itemMeta.setColor(color);
        itemStack.setItemMeta(itemMeta);

    }

    public Team getTeam(Player player){
        return teamMap.get(player);

    }

    public Team getRedTeam(){
        return teamList.get(0);

    }

    public Team getBlueTeam(){
        return teamList.get(1);

    }

    public List<Team> getTeamList(){
        return teamList;

    }

    public Role getDefend(){
        return roleList.get(0);

    }

    public Role getAttack(){
        return roleList.get(1);

    }

    public Map<Player, Team> getTeamMap(){
        return teamMap;

    }

}