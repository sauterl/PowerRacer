package shared.game.powerup;

import java.awt.Color;
import java.awt.Graphics;

import client.gui.Camera;
import shared.game.PowerRacerGame;
import shared.game.VisualRaceTrack;

/**
 * Inversion {@link Powerup}, which inverts game controls for other players.
 * 
 * @author Florian
 *
 */
public class Inversion extends Powerup {
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
	public Inversion(PowerRacerGame game, int timerMax) {
		super(game, timerMax);
		powerupImage = VisualRaceTrack.getPowerupImage(8);
	}

	/**
	 * Sends the effect packet with relevant identifier.
	 */
	@Override
	public void activate() {
		game.sendNewEffectPacket(1);
	}

	/**
	 * Paints the screen slightly purple and inverts the inputs if timerMax
	 * isn't 0.
	 */
	@Override
	public void paintVFX(Graphics g) {
		if (timerMax != 0) {
			// Invert Controls
			if (timer == 0) {
				game.invertInputs(timerMax);
			}
			// Draw purple fog
			int opacity = (int) (100 * ((timerMax - (double) timer) / ((double) timerMax + 50)) + 100);
			g.setColor(new Color(
					147,
					112,
					219,
					opacity));
			g.fillRect(0, 0, Camera.RENDER_WIDTH, Camera.RENDER_HEIGHT);
		}

	}
}
