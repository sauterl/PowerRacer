package client.gui;

import java.util.concurrent.ConcurrentLinkedQueue;

import shared.game.PowerRacerGame;
import shared.game.RaceTrack;
import shared.game.powerup.Boost;

public class CameraTest {

	public static void main(String[] args) {
		ConcurrentLinkedQueue<String> dummyCommandQueue = new ConcurrentLinkedQueue<String>();
		PowerRacerGame game = new PowerRacerGame(4, RaceTrack.GET_RANDOM,
				new int[] { 0, 1, 3, 4 }, 0, dummyCommandQueue);
		game.setPlayerNames(new String[] { "Marco", "Beni", "Sim", "Florian" });
		Camera camera = new Camera(game, 1280, 720, null);

		try {
			Thread.sleep(1000);
			game.setCountdown(3);
			Thread.sleep(1000);
			game.setCountdown(2);
			Thread.sleep(1000);
			game.setCountdown(1);
			Thread.sleep(1000);
			game.setCountdown(0);
			game.setControl(true);
			Thread.sleep(3000);
			// game.setPause(true);
			// Thread.sleep(5000);
			// game.setPause(false);
			while (true) {
				if (!dummyCommandQueue.isEmpty()) {
					dummyCommandQueue.clear();
				}
				Thread.sleep(100);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// game.setScoreboard("Beni",11597, "Marco",12344,
		// "SimeonJackman",23323, "Florian",34456);
		// game.setComplete();
	}

}
