package server.lobby;

import shared.game.RaceTrack;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Small GUI for server host to monitor activity and kick players.
 */
public class ServerGUI {

	private static ServerGUI serverGUI;

	private final JTextArea serverChatBox;

	/**
	 * Constructor which sets up ServerGUI. Sets JTextField listener to send
	 * server broadcasts and, with the command \k, kicks players.
	 */
	public ServerGUI() {
		JFrame frame = new JFrame("Server Console");
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.setResizable(false);
		serverChatBox = new JTextArea();
		serverChatBox.setEditable(false);
		serverChatBox.setLineWrap(true);

		JScrollPane scrollPane = new JScrollPane(serverChatBox,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setPreferredSize(new Dimension(800, 400));

		JTextField serverConsole = new JTextField();
		serverConsole.setMinimumSize(new Dimension(300, 30));
		serverConsole.addActionListener(e -> {
			if (serverConsole.getText().length() != 0) {
				String input = serverConsole.getText();
				addToConsole("> " + input);
				if (parseInput(input))
					addToConsole("Please use the window buttons to quit the server in GUI mode.");
				serverConsole.setText("");
			}
		});

		JPanel panel = new JPanel();
		GroupLayout layout = new GroupLayout(panel);
		layout.setHorizontalGroup(layout.createParallelGroup()
				.addComponent(scrollPane).addComponent(serverConsole));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addComponent(scrollPane).addComponent(serverConsole));

		panel.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		frame.getContentPane().add(panel);

		frame.pack();

		frame.setLocation(
				Toolkit.getDefaultToolkit().getScreenSize().width - frame.getWidth(),
				Toolkit.getDefaultToolkit().getScreenSize().height - frame.getHeight());
		frame.setVisible(true);
		serverGUI = this;
	}

	/**
	 * Adds the message to the appropriate server console.
	 *
	 * @param message text to be added to console
	 */
	public static void addToConsole(String message) {
		addToConsole(message, false);
	}

	/**
	 * Adds the message to the appropriate server console.
	 * In case of a headless server prints the message to the error stream.
	 *
	 * @param message text to be added to console
	 */
	public static void addToErrorConsole(String message) {
		addToConsole(message, true);
	}

	private static void addToConsole(String message, boolean error) {
		if (serverGUI != null) {
			serverGUI.serverChatBox.append(message + "\n");
			serverGUI.serverChatBox.setCaretPosition(serverGUI.serverChatBox.getDocument().getLength());
		} else {
			if (error) {
				System.err.println(message);
			} else {
				System.out.println(message);
			}
		}
	}

	public static void runServerConsole() {
		addToConsole("Headless server ready! Type \"help\" for a list of commands.");

		try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
			String input;
			while (true) {
				input = reader.readLine();
				if (input == null) {
					// Handle the null input case (e.g., CTRL-D) gracefully and continue the loop
					continue;
				}
				if (parseInput(input)) {
					break;
				}
			}
		} catch (IOException e) {
			addToConsole("An error occurred while reading input: " + e.getMessage());
		}

		addToConsole("Exiting server...");
		System.exit(0);
	}

	/**
	 * Parses a server console input string.
	 *
	 * @param input string to process
	 * @return true if the server should exit
	 */
	private static boolean parseInput(String input) {
		String lowerInput = input.toLowerCase();
		String helpMessage = """
				Available commands:
				  help - Print this help dialog.
				  k:ID:REASON - Kick a player with id ID displaying the text REASON.
				  k:NAME:REASON - Kick a player with name NAME displaying the text REASON. If there is a player with an ID identical to the specified NAME, the ID takes precedence.
				  b:TEXT - Send server broadcast with text TEXT.
				  players - List currently connected players.
				  lobbies - List currently open lobbies.
				  exit - Exit server.
				  quit - Quit server.""";
		switch (lowerInput) {
			case "help":
				addToConsole(helpMessage);
				break;
			case "exit":
			case "quit":
				return true;
			case "players":
				addToConsole("Currently online players:");
				for (Player player : PlayerManager.playerList) {
					addToConsole("  \"" + player.name + "\" - " + player.id);
				}
				break;
			case "lobbies":
				addToConsole("Currently available lobbies:");
				for (Lobby lobby : LobbyManager.lobbyList) {
					addToConsole("  ID: " + lobby.lobbyID);
					addToConsole("    Track: " + RaceTrack.getTrackName(lobby.getTrack()));
					addToConsole("    Creator: " + lobby.creatorPlayer.name);
					addToConsole("    Players: " + String.join(", ", lobby.lobbylist.stream()
							.map(player -> player.name).toArray(String[]::new)));
				}
				break;
			default:
				String[] parts = input.split(":");
				if (parts.length == 0)
					break;
				String command = parts[0];
				switch (command) {
					case "k":
						if (parts.length == 3) {
							Player player;
							try {
								int id = Integer.parseInt(parts[1]);
								player = PlayerManager.getPlayerWithId(id);
								if (player == null)
									player = PlayerManager.getPlayerWithName(parts[1]);
							} catch (NumberFormatException e) {
								player = PlayerManager.getPlayerWithName(parts[1]);
							}
							if (player != null) {
								player.kick(parts[2]);
							} else {
								addToErrorConsole("No player with name or ID \"" + parts[1] + "\"!");
							}
						} else {
							addToErrorConsole("Kick command formatted incorrectly!");
							addToConsole(helpMessage);
						}
						break;
					case "b":
						if (parts.length == 2) {
							String broadcast = parts[1];
							ChatLogic.serverBroadcast(broadcast);
						} else {
							addToErrorConsole("Broadcast command formatted incorrectly!");
							addToConsole(helpMessage);
						}
						break;
					case "rmlobby":
						if (parts.length == 2) {
							try {
								int lobbyId = Integer.parseInt(parts[1]);
								Lobby lobby = LobbyManager.getLobby(lobbyId);
								if (lobby == null) {
									addToErrorConsole("Lobby with ID " + lobbyId + " does not exist!");
									break;
								}
								if (lobby.getLobbylist().size() > 0) {
									addToErrorConsole("Lobby with ID " + lobbyId + " is not empty!");
									break;
								}
								LobbyLogic.sendLobbyDeletion(lobbyId);
								LobbyManager.lobbyList.remove(lobby);
								ServerGUI.addToConsole("Lobby with ID " + lobbyId + " destroyed.");
							} catch (NumberFormatException e) {
								addToErrorConsole("Lobby ID must be an integer!");
							}
						} else {
							addToErrorConsole("Remove lobby command formatted incorrectly!");
							addToConsole(helpMessage);
						}
						break;
					default:
						addToErrorConsole("Unknown command \"" + input + "\"");
				}
		}
		return false;
	}
}
