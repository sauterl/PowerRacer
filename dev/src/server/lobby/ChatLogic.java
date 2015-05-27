package server.lobby;

import java.lang.StringBuilder;

/**
 * ChatLogic handles all the chat-messages to and from players/server.
 * 
 * @author benzumbrunn
 * @author marco
 *
 */
class ChatLogic {

	/**
	 * Send a message to all users with use of StringBuilder to avoid
	 * reallocations
	 * 
	 * @param player
	 * @param msg
	 */
	public static void worldMessage(Player player, String msg) {
		StringBuilder sb = new StringBuilder(64);
		sb.append("CALLM:");
		sb.append(player.name);
		sb.append(":");
		sb.append(msg);
		String packet = sb.toString();
		for (int i = 0; i < PlayerManager.playerlist.size(); i++) {
			PlayerManager.playerlist.get(i).commandQueue.add(packet);
		}
	}

	/**
	 * Send a message to all the users in the game lobby with use of
	 * StringBuilder to avoid reallocations
	 * 
	 * @param player
	 * @param msg
	 * @param lobbyNumber
	 */
	public static void lobbyMessage(Player player, String msg, int lobbyNumber) {
		StringBuilder sb = new StringBuilder(64);
		sb.append("CLOBM:");
		sb.append(player.name);
		sb.append(":");
		sb.append(msg);
		String packet = sb.toString();
		for (int i = 0; i < player.getLobby().getLobbylist().size(); i++) {
			player.getLobby().getLobbylist().get(i).commandQueue.add(packet);
			;
		}
	}

	/**
	 * Send a message to a certain user (whispering) with use of StringBuilder
	 * to avoid reallocations. Checks for exceptions "sender=receiver" and
	 * "cant find receiver".
	 * 
	 * @param fPlayer
	 * @param tPlayer
	 * @param msg
	 */
	public static void privateMessage(String fPlayer, String tPlayer, String msg) {
		StringBuilder sb = new StringBuilder(128);
		sb.append("CWHIM:");
		sb.append(fPlayer);
		sb.append(":");
		sb.append(tPlayer);
		sb.append(":");
		sb.append(msg);
		String packet = sb.toString();
		if (fPlayer.equals(tPlayer)) {
			StringBuilder sbD = new StringBuilder(128);
			sbD.append("CWHID:");
			sbD.append(fPlayer);
			sbD.append(":");
			sbD.append(tPlayer);
			sbD.append(":");
			sbD.append(msg);
			String packetD = sbD.toString();
			for (int k = 0; k < PlayerManager.playerlist.size(); k++) {
				if (PlayerManager.playerlist.get(k).name.equals(fPlayer)) {
					PlayerManager.playerlist.get(k).commandQueue.add(packetD);
					return;
				}
			}
		} else {
			for (int i = 0; i < PlayerManager.playerlist.size(); i++) {
				if (PlayerManager.playerlist.get(i).name.equals(tPlayer)) {
					for (int j = 0; j < PlayerManager.playerlist.size(); j++) {
						if (PlayerManager.playerlist.get(j).name
								.equals(fPlayer)
								|| PlayerManager.playerlist.get(j).name
										.equals(tPlayer)) {
							PlayerManager.playerlist.get(j).commandQueue
									.add(packet);
						}
					}
					return;
				}
			}
			StringBuilder sbD = new StringBuilder(128);
			sbD.append("CWHID:");
			sbD.append(fPlayer);
			sbD.append(":");
			sbD.append(tPlayer);
			sbD.append(":");
			sbD.append(msg);
			String packetD = sbD.toString();
			for (int k = 0; k < PlayerManager.playerlist.size(); k++) {
				if (PlayerManager.playerlist.get(k).name.equals(fPlayer)) {
					PlayerManager.playerlist.get(k).commandQueue.add(packetD);
					return;
				}
			}

		}

	}

	/**
	 * Send a special Message to all players on the server with use of
	 * StringBuilder to avoid reallocations.
	 * 
	 * @param msg
	 */
	public static void serverBroadcast(String msg) {
		StringBuilder sb = new StringBuilder(64);
		sb.append("SBROI:");
		sb.append(msg);
		String packet = sb.toString();
		for (int i = 0; i < PlayerManager.playerlist.size(); i++) {
			PlayerManager.playerlist.get(i).commandQueue.add(packet);
		}
	}

	/**
	 * Heartbeat
	 * 
	 * @param player
	 */
	public void heartBeat(Player player) {
		String packet = "HBTSR";
		player.commandQueue.add(packet);
	}

}
