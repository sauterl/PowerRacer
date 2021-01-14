package server.lobby;

import server.game.GameManager;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;

/**
 * PlayerManager holds all the player in an ArrayList and provides some methods
 * to work with them more easily.
 * 
 * @author marco
 *
 */
public class PlayerManager {

	private static final int MAX_USER = 100;
	public static ArrayList<Player> playerlist;
	static Random rand = new Random();
	public static GameManager gameManager;

	/**
	 * Constructor of the PlayerManager, creates an ArrayList for all the
	 * players.
	 */
	public PlayerManager() {
		playerlist = new ArrayList<>();
		ServerGUI.addToConsole("Playermanager created.");
	}

	/**
	 * Creates a new Player object and add it to the playerlist.
	 * 
	 * @param in_Socket
	 *            the socket that carries the connection to the player
	 */
	public static void addUser(Socket in_Socket) {
		Player player = new Player(getNewID(), in_Socket);
		playerlist.add(player);
	}

	/**
	 * Creates a new Player object only to send him a LoginDenial packet.
	 * 
	 * @param in_Socket
	 *            the socket that carries the connection to the player
	 * @return the new player object
	 */
	public static Player addUserOnlyForDenial(Socket in_Socket) {
		return new Player(99999, in_Socket);
	}

	/**
	 * Lets a new random run to give back an unused ID for a new player.
	 */
	private static int getNewID() {
		// FIXME: This is a terrible way to generate player IDs and why are only up to 200 players allowed?
		while (true) {
			boolean used = false;
			int tempID = rand.nextInt(200) + 5;
			for (Player player : playerlist) {
				if (tempID == player.id) {
					used = true;
					break;
				}
			}
			if (!used)
				return tempID;
		}
	}

	/**
	 * Iterates trough the playerlist and removes the player with the given id
	 * from it.
	 * 
	 * @param player
	 *            the player who will be removed
	 */
	public static void removeUser(Player player) {
		playerlist.remove(player);
		if (player.getID() != 99999) {
			ServerGUI.addToConsole("Removed player with name: "
					+ player.getName() + " and ID: " + player.getID() + ".");
		}
	}

	/**
	 * Iterates trough the playerlist and returns if a given name is free or
	 * not.
	 * 
	 * @param prefName
	 *            the name the player would like to change to
	 * @return false if name is already in use, true if not
	 */
	public static boolean checkName(String prefName) {
		for (Player player : playerlist) {
			if (player.name.equals(prefName)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Iterates trough the playerlist and sends a given packet to all players
	 * except for the given player.
	 * 
	 * @param player
	 *            the player this packet will not be sent to
	 * @param string
	 *            the packet that will be sent
	 */
	public static void packetToOtherPlayers(Player player, String string) {
		for (Player other : playerlist) {
			if (!player.name.equals(other.name)) {
				other.commandQueue.add(string);
			}
		}
	}

	public static boolean hasSpace() {
		return PlayerManager.playerlist.size() < MAX_USER;
	}

	public static void newPlayerLogin(Player player, String prefName) {
		String prefNameIter = prefName;
		int i = 0;
		while (!checkName(prefNameIter)) {
			prefNameIter = prefName + i;
			i++;
		}
		player.setName(prefNameIter);
		LobbyLogic.sendLoginAccept(player);
		LobbyLogic.sendLoginInformationToOtherPlayers(player);
		LobbyLogic.sendOpenLobbyInformation(player);
		ServerGUI.addToConsole("New player connected with name: "
				+ player.getName() + " and ID: " + player.getID() + ".");
	}

	public static void newNameForUser(Player player, String parts) {
		String oldName = player.getName();
		player.setName(parts);
		LobbyLogic.sendNameChangeAccept(player);
		LobbyLogic.sendNameChangeInformationToOtherPlayers(player, oldName);
		ServerGUI.addToConsole("Namechange from player with ID: "
				+ player.getID() + " from " + oldName + " to "
				+ player.getName());
	}

	public static String getAllOnlinePlayerNames() {
		StringBuilder result = new StringBuilder();
		for (Player player : playerlist) {
			result.append(player.getName());
			result.append(":");
		}
		return result.toString();
	}

	public static void packetToAllPlayers(String packet) {
		for (int i = 0; i < PlayerManager.playerlist.size(); i++) {
			PlayerManager.playerlist.get(i).commandQueue.add(packet);
		}
	}

	public static Player getPlayerWithName(String string) {
		for (Player player : playerlist) {
			if (player.getName().equals(string))
				return player;
		}
		return null;
	}

	public static Player getPlayerWithId(int id) {
		for (Player player : playerlist) {
			if (player.id == id)
				return player;
		}
		return null;
	}

	public static int getMaxUser() {
		return MAX_USER;
	}
}
