package server.lobby;

import java.util.ArrayList;

/**
 * Lobby object which will be integrated in the LobbyManager.
 * 
 * @author marco
 *
 */
public class Lobby {

	public int lobbyID;
	public Player creatorPlayer;
	public LobbyManager lobbyManager;
	private byte track;
	public ArrayList<Player> lobbylist;

	public Lobby(int lobbyID, Player player) {
		setLobbyID(lobbyID);
		setCreatorPlayer(player);
		setLobbyManager(lobbyManager);
		setLobbylist(new ArrayList<Player>());
	}

	public ArrayList<Player> getLobbylist() {
		return lobbylist;
	}

	public void setLobbylist(ArrayList<Player> lobbylist) {
		this.lobbylist = lobbylist;
	}

	public int getLobbyID() {
		return lobbyID;
	}

	public void setTrack(byte parseByte) {
		this.track = parseByte;
	}

	public byte getTrack() {
		return this.track;
	}

	public Player getCreatorPlayer() {
		return creatorPlayer;
	}

	public void setCreatorPlayer(Player creatorPlayer) {
		this.creatorPlayer = creatorPlayer;
	}

	public LobbyManager getLobbyManager() {
		return lobbyManager;
	}

	public void setLobbyManager(LobbyManager lobbyManager) {
		this.lobbyManager = lobbyManager;
	}

	public void setLobbyID(int lobbyID) {
		this.lobbyID = lobbyID;
	}
}
