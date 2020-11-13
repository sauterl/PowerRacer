package shared.game.powerup;

import java.awt.Color;
import java.awt.Graphics;

import client.gui.Camera;
import shared.game.PowerRacerGame;
import shared.game.VisualRaceTrack;

/**
 * Lightning {@link Powerup} reducing speed by half and reducing visibility.
 * 
 * @author Florian
 *
 */
public class Lightning extends Powerup {

	/**
	 * The constructor setting whether the {@link Powerup} is applicable for
	 * this player or not. If the timerMax is set to 0, this object will not
	 * affect the player (Set to zero when collected from {@link PowerupBox}).
	 * 
	 * @param game
	 *            the relevant game object
	 * @param timerMax
	 *            the effect duration
	 */
	public Lightning(PowerRacerGame game, int timerMax) {
		super(game, timerMax);
		powerupImage = VisualRaceTrack.getPowerupImage(7);
	}

	/**
	 * Sends the effect packet with relevant identifier.
	 */
	@Override
	public void activate() {
		game.sendNewEffectPacket(0);
	}

	/**
	 * Paints the screen whiteish yellow and reduces speed by half if timerMax
	 * isn't 0.
	 */
	public void paintVFX(Graphics g) {
		if (timerMax != 0) {
			if (timer == 0) {
				// Slow down
				game.setCarSpeed(game.getCarIndex(), game.getCarSpeed() / 2);
			}
			// Draw Flash
			g.setColor(new Color(
					255,
					255,
					230,
					Math.min(
							230,
							(int) (460 * ((double) (timerMax - (double) timer) / (double) timerMax)))));
			g.fillRect(0, 0, Camera.RENDER_WIDTH, Camera.RENDER_HEIGHT);
		}

	}
}
