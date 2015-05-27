/**
 * 
 */
package shared.game.powerup;

import java.awt.Graphics;
import java.awt.Image;
import java.util.Random;

import shared.game.PowerRacerGame;

/**
 * Abstract class defining all necessary features for a Powerup object.
 * 
 * @author Florian
 *
 */
public abstract class Powerup {
	protected int timer;
	protected final int timerMax;
	Image powerupImage;
	protected PowerRacerGame game;

	public Powerup(PowerRacerGame game, int timerMax) {
		this.game = game;
		this.timerMax = timerMax;
	}

	public Image getPowerupImage() {
		return powerupImage;
	}

	/**
	 * An empty method for implementation, activated when a Powerup is used.
	 */
	public void activate() {}

	/**
	 * An empty method for implementation, activated when a Powerup is drawn on
	 * screen.
	 * 
	 * @param g
	 *            the graphics this animation will be drawn on
	 */
	public void paintVFX(Graphics g) {}

	/**
	 * Checks whether the animation is done and increases the timer in case it
	 * isn't.
	 * 
	 * @return if the animation is done
	 */
	public boolean paintIsDone() {
		if (timer >= timerMax) {
			timer = 0;
			return true;
		} else {
			timer++;
			return false;
		}
	}

	/**
	 * Generates a randomized temporary ID while avoiding collisions.
	 * 
	 * @return a new temporary ID
	 */
	public int getTempID() {
		Random rand = new Random();
		int randomID;
		do {
			randomID = rand.nextInt(200) + 500;
		} while (game.collidableExists(randomID) && !game.lockTempID(randomID));
		return randomID;
	}
}
