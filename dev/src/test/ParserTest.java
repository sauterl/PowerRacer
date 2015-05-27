/**
 * 
 */
package test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import server.lobby.CreateServer;
import server.lobby.Parser;
import server.lobby.Player;
import server.lobby.PlayerManager;

/**
 * This JUnit test does not use assertions, we only check which messages the
 * parser output to the server console. Most relevant tests are checked in
 * LobbyLogicTest.
 * 
 * @author benzumbrunn
 *
 */
public class ParserTest {

	private static String host;
	private static int port;
	private static Player testPlayer;
	private static Parser parser;

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

		createPlayer();

		parser = new Parser(testPlayer);

		System.out.println(PlayerManager.playerlist.contains(testPlayer));
		testPlayer.increaseKickCounter(5, "kick!");
		System.out.println(PlayerManager.playerlist.contains(testPlayer));
	}

	public static void createPlayer() {
		// create a player
		try {
			new Socket(host, port);
			Thread.sleep(1500);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		testPlayer = PlayerManager.playerlist.get(0);
		PlayerManager.newPlayerLogin(testPlayer, "testPlayer");
		testPlayer.playerSocket.setHeartBeatOff();
	}

	/**
	 * Test method for {@link server.lobby.Parser#parse(java.lang.String)}.
	 */
	@Test
	public void testParse() {

		try {
			// this should give an error to the server console
			parser.parse("notreallyavalidpacket");

			// this should be handled as an unknown packet
			parser.parse("ABCDE");

			// this should pass, but is wrongly formatted
			parser.parse("CWHIR");

			// this should pass
			parser.parse("LOBLR");

			/*
			 * Testing of very unsafe packets
			 */
			parser.parse("S");
			parser.parse(null);
			parser.parse(":::::");

			/*
			 * Testing LOBCR packets
			 */
			// this should pass
			parser.parse("LOBCR:0");
			// this should pass
			parser.parse("LOBCR:" + Byte.MAX_VALUE);
			// this should be caught
			int moreThanByte = Byte.MAX_VALUE + 1;
			parser.parse("LOBCR:" + moreThanByte);
			// this should be caught
			parser.parse("LOBCR: ");

		} catch (Exception e) {
			fail(e.toString());
		}
	}

	@AfterClass
	public static void afterClass() {
		try {
			// the tester should be able to see the outcome on the server
			// console after the tests
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
