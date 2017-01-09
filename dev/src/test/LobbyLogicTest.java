/**
 * 
 */
package test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import server.game.GameManager;
import server.lobby.CreateServer;
import server.lobby.LobbyLogic;
import server.lobby.LobbyManager;
import server.lobby.Player;
import server.lobby.PlayerManager;

/**
 * This JUnit test not only tests the whole lobbylogic, it also checks if the
 * lobby, the playermanager, the server and the player objects are working
 * properly. Some tests check if the correct packet is sent, we assert this to
 * check the commandqueue for the corresponding packet.
 * 
 * @author benzumbrunn
 *
 */
public class LobbyLogicTest {

	private static Player testPlayer;
	private static Player testPlayerCopy;
	private static String host;
	private static int port;

	/**
	 * This is run for initialization of our objects. It directly tests if a
	 * server can be started.
	 */
	@BeforeClass
	public static void beforeClass() {
		System.out.println("@BeforeClass");
		CreateServer.create();
		port = CreateServer.getPort();

		try {
			// try to get the system-dependent localhost address
			host = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			// set default host if none available
			host = "127.0.1.1";
		}

		// the server should have enough time to be initialized
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// we need functioning players all the time, so we create them before
		// testing
		initializeTestPlayers();
	}

	/**
	 * This method creates the test players which should stay throughout all the
	 * following tests.
	 */
	private static void initializeTestPlayers() {

		// try to establish two sockets to the server. The server will create
		// player-objects automatically for them.
		try {
			new Socket(host, port);
			new Socket(host, port);
			Thread.sleep(1500);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// we get references of the player-objects out of the playermanager for
		// a more easy access.
		testPlayer = PlayerManager.playerlist.get(0);
		testPlayerCopy = PlayerManager.playerlist.get(1);

		// let the two players login on the server and choose a name.
		PlayerManager.newPlayerLogin(testPlayer, "testPlayer");
		PlayerManager.newPlayerLogin(testPlayerCopy, "testPlayerCopy");

		// set the heartbeats of our players to off
		testPlayer.playerSocket.setHeartBeatOff();
		testPlayerCopy.playerSocket.setHeartBeatOff();

	}

	/**
	 * This method gets called when lobby tests are executed. JUnit tests should
	 * not depend on a specific order of execution, so we make clear that there
	 * is a usable lobby at any time. Both our players join the lobby through
	 * this method.
	 */
	private void createLobbyForTesting() {
		// create a new Lobby with a trackID. joins automatically.
		String trackID = "0";
		LobbyLogic.createNewLobby(testPlayer, trackID);

		// let the second player join the same lobby.
		LobbyLogic
				.joinLobby(testPlayerCopy, testPlayer.getLobby().getLobbyID());
	}

	/**
	 * This method creates a new joining player, because we need a new one in
	 * several test instances and it should be available untouched.
	 * 
	 * @return our newly created temporary player
	 */
	private Player createNewTemporaryPlayer() {
		try {
			// create a new socket for the temporary player
			new Socket(host, port);
			Thread.sleep(50);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		int listSize = PlayerManager.playerlist.size();
		Player temporaryPlayer = PlayerManager.playerlist.get(listSize - 1);
		LobbyLogic.newPlayerLogin(temporaryPlayer, "joiningPlayer");

		// heartbeats are annoying for testing
		temporaryPlayer.playerSocket.setHeartBeatOff();
		return temporaryPlayer;
	}

	/**
	 * Test method for checking if the player objects are initialized correctly.
	 * {@link server.lobby.Player#getName()}
	 */
	@Test
	public void testPlayerClass() {
		assertEquals("testPlayer", testPlayer.getName());
		assertEquals("testPlayerCopy", testPlayerCopy.getName());
	}

	/**
	 * Test method for checking if the playermanager is initialized correctly.
	 */
	@Test
	public void testPlayerManager() {
		StringBuilder sb = new StringBuilder();
		sb.append(testPlayer.getName());
		sb.append(':');
		sb.append(testPlayerCopy.getName());
		sb.append(':');
		String assertionPacket = sb.toString();

		PlayerManager.playerlist.clear();
		PlayerManager.playerlist.add(testPlayer);
		PlayerManager.playerlist.add(testPlayerCopy);

		assertEquals(assertionPacket, PlayerManager.getAllOnlinePlayerNames());
	}

	/**
	 * Test method for asserting that the correct packet will be sent when a
	 * player joins.
	 * {@link server.lobby.LobbyLogic#sendLoginAccept(server.lobby.Player)}.
	 */
	@Test
	public void testSendLoginAccept() {
		String assertionPacket = "LGINA:" + testPlayer.getName();
		LobbyLogic.sendLoginAccept(testPlayer);
		assertEquals(assertionPacket, testPlayer.commandQueue.remove());
	}

	/**
	 * Test method for asserting that a login information packet will be sent to
	 * all other players.
	 * {@link server.lobby.LobbyLogic#sendLoginInformationToOtherPlayers(server.lobby.Player)}
	 * .
	 */
	@Test
	public void testSendLoginInformationToOtherPlayers() {
		String assertionPacket = "LGINI:" + testPlayer.getName();
		LobbyLogic.sendLoginInformationToOtherPlayers(testPlayer);
		assertEquals(assertionPacket, testPlayerCopy.commandQueue.remove());
	}

	/**
	 * Test method for asserting that a logout is successful.
	 * {@link server.lobby.LobbyLogic#sendLogoutAccept(server.lobby.Player)}.
	 */
	@Test
	public void testSendLogoutAccept() {
		// a temporary player is needed, because he gets removed automatically
		// through the logout accept
		Player logOutPlayer = createNewTemporaryPlayer();

		LobbyLogic.sendLogoutAccept(logOutPlayer);

		// assert that the player was really removed
		assertFalse(PlayerManager.playerlist.contains(logOutPlayer));

		LobbyLogic.removePlayerComplete(logOutPlayer);

		// TODO marco, the else clause is never entered in eclemma, see
		// lobbylogic coverage
		// now we test multiple logouts
		logOutPlayer = createNewTemporaryPlayer();
		LobbyLogic.sendLogoutAccept(logOutPlayer);
		LobbyLogic.sendLogoutAccept(logOutPlayer);
		LobbyLogic.sendLogoutAccept(logOutPlayer);
		LobbyLogic.sendLogoutAccept(logOutPlayer);
		LobbyLogic.removePlayerComplete(logOutPlayer);
	}

	/**
	 * Test method for asserting that a player is completely removed from the
	 * server.
	 * {@link server.lobby.LobbyLogic#removePlayerComplete(server.lobby.Player)}
	 * .
	 */
	@Test
	public void testRemovePlayerComplete() {
		// we need a temporary player, our default players should stay
		Player removablePlayer = createNewTemporaryPlayer();

		LobbyLogic.removePlayerComplete(removablePlayer);

		try {
			// give the method time to close the threads
			// the server should have enough time to send one last heartbeat
			Thread.sleep(3500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// check if threads are closed
		assertFalse(removablePlayer.playerSocket.senderThread.isAlive());
		assertFalse(removablePlayer.playerSocket.receiverThread.isAlive());
		assertFalse(removablePlayer.playerSocket.heartbeatThread.isAlive());

		// check if playersocket is closed
		assertTrue(removablePlayer.playerSocket.socket.isClosed());
	}

	/**
	 * Test method for asserting that a packet will be sent to let the player
	 * know the server is full.
	 * {@link server.lobby.LobbyLogic#sendFullServer(server.lobby.Player)}.
	 */
	@Test
	public void testSendFullServer() {
		// LGIND is login denied
		String assertionPacket = "LGIND:" + PlayerManager.playerlist.size();
		LobbyLogic.sendFullServer(testPlayer);
		assertEquals(assertionPacket, testPlayer.commandQueue.remove());
	}

	/**
	 * Test method for asserting that all other players on the server receive a
	 * logout information from the server.
	 * {@link server.lobby.LobbyLogic#sendLogoutInformationToOtherPlayers(server.lobby.Player)}
	 * .
	 */
	@Test
	public void testSendLogoutInformationToOtherPlayers() {
		String assertionPacket = "LGOUI:" + testPlayer.getName();
		LobbyLogic.sendLogoutInformationToOtherPlayers(testPlayer);
		assertEquals(assertionPacket, testPlayerCopy.commandQueue.remove());
	}

	/**
	 * Test method for asserting that the name change accepted packet will be
	 * sent to the player.
	 * {@link server.lobby.LobbyLogic#sendNameChangeAccept(server.lobby.Player)}
	 * .
	 */
	@Test
	public void testSendNameChangeAccept() {
		String assertionPacket = "NAMCA:" + testPlayer.getName();
		LobbyLogic.sendNameChangeAccept(testPlayer);
		assertEquals(assertionPacket, testPlayer.commandQueue.remove());
	}

	/**
	 * Test method for asserting that the name change denied packet will be sent
	 * to the player.
	 * {@link server.lobby.LobbyLogic#sendNameChangeDenied(server.lobby.Player)}
	 * .
	 */
	@Test
	public void testSendNameChangeDenied() {
		String assertionPacket = "NAMCD:" + testPlayer.getName();
		LobbyLogic.sendNameChangeDenied(testPlayer);
		assertEquals(assertionPacket, testPlayer.commandQueue.remove());
	}

	/**
	 * Test method for asserting that all other players are receiving a packet
	 * containing the new name of a player.
	 * {@link server.lobby.LobbyLogic#sendNameChangeInformationToOtherPlayers(server.lobby.Player, java.lang.String)}
	 * .
	 */
	@Test
	public void testSendNameChangeInformationToOtherPlayers() {
		String oldName = "testPlayerOldName";
		String newName = testPlayer.getName();
		String assertionPacket = "NAMCI:" + oldName + ":" + newName;
		LobbyLogic.sendNameChangeInformationToOtherPlayers(testPlayer, oldName);
		assertEquals(assertionPacket, testPlayerCopy.commandQueue.remove());
	}

	/**
	 * Test method for asserting that the correct packet and message is sent to
	 * all users on the server.
	 * {@link server.lobby.LobbyLogic#worldMessage(server.lobby.Player, java.lang.String)}
	 * .
	 */
	@Test
	public void testWorldMessage() {
		String message = "Hello, World!";
		String assertionPacket = "CALLM:" + testPlayer.getName() + ":"
				+ message;
		LobbyLogic.worldMessage(testPlayer, message);
		assertEquals(assertionPacket, testPlayerCopy.commandQueue.remove());
	}

	/**
	 * Test method for asserting that the two corresponding packets are sent to
	 * the correct players. The first is the lobby creation accepted packet for
	 * the creating player, the second is the lobby information for all the
	 * other players.
	 * {@link server.lobby.LobbyLogic#createNewLobby(server.lobby.Player, java.lang.String)}
	 * .
	 */
	@Test
	public void testCreateNewLobby() {
		String trackID = "1";
		LobbyLogic.createNewLobby(testPlayer, trackID);

		// prepare a packet which is sent to the player who created the lobby
		String assertionPacket = "LOBCA:" + testPlayer.getLobby().getLobbyID()
				+ ":" + testPlayer.getLobby().getTrack();

		assertEquals(assertionPacket, testPlayer.commandQueue.remove());

		// the player needs to leave the lobby
		LobbyLogic.leaveLobby(testPlayer, testPlayer.getLobby().getLobbyID());
		// clear commandqueue for next tests
		testPlayer.commandQueue.clear();

		// testing of unrealistic trackIDs, these are handled by choosing a
		// default track, so they're possible
		String notAMapID = "99";
		String negativeID = "-1";

		LobbyLogic.createNewLobby(testPlayer, notAMapID);
		String assertionPacket2 = "LOBCA:" + testPlayer.getLobby().getLobbyID()
				+ ":" + testPlayer.getLobby().getTrack();
		assertEquals(assertionPacket2, testPlayer.commandQueue.remove());

		// the player needs to leave the lobby
		LobbyLogic.leaveLobby(testPlayer, testPlayer.getLobby().getLobbyID());
		// clear commandqueue for next tests
		testPlayer.commandQueue.clear();

		LobbyLogic.createNewLobby(testPlayer, negativeID);
		String assertionPacket3 = "LOBCA:" + testPlayer.getLobby().getLobbyID()
				+ ":" + testPlayer.getLobby().getTrack();
		assertEquals(assertionPacket3, testPlayer.commandQueue.remove());

		// the player needs to leave the lobby
		LobbyLogic.leaveLobby(testPlayer, testPlayer.getLobby().getLobbyID());
		// clear commandqueue for next tests

		// create lobby through copied player, let testplayer join
		LobbyLogic.createNewLobby(testPlayerCopy, trackID);
		LobbyLogic
				.joinLobby(testPlayer, testPlayerCopy.getLobby().getLobbyID());

		// assert that both players are in the same lobby
		assertEquals(testPlayerCopy.getLobby().getLobbyID(), testPlayer
				.getLobby().getLobbyID());

		// let testplayer create a new lobby, he should leave the old one
		// automatically
		LobbyLogic.createNewLobby(testPlayer, trackID);

		// assert that testplayer is really in a newly created lobby and left
		// the old one
		assertNotEquals(testPlayerCopy.getLobby().getLobbyID(), testPlayer
				.getLobby().getLobbyID());

		/*
		 * A comment to testing extreme values: Through the LobbyLogic, we are
		 * able to send trackIDs in byterange. The program handles this by
		 * choosing a default map when the trackID is not in the range of maps.
		 * We don't need to test numbers bigger than bytes, because these
		 * requests are denied by the parser. The same goes for empty strings.
		 */
	}

	/**
	 * Test method for asserting that a lobby message reaches the players in the
	 * same lobby. lobby
	 * {@link server.lobby.LobbyLogic#lobbyMessage(server.lobby.Player, java.lang.String, int)}
	 * .
	 */
	@Test
	public void testLobbyMessage() {
		// the following method lets both default players join the lobby, so the
		// message sent later should always reach its target.
		createLobbyForTesting();

		// building our packet to test.
		String message = "Hello, lobby!";
		String assertionPacket = "CLOBM:" + testPlayer.getName() + ":"
				+ message;

		// clear the commandqueues of our players, so accept-packets are ignored
		testPlayer.commandQueue.clear();
		testPlayerCopy.commandQueue.clear();

		// let the first player write into the lobby.
		LobbyLogic.lobbyMessage(testPlayer, message, testPlayer.getLobby()
				.getLobbyID());

		// check the assert on the second player.
		assertEquals(assertionPacket, testPlayerCopy.commandQueue.remove());

		// the players need to leave the lobby
		LobbyLogic.leaveLobby(testPlayer, testPlayer.getLobby().getLobbyID());
		LobbyLogic.leaveLobby(testPlayerCopy, testPlayerCopy.getLobby()
				.getLobbyID());
	}

	/**
	 * Test method for asserting that only the according player receives a
	 * whisper message.
	 * {@link server.lobby.LobbyLogic#privateMessage(java.lang.String, java.lang.String, java.lang.String)}
	 * .
	 */
	@Test
	public void testPrivateMessage() {
		// the temporary player is for asserting later that he did not get the
		// private message
		Player shouldNotHearPlayer = createNewTemporaryPlayer();

		// make sure nothing distracting is in its commandqueue
		shouldNotHearPlayer.commandQueue.clear();

		// delete all the 'player joined' packets and other malarkey
		testPlayer.commandQueue.clear();
		testPlayerCopy.commandQueue.clear();

		String message = "A silent whisper...";
		String assertionPacket1 = "CWHIM:" + testPlayer.getName() + ":"
				+ testPlayerCopy.getName() + ":" + message;
		LobbyLogic.privateMessage(testPlayer.getName(),
				testPlayerCopy.getName(), message);

		// make sure that the packet reached its correct target
		assertEquals(assertionPacket1, testPlayerCopy.commandQueue.remove());

		// make sure that the private message didn't reach our temp player
		assertTrue(shouldNotHearPlayer.commandQueue.peek() == null);

		// the sending player should also receive the message back, so he knows
		// it's not denied
		String assertionPacket2 = "CWHIM:" + testPlayer.getName() + ":"
				+ testPlayerCopy.getName() + ":" + message;
		assertEquals(assertionPacket2, testPlayer.commandQueue.remove());

		// we try to send a message to a player who does not exist, so a denied
		// packet should be sent
		String assertionPacket3 = "CWHID:" + testPlayer.getName()
				+ ":nonExistantPlayer:" + message;
		LobbyLogic.privateMessage(testPlayer.getName(), "nonExistantPlayer",
				message);
		assertEquals(assertionPacket3, testPlayer.commandQueue.remove());

		// remove the joined player, that one's obsolete now
		LobbyLogic.removePlayerComplete(shouldNotHearPlayer);
	}

	/**
	 * Test method for asserting that the 'heartbeat client accepted' packet
	 * reaches its correct target.
	 * {@link server.lobby.LobbyLogic#answerHeartbeat(server.lobby.Player)}.
	 */
	@Test
	public void testAnswerHeartbeat() {
		String assertionPacket = "HBTCA";
		LobbyLogic.answerHeartbeat(testPlayer);
		assertEquals(assertionPacket, testPlayer.commandQueue.remove());
	}

	/**
	 * Test method for asserting that the requesting player receives the correct
	 * list of all players who are online.
	 * {@link server.lobby.LobbyLogic#whoIsOnline(server.lobby.Player)}.
	 */
	@Test
	public void testWhoIsOnline() {
		String assertionPacket = "WHOOI:"
				+ PlayerManager.getAllOnlinePlayerNames();

		// clear the queue again, some packets are slipping in for some reason
		testPlayer.commandQueue.clear();

		LobbyLogic.whoIsOnline(testPlayer);
		assertEquals(assertionPacket, testPlayer.commandQueue.remove());
	}

	/**
	 * Test method for asserting that a heartbeat is returned.
	 * {@link server.lobby.LobbyLogic#setHeartbeat(server.lobby.Player)}.
	 */
	@Test
	public void testSetHeartbeat() {
		LobbyLogic.setHeartbeat(testPlayer);
		assertTrue(testPlayer.heartBeatReturned);
	}

	/**
	 * Test method for asserting that a player can join a new lobby.
	 * {@link server.lobby.LobbyLogic#joinLobby(server.lobby.Player, int)}.
	 */
	@Test
	public void testJoinLobby() {
		createLobbyForTesting();

		Player lobbyJoiningPlayer = createNewTemporaryPlayer();

		// let the new player join our standard lobby
		LobbyLogic.joinLobby(lobbyJoiningPlayer, testPlayer.getLobby()
				.getLobbyID());

		// assert that our new player joined the specified lobby
		assertTrue(testPlayer.getLobby().lobbylist.contains(lobbyJoiningPlayer));

		// test a join request in a full lobby
		Player lobbyJoiningPlayer2 = createNewTemporaryPlayer();
		Player lobbyJoiningPlayer3 = createNewTemporaryPlayer();
		LobbyLogic.joinLobby(lobbyJoiningPlayer2, testPlayer.getLobby()
				.getLobbyID());
		LobbyLogic.joinLobby(lobbyJoiningPlayer3, testPlayer.getLobby()
				.getLobbyID());
		Player lobbyJoiningPlayer4 = createNewTemporaryPlayer();
		Player lobbyJoiningPlayer5 = createNewTemporaryPlayer();
		LobbyLogic.joinLobby(lobbyJoiningPlayer4, testPlayer.getLobby()
				.getLobbyID());
		LobbyLogic.joinLobby(lobbyJoiningPlayer5, testPlayer.getLobby()
				.getLobbyID());
		Player lobbyJoiningPlayer6 = createNewTemporaryPlayer();
		Player lobbyJoiningPlayer7 = createNewTemporaryPlayer();
		LobbyLogic.joinLobby(lobbyJoiningPlayer6, testPlayer.getLobby()
				.getLobbyID());
		LobbyLogic.joinLobby(lobbyJoiningPlayer7, testPlayer.getLobby()
				.getLobbyID());
		// the last player should not be in the lobby
		assertFalse(testPlayer.getLobby().getLobbylist()
				.contains(lobbyJoiningPlayer7));

		// copy player needs to leave lobby and create a new one for next test
		LobbyLogic.leaveLobby(testPlayerCopy, testPlayerCopy.getLobby()
				.getLobbyID());
		LobbyLogic.createNewLobby(testPlayerCopy, "1");
		// player should leave old lobby automatically and join the new one
		LobbyLogic
				.joinLobby(testPlayer, testPlayerCopy.getLobby().getLobbyID());
		// assert that it worked
		assertEquals(testPlayerCopy.getLobby().getLobbyID(), testPlayer
				.getLobby().getLobbyID());

		// remove the joined players, these are obsolete now
		LobbyLogic.removePlayerComplete(lobbyJoiningPlayer);
		LobbyLogic.removePlayerComplete(lobbyJoiningPlayer2);
		LobbyLogic.removePlayerComplete(lobbyJoiningPlayer3);
		LobbyLogic.removePlayerComplete(lobbyJoiningPlayer4);
		LobbyLogic.removePlayerComplete(lobbyJoiningPlayer5);
		LobbyLogic.removePlayerComplete(lobbyJoiningPlayer6);
		LobbyLogic.removePlayerComplete(lobbyJoiningPlayer7);
		

		// the players need to leave the lobby
		LobbyLogic.leaveLobby(testPlayer, testPlayer.getLobby().getLobbyID());
		LobbyLogic.leaveLobby(testPlayerCopy, testPlayerCopy.getLobby()
				.getLobbyID());

	}

	/**
	 * Test method for asserting that a player can leave a lobby.
	 * {@link server.lobby.LobbyLogic#leaveLobby(server.lobby.Player, int)}.
	 */
	@Test
	public void testLeaveLobby() {
		createLobbyForTesting();

		Player lobbyLeavingPlayer = createNewTemporaryPlayer();

		// firstly, let our new player join the lobby
		LobbyLogic.joinLobby(lobbyLeavingPlayer, testPlayer.getLobby()
				.getLobbyID());

		// assert that he really joined the lobby
		assertTrue(testPlayer.getLobby().lobbylist.contains(lobbyLeavingPlayer));

		// let him leave
		LobbyLogic.leaveLobby(lobbyLeavingPlayer, testPlayer.getLobby()
				.getLobbyID());

		// then assert that he left
		assertFalse(testPlayer.getLobby().lobbylist
				.contains(lobbyLeavingPlayer));

		// remove the temporary player
		LobbyLogic.removePlayerComplete(lobbyLeavingPlayer);

		// the players need to leave the lobby
		LobbyLogic.leaveLobby(testPlayer, testPlayer.getLobby().getLobbyID());
		LobbyLogic.leaveLobby(testPlayerCopy, testPlayerCopy.getLobby()
				.getLobbyID());

	}

	/**
	 * Test method for asserting that the 'lobby open information' is sent to
	 * the correct player.
	 * {@link server.lobby.LobbyLogic#sendOpenLobbyInformation(server.lobby.Player)}
	 * .
	 */
	@Test
	public void testSendOpenLobbyInformation() {
		String assertionPacket = "LOBOI:" + LobbyManager.getAllOpenLobbys();
		LobbyLogic.sendOpenLobbyInformation(testPlayer);
		assertEquals(assertionPacket, testPlayer.commandQueue.remove());
	}

	/**
	 * Test method for asserting that the 'lobby deletion information' packet is
	 * sent to all the players on the server.
	 * {@link server.lobby.LobbyLogic#sendLobbyDeletion(int)}.
	 */
	@Test
	public void testSendLobbyDeletion() {
		// we use an unusual lobbyID, just for testing
		int lobbyID = 777777;
		String assertionPacket = "LOBDI:" + lobbyID;
		LobbyLogic.sendLobbyDeletion(lobbyID);

		// assert that each testplayer gets the packet
		assertEquals(assertionPacket, testPlayer.commandQueue.remove());
		assertEquals(assertionPacket, testPlayerCopy.commandQueue.remove());
	}

	/**
	 * Test method for asserting that a new player can join the server.
	 * {@link server.lobby.LobbyLogic#newPlayerLogin(server.lobby.Player, java.lang.String)}
	 * .
	 */
	@Test
	public void testNewPlayerLogin() {
		// The playermanager for our test only holds two players, so this test
		// should work every time
		Player joiningPlayer = createNewTemporaryPlayer();

		// assert that the joining player is on the server
		assertTrue(PlayerManager.playerlist.contains(joiningPlayer));

		// remove this player, that one's obsolete now
		LobbyLogic.removePlayerComplete(joiningPlayer);

		// fill the server with lots and lots of players
		// 2 are already there
		for (int i = 0; i < PlayerManager.getMaxUser() - 2; i++) {
			createNewTemporaryPlayer();
			System.out.println("Player " + i + " created. "
					+ "Still creating players, please be patient.");
		}
		// create one more player which should not be able to join the server
		// create a new socket for the temporary player
		try {
			// create a new socket for the temporary player
			new Socket(host, port);
			Thread.sleep(1500);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// the playermanager should not hold that user
		assertEquals(PlayerManager.getMaxUser(),
				PlayerManager.playerlist.size());

		// remove all temporary players
		for (int i = PlayerManager.getMaxUser() - 1; i > 2; i--) {
			LobbyLogic.removePlayerComplete(PlayerManager.playerlist.get(i));
		}
	}

	/**
	 * Test method for asserting that a name change can be done correctly.
	 * {@link server.lobby.LobbyLogic#nameChangeRequest(server.lobby.Player, java.lang.String)}
	 * .
	 */
	@Test
	public void testNameChangeRequest() {
		String oldName = testPlayer.getName();
		String newName = "testPlayerNewName";

		// no other player on the server should have the name "testplayer", so
		// this should work
		LobbyLogic.nameChangeRequest(testPlayer, newName);
		assertEquals(newName, testPlayer.getName());

		// commandqueue needs to be empty for the next test
		testPlayer.commandQueue.clear();

		// test case when name is already in use
		String assertionPacket = "NAMCD:" + testPlayer.getName();
		LobbyLogic.nameChangeRequest(testPlayer, PlayerManager.playerlist
				.get(0).getName());
		assertEquals(assertionPacket, testPlayer.commandQueue.remove());

		// this request should fail because of the space
		LobbyLogic.nameChangeRequest(testPlayer, "new name");
		assertNotEquals("new name", testPlayer.getName());

		// test is complete, let's change the name back and test again
		LobbyLogic.nameChangeRequest(testPlayer, oldName);
		assertEquals(oldName, testPlayer.getName());
	}

	/**
	 * Test method for asserting that a player can set his status in the lobby
	 * as ready to play.
	 * {@link server.lobby.LobbyLogic#playerLobbyReady(server.lobby.Player, java.lang.String)}
	 * .
	 */
	@Test
	public void testPlayerLobbyReady() {
		createLobbyForTesting();

		testPlayer.setCarIndex(0);

		// the tested method needs the car index as a string
		String carIndex = String.valueOf(testPlayer.getCarIndex());
		LobbyLogic.playerLobbyReady(testPlayer, carIndex);

		// assert that the function has taken effect
		assertTrue(testPlayer.getLobbyReady());

		// test when lobby is ready
		Player testGamer1 = createNewTemporaryPlayer();
		Player testGamer2 = createNewTemporaryPlayer();
		LobbyLogic.joinLobby(testGamer1, testPlayer.getLobby().getLobbyID());
		LobbyLogic.joinLobby(testGamer2, testPlayer.getLobby().getLobbyID());

		// set everybody ready
		testPlayer.setLobbyReady(true);
		testPlayerCopy.setLobbyReady(true);
		testGamer1.setLobbyReady(true);
		testGamer2.setLobbyReady(true);
		LobbyLogic.playerLobbyReady(testPlayer, "0");
		assertFalse(GameManager.getGamelist().size() == 0);

		// the players need to leave the lobby
		LobbyLogic.leaveLobby(testPlayer, testPlayer.getLobby().getLobbyID());
		LobbyLogic.leaveLobby(testPlayerCopy, testPlayerCopy.getLobby()
				.getLobbyID());

		// remove temporary players
		LobbyLogic.removePlayerComplete(testGamer1);
		LobbyLogic.removePlayerComplete(testGamer2);

		// clear the current games for future testing
		GameManager.getGamelist().clear();
	}

	/**
	 * Test method for switching the ready status to 'not ready'.
	 * {@link server.lobby.LobbyLogic#playerLobbyUnready(server.lobby.Player)}.
	 */
	@Test
	public void testPlayerLobbyUnready() {
		LobbyLogic.playerLobbyUnready(testPlayer);

		// assert that the player is not ready to start a game by checking the
		// corresponding boolean
		assertFalse(testPlayer.getLobbyReady());
	}

	/**
	 * Test method for asserting that no game is running. Other tests with this
	 * method would not be specific unit tests, so we avoid them.
	 * {@link server.lobby.LobbyLogic#sendRunningGames(server.lobby.Player)}.
	 */
	@Test
	public void testSendRunningGames() {
		// there are no current running games
		String CURGD = "CURGD";
		LobbyLogic.sendRunningGames(testPlayer);
		// that means the according packet has to be sent
		assertEquals(CURGD, testPlayer.commandQueue.remove());

		createLobbyForTesting();

		Player testGamer1 = createNewTemporaryPlayer();
		Player testGamer2 = createNewTemporaryPlayer();
		LobbyLogic.joinLobby(testGamer1, testPlayer.getLobby().getLobbyID());
		LobbyLogic.joinLobby(testGamer2, testPlayer.getLobby().getLobbyID());

		// set everybody ready
		testPlayer.setLobbyReady(true);
		testPlayerCopy.setLobbyReady(true);
		testGamer1.setLobbyReady(true);
		testGamer2.setLobbyReady(true);
		LobbyLogic.playerLobbyReady(testPlayer, "0");

		// run our method to test
		LobbyLogic.sendRunningGames(testPlayer);

		// our player should receive a packet now
		assertTrue(testPlayer.commandQueue.size() != 0);

		// the players need to leave the lobby
		LobbyLogic.leaveLobby(testPlayer, testPlayer.getLobby().getLobbyID());
		LobbyLogic.leaveLobby(testPlayerCopy, testPlayerCopy.getLobby()
				.getLobbyID());

		// the temp players need to be removed
		LobbyLogic.removePlayerComplete(testGamer1);
		LobbyLogic.removePlayerComplete(testGamer2);
	}

	/**
	 * This method is for cleaning the commandqueues of all players after each
	 * test. This is crucial, because some functions sometimes send more packets
	 * that the test removes.
	 */
	@After
	public void cleanPlayerCommandQueues() {
		testPlayer.commandQueue.clear();
		testPlayerCopy.commandQueue.clear();
	}

	/**
	 * This method is for cleaning up after all tests are done.
	 */
	@AfterClass
	public static void afterClass() {
		CreateServer.terminate();
	}

}
