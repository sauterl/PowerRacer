package shared.game.powerup;

import java.util.Random;

import shared.game.PowerRacerGame;
import shared.game.VisualRaceTrack;

/**
 * Itembox, from which a player can collect {@link Powerup}s.
 * 
 * @author Florian
 *
 */
public class PowerupBox extends Collidable {

	static Random rand = new Random();

	public PowerupBox(double x, double y, int ID) {
		setPosition(x, y);
		setAutoRotate(Math.PI / 24);
		setRotation(rand.nextDouble() * 2 * Math.PI);
		setMoving(false);
		setID(ID);
		setImage(VisualRaceTrack.getPowerupImage(2));
	}

	/**
	 * If the Player currently has no {@link Powerup}, the player is given a
	 * random {@link Powerup} according to the generated probability.
	 */
	@Override
	public void activate(PowerRacerGame game) {
		if (game.powerupIsNull()) {
			game.setPowerup(getCorrespondingPowerup(rand.nextInt(12), game));
			setEnabled(false);
			game.sendDisabledPacket(getID());
		}
	}

	/**
	 * Gets the {@link Powerup} corresponding with the integer according to the
	 * probability.
	 * 
	 * @param index
	 *            the generated number
	 * @param game
	 *            the relevant game
	 * @return the new generated {@link Powerup}
	 */
	public static Powerup getCorrespondingPowerup(int index, PowerRacerGame game) {
		switch (index) {
			case 0:
				return new Boost(game);
			case 1:
			case 2:
			case 3:
			case 4:
				return new RocketPowerup(game);
			case 5:
			case 6:
			case 7:
			case 8:
				return new OilSlickPowerup(game);
			case 9:
			case 10:
				return new Lightning(game, 0);
			case 11:
				return new Inversion(game, 0);
			default:
				return new Boost(game);
		}
	}
}
