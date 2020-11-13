package shared.game.powerup;

import client.gui.Camera;
import shared.game.PowerRacerGame;
import shared.game.VisualRaceTrack;

/**
 * The {@link Powerup} spawning five {@link RocketCollidable}s when used.
 * 
 * @author Florian
 *
 */
public class RocketPowerup extends Powerup {

	public RocketPowerup(PowerRacerGame game) {
		super(game, 0);
		powerupImage = VisualRaceTrack.getPowerupImage(3);
	}

	/**
	 * Generates five {@link RocketCollidable}s in front of the user Car .
	 */
	@Override
	public void activate() {
		int carIndex = game.getCarIndex();
		double collidableRotation = game.getCarRotation(carIndex);
		double collidableX = game.getCarX(carIndex)
				+ ((double) Camera.TILE_SIDE_LENGTH)
				* Math.cos(collidableRotation);
		double collidableY = game.getCarY(carIndex)
				+ ((double) Camera.TILE_SIDE_LENGTH)
				* Math.sin(collidableRotation);
		int randomID = getTempID();
		int randomID2 = getTempID();
		int randomID3 = getTempID();
		int randomID4 = getTempID();
		int randomID5 = getTempID();

		game.addToCollidables(new RocketCollidable(collidableX, collidableY,
				collidableRotation, game, randomID));
		game.addToCollidables(new RocketCollidable(collidableX, collidableY,
				collidableRotation - Math.PI / 4, game, randomID2));
		game.addToCollidables(new RocketCollidable(collidableX, collidableY,
				collidableRotation - Math.PI / 4 / 2, game, randomID3));
		game.addToCollidables(new RocketCollidable(collidableX, collidableY,
				collidableRotation + Math.PI / 4, game, randomID4));
		game.addToCollidables(new RocketCollidable(collidableX, collidableY,
				collidableRotation + Math.PI / 4 / 2, game, randomID5));
		game.sendNewCollidablePacket(0, collidableX, collidableY,
				collidableRotation, randomID);
		game.sendNewCollidablePacket(0, collidableX, collidableY,
				collidableRotation - Math.PI / 4, randomID2);
		game.sendNewCollidablePacket(0, collidableX, collidableY,
				collidableRotation - Math.PI / 4 / 2, randomID3);
		game.sendNewCollidablePacket(0, collidableX, collidableY,
				collidableRotation + Math.PI / 4, randomID4);
		game.sendNewCollidablePacket(0, collidableX, collidableY,
				collidableRotation + Math.PI / 4 / 2, randomID5);

	}
}
