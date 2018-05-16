package shared.game;

import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;

import client.gui.Camera;
import client.gui.RandomMapGenerator;
import shared.game.powerup.Collidable;
import shared.game.powerup.Powerup;

/**
 * The Class keeping track of the games playing field.
 * 
 * @author Florian
 *
 */
public class RaceTrack {
	private static String trackName;
	byte[][] tileMap;
	private int diffCars = 5; // how many car types exist
	Image[] tiles, carImages = new Image[4 * diffCars];
	static Image[] powerupImages;
	Image checkpointFeedback;
	boolean[] solid, transparent;
	int[] checkpointNumber;
	double[] frictionCoefficients;
	public int[][] startingPositions, itemBoxPositions = {};
	int defaultTile;
	int maxCheckpoint;
	int maxLap;
	public static String randomString;

	public static final byte RACETRACK_SMALL = 0, RACETRACK_GRASS = 1,
			RACETRACK_SAND = 2, RACETRACK_ICE = 3, RANDOM = 4, GET_RANDOM = 5;
	public static final String[] TRACK_NAMES = { "Small track", "Grass track",
			"Sand track", "Ice track", "Random" };

	public static int numberOfTracks() {
		return TRACK_NAMES.length;
	}

	/**
	 * Generates a track object from the track identifier.
	 * 
	 * @param track
	 *            the track identifier corresponding with the chosen track
	 */
	public RaceTrack(byte track) {
		loadTiles();
		loadCarImages();
		loadOtherImages();

		switch (track) {
			case RACETRACK_SMALL:
				loadSmall();
				break;
			case RACETRACK_GRASS:
				loadGrass();
				break;
			case RACETRACK_SAND:
				loadSand();
				break;
			case RACETRACK_ICE:
				loadIce();
				break;
			case RANDOM:
				loadGetRandom();
				break;
			case GET_RANDOM:
				loadRandom();
				break;
			default:
				System.out
						.println("ERROR: Track not found!\nLoading Small Track instead...");
				loadSmall();
				break;

		}
	}

	public static ImageIcon[] getCarImageArray() {
		ImageIcon[] carII = new ImageIcon[5];
		carII[0] = scaleImageIcon(new ImageIcon(
				RaceTrack.class.getResource("/images/car1Red.png")));
		carII[1] = scaleImageIcon(new ImageIcon(
				RaceTrack.class.getResource("/images/car2Blue.png")));
		carII[2] = scaleImageIcon(new ImageIcon(
				RaceTrack.class
						.getResource("/images/car3Green.png")));
		carII[3] = scaleImageIcon(new ImageIcon(
				RaceTrack.class
						.getResource("/images/car4Yellow.png")));
		carII[4] = scaleImageIcon(new ImageIcon(
				RaceTrack.class.getResource("/images/car5Red.png")));
		return carII;
	}

	private static ImageIcon scaleImageIcon(ImageIcon i) {
		Image img = i.getImage();
		BufferedImage bi = new BufferedImage(Camera.TILE_SIDE_LENGTH,
				Camera.TILE_SIDE_LENGTH, BufferedImage.TYPE_INT_ARGB);
		bi.getGraphics().drawImage(img, 0, 0, Camera.TILE_SIDE_LENGTH,
				Camera.TILE_SIDE_LENGTH, null);
		return new ImageIcon(bi);
	}

	/**
	 * Loads non-car and non-tile images. This includes images for
	 * {@link Collidable}s and {@link Powerup}s.
	 */
	private void loadOtherImages() {
		ImageIcon fetch;
		fetch = new ImageIcon(getClass().getResource(
				"/images/checkpointFeedback.png"));
		checkpointFeedback = fetch.getImage();

		// load Powerup Images
		String[] powerupImageNames = { "NoPowerup", "Boost", "itemBox",
				"Rocket", "RocketCollidable", "OilSlick", "OilSlickCollidable",
				"Lightning", "Inversion" };
		powerupImages = new Image[powerupImageNames.length];

		for (int i = 0; i < powerupImages.length; i++) {
			fetch = new ImageIcon(getClass().getResource(
					"/images/" + powerupImageNames[i] + ".png"));
			powerupImages[i] = fetch.getImage();
		}
	}

	/**
	 * Loads the {@link Car} images.
	 */
	private void loadCarImages() {
		ImageIcon fetch;
		// TODO: add code that scans the images dir to create variable lengths
		// of arrays
		String[] carColors = { "Red", "Blue", "Green", "Yellow" };
		int length = carColors.length;
		for (int j = 1; j <= diffCars; j++) { // j is the amount of different
											  // car types
			// existent
			for (int i = 0; i < carColors.length; i++) {
				fetch = new ImageIcon(getClass().getResource(
						"/images/car" + j + carColors[i] + ".png"));
				/*
				 * Store the car images in the rigth order first sortet by
				 * cartype than by carcolor example: 1: type1 color1 2: type1
				 * color2 3: type1 color3 4: type1 color4 5: type2 color1 et
				 * cetera
				 */
				carImages[(j - 1) * length + i] = fetch.getImage();
			}
		}
	}

	/**
	 * Loads the tile images and sets their individual statistics.
	 */
	private void loadTiles() {
		ImageIcon fetch;
		Tile[] defineTiles = { new Tile("grass", false, 0, 0.1, false),
				new Tile("asphalt", false, 0, 0.05, false),
				new Tile("sand", false, 0, 0.15, false),
				new Tile("snow", false, 0, 0.125, false),
				new Tile("checkpointOne", false, 1, 0.05, false),
				new Tile("checkpointTwo", false, 2, 0.05, false),
				new Tile("checkpointThree", false, 3, 0.05, false),
				new Tile("finishLine", false, 99, 0.05, false),
				new Tile("checkpointFour", false, 4, 0.05, false),
				new Tile("checkpointFive", false, 5, 0.05, false),
				new Tile("checkpointSix", false, 6, 0.05, false),
				new Tile("checkpointSeven", false, 7, 0.05, false),
				new Tile("checkpointEight", false, 8, 0.05, false),
				new Tile("checkpointNine", false, 9, 0.05, false),
				new Tile("checkpointTen", false, 10, 0.05, false),
				new Tile("asphaltUp", false, 0, 0.05, false),
				new Tile("asphaltDown", false, 0, 0.05, false),
				new Tile("asphaltLeft", false, 0, 0.05, false),
				new Tile("asphaltRight", false, 0, 0.05, false),
				new Tile("asphaltUpRight", false, 0, 0.05, true),
				new Tile("asphaltUpLeft", false, 0, 0.05, true),
				new Tile("asphaltDownRight", false, 0, 0.05, true),
				new Tile("asphaltDownLeft", false, 0, 0.05, true),
				new Tile("ice", false, 0, 0.125, false),
				new Tile("asphalticeOne", false, 0, 0.055, true),
				new Tile("asphalticeTwo", false, 0, 0.055, true),
				new Tile("asphalticeThree", false, 0, 0.055, true),
				new Tile("asphalticeFour", false, 0, 0.055, true),
				new Tile("BoostTile", false, 0, -0.05, false) };

		tiles = new Image[defineTiles.length];
		solid = new boolean[defineTiles.length];
		checkpointNumber = new int[defineTiles.length];
		frictionCoefficients = new double[defineTiles.length];
		transparent = new boolean[defineTiles.length];
		for (int i = 0; i < defineTiles.length; i++) {
			fetch = new ImageIcon(getClass().getResource(
					"/images/" + defineTiles[i].getFileName()
							+ ".png"));
			tiles[i] = fetch.getImage();
			solid[i] = defineTiles[i].getSolid();
			checkpointNumber[i] = defineTiles[i].getCheckpointNumber();
			frictionCoefficients[i] = defineTiles[i].getFrictionCoefficient();
			transparent[i] = defineTiles[i].getTransparent();
		}
	}

	private void loadIce() {
		tileMap = PresetTracks.RACETRACK_ICE;
		startingPositions = new int[][] { { 22, 3 }, { 21, 4 }, { 22, 5 },
				{ 21, 6 }, { 21, 3 }, { 22, 4 }, { 21, 5 },
				{ 22, 6 } };
		itemBoxPositions = new int[][] { { 45, 7 }, { 48, 7 }, { 36, 16 },
				{ 36, 18 }, { 52, 28 }, { 52, 25 }, { 28, 48 }, { 28, 49 },
				{ 28, 50 }, { 32, 58 }, { 16, 55 }, { 5, 30 }, { 6, 30 },
				{ 7, 30 }, { 8, 30 } };
		defaultTile = 23;
		maxCheckpoint = 9;
		maxLap = 2;
	}

	private void loadSand() {
		tileMap = PresetTracks.RACETRACK_SAND;
		startingPositions = new int[][] { { 28, 2 }, { 27, 3 }, { 28, 4 },
				{ 27, 5 }, { 27, 2 }, { 28, 3 }, { 27, 4 },
				{ 28, 5 } };
		itemBoxPositions = new int[][] { { 47, 38 }, { 46, 38 }, { 45, 38 },
				{ 44, 38 }, { 32, 36 }, { 33, 36 }, { 34, 36 }, { 35, 36 },
				{ 4, 36 }, { 5, 36 }, { 6, 36 }, { 7, 36 }, { 8, 36 },
				{ 17, 5 }, { 17, 4 }, { 17, 3 }, { 17, 2 } };
		defaultTile = 2;
		maxCheckpoint = 6;
		maxLap = 2;
	}

	private void loadGrass() {
		tileMap = PresetTracks.RACETRACK_GRASS;
		startingPositions = new int[][] { { 9, 3 }, { 9, 2 }, { 9, 1 },
				{ 8, 3 }, { 8, 2 }, { 8, 1 }, { 7, 3 },
				{ 7, 2 } };
		itemBoxPositions = new int[][] { { 16, 9 }, { 17, 9 }, { 18, 9 } };
		defaultTile = 0;
		maxCheckpoint = 3;
		maxLap = 5;
	}

	private void loadSmall() {
		tileMap = PresetTracks.RACETRACK_SMALL;
		startingPositions = new int[][] { { 18, 22 }, { 19, 23 }, { 18, 24 },
				{ 19, 25 }, { 19, 22 }, { 18, 23 }, { 19, 24 },
				{ 18, 25 } };
		itemBoxPositions = new int[][] { { 20, 4 }, { 20, 5 }, { 20, 6 },
				{ 20, 7 } };
		defaultTile = 0;
		maxCheckpoint = 3;
		maxLap = 5;
	}

	private void loadRandom() {
		RandomMapGenerator rmg = new RandomMapGenerator(72);
		tileMap = rmg.getTrack();
		startingPositions = rmg.getStartingPositions();
		itemBoxPositions = rmg.getItemPositions();
		// System.out.println(itemBoxPositions.length);
		defaultTile = 0;
		maxCheckpoint = rmg.getCheckpoints();
		maxLap = 3;
	}

	private void loadGetRandom() {
		tileMap = RandomMapGenerator.stringToTrack(randomString);
		startingPositions = RandomMapGenerator
				.stringToStartingPositions(randomString);
		itemBoxPositions = RandomMapGenerator
				.stringToItemPositions(randomString);
		maxCheckpoint = RandomMapGenerator.stringToCheckpoints(randomString);
		maxLap = 3;
		defaultTile = 0;
	}

	public Image getCarImage(int carIndex) {
		return carImages[carIndex];
	}

	public Image getTileImage(int index) {
		return tiles[index];
	}

	public Image getTile(int x, int y) {
		return getTileImage(tileMap[x][y]);
	}

	public boolean getSolid(int x, int y) {
		return solid[tileMap[x][y]];
	}

	public boolean getTransparent(int x, int y) {
		return transparent[tileMap[x][y]];
	}

	public int getCheckpointNumber(int x, int y) {
		return checkpointNumber[tileMap[x][y]];
	}

	public double getfrictionCoefficient(int x, int y) {
		return frictionCoefficients[tileMap[x][y]];
	}

	public int numberOfTiles() {
		return tiles.length;
	}

	public int getStartingPositionX(int carIndex) {
		return startingPositions[carIndex][0];
	}

	public int getStartingPositionY(int carIndex) {
		return startingPositions[carIndex][1];
	}

	public Image getDefaultTile() {
		return tiles[defaultTile];
	}

	public double getDefaultTileFriction() {
		return frictionCoefficients[defaultTile];
	}

	public int getTrackWidth() {
		return tileMap.length;
	}

	public int getTrackHeight() {
		return tileMap[0].length;
	}

	public int getMaxCheckpoint() {
		return maxCheckpoint;
	}

	public int getMaxLap() {
		return maxLap;
	}

	public int numberOfItemBoxes() {
		return itemBoxPositions.length;
	}

	public int getItemBoxPositionX(int itemBoxID) {
		return itemBoxPositions[itemBoxID][0];
	}

	public int getItemBoxPositionY(int itemBoxID) {
		return itemBoxPositions[itemBoxID][1];
	}

	/**
	 * Private class for exclusive use when loading tiles to make setting tile
	 * properties more controllable.
	 * 
	 * @author Florian
	 *
	 */
	private class Tile {
		String fileName;
		boolean solid;
		int checkpointNumber;
		double frictionCoefficient;
		boolean transparent;

		Tile(String fileName, boolean solid, int checkpointNumber,
				double frictionCoefficient, boolean transparent) {
			this.fileName = fileName;
			this.solid = solid;
			this.checkpointNumber = checkpointNumber;
			this.frictionCoefficient = frictionCoefficient;
			this.transparent = transparent;
		}

		String getFileName() {
			return fileName;
		}

		boolean getSolid() {
			return solid;
		}

		int getCheckpointNumber() {
			return checkpointNumber;
		}

		double getFrictionCoefficient() {
			return frictionCoefficient;
		}

		boolean getTransparent() {
			return transparent;
		}
	}

	public String getTrackName() {
		return trackName;
	}

	public static String getTrackName(byte trackIdentifier) {
		switch (trackIdentifier) {
			case RACETRACK_SMALL:
				trackName = "RACETRACK_SMALL";
				return "Small Track";
			case RACETRACK_GRASS:
				trackName = "RACETRACK_GRASS";
				return "Grass Track";
			case RACETRACK_SAND:
				trackName = "RACETRACK_SAND";
				return "Sand Track";
			case RACETRACK_ICE:
				trackName = "RACETRACK_ICE";
				return "Ice Track";
			case RANDOM:
				trackName = "RANDOM";
				return "Random Track";
			default:
				System.out
						.println("ERROR: Track not found!\nReturning Small Track instead.");
				return "Small Track";

		}
	}

	public Image getCheckpointFeedback() {
		return checkpointFeedback;
	}

	public static Image getPowerupImage(int imageIndex) {
		return powerupImages[imageIndex];
	}
}
