package shared.game.powerup;

import java.util.Random;

import shared.game.PowerRacerGame;
import shared.game.RaceTrack;

/**
 * Collidable setting the speed of a colliding player to 0.
 * 
 * @author Florian
 *
 */
public class OilSlickCollidable extends Collidable {
	/**
	 * Constructor with spawning position information.
	 * 
	 * @param x
	 *            the horizontal position
	 * @param y
	 *            the vertical position
	 * @param rotation
	 *            the OilSlick's drawn rotation
	 * @param game
	 *            the relevant game object
	 * @param ID
	 *            this {@link Collidable}'s ID
	 */
	public OilSlickCollidable(double x, double y, double rotation,
			PowerRacerGame game, int ID) {
		setGame(game);
		setPosition(x, y);
		setRotation(new Random().nextDouble() * 2 * Math.PI);
		setMoving(true);
		setSpeed(0);
		setID(ID);
		setImage(RaceTrack.getPowerupImage(6));
	}

	/**
	 * Sets the Car's speed to 0 and removes this {@link Collidable}.
	 */
	@Override
	public void activate(PowerRacerGame game) {
		game.setCarSpeed(game.getCarIndex(), 0);
		game.removeFromCollidables(getID());
		game.sendRemovePacket(getID());
	}
}
