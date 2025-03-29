package me.worddexe.uCombat;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.UUID;

public class UCombat extends JavaPlugin implements Listener, CommandExecutor {
    private final HashMap<UUID, Long> taggedPlayers = new HashMap<>();
    private final HashMap<UUID, UUID> lastDamagers = new HashMap<>();
    private final HashMap<UUID, BukkitRunnable> countdownTasks = new HashMap<>();
    private FileConfiguration config;
    private long tagDuration;
    private boolean punishLogout;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        config = getConfig();
        tagDuration = config.getLong("combat.tag-duration", 15) * 1000;
        punishLogout = config.getBoolean("combat.punish-logout", true);

        Bukkit.getPluginManager().registerEvents(this, this);
        getCommand("ucombat").setExecutor(this);
    }

    @Override
    public void onDisable() {
        taggedPlayers.clear();
        lastDamagers.clear();
        countdownTasks.values().forEach(BukkitRunnable::cancel);
        countdownTasks.clear();
    }

    @EventHandler
    public void onPlayerAttack(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player attacker && event.getEntity() instanceof Player victim) {
            tagPlayer(attacker);
            tagPlayer(victim);
            lastDamagers.put(victim.getUniqueId(), attacker.getUniqueId());
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (taggedPlayers.containsKey(player.getUniqueId()) && punishLogout) {
            player.setHealth(0);
            Bukkit.broadcastMessage(ChatColor.RED + player.getName() + " logged out during combat and was killed!");
        }

        if (lastDamagers.containsKey(player.getUniqueId())) {
            UUID lastDamagerUUID = lastDamagers.get(player.getUniqueId());
            taggedPlayers.remove(lastDamagerUUID);
            lastDamagers.remove(lastDamagerUUID);
        }

        taggedPlayers.remove(player.getUniqueId());
        lastDamagers.remove(player.getUniqueId());
    }

    private void tagPlayer(Player player) {
        UUID uuid = player.getUniqueId();
        long expireTime = System.currentTimeMillis() + tagDuration;
        taggedPlayers.put(uuid, expireTime);

        if (countdownTasks.containsKey(uuid)) {
            countdownTasks.get(uuid).cancel();
        }

        BukkitRunnable countdownTask = new BukkitRunnable() {
            @Override
            public void run() {
                long timeLeft = (expireTime - System.currentTimeMillis()) / 1000;
                if (timeLeft <= 0 || !taggedPlayers.containsKey(uuid)) {
                    taggedPlayers.remove(uuid);
                    countdownTasks.remove(uuid);
                    cancel();
                    return;
                }
                player.sendActionBar(ChatColor.RED + "You are in combat! " + ChatColor.YELLOW + timeLeft + "s left");
            }
        };

        countdownTasks.put(uuid, countdownTask);
        countdownTask.runTaskTimer(this, 0, 20);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            if (!player.hasPermission("ucombat.admin")) {
                player.sendMessage(ChatColor.RED + "You do not have permission to reload the config.");
                return true;
            }
            reloadConfig();
            config = getConfig();
            tagDuration = config.getLong("combat.tag-duration", 15) * 1000;
            punishLogout = config.getBoolean("combat.punish-logout", true);
            player.sendMessage(ChatColor.GREEN + "uCombat config reloaded!");

            new BukkitRunnable() {
                @Override
                public void run() {
                    player.sendActionBar(ChatColor.GREEN + "Config successfully reloaded!");
                }
            }.runTaskLater(this, 20);

            return true;
        }

        player.sendMessage(ChatColor.YELLOW + "uCombat Plugin v1.0 - Use /ucombat reload to reload config.");
        return true;
    }
}