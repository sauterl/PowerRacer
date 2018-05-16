package server.lobby;

import server.data.DataLogic;
import server.game.GameLogic;
import shared.game.RaceTrack;
import shared.protocol.Protocol;

/**
 * Parser which parses the incoming packets and processes them to give a answer.
 * Got many references to have an easier access to the needed fields and
 * methods.
 * 
 * @author marco
 * @author benzumbrunn
 *
 */
public class Parser {

	private static final int PACKET_LENGTH = 5;
	private Player player;
	private String[] parts; // Array containing the parsed package
	private int gameFinishedinputTracker;

	/**
	 * Constructor of the parser object with many references for an easier
	 * access.
	 * 
	 * @param player
	 *            the parser's player
	 */
	public Parser(Player player) {
		this.player = player;
	}

	/**
	 * Parses the incoming packet and checks if it is defined in the protocol.
	 * If yes, process the packet and gives a respond or start methods to give
	 * respond.
	 * 
	 * @param inComPack
	 *            the string contents of the incoming packet
	 */
	public void parse(String inComPack) {

		if (inComPack != null && inComPack.length() >= 5
				&& enumContains(inComPack.substring(0, PACKET_LENGTH))
				&& player.getID() != 99999) {
			parts = inComPack.split(":"); // Generate String array out of the
										  // package
			switch (Protocol.valueOf(inComPack.substring(0, PACKET_LENGTH))) {
				case LGINR:
					if (checkPacket(parts, 2,
							new String[] { "string", "string" })) {
						if (player.getName().equals("")) {
							LobbyLogic.newPlayerLogin(this.player, parts[1]);
						} else {
							ServerGUI.addToConsole("Player: "
									+ player.getName()
									+ " tried to log in multiple times");
						}
					} else {
						ServerGUI.addToConsole("Wrongly formatted packet:"
								+ inComPack + " from player: "
								+ player.getName());
					}
					break;
				case LGOUR:
					if (checkPacket(parts, 1, new String[] { "string" })) {
						LobbyLogic.sendLogoutAccept(this.player);
					} else {
						ServerGUI.addToConsole("Wrongly formatted packet:"
								+ inComPack + " from player: "
								+ player.getName());
					}
					break;
				case NAMCR:
					if (checkPacket(parts, 2,
							new String[] { "string", "string" })) {
						LobbyLogic.nameChangeRequest(this.player, parts[1]);
					} else {
						ServerGUI.addToConsole("Wrongly formatted packet:"
								+ inComPack + " from player: "
								+ player.getName());
					}
					break;
				case CALLR:
					if (checkPacket(parts, 2,
							new String[] { "string", "string" })) {
						LobbyLogic.worldMessage(this.player, parts[1]);
					} else {
						ServerGUI.addToConsole("Wrongly formatted packet:"
								+ inComPack + " from player: "
								+ player.getName());
					}
					break;
				case CLOBR:
					if (checkPacket(parts, 2,
							new String[] { "string", "string" })) {
						if (player.getLobby() != null) {
							LobbyLogic.lobbyMessage(this.player, parts[1],
									this.player.lobby.getLobbyID());
						} else {
							ServerGUI.addToConsole("Player: "
									+ player.getName()
									+ " tried to chat in a nonexist lobby");
						}
					} else {
						ServerGUI.addToConsole("Wrongly formatted packet:"
								+ inComPack + " from player: "
								+ player.getName());
					}
					break;
				case CWHIR:
					if (checkPacket(parts, 4, new String[] { "string",
							"string", "string", "string" })) {
						LobbyLogic.privateMessage(player.getName(), parts[2],
								parts[3]);
					} else {
						ServerGUI.addToConsole("Wrongly formatted packet:"
								+ inComPack + " from player: "
								+ player.getName());
					}
					break;
				case HBTCR:
					if (checkPacket(parts, 1, new String[] { "string" })) {
						LobbyLogic.answerHeartbeat(this.player);
					} else {
						ServerGUI.addToConsole("Wrongly formatted packet:"
								+ inComPack + " from player: "
								+ player.getName());
					}
					break;
				case WHOOR:
					if (checkPacket(parts, 1, new String[] { "string" })) {
						LobbyLogic.whoIsOnline(this.player);
					} else {
						ServerGUI.addToConsole("Wrongly formatted packet:"
								+ inComPack + " from player: "
								+ player.getName());
					}
					break;
				case LOBCR:
					if (checkPacket(parts, 2, new String[] { "string", "byte" })) {
						if (Byte.parseByte(parts[1]) < RaceTrack
								.numberOfTracks()) {
							LobbyLogic.createNewLobby(this.player, parts[1]);
						} else {
							ServerGUI
									.addToConsole("Player: "
											+ player.getName()
											+ " tried to create a lobby with a nonexisting track");
						}
					} else {
						ServerGUI.addToConsole("Wrongly formatted packet:"
								+ inComPack + " from player: "
								+ player.getName());
					}
					break;
				case LOBJR:
					if (checkPacket(parts, 2, new String[] { "string", "int" })) {
						if (LobbyManager.getLobby(Integer.parseInt(parts[1])) != null) {
							LobbyLogic.joinLobby(this.player,
									Integer.parseInt(parts[1]));
						} else {
							ServerGUI.addToConsole("Player: "
									+ player.getName()
									+ " tried to join a nonexisting lobby");
						}
					} else {
						ServerGUI.addToConsole("Wrongly formatted packet:"
								+ inComPack + " from player: "
								+ player.getName());
					}
					break;
				case LOBLR:
					if (checkPacket(parts, 1, new String[] { "string" })) {
						if (player.getLobby() != null) {
							LobbyLogic.leaveLobby(this.player, this.player
									.getLobby().getLobbyID());
						} else {
							ServerGUI
									.addToConsole("Player: "
											+ player.getName()
											+ " tried to leave a lobby but is not in a lobby.");

						}
					} else {
						ServerGUI.addToConsole("Wrongly formatted packet:"
								+ inComPack + " from player: "
								+ player.getName());
					}
					break;
				case HBTSA:
					if (checkPacket(parts, 1, new String[] { "string" })) {
						LobbyLogic.setHeartbeat(this.player);
					} else {
						ServerGUI.addToConsole("Wrongly formatted packet:"
								+ inComPack + " from player: "
								+ player.getName());
					}
					break;
				case LOBRR:
					if (checkPacket(parts, 2, new String[] { "string", "int" })) {
						if (player.getLobby() != null) {
							LobbyLogic.playerLobbyReady(this.player, parts[1]);
						} else {
							ServerGUI
									.addToConsole("Player: "
											+ player.getName()
											+ " tried to be ready but is not in a lobby.");
						}
					} else {
						ServerGUI.addToConsole("Wrongly formatted packet:"
								+ inComPack + " from player: "
								+ player.getName());
					}
					break;
				case LOBUR:
					if (checkPacket(parts, 1, new String[] { "string" })) {
						if (player.getLobby() != null) {
							LobbyLogic.playerLobbyUnready(this.player);
						} else {
							ServerGUI
									.addToConsole("Player: "
											+ player.getName()
											+ " tried to be unready but is not in a lobby.");
						}
					} else {
						ServerGUI.addToConsole("Wrongly formatted packet:"
								+ inComPack + " from player: "
								+ player.getName());
					}
					break;
				case GINPI:
					if (checkPacket(parts, 9, new String[] { "string",
							"boolean", "boolean", "boolean", "boolean",
							"double", "double", "double", "double" })) {
						if (player.getGame() != null) {
							gameFinishedinputTracker = 0;
							GameLogic.sendInformationToOtherPlayers(
									this.player, parts);
						} else {
							gameFinishedinputTracker += 1;
							if (gameFinishedinputTracker >= 50) {
								ServerGUI
										.addToConsole("Player: "
												+ player.getName()
												+ " tried to send inputs to game but is not in a game");
								gameFinishedinputTracker = 0;
							}

						}
					} else {
						ServerGUI.addToConsole("Wrongly formatted packet:"
								+ inComPack + " from player: "
								+ player.getName());
					}
					break;
				case GFINI:
					if (checkPacket(parts, 1, new String[] { "string" })) {
						if (player.getGame() != null) {
							if (player.getLegitPlayer() > 98) {
								GameLogic
										.sendGameFinishedPacketWithPlace(this.player);
							} else {
								ServerGUI
										.addToConsole("Player: "
												+ player.getName()
												+ " tried to finish a game but is not a legit player");
							}
						} else {
							ServerGUI
									.addToConsole("Player: "
											+ player.getName()
											+ " tried to finish a game but is not in a game");
						}
					} else {
						ServerGUI.addToConsole("Wrongly formatted packet:"
								+ inComPack + " from player: "
								+ player.getName());
					}
					break;
				case PREGR:
					if (checkPacket(parts, 1, new String[] { "string" })) {
						DataLogic.sendCSV(this.player);
					} else {
						ServerGUI.addToConsole("Wrongly formatted packet:"
								+ inComPack + " from player: "
								+ player.getName());
					}
					break;
				case CURGR:
					if (checkPacket(parts, 1, new String[] { "string" })) {
						LobbyLogic.sendRunningGames(this.player);
					} else {
						ServerGUI.addToConsole("Wrongly formatted packet:"
								+ inComPack + " from player: "
								+ player.getName());
					}
					break;
				case GCLDI:
					if (checkPacket(parts, 2, new String[] { "string", "int" })) {
						GameLogic.sendCollidableDisabledToOtherPlayers(
								this.player, parts[1]);
					} else {
						ServerGUI.addToConsole("Wrongly formatted packet:"
								+ inComPack + " from player: "
								+ player.getName());
					}
					break;
				case GCLCR:
					if (checkPacket(parts, 6, new String[] { "string", "int",
							"double", "double", "double", "int" })) {
						if (player.getGame() != null) {
							if (player.getGame().addCollidableFromPacket(
									Integer.parseInt(parts[1]), 0, 0, 0, 0)) {
								GameLogic.handleCollidableCreationPacket(
										this.player, parts[1], parts[2],
										parts[3], parts[4], parts[5]);
							} else {
								ServerGUI.addToConsole("Player: "
										+ player.getName()
										+ " send wrong collidable identifier.");
							}
						} else {
							ServerGUI
									.addToConsole("Player: "
											+ player.getName()
											+ " tryed to create collidable but is not in a game.");
						}
					} else {
						ServerGUI.addToConsole("Wrongly formatted packet:"
								+ inComPack + " from player: "
								+ player.getName());
					}
					break;
				case GCLRR:
					if (checkPacket(parts, 2, new String[] { "string", "int" })) {
						if (player.getGame() != null) {
							GameLogic.sendCollidableRemovedToOtherPlayers(
									this.player, parts[1]);
						} else {
							ServerGUI
									.addToConsole("Player: "
											+ player.getName()
											+ " tryed to remove collidable but is not in a game.");
						}
					} else {
						ServerGUI.addToConsole("Wrongly formatted packet:"
								+ inComPack + " from player: "
								+ player.getName());
					}
					break;
				case GCLRI:
					if (checkPacket(parts, 2, new String[] { "string", "int" })) {
						if (player.getGame() != null) {
							GameLogic.removeCollidableOutOfList(this.player,
									parts[1]);
						} else {
							ServerGUI
									.addToConsole("Player: "
											+ player.getName()
											+ " tryed to destroy collidable but is not in a game.");
						}
					} else {
						ServerGUI.addToConsole("Wrongly formatted packet:"
								+ inComPack + " from player: "
								+ player.getName());
					}
					break;
				case GCEFI:
					if (checkPacket(parts, 2, new String[] { "string", "int" })) {
						if (player.getGame() != null) {
							GameLogic.sendEffectToOtherPlayers(this.player,
									parts[1]);
						} else {
							ServerGUI
									.addToConsole("Player: "
											+ player.getName()
											+ " tryed to make effect but is not in a game.");
						}
					} else {
						ServerGUI.addToConsole("Wrongly formatted packet:"
								+ inComPack + " from player: "
								+ player.getName());
					}
					break;
				default:
					break;
			}
		} else
			if (player.getID() != 99999) {
				ServerGUI.addToConsole("Unknown packet: " + inComPack
						+ " from player: " + player.getName());
			}
	}

	/**
	 * Method to catch and disregard bogus requests from clients.
	 * 
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
							return false;
						}
						break;
					case "double":
						try {
							Double.parseDouble(parts[i]);
						} catch (Exception e) {
							return false;
						}
						break;
					case "string":
						break;
					case "byte":
						try {
							if (Byte.parseByte(parts[i]) < 0) {
								return false;
							}
						} catch (Exception e) {
							return false;
						}
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
