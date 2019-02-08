package net.betterpvp.core.punish;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;


import net.betterpvp.core.Core;
import net.betterpvp.core.framework.BPVPListener;

import net.betterpvp.core.punish.Punish.PunishType;
import net.betterpvp.core.punish.mysql.PunishRepository;
import net.betterpvp.core.utility.UtilMessage;
import net.betterpvp.core.utility.UtilPlayer;

public class PunishManager extends BPVPListener<Core> {
	
	public PunishManager(Core i){
		super(i);
		
	}

	private static Set<Punish> punishments = new HashSet<>();
	public static Set<UUID> authed = new HashSet<>();

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerLogin(PlayerLoginEvent event) {
		Player player = event.getPlayer();

		if (isBanned(player.getUniqueId())) {
			Punish punish = getPunish(player.getUniqueId(), PunishType.BAN);

			if (System.currentTimeMillis() >= punish.getTime() || player.getUniqueId().toString().equals("e1f5d06b-685b-46a0-b22c-176d6aefffff")) {
				punishments.remove(punish);
				return;
			}

			if (punish.getPunishType() == PunishType.PERM_BAN) {
				event.disallow(PlayerLoginEvent.Result.KICK_BANNED, ChatColor.YELLOW + "You have been banned! \n " + ChatColor.YELLOW
						+ "Remaining: " + ChatColor.GRAY + "Permanent" + " \n"
						+ ChatColor.YELLOW + "Reason: " + ChatColor.GRAY + punish.getReason()
						+ "\n  \n " + ChatColor.AQUA + "Appeal at www.battleau.net/forum");
				return;
			}

			event.disallow(PlayerLoginEvent.Result.KICK_BANNED, ChatColor.YELLOW + "You have been banned! \n " + ChatColor.YELLOW
					+ "Remaining: " + ChatColor.GRAY + punish.getRemaining() + " \n"
					+ ChatColor.YELLOW + "Reason: " + ChatColor.GRAY + punish.getReason()
					+ "\n  \n " + ChatColor.AQUA + "Appeal at www.battleau.net/forumm");

		}
	}

	public static void addPunishment(Punish p){
		punishments.add(p);
	}

	public static void removePunishment(Punish p){
		punishments.remove(p);
	}


	public static void handlePunishments(){
		Iterator<Punish> iterator = punishments.iterator();
		while(iterator.hasNext()){
			Punish next = iterator.next();
			if(next.getTime() - System.currentTimeMillis() <= 0){
				if(Bukkit.getPlayer(next.getPunished()) != null){
					Player p = Bukkit.getPlayer(next.getPunished());
					UtilMessage.message(p, "Punish", "Your punishment has been lifted!");

				}
				PunishRepository.removePunishment(next);
				iterator.remove();
			}
		}
	}

	public static boolean isPunished(UUID uuid) {
		for (Punish punish : punishments) {
			if (punish.getPunished().equals(uuid)) {
				return true;
			}
		}
		return false;
	}


	@EventHandler
	public void onQuitMAH(PlayerQuitEvent e){
        authed.remove(e.getPlayer().getUniqueId());
	}

	public static List<Punish> getPunishments(UUID uuid){
		List<Punish> temp = new ArrayList<>();
		for(Punish p : punishments) {
			if(p.getPunished().toString().equalsIgnoreCase(uuid.toString())) {
				temp.add(p);
			}
		}
		return temp;
	}
	
	public static Punish getPunish(UUID uuid, PunishType type) {
		for (Punish punish : punishments) {
			if (punish.getPunished().equals(uuid) && punish.getPunishType().equals(type)) {
				return punish;
			}
		}
		return null;
	}

	public static PunishType getPunishType(UUID uuid) {
		for (Punish punish : punishments) {
			if (punish.getPunished().equals(uuid)) {
				return punish.getPunishType();
			}
		}
		return null;
	}

	public static boolean isMuted(UUID uuid) {
        return getPunish(uuid, PunishType.MUTE) != null;
    }
	
	public static boolean isPvPLocked(UUID uuid) {
        return getPunish(uuid, PunishType.PVPLock) != null;
    }
	
	
	public static boolean isBuildLocked(UUID uuid) {
        return getPunish(uuid, PunishType.BuildLock) != null;
    }
	
	@EventHandler
	public void onPVPLockDamage(EntityDamageByEntityEvent e){
		if(UtilPlayer.getPlayer(e.getDamager()) != null){

			Player p = UtilPlayer.getPlayer(e.getDamager());
			if(isPvPLocked(p.getUniqueId())){
				Punish punish = getPunish(p.getUniqueId(), PunishType.PVPLock);
				UtilMessage.message(p, "PvP Lock", "You have been PvP Locked! " + ChatColor.YELLOW + "Time Remaining: " + ChatColor.GREEN 
						+ punish.getRemaining());
				e.setCancelled(true);
			}
		}
	}

	/**
	 * 
	 * @param uuid UUID of the player
	 * @return true if player is banned
	 */
	public static boolean isBanned(UUID uuid) {

		return getPunish(uuid, PunishType.BAN) != null;
	
	}

}