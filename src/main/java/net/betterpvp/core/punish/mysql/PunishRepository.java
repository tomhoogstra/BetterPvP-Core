package net.betterpvp.core.punish.mysql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.bukkit.scheduler.BukkitRunnable;


import net.betterpvp.core.Core;
import net.betterpvp.core.client.ClientUtilities;
import net.betterpvp.core.database.Connect;
import net.betterpvp.core.database.LoadPriority;
import net.betterpvp.core.database.Log;
import net.betterpvp.core.database.Query;
import net.betterpvp.core.database.Repository;
import net.betterpvp.core.punish.Punish;
import net.betterpvp.core.punish.Punish.PunishType;
import net.betterpvp.core.punish.PunishManager;

public class PunishRepository implements Repository<Core>{

	//    UUID punished, UUID punisher, PunishType type, Category category, long time, String reason
	public static final String CREATE_PUNISHMENTS_TABLE = "CREATE TABLE IF NOT EXISTS Punishments "
			+ "(Punished VARCHAR(64), "
			+ "Punisher VARCHAR(64), "
			+ "PunisherName VARCHAR(254), "
			+ "PunishType VARCHAR(64), "
			+ "Time bigint(255),"
			+ "Reason VARCHAR(1024)); ";

	public static void savePunishment(Punish punish) {
		String query = "INSERT INTO punishments (Punished, Punisher, PunishType, Time, Reason) VALUES "
				+ "('" + punish.getPunished().toString() + "', "
				+ "'" + punish.getPunisher().toString() + "', "
				+ "'" + punish.getPunishType().toString() + "', "
				+ "'" + punish.getTime() + "', "
				+ "'" + punish.getReason() + "')";
		Log.write("Clans", "Saved Punishment [" + ClientUtilities.getClient(punish.getPunished()).getName() + "]");
		new Query(query);
	}



	public static void removePunishment(Punish punish) {
		String query = "DELETE FROM punishments WHERE Punished='" + punish.getPunished().toString() + "'";
		new Query(query);
	}


	@Override
	public void initialize() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void load(Core instance) {
		new BukkitRunnable(){

			@Override
			public void run() {
				int count = 0;
				try {
					PreparedStatement statement = Connect.getConnection().prepareStatement("SELECT * FROM punishments");
					ResultSet result = statement.executeQuery();

					while (result.next()) {
						UUID punisher = UUID.fromString(result.getString(1));
						UUID punished = UUID.fromString(result.getString(2));
						PunishType type = PunishType.valueOf(result.getString(3));
						Long time = result.getLong(4);
						String reason = result.getString(5);

						PunishManager.addPunishment(new Punish(punisher, punished, type, time, reason));
						count++;
					}

					statement.close();
					result.close();

					
					Log.debug("MySQL", "Loaded " + count + " Punishments");

				} catch (SQLException ex) {
					Log.debug("Connection", "Could not load Punishments (Connection Error), ");
					ex.printStackTrace();
				}
			}
			
			
		}.runTaskAsynchronously(instance);
		
	}

	@Override
	public LoadPriority getLoadPriority() {
		// TODO Auto-generated method stub
		return LoadPriority.HIGH;
	}
}
