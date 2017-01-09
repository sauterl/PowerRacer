package server.lobby;

import java.util.ArrayList;
import java.util.Random;

/**
 * LobbyManager which holds all the Lobby objects. Has a reference to
 * PlayerManager and an ArrayList with all the created lobbys.
 * 
 * @author marco
 *
 */
public class LobbyManager {

	static ArrayList<Lobby> lobbylist;
	static Random rand = new Random();

	public LobbyManager() {
		lobbylist = new ArrayList<Lobby>();
		ServerGUI.addToConsole("LobbyManager created.");
	}

	/**
	 * Creates a new Lobby and adds the creatorPlayer to it.
	 * 
	 * @param player
	 *            the relevant player
	 * @param parts
	 *            the packet parts
	 */
	public static void addLobby(Player player, String parts) {
		Lobby lobby = new Lobby(getNewID(), player);
		lobby.setTrack(Byte.parseByte(parts));
		lobbylist.add(lobby);
		player.setLobby(lobby);
		ServerGUI.addToConsole("New Lobby created with ID: "
				+ lobby.getLobbyID() + " and track: " + lobby.getTrack());
	}

	/**
	 * Lets a new random run to give back an unused ID for a new Lobby.
	 * 
	 * @return
	 */
	private static int getNewID() {
		while (true) {
			out: while (true) {
				int tempID = rand.nextInt(200) + 5;
				for (int i = 0; i < lobbylist.size(); i++) {
					if (tempID == lobbylist.get(i).lobbyID) {
						break out;
					}
				}
				return tempID;
			}
		}
	}

	/**
	 * Returns the Lobby-object which got the right lobbyID.
	 * 
	 * @param lobbyID
	 *            the relevant lobby's ID
	 * @return the lobby with the specified ID
	 */
	public static Lobby getLobby(int lobbyID) {
		for (int i = 0; i < lobbylist.size(); i++) {
			if (lobbyID == lobbylist.get(i).lobbyID) {
				return lobbylist.get(i);
			}
		}
		return null;
	}

	public static String getAllOpenLobbys() {
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < lobbylist.size(); i++) {
			result.append(lobbylist.get(i).getLobbyID());
			result.append(":");
			result.append(lobbylist.get(i).getTrack());
			result.append(":");
		}
		return result.toString();
	}

	public static boolean isLobbyReady(Lobby lobby) {
		int readyCount = 0;
		for (int i = 0; i < lobby.getLobbylist().size(); i++) {
			if (lobby.getLobbylist().get(i).getLobbyReady() == true) {
				readyCount += 1;
			}
		}
		if (readyCount == lobby.getLobbylist().size()) {
			return true;
		} else {
			return false;
		}
	}

}
