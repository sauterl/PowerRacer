package shared.game.powerup;

import client.gui.Camera;
import shared.game.PowerRacerGame;
import shared.game.RaceTrack;

/**
 * Rocket {@link Collidable} reducing a colliding Car's speed to 0.
 * 
 * @author Florian
 *
 */
public class RocketCollidable extends Collidable {
	int life;

	public RocketCollidable(double x, double y, double rotation,
			PowerRacerGame game, int ID) {
		setGame(game);
		setPosition(x, y);
		setRotation(rotation);
		setMoving(true);
		setSpeed(Camera.TILE_SIDE_LENGTH / 2);
		setID(ID);
		setImage(RaceTrack.getPowerupImage(4));
	}

	/**
	 * Reduces the colliding Car's speed to 0.
	 */
	@Override
	public void activate(PowerRacerGame game) {
		game.setCarSpeed(game.getCarIndex(), 0);
		game.removeFromCollidables(getID());
		game.sendRemovePacket(getID());
	}

	/**
	 * Overwrites the general {@link Collidable} method to include a life span
	 * for the rockets, so they don't exist forever and use up unnecessary
	 * computational power.
	 */
	@Override
	public void updatePosAndRot() {
		if (life > 200) {
			getGame().removeFromCollidables(getID());
			getGame().sendRemoveInformation(getID());
			return;
		}
		life++;
		super.updatePosAndRot();
	}
}
