package server.lobby;

import server.game.GameManager;

/**
 * All the methods that need to be run at the creation of a new server. Creates
 * the static objects playerManager, lobbyManager, updateManager, checkinServer
 * and starts the threads of CheckinServer and UpdateManager.
 * 
 * @author marco
 *
 */

public class CreateServer {

	static PlayerManager playerManager;
	static CheckinServer checkinServer;
	static UpdateManager updateManager;
	static LobbyManager lobbyManager;
	static GameManager gameManager;
	static ServerGUI serverGUI;
	static int port = 21000;

	/**
	 * Creates all the needed objects and start all the needed threads.
	 */
	public static void create() {
		createPlayerManager();
		createLobbyManager();
		createUpdateManager();
		createCheckinServer();
		createGameManager();
		startCheckinServer();
		startUpdateManager();
	}

	public static void createWithGUI() {
		createGUI();
		create();
	}

	/**
	 * Creates at servercreation a separated GUI for server-broadcasts
	 */
	private static void createGUI() {
		serverGUI = new ServerGUI();
	}

	/**
	 * Creates all the needed objects and start all the needed threads at the
	 * specific port.
	 * 
	 * @param port
	 *            specified port for communication
	 */
	public static void create(int port) {
		CreateServer.port = port;
		create();
	}

	public static void createWithGUI(int port) {
		CreateServer.port = port;
		createWithGUI();
	}

	/**
	 * Stops the server-threads.
	 */
	public static void terminate() {
		checkinServer.terminate();
		updateManager.terminate();
	}

	private static void createLobbyManager() {
		lobbyManager = new LobbyManager();
	}

	private static void startCheckinServer() {
		checkinServer.start();
	}

	private static void startUpdateManager() {
		updateManager.start();
	}

	private static void createCheckinServer() {
		checkinServer = new CheckinServer(port);
	}

	private static void createUpdateManager() {
		updateManager = new UpdateManager();
	}

	private static void createPlayerManager() {
		playerManager = new PlayerManager();
	}

	private static void createGameManager() {
		gameManager = new GameManager();
	}

	public static int getPort() {
		return port;
	}
}
