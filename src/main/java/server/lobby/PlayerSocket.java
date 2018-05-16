package server.lobby;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;

/**
 * PlayerSocket object which holds the connection, reads the input and writes to
 * the output. Lets the three threads sender, receiver and heartbeat run.
 * 
 * @author marco
 * @author benzumbrunn
 *
 */
public class PlayerSocket implements Runnable {

	public int id;
	public Socket socket;
	private BufferedReader in;
	private PrintWriter out;
	public Thread senderThread;
	public Thread receiverThread;
	public Thread heartbeatThread;
	public Parser parser;
	private Player player;
	private String inComPack;
	private int heartBeatTicker;
	private boolean terminate;
	private boolean heartBeatOn;

	/**
	 * Constructor for the PlayerSocket object. Sets all the needed fields with
	 * given information, creates the three threads sender, receiver and
	 * heartbeat and let them run.
	 * 
	 * @param id
	 *            the player's ID
	 * @param socket
	 *            the player's Socket
	 * @param player
	 *            the actual player object
	 */
	public PlayerSocket(int id, Socket socket, Player player) {
		this.id = id;
		this.socket = socket;
		this.player = player;
		try {
			this.in = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
			this.out = new PrintWriter(socket.getOutputStream(), true);
			socket.setTcpNoDelay(true);
			socket.setKeepAlive(true);
			this.senderThread = new Thread(this);
			senderThread.start();
			this.receiverThread = new Thread(this);
			receiverThread.start();
			setHeartBeatOn();
			this.heartbeatThread = new Thread(this);
			heartbeatThread.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Run method for all the three threads which starts the right thread.
	 */
	public void run() {
		if (Thread.currentThread() == this.receiverThread) {
			try {
				Receiver();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			if (Thread.currentThread() == this.senderThread) {
				Sender();
			} else {
				if (Thread.currentThread() == this.heartbeatThread) {
					Heartbeat();
				}
			}
		}
	}

	/**
	 * Heartbeat thread which sends a heartbeat packet in a specific interval
	 * and waits for an answer. If no Answer is given in the specific time, the
	 * user will be removed form the playerlist in the playermanager and all the
	 * threads of this user will be stopped.
	 */
	private void Heartbeat() {
		while (!terminate && heartBeatOn) {
			this.player.heartBeatReturned = false;
			this.player.commandQueue.add("HBTSR");
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (!terminate) {
				if (!this.player.heartBeatReturned && heartBeatOn) {
					heartBeatTicker++;
				} else {
					heartBeatTicker = 0;
				}
				if (heartBeatTicker > 4) {
					terminate();
					LobbyLogic.removePlayerComplete(this.player);
				}
			}
		}
	}

	/**
	 * checks for a non-empty commandQueue and if this is right, the first
	 * command will be taken from the queue and trough the playersocket sent.
	 */
	private void Sender() {
		while (!terminate) {
			while (this.player.commandQueue.isEmpty() == false) {
				try {
					String packet = this.player.commandQueue.remove();
					out.println(packet);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				// Normal thing if a sleep is interrupted.
			}
		}
	}

	/**
	 * Creates a parser and checks for incoming packets. If there is an incoming
	 * packet, it will be given to the parser.
	 */
	private void Receiver() throws IOException {
		this.parser = new Parser(this.player);
		while (!terminate) {
			try {
				while ((inComPack = in.readLine()) != null) {
					this.parser.parse(inComPack);
				}
			} catch (SocketException e) {
				// Heartbeat will take care
				return;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void closeThreads() {
		try {
			terminate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void terminate() {
		terminate = true;
	}

	public void setHeartBeatOn() {
		heartBeatOn = true;
	}

	public void setHeartBeatOff() {
		heartBeatOn = false;
	}

	public boolean getHeartBeatOn() {
		return heartBeatOn;
	}
}