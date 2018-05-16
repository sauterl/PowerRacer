package client.lobby;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.swing.JOptionPane;

import shared.game.PowerRacerGame;
import client.lobby.ClientParser;
import client.gui.ClientGUI;

/**
 * Manages Client to server connection on client side.
 * <p>
 * Keeps track of sending and receiving messages and links ClientParser and
 * ClientGUI.
 * 
 * @author Florian
 *
 */
public class Client implements Runnable {

	public String name;
	private BufferedReader in;
	private PrintWriter out;
	public int port;
	public String host = "localhost";
	public Socket socket;
	public Thread senderThread;
	public Thread receiverThread;
	public ConcurrentLinkedQueue<String> commandQueue = new ConcurrentLinkedQueue<String>();
	ClientParser parser;
	ClientGUI clientGUI;
	public boolean connectionApproved, termination, heartBeatReturned;
	byte heartBeatTicker;
	private static PowerRacerGame game;

	/**
	 * Constructor using host name and ClientGUI object.
	 * <p>
	 * Starts necessary threads such as senderThread and receiverThread.
	 * 
	 * @param host
	 *            host name
	 * @param clientGUI
	 *            reference to the creating ClientGUI
	 * @param port
	 *            the port number
	 */
	public Client(String host, ClientGUI clientGUI, int port) {
		this.port = port;
		this.clientGUI = clientGUI;
		try {
			socket = new Socket(host, port);
			socket.setTcpNoDelay(true);
			try {
				in = new BufferedReader(new InputStreamReader(
						socket.getInputStream()));
				out = new PrintWriter(socket.getOutputStream(), true);
				out.println("LGINR:" + System.getProperty("user.name"));
				senderThread = new Thread(this);
				receiverThread = new Thread(this);
				senderThread.start();
				receiverThread.start();
			} catch (Exception e) {
				System.err.println("Client Error1: " + e.getMessage());
				System.err.println("Localized: " + e.getLocalizedMessage());
				System.err.println("Stack Trace: " + e.getStackTrace());
				System.err.println("To String: " + e.toString());
			}
		} catch (Exception e) {
			clientGUI.setFeedback(e.getMessage());
			clientGUI.connectIpButton.setEnabled(true);
			return;
		}
		new Thread(this).start();
	}

	/**
	 * Terminates the Client, closing all sockets and interrupting all threads.
	 */
	public void terminate() {
		termination = true;
		senderThread.interrupt();
		receiverThread.interrupt();
		try {
			socket.close();
		} catch (IOException e) {
			System.out.println("ERROR in Client!");
			e.printStackTrace();
		}

	}

	/**
	 * Starts the necessary methods for the right threads.
	 */
	@Override
	public void run() {
		if (Thread.currentThread() == this.receiverThread) {
			Receiver();
		} else
			if (Thread.currentThread() == this.senderThread) {
				Sender();
			} else {
				checkTimeOut();
			}
	}

	/**
	 * Checks whether the initial connection times out.
	 */
	private void checkTimeOut() {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {

		}
		if (!connectionApproved) {
			clientGUI.setFeedback("Connection time-out!");
			clientGUI.connectIpButton.setEnabled(true);
			terminate();
		} else {
			new HeartBeatThread();
		}
	}

	/**
	 * Sends packets stored in commandQueue.
	 */
	private void Sender() {
		while (!termination) {
			while (commandQueue.isEmpty() == false) {
				try {
					out.println(commandQueue.remove());
				} catch (Exception e) {
					if (termination) {
						return;
					} else {
						System.err.println("Client Error2: " + e.getMessage());
						System.err.println("Localized: "
								+ e.getLocalizedMessage());
						System.err.println("Stack Trace: " + e.getStackTrace());
						System.err.println("To String: " + e.toString());
					}
				}
			}
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				// System.out.println("Sender thread shutting down...");
			}
		}
	}

	/**
	 * Receives packets from socket and parses them using the ClientParser.
	 */
	private void Receiver() {
		String inComPack;
		parser = new ClientParser(this);
		while (!termination) {
			try {
				while ((inComPack = in.readLine()) != null) {
					this.parser.parse(inComPack);
				}
			} catch (IOException e) {
				if (termination) {
					return;
				} else {
					// System.err.println("Client Error3: " + e.getMessage());
					// System.err.println("Localized: " +
					// e.getLocalizedMessage());
					// System.err.println("Stack Trace: " + e.getStackTrace());
					// System.err.println("To String: " + e.toString());
				}
			}
		}
	}

	/**
	 * Periodically tests client server connection. Returns the user to server
	 * select screen in case of a 10 second timeout.
	 * 
	 * @author Florian
	 *
	 */
	private class HeartBeatThread extends Thread {

		HeartBeatThread() {
			super();
			this.start();
		}

		@Override
		public void run() {
			try {
				while (!termination && heartBeatTicker < 5) {
					heartBeatReturned = false;
					commandQueue.add("HBTCR");
					// System.out.println("HeartBeat sent!");
					Thread.sleep(2000);
					if (!heartBeatReturned) {
						System.out.println("HeartBeat missing!");
						heartBeatTicker++;
					} else {
						heartBeatTicker = 0;
					}
				}
				if (heartBeatTicker > 4) {
					JOptionPane.showMessageDialog(clientGUI.frame,
							"Server connection has timed out.");
					clientGUI.frame.dispose();
					ClientGUI.reset();
					terminate();
				}
			} catch (Exception e) {
				System.out.println("HeartBeat stopped.");
			}
		}
	}

	/**
	 * Starts a new game with the given parameters.
	 * 
	 * @param numberOfPlayers
	 *            the number of human players participating in this game
	 * @param carIndex
	 *            this players car index
	 * @param carTypes
	 *            the selected car indexes needed to display the correct images
	 * @param raceTrackNumber
	 *            the number of the selected track
	 * @param playerNames
	 *            the names of the participating players
	 */
	public void startGame(int numberOfPlayers, int carIndex, int[] carTypes,
			byte raceTrackNumber, String[] playerNames) {
		if (raceTrackNumber == 4) {
			try {
				Thread.sleep(1000);
			} catch (Exception e) {
				// To do with RandomTrack
			}
		}
		System.out.println("Setting game!");
		game = new PowerRacerGame(numberOfPlayers, raceTrackNumber, carTypes,
				carIndex, commandQueue);
		game.setPlayerNames(playerNames);
		clientGUI.startCamera(game);
	}

	/**
	 * Sets the Car's input and position information.
	 * 
	 * @param carIndex
	 *            the index of the Car to which the inputs belong
	 * @param upIsPressed
	 *            the Car's up input
	 * @param downIsPressed
	 *            the Car's down input
	 * @param leftIsPressed
	 *            the Car's left input
	 * @param rightIsPressed
	 *            the Car's right input
	 * @param x
	 *            the Car horizontal position
	 * @param y
	 *            the Car vertical position
	 * @param speed
	 *            the Car current speed
	 * @param rotation
	 *            the Car current rotation
	 */
	public void setCarInfo(int carIndex, boolean upIsPressed,
			boolean downIsPressed, boolean leftIsPressed,
			boolean rightIsPressed, double x, double y, double speed,
			double rotation) {
		game.setCarInputUp(carIndex, upIsPressed);
		game.setCarInputDown(carIndex, downIsPressed);
		game.setCarInputLeft(carIndex, leftIsPressed);
		game.setCarInputRight(carIndex, rightIsPressed);
		game.setCarPosition(carIndex, x, y);
		game.setCarSpeed(carIndex, speed);
		game.setCarRotation(carIndex, rotation);
	}

	/**
	 * Sets the countdown to the given value.
	 * 
	 * @param countdown
	 *            the new value for countdown
	 */
	public void setCountdown(int countdown) {
		game.setCountdown(countdown);
	}

	/**
	 * Sets the games control to allow players to manipulate inputs or not.
	 * 
	 * @param control
	 *            the new value for control
	 */
	public void setControl(boolean control) {
		game.setControl(control);
	}

	/**
	 * Sets the game to be complete.
	 */
	public void setGameComplete() {
		game.setComplete();
	}

	/**
	 * Sets the Scoreboard to show the names of the victors and their finish
	 * times.
	 *
	 * @param times
	 *            the players' finish times
	 * @param names
	 *            the name of the players
	 */
	public void setScoreboard(int[] times, String[] names) {
		game.setScoreboard(times, names);
	}

	/**
	 * Checks whether the game pointer is null or not. Used to determine whether
	 * a null pointer may be caused.
	 * 
	 * @return whether the game isn't null
	 */
	public static boolean gameNotNull() {
		return game != null;
	}

	/**
	 * Returns the static pointer on the game object.
	 * 
	 * @return the static pointer on the game object
	 */
	public static PowerRacerGame getGame() {
		return game;
	}
}
