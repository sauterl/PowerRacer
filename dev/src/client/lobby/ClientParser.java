package client.lobby;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import client.gui.ClientGUI;
import shared.game.RaceTrack;
import shared.game.powerup.Inversion;
import shared.game.powerup.Lightning;
import shared.protocol.Protocol;

/**
 * Parser to translate incoming packages and react accordingly.
 * 
 * @author Florian
 * 
 *
 */
class ClientParser {

	private Client client;

	public ClientParser(Client client) {
		this.client = client;
	}

	/**
	 * Parses the argument message by checking the enum for matches and then
	 * reacting accordingly.
	 * 
	 * @param inComPack
	 *            package for parsing
	 */
	public void parse(String inComPack) {
		String command = inComPack.substring(0, 5);
		String[] parts = inComPack.split(":");
		if (enumContains(command)) {
			switch (Protocol.valueOf(command)) {
				case LGINA:
					if (checkPacket(parts, 2,
							new String[] { "String", "String" })) {
						moveToServerAndApproveConnection(parts);
					}
					break;
				case LGIND:
					if (checkPacket(parts, 2,
							new String[] { "String", "String" })) {
						returnFeedbackToPlayerAndTerminateClient(parts);
					}
					break;
				case LGOUA:
					disposeOfAllResourcesAndRestartGUI();
					break;
				case NAMCA:
					if (checkPacket(parts, 2,
							new String[] { "String", "String" })) {
						returnFeedbackToPlayerAndChangeClientName(parts);
					}
					break;
				case NAMCD:
					if (checkPacket(parts, 2,
							new String[] { "String", "String" })) {
						returnInvalidNameChangeFeedbackToPlayer(parts);
					}
					break;
				case HBTCA:
					notifyClientOfReturnedHeartBeat();
					break;
				case CALLM:
					if (checkPacket(parts, 3, new String[] { "String",
							"String", "String" })) {
						addGlobalChatMessageToChat(parts);
					}
					break;
				case CLOBM:
					if (checkPacket(parts, 3, new String[] { "String",
							"String", "String" })) {
						addLobbyChatMessageToChat(parts);
					}
					break;
				case CWHIM:
					if (checkPacket(parts, 4, new String[] { "String",
							"String", "String", "String" })) {
						addWhisperChatMessageToChat(parts);
					}
					break;
				case CWHID:
					if (checkPacket(parts, 4, new String[] { "String",
							"String", "String", "String" })) {
						addWhisperDeniedAlertToChat(parts);
					}
					break;
				case WHOOI:
					addWhoIsOnlineInformationToChat(parts);
					break;
				case LGINI:
					if (checkPacket(parts, 2,
							new String[] { "String", "String" })) {
						addLoginInformationToChat(parts);
					}
					break;
				case LGOUI:
					if (checkPacket(parts, 2,
							new String[] { "String", "String" })) {
						addLogoutInformationToChat(parts);
					}
					break;
				case NAMCI:
					if (checkPacket(parts, 3, new String[] { "String",
							"String", "String" })) {
						addNameChangeInformationToChat(parts);
					}
					break;
				case LOBCI:
					if (checkPacket(parts, 4, new String[] { "String",
							"String", "String", "byte" })) {
						addCreatedLobbyToLobbyList(parts);
					}
					break;
				case LOBJI:
					if (checkPacket(parts, 3, new String[] { "String",
							"String", "String" })) {
						addLobbyJoinInformationToChat(parts);
					}
					break;
				case LOBLI:
					if (checkPacket(parts, 3, new String[] { "String",
							"String", "String" })) {
						addLobbyLeaveInformationToChat(parts);
					}
					break;
				case LOBCA:
					if (checkPacket(parts, 3, new String[] { "String",
							"String", "byte" })) {
						moveToSpecifiedLobby(parts);
					}
					break;
				case LOBCD:
					if (checkPacket(parts, 3, new String[] { "String",
							"String", "String" })) {
						returnLobbyCreateDeniedFeedbackToPlayer(parts);
					}
					break;
				case LOBJA:
					if (checkPacket(parts, 3, new String[] { "String",
							"String", "byte" })) {
						moveToSpecifiedLobby(parts);
					}
					break;
				case LOBJD:
					if (checkPacket(parts, 2,
							new String[] { "String", "String" })) {
						returnLobbyJoinDeniedFeedbackToPlayer(parts);
					}
					break;
				case LOBLA:
					returnToServerFromLobby();
					break;
				case LOBLD:
					// This packet is never sent, and even if it was, nothing
					// would have to be done here
					break;
				case LOBOI:
					if (multicheckPacket(parts,
							new String[] { "String", "byte" })) {
						setLobbyListToListOfOpenLobbies(parts);
					}
					break;
				case LOBDI:
					if (parts.length > 1) {
						removeLobbyFromLobbyList(parts);
					}
					break;
				case HBTSR:
					returnHeartBeatToServer();
					break;
				case LOBRA:
					setLobbyReadyFeedbackTrue();
					break;
				case LOBUA:
					setLobbyReadyFeedbackFalse();
					break;
				case GCRER:
					if (checkPacket(Arrays.copyOf(parts, 8), 8, new String[] {
							"String", "int", "int", "int", "int", "int", "int",
							"byte" })) {
						sendLobbyUnReadyRequestAndStartGame(parts);
					}
					break;
				case GINPI:
					if (checkPacket(parts, 10, new String[] { "String", "int",
							"boolean", "boolean", "boolean", "boolean",
							"double", "double", "double", "double" })) {

					}
					setGameInformation(parts);
					break;
				case GCODI:
					setGameCountdown(parts);
					break;
				case GCOMI:
					setGameResults(parts);
					break;
				case SBROI:
					addServerBroadcastInformationToChat(parts);
					break;
				case PREGI:
					addGameHistoryInformationToChat(parts);
					break;
				case PREGD:
					addGameHistoryNotFoundFeedbackToChat();
					break;
				case CURGI:
					addCurrentGameInformationToChat(parts);
					break;
				case CURGD:
					addNoCurrentGamesRunningFeedbackToChat();
					break;
				case GCLDI:
					if (checkPacket(parts, 2, new String[] { "String", "int" })) {
						disableCollidableWithId(parts);
					}
					break;
				case GCLCA:
					if (checkPacket(parts, 3, new String[] { "String", "int",
							"int" })) {
						replaceCollidableId(parts);
					}
					break;
				case GCLCR:
					if (checkPacket(parts, 6, new String[] { "String", "int",
							"double", "double", "double", "int" })) {
						addCollidable(parts);
					}
					break;
				case GCLRR:
					if (checkPacket(parts, 2, new String[] { "String", "int" })) {
						removeCollidable(parts);
					}
					break;
				case GPAUI:
					if (checkPacket(parts, 2, new String[] { "String",
							"boolean" })) {
						setPause(parts);
					}
					break;
				case GNRMG:
					if (checkPacket(parts, 2,
							new String[] { "String", "String" })) {
						RaceTrack.randomString = parts[1];
					}
					break;
				case SKICK:
					client.clientGUI.frame.dispose();
					client.terminate();
					String message = "";
					if (parts.length > 1) {
						message = parts[1];
					}
					try {
						Thread.sleep(400);
					} catch (InterruptedException e) {
					}
					ClientGUI.kickReset(message);
					break;
				case GCEFI:
					if (checkPacket(parts, 2, new String[] { "String", "int" })) {
						addEffect(Integer.parseInt(parts[1]));
					}
					break;
				default:
					break;
			}
		} else {
			System.out.println("I dont know what to do with this packet: "
					+ inComPack);
		}
	}

	private void addEffect(int effectIndex) {
		if (client.clientGUI.cameraExists()) {
			switch (effectIndex) {
				case 0:
					client.clientGUI.addEffect(new Lightning(Client.getGame(),
							120));
					break;
				case 1:
					client.clientGUI.addEffect(new Inversion(Client.getGame(),
							80));
					break;
				default:
					break;
			}
		}
	}

	private void setPause(String[] parts) {
		if (Client.gameNotNull()) {
			Client.getGame().setPause(Boolean.parseBoolean(parts[1]));
		}
	}

	private void removeCollidable(String[] parts) {
		if (Client.gameNotNull()) {
			Client.getGame().removeFromCollidables(Integer.parseInt(parts[1]));
		}
	}

	private void addCollidable(String[] parts) {
		if (Client.gameNotNull()) {
			Client.getGame().addCollidableFromPacket(
					Integer.parseInt(parts[1]), Double.parseDouble(parts[2]),
					Double.parseDouble(parts[3]), Double.parseDouble(parts[4]),
					Integer.parseInt(parts[5]));
		}
	}

	private void replaceCollidableId(String[] parts) {
		if (Client.gameNotNull()) {
			Client.getGame().replaceCollidableID(Integer.parseInt(parts[1]),
					Integer.parseInt(parts[2]));
		}
	}

	private void disableCollidableWithId(String[] parts) {
		if (Client.gameNotNull()) {
			Client.getGame().setDisabled(Integer.parseInt(parts[1]));
		}
	}

	private void addCurrentGameInformationToChat(String[] parts) {
		StringBuilder result = new StringBuilder(128);
		result.append("There is currently a game running with the players:");
		for (int i = 1; i < parts.length; i++) {
			result.append("\n" + parts[i]);
		}
		client.clientGUI.addToChat(result.toString());
	}

	private void addNoCurrentGamesRunningFeedbackToChat() {
		client.clientGUI.addToChat("--> No games currently running!");
	}

	private void addGameHistoryNotFoundFeedbackToChat() {
		client.clientGUI.addToChat("--> No previous games recorded!");
	}

	private void addGameHistoryInformationToChat(String[] parts) {
		client.clientGUI
				.addToChat("-----------------------------------------------\n                                Game History\n-----------------------------------------------");
		for (int i = 1; i < parts.length; i++) {

			String[] historyContent = parts[i].split(",");
			StringBuilder result = new StringBuilder(150);
			if (historyContent.length == 10) {
				String date = historyContent[0];
				String trackName = historyContent[1];
				String playerOne = historyContent[2];
				int seconds = (int) TimeUnit.MILLISECONDS.toSeconds(Long
						.parseLong(historyContent[3]));
				int milliseconds = (int) Long.parseLong(historyContent[3]) % 1000;
				String scoreOne = seconds + "." + milliseconds + "s";
				String playerTwo = historyContent[4];
				seconds = (int) TimeUnit.MILLISECONDS.toSeconds(Long
						.parseLong(historyContent[5]));
				milliseconds = (int) Long.parseLong(historyContent[5]) % 1000;
				String scoreTwo = seconds + "." + milliseconds + "s";
				String playerThree = historyContent[6];
				seconds = (int) TimeUnit.MILLISECONDS.toSeconds(Long
						.parseLong(historyContent[7]));
				milliseconds = (int) Long.parseLong(historyContent[7]) % 1000;
				String scoreThree = seconds + "." + milliseconds + "s";
				String playerFour = historyContent[8];
				seconds = (int) TimeUnit.MILLISECONDS.toSeconds(Long
						.parseLong(historyContent[9]));
				milliseconds = (int) Long.parseLong(historyContent[9]) % 1000;
				String scoreFour = seconds + "." + milliseconds + "s";

				result.append("Game from ");
				result.append(date.replace(";", ":"));
				result.append(" on ");
				result.append(trackName);
				result.append("\n");
				result.append(playerOne + ": " + scoreOne + "\n");
				result.append(playerTwo + ": " + scoreTwo + "\n");
				result.append(playerThree + ": " + scoreThree + "\n");
				result.append(playerFour + ": " + scoreFour);
			} else
				if (historyContent.length < 10) {
					client.clientGUI.addToChat("Game History Corrupted!");
					client.clientGUI
							.addToChat("-----------------------------------------------");
					continue;
				} else {
					String date = historyContent[0];
					String trackName = historyContent[1];
					StringBuilder players = new StringBuilder();
					String prefix = "";
					for (int j = 2; j < historyContent.length - 1; j++) {
						if (isLong(historyContent[j + 1])) {
							int seconds = (int) TimeUnit.MILLISECONDS
									.toSeconds(Long
											.parseLong(historyContent[j + 1]));
							int milliseconds = (int) Long
									.parseLong(historyContent[j + 1]) % 1000;
							String score = seconds + "." + milliseconds + "s";
							players.append(prefix + historyContent[j] + ": "
									+ score);
							prefix = "\n";
							j++;
						} else {
							players.append(prefix + historyContent[j] + ",");
							prefix = "";
						}
					}
					result.append("Game from ");
					result.append(date.replace(";", ":"));
					result.append(" on ");
					result.append(trackName);
					result.append("\n");
					result.append(players);
				}

			client.clientGUI.addToChat(result.toString());
		}
		client.clientGUI
				.addToChat("-----------------------------------------------");
	}

	private void addServerBroadcastInformationToChat(String[] parts) {
		client.clientGUI
				.addToChat("-----------------------------------------------\n                                SERVER BROADCAST\n-----------------------------------------------");
		client.clientGUI.addToChat(parts[1]);
		client.clientGUI
				.addToChat("-----------------------------------------------");
	}

	private void setGameResults(String[] parts) {
		client.setScoreboard(parts[2], Integer.parseInt(parts[1]), parts[4],
				Integer.parseInt(parts[3]), parts[6],
				Integer.parseInt(parts[5]), parts[8],
				Integer.parseInt(parts[7]));
		client.setGameComplete();
	}

	private void setGameCountdown(String[] parts) {
		int countdown = Integer.parseInt(parts[2]);
		client.setCountdown(countdown);
		if (countdown == 0) {
			client.setControl(true);
		}
	}

	private void setGameInformation(String[] parts) {
		client.setCarInfo(Integer.parseInt(parts[1]),
				Boolean.parseBoolean(parts[2]), Boolean.parseBoolean(parts[3]),
				Boolean.parseBoolean(parts[4]), Boolean.parseBoolean(parts[5]),
				Double.parseDouble(parts[6]), Double.parseDouble(parts[7]),
				Double.parseDouble(parts[8]), Double.parseDouble(parts[9]));
	}

	private void sendLobbyUnReadyRequestAndStartGame(String[] parts) {
		client.commandQueue.add("LOBUR");
		ArrayList<String> playerNames = new ArrayList<String>(4);
		for (int i = 8; i < parts.length; i++) {
			playerNames.add(parts[i]);
		}
		client.startGame(
				Integer.parseInt(parts[1]),
				Integer.parseInt(parts[2]),
				new int[] { Integer.parseInt(parts[3]),
						Integer.parseInt(parts[4]), Integer.parseInt(parts[5]),
						Integer.parseInt(parts[6]) }, Byte.parseByte(parts[7]),
				playerNames.toArray(new String[0]));
	}

	private void setLobbyReadyFeedbackFalse() {
		client.clientGUI.setReady(false);
	}

	private void setLobbyReadyFeedbackTrue() {
		client.clientGUI.setReady(true);
	}

	private void returnHeartBeatToServer() {
		client.commandQueue.add("HBTSA");
	}

	private void removeLobbyFromLobbyList(String[] parts) {
		client.clientGUI.removeFromLobbyList(parts[1]);
	}

	private void setLobbyListToListOfOpenLobbies(String[] parts) {
		for (int i = 1; i < parts.length; i += 2) {
			client.clientGUI.addToLobbyList(parts[i],
					RaceTrack.getTrackName(Byte.parseByte(parts[i + 1])));
		}
	}

	private void returnToServerFromLobby() {
		client.clientGUI.restoreToServer();
	}

	private void returnLobbyJoinDeniedFeedbackToPlayer(String[] parts) {
		client.clientGUI.addToChat("Join denied due to: " + parts[1]);
	}

	private void returnLobbyCreateDeniedFeedbackToPlayer(String[] parts) {
		client.clientGUI.addToChat("Lobby creation denied due to: " + parts[2]);
	}

	private void moveToSpecifiedLobby(String[] parts) {
		client.clientGUI.moveToLobby(parts[1] + " "
				+ RaceTrack.getTrackName(Byte.parseByte(parts[2])));
	}

	private void addLobbyLeaveInformationToChat(String[] parts) {
		client.clientGUI.addToChat(parts[1] + " has left Lobby " + parts[2]
				+ ".");
	}

	private void addLobbyJoinInformationToChat(String[] parts) {
		client.clientGUI.addToChat(parts[1] + " has joined Lobby " + parts[2]
				+ ".");
	}

	private void addCreatedLobbyToLobbyList(String[] parts) {
		client.clientGUI.addToChat(parts[1] + " has created Lobby " + parts[2]
				+ " with " + RaceTrack.getTrackName(Byte.parseByte(parts[3])));
		client.clientGUI.addToLobbyList(parts[2],
				RaceTrack.getTrackName(Byte.parseByte(parts[3])));
	}

	private void addNameChangeInformationToChat(String[] parts) {
		client.clientGUI.addToChat(parts[1] + " has changed their name to "
				+ parts[2]);
	}

	private void addLogoutInformationToChat(String[] parts) {
		client.clientGUI.addToChat(parts[1] + " has left.");
	}

	private void addLoginInformationToChat(String[] parts) {
		client.clientGUI.addToChat(parts[1] + " has connected.");
	}

	private void addWhoIsOnlineInformationToChat(String[] parts) {
		client.clientGUI.addToChat("The following players are present:");
		for (int i = 1; i < parts.length; i++) {
			client.clientGUI.addToChat(parts[i]);
		}
	}

	private void addWhisperDeniedAlertToChat(String[] parts) {
		if (parts[1].equals(parts[2])) {
			client.clientGUI
					.addToChat("-->whisper failed: You can't whisper to yourself!");
		} else {
			client.clientGUI
					.addToChat("-->whisper failed: No player with Name \""
							+ parts[2] + "\"");
		}
	}

	private void addWhisperChatMessageToChat(String[] parts) {
		if (parts[1].equals(client.name)) {
			client.clientGUI.addToChat("<whisper to " + parts[2] + ">: "
					+ parts[3].replace("\\;", ":"));
		} else {
			client.clientGUI.addToChat("<whisper from " + parts[1] + ">: "
					+ parts[3].replace("\\;", ":"));
		}
	}

	private void addLobbyChatMessageToChat(String[] parts) {
		client.clientGUI.addToChat("[Lobby] " + parts[1] + ": "
				+ parts[2].replace("\\;", ":"));
	}

	private void addGlobalChatMessageToChat(String[] parts) {
		client.clientGUI.addToChat(parts[1] + ": "
				+ parts[2].replace("\\;", ":"));
	}

	private void notifyClientOfReturnedHeartBeat() {
		client.heartBeatReturned = true;
	}

	private void returnInvalidNameChangeFeedbackToPlayer(String[] parts) {
		client.clientGUI.addToChat("Name change refused! Name: " + parts[1]);
	}

	private void returnFeedbackToPlayerAndChangeClientName(String[] parts) {
		client.clientGUI.addToChat("Name change accepted! Name: " + parts[1]);
		client.name = parts[1];
	}

	private void disposeOfAllResourcesAndRestartGUI() {
		client.clientGUI.frame.dispose();
		ClientGUI.reset();
		client.terminate();
	}

	private void returnFeedbackToPlayerAndTerminateClient(String[] parts) {
		client.clientGUI.setFeedback("Server full! Max. Players: " + parts[1]);
		client.connectionApproved = true;
		client.terminate();
	}

	private void moveToServerAndApproveConnection(String[] parts) {
		client.clientGUI.moveToServer(parts[1]);
		client.name = parts[1];
		client.connectionApproved = true;
	}

	/**
	 * Method to catch and disregard bogus requests from clients.
	 * 
	 * @param command
	 * @return true if enum contains the command
	 */
	private boolean enumContains(String command) {
		try {
			Protocol.valueOf(command);
			return true;
		} catch (IllegalArgumentException e) {
			return false;
		}
	}

	private boolean isLong(String l) {
		try {
			Long.parseLong(l);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}

	private boolean multicheckPacket(String[] parts, String[] types) {
		if (parts.length % types.length == 1) {
			for (int i = 1; i <= parts.length - types.length; i += types.length) {
				for (int j = 0; j < types.length; j++) {
					if (!checkPacket(new String[] { parts[i + j] }, 1,
							new String[] { types[j] })) {
						return false;
					}
				}
			}
		} else {
			return false;
		}
		return true;
	}

	/**
	 * Method to check if a incoming packet has a valid length.
	 */
	private boolean checkPacket(String[] parts, int length, String[] types) {
		if (parts.length == length) {
			for (int i = 0; i < parts.length; i++) {
				switch (types[i]) {
					case "boolean":
						if (!parts[i].equalsIgnoreCase("true")
								&& !parts[i].equalsIgnoreCase("false")) {
							return false;
						}
						break;
					case "int":
						try {
							if (Integer.parseInt(parts[i]) < 0) {
								return false;
							}
						} catch (Exception e) {
							e.printStackTrace();
							return false;
						}
						break;
					case "byte":
						try {
							if (Byte.parseByte(parts[i]) < 0) {
								return false;
							}
						} catch (Exception e) {
							e.printStackTrace();
							return false;
						}
						break;
					case "double":
						try {
							Double.parseDouble(parts[i]);
						} catch (Exception e) {
							e.printStackTrace();
							return false;
						}
						break;
					case "string":
						break;
					default:
						break;
				}
			}
			return true;
		} else {
			return false;
		}
	}
}
