package server.lobby;

import java.util.Arrays;

import server.game.GameLogic;
import server.game.GameManager;

/**
 * LobbyLogic handles all the lobby related things like creation, joining,
 * deletion, etc.
 * 
 * @author marco
 * @author benzumbrunn
 *
 */
public class LobbyLogic {

	/**
	 * Sends the player an approval of his login on the server and his name.
	 * 
	 * @param player
	 *            which login gets accepted
	 */
	public static void sendLoginAccept(Player player) {
		player.commandQueue.add("LGINA:" + player.getName());
	}

	/**
	 * Sends information to other players about a new player login with the name
	 * of the new player
	 * 
	 * @param player
	 *            which sends the information
	 */
	public static void sendLoginInformationToOtherPlayers(Player player) {
		PlayerManager.packetToOtherPlayers(player, "LGINI:" + player.getName());
	}

	/**
	 * Sends the approval of logout to a player, calls the remove method and
	 * informs all the other players about the logout with the name of the
	 * player who logged out.
	 * 
	 * @param player
	 *            which logs out
	 */
	public static void sendLogoutAccept(Player player) {
		if (PlayerManager.playerlist.contains(player)) {
			player.commandQueue.add("LGOUA:" + player.getName());
			try {
				Thread.sleep(500);
				removePlayerComplete(player);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			ServerGUI.addToConsole("Multiple Logouts from the same player.");
		}
	}

	/**
	 * Removes the player completely, checks if the player is in a game or in a
	 * lobby and handles this.
	 * 
	 * @param player
	 *            which gets removed
	 */
	public static void removePlayerComplete(Player player) {
		if (PlayerManager.playerlist.contains(player)) {
			if (player.getGame() != null) {
				GameLogic.sendPauseAndFinishedPacket(player);
			}
			if (player.getLobby() != null) {
				LobbyLogic.leaveLobby(player, player.getLobby().getLobbyID());
			}
			try {
				player.playerSocket.closeThreads();
				player.playerSocket.socket.close();
				PlayerManager.removeUser(player);
				LobbyLogic.sendLogoutInformationToOtherPlayers(player);
			} catch (Exception e) {
				ServerGUI
						.addToConsole("Exception in LobbyLogic.removePlayerComplete:");
				ServerGUI.addToConsole(Arrays.toString(e.getStackTrace())
						.replaceAll(", ", "\n"));
			}
		}
	}

	/**
	 * Sends the player a login denied packet because the server is full.
	 * 
	 * @param player
	 *            which tries to log in
	 */
	public static void sendFullServer(Player player) {
		player.commandQueue.add("LGIND:" + PlayerManager.playerlist.size());
	}

	/**
	 * Sends information about a player logout to the other players.
	 * 
	 * @param player
	 *            which logs out
	 */
	public static void sendLogoutInformationToOtherPlayers(Player player) {
		PlayerManager.packetToOtherPlayers(player, "LGOUI:" + player.getName());
	}

	/**
	 * Sends the approval of the NameChange to the player.
	 * 
	 * @param player
	 *            which name gets changed
	 */
	public static void sendNameChangeAccept(Player player) {
		player.commandQueue.add("NAMCA:" + player.getName());
	}

	/**
	 * Sends the denial of the NameChange to the player.
	 * 
	 * @param player
	 *            which namechange gets denied
	 */
	public static void sendNameChangeDenied(Player player) {
		player.commandQueue.add("NAMCD:" + player.getName());
	}

	/**
	 * Sends the NameChange information to other players.
	 * 
	 * @param player
	 *            PlayerObject
	 * @param oldUsername
	 *            the old UserName of the player
	 */
	public static void sendNameChangeInformationToOtherPlayers(Player player,
			String oldUsername) {
		PlayerManager.packetToOtherPlayers(player, "NAMCI:" + oldUsername + ":"
				+ player.getName());
	}

	/**
	 * Sends the Message from the player to all the other players.
	 * 
	 * @param msg
	 *            the Message from the player to the server
	 */
	public static void worldMessage(Player player, String msg) {
		ChatLogic.worldMessage(player, msg);
	}

	/**
	 * Calls the ChatLogic for a LobbyMessage with the message and the LobbyID.
	 * 
	 * @param msg
	 *            the Message from the player to the lobby
	 * @param lobbyID
	 *            where the message gets sent to
	 */
	public static void lobbyMessage(Player player, String msg, int lobbyID) {
		ChatLogic.lobbyMessage(player, msg, player.lobby.getLobbyID());
	}

	/**
	 * Calls the ChatLogic for a WhisperMessage with the sending player, the
	 * receiving player and the message.
	 * 
	 * @param fPlayer
	 *            sending player
	 * @param tPlayer
	 *            receiving player
	 * @param msg
	 *            the WhisperMessage
	 */
	public static void privateMessage(String fPlayer, String tPlayer, String msg) {
		ChatLogic.privateMessage(fPlayer, tPlayer, msg);
	}

	public static void answerHeartbeat(Player player) {
		player.commandQueue.add("HBTCA");
	}

	/**
	 * Sends all the online playerNames to the player.
	 * 
	 * @param player
	 *            who requests the information
	 */
	public static void whoIsOnline(Player player) {
		player.commandQueue.add("WHOOI:"
				+ PlayerManager.getAllOnlinePlayerNames());
	}

	public static void setHeartbeat(Player player) {
		player.setHeartBeatReturnedTrue();
	}

	/**
	 * Creates a new Lobby for the player with a TrackID.
	 * 
	 * @param player
	 *            the creating player
	 * @param parts
	 *            LobbyID
	 */
	public static void createNewLobby(Player player, String parts) {
		if (player.getLobby() != null) {
			LobbyLogic.leaveLobby(player, player.getLobby().getLobbyID());
		}
		player.setLobbyReady(false);
		LobbyManager.addLobby(player, parts);
		player.getLobby().getLobbylist().add(player);
		player.commandQueue.add("LOBCA:" + player.lobby.getLobbyID() + ":"
				+ player.getLobby().getTrack());
		PlayerManager.packetToOtherPlayers(player, "LOBCI:" + player.getName()
				+ ":" + player.lobby.getLobbyID() + ":"
				+ player.getLobby().getTrack());
	}

	/**
	 * Joins a Lobby for the player.
	 * 
	 * @param player
	 *            the joining player
	 * @param lobbyID
	 *            the LobbyID of the lobby to join
	 */
	public static void joinLobby(Player player, int lobbyID) {
		if (lobbyHasPlace(lobbyID) == true) {
			if (player.getLobby() != null) {
				LobbyLogic.leaveLobby(player, player.getLobby().getLobbyID());
			}
			player.setLobbyReady(false);
			LobbyManager.getLobby(lobbyID).getLobbylist().add(player);
			player.setLobby(LobbyManager.getLobby((lobbyID)));
			player.commandQueue.add("LOBJA:" + player.lobby.getLobbyID() + ":"
					+ player.getLobby().getTrack());
			PlayerManager.packetToOtherPlayers(
					player,
					"LOBJI:" + player.getName() + ":"
							+ player.lobby.getLobbyID());
			ServerGUI.addToConsole("Player with name: " + player.getName()
					+ " and ID: " + player.getID() + " joined Lobby "
					+ player.lobby.getLobbyID() + ".");
		} else {
			player.commandQueue.add("LOBJD:Lobby is full");
		}

	}

	/**
	 * Checks if a Lobby has place for a new player.
	 * 
	 * @param lobbyID
	 *            which gets checked
	 * @return boolean if LobbyHasPlace
	 */
	private static boolean lobbyHasPlace(int lobbyID) {
		if (LobbyManager.getLobby(lobbyID).getLobbylist().size() < 4) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Handles the Lobby joining.
	 * 
	 * @param player
	 *            the leaving player
	 * @param lobbyID
	 *            the LobbyID of the Lobby to leave
	 */
	public static void leaveLobby(Player player, int lobbyID) {
		player.getLobby().getLobbylist().remove(player);
		player.lobby = null;
		player.setLobbyReady(false);
		player.commandQueue.add("LOBLA:" + lobbyID);
		sendOpenLobbyInformation(player);
		PlayerManager.packetToOtherPlayers(player, "LOBLI:" + player.getName()
				+ ":" + lobbyID);
		ServerGUI.addToConsole("Player with name: " + player.getName()
				+ " and ID: " + player.getID() + " leaved Lobby " + lobbyID
				+ ".");
	}

	public static void sendOpenLobbyInformation(Player player) {
		player.commandQueue.add("LOBOI:" + LobbyManager.getAllOpenLobbys());
	}

	public static void sendLobbyDeletion(int lobbyID) {
		PlayerManager.packetToAllPlayers("LOBDI:" + lobbyID);
	}

	/**
	 * Handles the new Player login.
	 * 
	 * @param player
	 *            the new player to login
	 * @param name
	 *            the name of the new player to login
	 */
	public static void newPlayerLogin(Player player, String name) {
		PlayerManager.newPlayerLogin(player, name);
	}

	/**
	 * Checks if the requested name is valid and handles the NameChange.
	 * 
	 * @param player
	 *            the player which wants to change his name
	 * @param name
	 *            the requested name
	 */
	public static void nameChangeRequest(Player player, String name) {
		if (PlayerManager.checkName(name) == false || name.contains(" ")) {
			LobbyLogic.sendNameChangeDenied(player);
		} else {
			PlayerManager.newNameForUser(player, name);
		}
	}

	/**
	 * Sets the player in a Lobby to ready and checks if all the players in this
	 * Lobby are ready.
	 * 
	 * @param parts
	 *            boolean if ready or not
	 */
	public static void playerLobbyReady(Player player, String parts) {
		player.setLobbyReady(true);
		player.setCarIndex(Integer.parseInt(parts));
		player.commandQueue.add("LOBRA:");
		if (LobbyManager.isLobbyReady(player.getLobby()) == true) {
			GameManager.addGame(player.getLobby());
		}
	}

	/**
	 * Sets the player in a Lobby to UnReady.
	 * 
	 * @param player
	 *            which unreadys himself
	 */
	public static void playerLobbyUnready(Player player) {
		player.setLobbyReady(false);
		player.commandQueue.add("LOBUA:");
	}

	/**
	 * Sends information about running games or a denial if there are no running
	 * games.
	 * 
	 * @param player
	 *            which requests the information
	 */
	public static void sendRunningGames(Player player) {
		if (GameManager.getGamelist().size() != 0) {
			for (int i = 0; i < GameManager.getGamelist().size(); i++) {
				StringBuilder sb = new StringBuilder(512);
				sb.append("CURGI:");
				for (int j = 0; j < GameManager.getGamelist().get(i)
						.getPlayers().size(); j++) {
					sb.append(GameManager.getGamelist().get(i).getPlayers()
							.get(j));
					sb.append(":");
				}
				System.out.println(sb.toString());
				player.commandQueue.add(sb.toString());
			}
		} else {
			player.commandQueue.add("CURGD");
		}
	}

}
