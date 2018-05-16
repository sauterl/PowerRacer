package shared.game;

import java.awt.Image;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

import server.game.powerup.PowerupManager;
import shared.game.powerup.Collidable;
import shared.game.powerup.OilSlickCollidable;
import shared.game.powerup.Powerup;
import shared.game.powerup.PowerupBox;
import shared.game.powerup.RocketCollidable;
import client.gui.Camera;

/**
 * The Game object responsible for updating information and sending it to the
 * server.
 * 
 * @author Florian
 *
 */
public class PowerRacerGame {

	Car[] cars;
	public RaceTrack track;
	int carIndex, countdown = -40;
	ConcurrentLinkedQueue<String> commandQueue;
	private boolean control, complete, botOn, pause;
	private int invertInputs;
	int numberOfPlayers;
	ArrayList<String> players = new ArrayList<String>();
	ArrayList<String> finishedPlayers = new ArrayList<String>();
	int[] times;
	private byte trackIdentifier;
	String[] playerNames;
	private PowerupManager PowerupManager;

	public static boolean[] tempID = new boolean[200];

	private ConcurrentLinkedQueue<Collidable> collidables = new ConcurrentLinkedQueue<Collidable>();

	/**
	 * Constructs a new game from the given parameters.
	 * 
	 * @param numberOfPlayers
	 *            the number of players in this game
	 * @param trackIdentifier
	 *            the identifier required to load the correct track
	 * @param carTypes
	 *            the types of cars participating in this race
	 * @param carIndex
	 *            the car index of this players car
	 * @param commandQueue
	 *            the command queue needed to send packets to the server
	 */
	public PowerRacerGame(int numberOfPlayers, byte trackIdentifier,
			int[] carTypes, int carIndex,
			ConcurrentLinkedQueue<String> commandQueue) {
		this.numberOfPlayers = numberOfPlayers;
		this.trackIdentifier = trackIdentifier;
		this.carIndex = carIndex;
		this.commandQueue = commandQueue;

		track = new RaceTrack(trackIdentifier);

		cars = new Car[numberOfPlayers];
		for (int i = 0; i < cars.length; i++) {
			/*
			 * Creates cars from car file (x-carpos,y-carpos,cartype)
			 */
			cars[i] = new Car(setStartingPositionX(i), setStartingPositionY(i),
					carTypes[i], this); // change last argument to change
										// cartypes
		}
		for (int i = 0; i < track.numberOfItemBoxes(); i++) {
			addToCollidables(new PowerupBox(
					(track.getItemBoxPositionX(i) + 0.5)
							* Camera.TILE_SIDE_LENGTH,
					(track.getItemBoxPositionY(i) + 0.5)
							* Camera.TILE_SIDE_LENGTH, i));
		}
	}

	public void setPause(boolean pause) {
		control = !pause;
		this.pause = pause;
	}

	public boolean getPause() {
		return pause;
	}

	/**
	 * Adds the given Collidable to the game.
	 * 
	 * @param collidable
	 *            the Collidable being added.
	 */
	public void addToCollidables(Collidable collidable) {
		collidables.add(collidable);
	}

	/**
	 * Removes the Collidable with the id from the list. If there is no
	 * Collidable with this id, does nothing.
	 * 
	 * @param id
	 *            the id of the Collidable to be removed
	 * @return whether a Collidable was removde or not
	 */
	public boolean removeFromCollidables(int id) {
		if (id - 500 < 200 && id - 500 >= 0) {
			freeTempID(id);
		}
		for (Collidable c : collidables) {
			if (c.getID() == id) {
				collidables.remove(c);
				return true;
			}
		}
		return false;
	}

	/**
	 * To find out if a Collidable exists with a certain id.
	 * 
	 * @param id
	 *            the checked Collidables id
	 * @return if the Collidable exists
	 */
	public boolean collidableExists(int id) {
		for (Collidable c : collidables) {
			if (c.getID() == id) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Locks the temp id from being generated again.
	 * 
	 * @param tempID
	 *            the id to be locked
	 * @return if the id was unlocked before
	 */
	public boolean lockTempID(int tempID) {
		tempID -= 500;
		if (tempID >= 0 && tempID < 200) {
			if (PowerRacerGame.tempID[tempID]) {
				return false;
			} else {
				PowerRacerGame.tempID[tempID] = true;
				return true;
			}
		}
		return false;
	}

	/**
	 * Unlocks the tempID.
	 * 
	 * @param tempID
	 *            the id to be unlocked
	 */
	public void freeTempID(int tempID) {
		tempID -= 500;
		if (tempID >= 0 && tempID < 200) {
			PowerRacerGame.tempID[tempID] = false;
		}
	}

	/**
	 * Replaces a Collidable's ID. If Collidable with oldID does not exist, does
	 * nothing.
	 * 
	 * @param oldID
	 *            the id before
	 * @param newID
	 *            the id after
	 * @return if a Collidable was found with oldID
	 */
	public boolean replaceCollidableID(int oldID, int newID) {
		if (oldID - 500 < 200 && oldID - 500 >= 0) {
			freeTempID(oldID);
		}
		for (Collidable c : collidables) {
			if (c.getID() == oldID) {
				c.setID(newID);
				return true;
			}
		}
		return false;
	}

	public ConcurrentLinkedQueue<Collidable> getCollidables() {
		return collidables;
	}

	/*
	 * Set x startingposition according to trackproperties
	 */
	private double setStartingPositionX(int carIndex) {
		return (track.getStartingPositionX(carIndex) + 0.5)
				* Camera.TILE_SIDE_LENGTH;
	}

	/*
	 * Set y startingposition according to trackproperties
	 */
	private double setStartingPositionY(int carIndex) {
		return (track.getStartingPositionY(carIndex) + 0.5)
				* Camera.TILE_SIDE_LENGTH;
	}

	public Image getCarImage(int carNum) {
		int num = (carNum % 4) + (cars[carNum].getImageNum()) * 4; // 4 is the number
															 // of different
															 // colors available
		return track.getCarImage(num);
	}

	public double getCarRotation(int carIndex) {
		return cars[carIndex].getRotation();
	}

	public double getCarX(int carIndex) {
		return cars[carIndex].getX();
	}

	public double getCarY(int carIndex) {
		return cars[carIndex].getY();
	}

	public Image getTile(int x, int y) {
		return track.getTile(x, y);
	}

	public int getNumberOfPlayers() {
		return numberOfPlayers;
	}

	public Image getDefaultTile() {
		return track.getDefaultTile();
	}

	public int getTrackWidth() {
		return track.getTrackWidth();
	}

	public int getTrackHeight() {
		return track.getTrackHeight();
	}

	public int getCarCheckpoint(int carIndex) {
		return cars[carIndex].getCheckpoint();
	}

	public int getCarLap(int carIndex) {
		return cars[carIndex].getLap();
	}

	public int getCarIndex() {
		return carIndex;
	}

	public boolean getControl() {
		return control;
	}

	public int getCountdown() {
		return countdown;
	}

	public boolean getComplete() {
		return complete;
	}

	public void setCarRotation(int carIndex, double rotation) {
		cars[carIndex].setRotation(rotation);
	}

	public void setCarSpeed(int carIndex, double speed) {
		cars[carIndex].setSpeed(speed);
	}

	public void setCarInputUp(int carIndex, boolean upIsPressed) {
		cars[carIndex].setUp(upIsPressed);
	}

	public void setCarInputDown(int carIndex, boolean downIsPressed) {
		cars[carIndex].setDown(downIsPressed);
	}

	public void setCarInputLeft(int carIndex, boolean leftIsPressed) {
		cars[carIndex].setLeft(leftIsPressed);
	}

	public void setCarInputRight(int carIndex, boolean rightIsPressed) {
		cars[carIndex].setRight(rightIsPressed);
	}

	public void setCarPosition(int carIndex, double x, double y) {
		cars[carIndex].setPosition(x, y);
	}

	public void setCountdown(int countdown) {
		this.countdown = countdown;
	}

	public void setControl(boolean control) {
		this.control = control;
	}

	public void setComplete() {
		complete = true;
	}

	public ArrayList<String> getFinishedPlayers() {
		return finishedPlayers;
	}

	public void update() {
		if (!pause) {
			updateGame();
		}
	}

	/**
	 * Updates the game and all Cars and Collidables. The Core game logic.
	 */
	public void updateGame() {

		for (int i = 0; i < cars.length; i++) {
			double frictionCoefficient;
			int x = (int) cars[i].getX() / Camera.TILE_SIDE_LENGTH;
			int y = (int) cars[i].getY() / Camera.TILE_SIDE_LENGTH;
			try {
				frictionCoefficient = track.getfrictionCoefficient(x, y);
			} catch (IndexOutOfBoundsException e) {
				frictionCoefficient = track.getDefaultTileFriction();
			}
			if (invertInputs > 0 && carIndex == i) {
				// boolean downIsPressed = cars[carIndex].getUpIsPressed();
				// boolean upIsPressed = cars[carIndex].getDownIsPressed();
				boolean rightIsPressed = cars[carIndex].getLeftIsPressed();
				boolean leftIsPressed = cars[carIndex].getRightIsPressed();
				// cars[carIndex].setUp(upIsPressed);
				// cars[carIndex].setDown(downIsPressed);
				cars[carIndex].setLeft(leftIsPressed);
				cars[carIndex].setRight(rightIsPressed);
				cars[i].updateSpeedAndRotation(frictionCoefficient);
				// cars[carIndex].setUp(downIsPressed);
				// cars[carIndex].setDown(upIsPressed);
				cars[carIndex].setLeft(rightIsPressed);
				cars[carIndex].setRight(leftIsPressed);
				invertInputs--;
			} else {
				cars[i].updateSpeedAndRotation(frictionCoefficient);
			}

			// TODO: realistic collision detection
			if (i == carIndex && !complete) {
				if (cars[i].getSpaceIsPressed()) {
					activatePowerup();
					cars[i].setSpace(false);
				}
				for (Collidable c : collidables) {
					if (c.isEnabled()) {
						c.updatePosAndRot();
						if (collide(i, c)) {
							c.activate(this);
						}
					} else {
						c.updateDisabled();
					}
				}
				try {
					if (track.getCheckpointNumber(x, y) == cars[i]
							.getCheckpoint() + 1) {
						cars[i].increaseCheckpoint();
					} else
						if (track.getCheckpointNumber(x, y) == 99
								&& cars[i].getCheckpoint() == track
										.getMaxCheckpoint()) {
							cars[i].setCheckpoint(0);
							cars[i].increaseLap();
							if (cars[i].getLap() == track.getMaxLap()) {
								// System.out.println("Yay, you did it!");
								commandQueue.add("GFINI");
								control = false;
								botOn = true;
								cars[i].setUp(false);
								cars[i].setDown(false);
								cars[i].setRight(false);
								cars[i].setLeft(false);
							}
						}
				} catch (IndexOutOfBoundsException e) {
					// Do nothing
				}
			}
			cars[i].setPosition(
					cars[i].getX() + cars[i].getSpeed()
							* Math.cos(cars[i].getRotation()),
					cars[i].getY() + cars[i].getSpeed()
							* Math.sin(cars[i].getRotation()));

			if (i == carIndex && !complete) {
				/*
				 * Send Input to server
				 */
				commandQueue.add("GINPI" + ":"
						+ Boolean.toString(cars[i].getUpIsPressed()) + ":"
						+ Boolean.toString(cars[i].getDownIsPressed()) + ":"
						+ Boolean.toString(cars[i].getLeftIsPressed()) + ":"
						+ Boolean.toString(cars[i].getRightIsPressed()) + ":"
						+ cars[i].getX() + ":" + cars[i].getY() + ":"
						+ cars[i].getSpeed() + ":" + cars[i].getRotation());
			}
		}
	}

	/**
	 * Checks whether a car collides with a certain collidable.
	 * 
	 * @param carIndex
	 *            the car's index
	 * @param c
	 *            the collidable
	 * @return if a collision occurs
	 */
	private boolean collide(int carIndex, Collidable c) {
		return Math.sqrt(Math.pow(cars[carIndex].getX() - c.getX(), 2)
				+ Math.pow(cars[carIndex].getY() - c.getY(), 2)) < Camera.TILE_SIDE_LENGTH;
	}

	public void setScoreboard(int[] times, String[] names) {
		this.times = times;
		for (String name : names) {
			finishedPlayers.add(name);
		}
	}

	public String[] getScoreboard() {
		return finishedPlayers.toArray(new String[0]);
	}

	public int[] getTimes() {
		return times;
	}

	public boolean getBotOn() {
		return botOn;
	}

	public String getTrackName() {
		return RaceTrack.getTrackName(trackIdentifier);
	}

	public void setNumberOfPlayers(int numberOfPlayers) {
		this.numberOfPlayers = numberOfPlayers;
	}

	public void setPlayerNames(String[] playerNames) {
		this.playerNames = playerNames;
	}

	public String[] getPlayerNames() {
		return playerNames;
	}

	public Image getPowerupImage() {
		try {
			return cars[carIndex].getPowerup().getPowerupImage();
		} catch (NullPointerException e) {
			return RaceTrack.getPowerupImage(0);
		}
	}

	public void setPowerup(Powerup powerup) {
		cars[carIndex].setPowerup(powerup);
	}

	public boolean powerupIsNull() {
		try {
			return cars[carIndex].getPowerup() == null;
		} catch (NullPointerException e) {
			return true;
		}
	}

	public void activatePowerup() {
		try {
			cars[carIndex].getPowerup().activate();
			cars[carIndex].setPowerup(null);
		} catch (NullPointerException e) {

		}
	}

	public void setPowerupManager(PowerupManager powerupManager) {
		this.PowerupManager = powerupManager;
	}

	public PowerupManager getPowerupManager() {
		return this.PowerupManager;
	}

	public boolean getTransparent(int x, int y) {
		return track.getTransparent(x, y);
	}

	public void setSpace(boolean space) {
		cars[carIndex].setSpace(space);
	}

	public Powerup getPowerup() {
		return cars[carIndex].getPowerup();
	}

	public ArrayList<String> getPlayers() {
		return players;
	}

	public void setPlayers(ArrayList<String> players) {
		this.players = players;
	}

	public void sendDisabledPacket(int id) {
		commandQueue.add("GCLDI:" + id);
	}

	public void setDisabled(int id) {
		for (Collidable c : collidables) {
			if (c.getID() == id) {
				c.setEnabled(false);
				return;
			}
		}
	}

	public void sendNewCollidablePacket(int collidableIdentifier, double x,
			double y, double rotation, int randomID) {
		commandQueue.add("GCLCR:" + collidableIdentifier + ":" + x + ":" + y
				+ ":" + rotation + ":" + randomID);
	}

	public boolean addCollidableFromPacket(int collidableIdentifier, double x,
			double y, double rotation, int id) {
		Collidable collidable = null;
		switch (collidableIdentifier) {
			case 0:
				collidable = new RocketCollidable(x, y, rotation, this, id);
				break;
			case 1:
				collidable = new OilSlickCollidable(x, y, rotation, this, id);
				break;
			default:
				break;
		}
		if (collidable != null) {
			addToCollidables(collidable);
			return true;
		}
		return false;
	}

	public void sendRemovePacket(int id) {
		commandQueue.add("GCLRR:" + id);
	}

	public void sendRemoveInformation(int id) {
		commandQueue.add("GCLRI:" + id);
	}

	public void sendNewEffectPacket(int effectIndex) {
		commandQueue.add("GCEFI:" + effectIndex);
	}

	public double getCarSpeed() {
		return cars[carIndex].getSpeed();
	}

	public void invertInputs(int invertInputs) {
		this.invertInputs = invertInputs;
	}

	public int getMaxCheckpoint() {
		return track.getMaxCheckpoint();
	}

	public int getMaxLap() {
		return track.getMaxLap();
	}
}
