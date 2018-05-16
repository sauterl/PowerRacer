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
//			game.setScoreboard(
//					new int[] { 11597, 12344, 23323, 34456, 35738, 36127, 40000,
//							99999 },
//					new String[] { "Beni", "Marco", "SimeonJackman", "Florian",
//							"Cake", "Test", "Just4Kicks", "Haxor" });
//			game.setComplete();
			while (true) {
				if (!dummyCommandQueue.isEmpty()) {
					dummyCommandQueue.clear();
				}
				Thread.sleep(100);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
