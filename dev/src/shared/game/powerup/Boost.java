/**
 * 
 */
package shared.game.powerup;

import java.awt.Graphics;
import client.gui.Camera;
import shared.game.PowerRacerGame;
import shared.game.RaceTrack;

/**
 * Boost {@link Powerup}, which increases the car speed to a predetermined
 * value.
 * 
 * @author Florian
 *
 */
public class Boost extends Powerup {

	/**
	 * The Boost constructor which sets the relevant {@link PowerRacerGame}
	 * object.
	 * 
	 * @param game
	 *            the relevant game
	 */
	public Boost(PowerRacerGame game) {
		super(game, 40);
		powerupImage = RaceTrack.getPowerupImage(1);
	}

	/**
	 * Sets the users speed to the predetermined value.
	 */
	@Override
	public void activate() {
		game.setCarSpeed(game.getCarIndex(), 70);
	}

	/**
	 * Draws the Boost aimation to the screen.
	 */
	public void paintVFX(Graphics g) {
		g.drawImage(
				powerupImage,
				(Camera.RENDER_WIDTH - Camera.TILE_SIDE_LENGTH) / 2,
				Camera.RENDER_HEIGHT
						- ((Camera.RENDER_HEIGHT + Camera.TILE_SIDE_LENGTH) / timerMax)
						* timer - Camera.TILE_SIDE_LENGTH,
				Camera.TILE_SIDE_LENGTH, Camera.TILE_SIDE_LENGTH, null);
	}
}
