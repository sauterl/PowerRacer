package shared.game.powerup;

import client.gui.Camera;
import shared.game.PowerRacerGame;
import shared.game.VisualRaceTrack;

/**
 * Oil Slick {@link Powerup}, which spawns an {@link OilSlickCollidable
 * }.
 * 
 * @author Florian
 *
 */
public class OilSlickPowerup extends Powerup {

	public OilSlickPowerup(PowerRacerGame game) {
		super(game, 0);
		powerupImage = VisualRaceTrack.getPowerupImage(5);
	}

	/**
	 * Adds a new {@link OilSlickCollidable} to the game, right behind the user
	 * Car.
	 */
	@Override
	public void activate() {
		int carIndex = game.getCarIndex();
		double collidableRotation = game.getCarRotation(carIndex);
		double collidableX = game.getCarX(carIndex) - 1.1
				* ((double) Camera.TILE_SIDE_LENGTH)
				* Math.cos(collidableRotation);
		double collidableY = game.getCarY(carIndex) - 1.1
				* ((double) Camera.TILE_SIDE_LENGTH)
				* Math.sin(collidableRotation);

		int randomID = getTempID();
		game.addToCollidables(new OilSlickCollidable(collidableX, collidableY,
				collidableRotation, game, randomID));
		game.sendNewCollidablePacket(1, collidableX, collidableY,
				collidableRotation, randomID);
	}
}
