package server.lobby;

import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;

import shared.game.PowerRacerGame;

/**
 * Player object with all the player informations.
 *
 * @author marco
 */
public class Player {

	public int id;
	public String name;
	public Socket in_Socket;
	public PlayerSocket playerSocket;
	public ConcurrentLinkedQueue<String> commandQueue;
	public Lobby lobby;
	public boolean heartBeatReturned;
	private boolean isLobbyReady;
	private PowerRacerGame game;
	public int carIndex;
	private long endTime;
	private long startTime;
	private int legitPlayer;

	/**
	 * Constructor of the Player object.
	 *
	 * @param id        the player's id on the server
	 * @param in_Socket the player's communication socket
	 */
	public Player(int id, Socket in_Socket) {
		this.id = id;
		this.name = "";
		this.in_Socket = in_Socket;
		this.commandQueue = new ConcurrentLinkedQueue<>();
		this.playerSocket = new PlayerSocket(this.id, this.in_Socket, this);
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getID() {
		return id;
	}

	public void setHeartBeatReturnedTrue() {
		this.heartBeatReturned = true;
	}

	public void setLobby(Lobby lobby) {
		this.lobby = lobby;
	}

	public Lobby getLobby() {
		return lobby;
	}

	public void setLobbyReady(boolean b) {
		this.isLobbyReady = b;
	}

	public boolean getLobbyReady() {
		return this.isLobbyReady;
	}

	public void setGame(PowerRacerGame game) {
		this.game = game;
	}

	public PowerRacerGame getGame() {
		return this.game;
	}

	public void setCarIndex(int carIndex) {
		this.carIndex = carIndex;
	}

	public int getCarIndex() {
		return this.carIndex;
	}

	public void setStartTime(long currentTimeMillis) {
		this.startTime = currentTimeMillis;
	}

	public long getStartTime() {
		return this.startTime;
	}

	public void setEndTime(long currentTimeMillis) {
		this.endTime = currentTimeMillis;
	}

	public long getEndTime() {
		return this.endTime;
	}

	public void kick(String msg) {

		commandQueue.add("SKICK:" + msg);
		try {
			Thread.sleep(200);
		} catch (Exception ignored) {
		}
		LobbyLogic.removePlayerComplete(this);
	}

	/**
	 * @return the legitPlayer
	 */
	public int getLegitPlayer() {
		return legitPlayer;
	}

	/**
	 * @param legitPlayer the legitPlayer to set
	 */
	public void setLegitPlayer(int legitPlayer) {
		this.legitPlayer = legitPlayer;
	}
}
