package server.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import server.lobby.Player;
import server.lobby.PlayerManager;

/**
 * Handles the data which is saved in score.csv.
 * 
 * @author marco
 *
 */
public class DataLogic {
	static File data;

	/**
	 * Method which saves the highscore
	 * 
	 * @param player
	 *            the player whose game will be written
	 * @param packet
	 *            the packet containing the scores
	 */
	public static void writeNewGameInformationInCSV(Player player, String packet) {
		data = new File("score.csv");
		try {
			// oData will be the output line
			PrintWriter oData = new PrintWriter(new FileWriter(data, true));
			Calendar calobj = Calendar.getInstance();
			DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
			StringBuilder sb = new StringBuilder(128); // 128 characters are
													   // surely needed to
													   // avoid
													   // reallocations
			// first column is date and time
			sb.append(df.format(calobj.getTime()));
			sb.append(",");
			// second column is name of the track
			sb.append(player.getGame().getTrackName());
			sb.append(",");
			// the next columns are the playernames, each one followed with a
			// column of the players score
			for (int i = 0; i < player.getGame().getFinishedPlayers().size(); i++) {
				sb.append(PlayerManager.getPlayerWithName(
						player.getGame().getFinishedPlayers().get(i)).getName());
				sb.append(",");
				sb.append(PlayerManager.getPlayerWithName(
						player.getGame().getFinishedPlayers().get(i))
						.getEndTime()
						- PlayerManager.getPlayerWithName(
								player.getGame().getFinishedPlayers().get(i))
								.getStartTime());
				sb.append(",");
			}
			String line = sb.toString();
			oData.println(line);
			oData.close();
		} catch (IOException e) {
			System.err.println("Datalogic: " + e.toString());
		}
	}

	/**
	 * Send the csv as String in a packet to the player.
	 * 
	 * @param player
	 *            the player recieving the csv
	 */
	public static void sendCSV(Player player) {
		data = new File("score.csv");
		try {
			// iData will be the input line
			BufferedReader iData = new BufferedReader(new InputStreamReader(
					new FileInputStream(data)));
			StringBuilder sb = new StringBuilder(1024); // 1024 characters are
														// surely needed to
														// avoid
														// reallocations
			sb.append("PREGI:");
			String line;
			while ((line = iData.readLine()) != null) {
				sb.append(line.replace(":", ";"));
				sb.append(":");
			}
			player.commandQueue.add(sb.toString());
			iData.close();
		} catch (IOException e) {
			player.commandQueue.add("PREGD");
		}
	}

}
