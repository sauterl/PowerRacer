package server.lobby;

import shared.game.RaceTrack;

import javax.swing.*;
import java.awt.*;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Scanner;

/**
 * Small GUI for server host to monitor activity and kick players.
 *
 * @author Florian
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
				String text = serverConsole.getText();
				if (text.split(":")[0].equals("\\k")) {
					String[] parts = text.split(":");
					if (text.split(":").length == 3) {
						Objects.requireNonNull(PlayerManager.getPlayerWithName(parts[1]))
								.increaseKickCounter(10, parts[2]);
					} else {
						ServerGUI
								.addToConsole("Not the right number of \":\"\n\\k:name:reason");
					}
				} else {
					ChatLogic.serverBroadcast(text);
				}
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
	 * Adds the argument message to the JTextArea chatBox and scrolls its Focus
	 * down to the new message.
	 *
	 * @param message text to be added to chat
	 */
	public static void addToConsole(String message) {
		if (serverGUI != null) {
			serverGUI.serverChatBox.append(message + "\n");
			serverGUI.serverChatBox.setCaretPosition(serverGUI.serverChatBox
					.getDocument().getLength());
		} else {
			System.out.println(message);
		}
	}

	public static void runServerConsole() {
		System.out.println("Headless server ready! Type \"help\" for a list of commands.");
		String helpMessage = "Available commands:\n" +
				"  help - Print this help dialog.\n" +
				"  k:NAME:REASON - Kick a player with name NAME displaying the text REASON.\n" +
				"  b:TEXT - Send server broadcast with text TEXT.\n" +
				"  players - List currently connected players.\n" +
				"  lobbies - List currently open lobbies.\n" +
				"  exit - Exit server.\n" +
				"  quit - Quit server.";
		Scanner scan = new Scanner(System.in);
		serverLoop:
		while (true) {
			try {
				String input = scan.nextLine();
				String lowerInput = input.toLowerCase();
				switch (lowerInput) {
					case "help":
						System.out.println(helpMessage);
						break;
					case "exit":
					case "quit":
						break serverLoop;
					case "players":
						System.out.println("Currently online players:");
						for (Player player : PlayerManager.playerlist) {
							System.out.println("  " + player.name);
						}
						break;
					case "lobbies":
						System.out.println("Currently available lobbies:");
						for (Lobby lobby : LobbyManager.lobbylist) {
							System.out.println("  ID: " + lobby.lobbyID);
							System.out.println("    Track: " + RaceTrack.getTrackName(lobby.getTrack()));
							System.out.println("    Creator: " + lobby.creatorPlayer.name);
							System.out.println("    Players: " + String.join(", ",
									lobby.lobbylist.stream()
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
									Player player = PlayerManager.getPlayerWithName(parts[1]);
									if (player != null) {
										player.increaseKickCounter(10, parts[2]);
									} else {
										System.err.println("No player with name \"" + parts[1] + "\"!");
									}
								} else {
									System.err.println("Kick command formatted incorrectly!");
									System.out.println(helpMessage);
								}
								break;
							case "b":
								if (parts.length == 2) {
									String broadcast = parts[1];
									ChatLogic.serverBroadcast(broadcast);
								} else {
									System.err.println("Broadcast command formatted incorrectly!");
									System.out.println(helpMessage);
								}
								break;
							case "rmlobby":
								if (parts.length == 2) {
									try {
										int lobbyId = Integer.parseInt(parts[1]);
										Lobby lobby = LobbyManager.getLobby(lobbyId);
										if (lobby == null) {
											System.err.println("Lobby with ID " + lobbyId + " does not exist!");
											break;
										}
										if (lobby.getLobbylist().size() > 0) {
											System.err.println("Lobby with ID " + lobbyId + " is not empty!");
											break;
										}
										LobbyLogic.sendLobbyDeletion(lobbyId);
										LobbyManager.lobbylist.remove(lobby);
										ServerGUI.addToConsole("Lobby with ID " + lobbyId + " destroyed.");
									} catch (NumberFormatException e) {
										System.err.println("Lobby ID must be an integer!");
									}
								} else {
									System.err.println("Remove lobby command formatted incorrectly!");
									System.out.println(helpMessage);
								}
								break;
							default:
								System.err.println("Unknown command \"" + input + "\"");
						}
				}
			} catch (NoSuchElementException e) {
				// CTRL-D detected
				break;
			}
		}
		System.out.println("Exiting server...");
		System.exit(0);
	}
}
