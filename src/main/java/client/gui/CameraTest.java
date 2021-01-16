package client.gui;

import shared.game.PowerRacerGame;
import shared.game.RaceTrack;

import java.util.concurrent.ConcurrentLinkedQueue;

public class CameraTest {

	public static void main(String[] args) {
		ConcurrentLinkedQueue<String> dummyCommandQueue = new ConcurrentLinkedQueue<>();
		PowerRacerGame game = new PowerRacerGame(4, RaceTrack.GET_RANDOM, new int[]{0, 1, 3, 4}, 0, dummyCommandQueue);
		game.setPlayerNames(new String[]{"Marco", "Beni", "Sim", "Florian"});
		new Camera(game, 1280, 720, null);

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
