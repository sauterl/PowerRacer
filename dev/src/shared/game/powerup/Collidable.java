package shared.game.powerup;

import java.awt.Image;

import shared.game.PowerRacerGame;

/**
 * Abstract class defining all necessary features of a collidable game object.
 * Extend to create new object for Cars to collide with.
 * 
 * @author Florian
 */
public abstract class Collidable {

	private Image collidableImage;
	private int ID, disabledTimer, timerMax = 80;
	private double x;
	private double y;
	private double speed;
	private boolean moving, enabled = true;
	private double rotation, autoRotate;
	private PowerRacerGame game;

	public PowerRacerGame getGame() {
		return game;
	}

	public void setGame(PowerRacerGame game) {
		this.game = game;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public double getX() {
		return this.x;
	}

	public double getY() {
		return this.y;
	}

	public int getID() {
		return this.ID;
	}

	public void setID(int ID) {
		this.ID = ID;
	}

	/**
	 * Method stub to be completed differently for each extending object.
	 * 
	 * @param game
	 *            the game on which it will be activated
	 */
	public void activate(PowerRacerGame game) {}

	public void setPosition(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public double getSpeed() {
		return this.speed;
	}

	public boolean getMoving() {
		return this.moving;
	}

	public void setMoving(boolean moving) {
		this.moving = moving;
	}

	public void setSpeed(double speed) {
		this.speed = speed;
	}

	public void setRotation(double rotation) {
		this.rotation = rotation;
	}

	protected void setAutoRotate(double autoRotate) {
		this.autoRotate = autoRotate;
	}

	public double getRotation() {
		return this.rotation;
	}

	public void setImage(Image i) {
		collidableImage = i;
	}

	public Image getImage() {
		return collidableImage;
	}

	/**
	 * Updates this collidable's position and rotation.
	 */
	public void updatePosAndRot() {
		// update rotation
		rotation += autoRotate;

		// update position
		if (getMoving()) {
			setPosition(getX() + getSpeed() * Math.cos(getRotation()), getY()
					+ getSpeed() * Math.sin(getRotation()));
		}
	}

	/**
	 * Increases the disabled timer and checks whether the collidable should
	 * still be disabled.
	 */
	public void updateDisabled() {
		disabledTimer++;
		if (disabledTimer > timerMax) {
			disabledTimer = 0;
			setEnabled(true);
		}
	}
}
