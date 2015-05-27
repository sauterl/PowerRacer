package shared.game;

import client.gui.Camera;

/**
 * This class generates lines used by the bot-cars to find out which way to go.
 * It returns how many steps it could "walk" with its rotation value
 * 
 * @author marco
 *
 */
public class MagicLine {
	private double rot;
	private int steps;

	public MagicLine(double rot) {
		this.rot = rot;
		this.steps = 0;
	}

	public int calculateSteps(PowerRacerGame game, Car car) {
		for (int i = 1; i <= 100000; i++) {
			try {
				if (game.track
						.getfrictionCoefficient(
								(int) ((car.getX() + i * Math.cos(this.rot)) / Camera.TILE_SIDE_LENGTH),
								(int) ((car.getY() + i * Math.sin(this.rot)) / Camera.TILE_SIDE_LENGTH)) <= 0.05) {
					this.steps += 1;
				} else {
					return this.steps;
				}
			} catch (IndexOutOfBoundsException e) {
				return this.steps;
			}
		}
		return this.steps;

	}

	public double getRot() {
		return this.rot;
	}

}
