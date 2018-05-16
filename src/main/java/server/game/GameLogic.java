package server.game;

import server.data.DataLogic;
import server.lobby.Player;
import server.lobby.PlayerManager;
import server.lobby.ServerGUI;
import shared.game.PowerRacerGame;

import java.lang.StringBuilder;

/**
 * Handles the game communication.
 * 
 * @author benzumbrunn
 * @author marco
 * @since 2015/04/06
 *
 */
public class GameLogic {

	PlayerManager playermanager;

	/**
	 * Builds the string with all the input information.
	 * 
	 * @param player
	 *            from who the information is from
	 * @param parts
	 *            from packet 'GINPI' (game input information)
	 */
	public static void sendInformationToOtherPlayers(Player player,
			String[] parts) {

		if (player.getLegitPlayer() < 100) {
			player.setLegitPlayer(player.getLegitPlayer() + 1);
		}
		// Updates the ServerSide game at first (setting x,y, etc...)
		updateServerSideGame(player, parts);
		// Packet sending to other players
		StringBuilder sb = new StringBuilder(512); // 512 characters should be
												   // enough to avoid
												   // reallocations
		sb.append("GINPI:");
		sb.append(player.carIndex);

		// append rest of the string
		for (int i = 1; i < parts.length; i++) {
			sb.append(":");
			sb.append(parts[i]);
		}
		String packet = sb.toString();

		// iterate through whole playerlist
		for (int i = 0; i < PlayerManager.playerlist.size(); i++) {
			// send packet to all players in the same game except for the sender
			if ((PlayerManager.playerlist.get(i).getGame() == player.getGame())
					&& (PlayerManager.playerlist.get(i) != player)) {
				PlayerManager.playerlist.get(i).commandQueue.add(packet);
			}
		}
	}

	/**
	 * Updates the game through incoming packets.
	 * 
	 * @param player
	 *            from which the packet is sent.
	 * @param parts
	 *            from the packet.
	 */
	private static void updateServerSideGame(Player player, String[] parts) {
		// GINPI:false:false:false:false:2422.5:212.5:0.0:0.0
		if (player.getGame() != null) {
			// up-input
			player.getGame().setCarInputUp(player.getCarIndex(),
					Boolean.parseBoolean(parts[1]));
			// down-input
			player.getGame().setCarInputDown(player.getCarIndex(),
					Boolean.parseBoolean(parts[2]));
			// left-input
			player.getGame().setCarInputLeft(player.getCarIndex(),
					Boolean.parseBoolean(parts[3]));
			// right-input
			player.getGame().setCarInputRight(player.getCarIndex(),
					Boolean.parseBoolean(parts[4]));
			// car position
			player.getGame().setCarPosition(player.getCarIndex(),
					Double.parseDouble(parts[5]), Double.parseDouble(parts[6]));
			// car speed
			player.getGame().setCarSpeed(player.getCarIndex(),
					Double.parseDouble(parts[7]));
			// car rotation
			player.getGame().setCarRotation(player.getCarIndex(),
					Double.parseDouble(parts[8]));
		}
	}

	/**
	 * Gets called when the player finishes, sends the required data to the
	 * gamelogic.
	 * 
	 * @param player
	 *            who finished.
	 */
	public static void sendGameFinishedPacketWithPlace(Player player) {
		PowerRacerGame game = player.getGame();
		game.getFinishedPlayers().add(player.getName());
		player.setEndTime(System.currentTimeMillis());
		if (game.getFinishedPlayers().size() == game.getNumberOfPlayers()) {
			GameLogic.sendGameFinishedInformationToAllPlayers(player);
		}

	}

	/**
	 * Builds packet which is sent when a player is finished.
	 * 
	 * @param player
	 *            who crossed the finish line
	 */
	private static void sendGameFinishedInformationToAllPlayers(Player player) {
		PowerRacerGame game = player.getGame();
		StringBuilder result = new StringBuilder();
		result.append("GCOMI:");
		for (int i = 0; i < game.getFinishedPlayers().size(); i++) {
			result.append(PlayerManager.getPlayerWithName(
					game.getFinishedPlayers().get(i)).getEndTime()
					- PlayerManager.getPlayerWithName(
							game.getFinishedPlayers().get(i)).getStartTime());
			result.append(":");
			result.append(game.getFinishedPlayers().get(i));
			result.append(":");
		}
		String packet = result.toString();
		DataLogic.writeNewGameInformationInCSV(player, packet);
		for (int i = 0; i < PlayerManager.playerlist.size(); i++) {
			// send packet to all players in the same game
			if ((PlayerManager.playerlist.get(i).getGame() == player.getGame())) {
				PlayerManager.playerlist.get(i).commandQueue.add(packet);
			}
		}
		ServerGUI.addToConsole("Finished game with players: "
				+ game.getPlayers().toString() + ".");
		GameManager.removeGame(game);
	}

	/**
	 * Timer for highscore purposes.
	 * 
	 * @param game
	 *            object which is updated
	 */
	public static void startTimer(PowerRacerGame game) {
		for (int i = 0; i < PlayerManager.playerlist.size(); i++) {
			if (PlayerManager.playerlist.get(i).getGame() == game) {
				PlayerManager.playerlist.get(i).setStartTime(
						System.currentTimeMillis());
			}
		}
	}

	public static void sendPacketToAllPlayers(PowerRacerGame game, String packet) {
		// iterate through whole playerlist
		for (int i = 0; i < PlayerManager.playerlist.size(); i++) {
			// send packet to all players in the same game except for the sender
			if ((PlayerManager.playerlist.get(i).getGame() == game)) {
				PlayerManager.playerlist.get(i).commandQueue.add(packet);
			}
		}

	}

	public static void sendCollidableDisabledToOtherPlayers(Player player,
			String colID) {
		StringBuilder sb = new StringBuilder(512);
		sb.append("GCLDI:");
		sb.append(colID);
		String packet = sb.toString();

		sendPacketToOtherPlayers(player, packet);
	}

	public static void handleCollidableCreationPacket(Player player,
			String identifier, String posX, String posY, String rot,
			String tempID) {
		int newID = player.getGame().getPowerupManager().getNewID();

		// create packet for player
		StringBuilder sb = new StringBuilder(512);
		sb.append("GCLCA:");
		sb.append(tempID);
		sb.append(":");
		sb.append(newID);
		String packetToPlayer = sb.toString();
		player.commandQueue.add(packetToPlayer);
		// create packet for other players
		sb = new StringBuilder(512);
		sb.append("GCLCR:");
		sb.append(identifier);
		sb.append(":");
		sb.append(posX);
		sb.append(":");
		sb.append(posY);
		sb.append(":");
		sb.append(rot);
		sb.append(":");
		sb.append(newID);
		String packetToOtherPlayers = sb.toString();
		sendPacketToOtherPlayers(player, packetToOtherPlayers);
	}

	public static void sendPacketToOtherPlayers(Player player, String packet) {
		// iterate through whole playerlist
		for (int i = 0; i < PlayerManager.playerlist.size(); i++) {
			// send packet to all players in the same game except for the sender
			if ((PlayerManager.playerlist.get(i).getGame() == player.getGame())
					&& (PlayerManager.playerlist.get(i) != player)) {
				PlayerManager.playerlist.get(i).commandQueue.add(packet);
			}
		}
	}

	public static void sendCollidableRemovedToOtherPlayers(Player player,
			String colID) {
		player.getGame().getPowerupManager().getCollidableIDlist()
				.remove(colID);
		StringBuilder sb = new StringBuilder(512);
		sb.append("GCLRR:");
		sb.append(colID);
		String packet = sb.toString();
		sendPacketToOtherPlayers(player, packet);

	}

	public static void removeCollidableOutOfList(Player player, String colID) {
		player.getGame().getPowerupManager().getCollidableIDlist()
				.remove(colID);
	}

	public static void sendPauseAndFinishedPacket(Player player) {
		for (int i = 0; i < player.getLobby().getLobbylist().size(); i++) {
			String packet = "GPAUI:true";
			player.getLobby().getLobbylist().get(i).commandQueue.add(packet);
		}
		ServerGUI.addToConsole("Abort game with players: "
				+ player.getGame().getPlayers().toString()
				+ " because of disconnect.");
		GameManager.removeGame(player.getGame());
	}

	public static void sendEffectToOtherPlayers(Player player, String string) {
		StringBuilder sb = new StringBuilder(512);
		sb.append("GCEFI:");
		sb.append(string);
		String packet = sb.toString();
		sendPacketToOtherPlayers(player, packet);
	}
}
