package shared.game;

import client.gui.Camera;
import java.awt.Image;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import shared.game.model.RaceTrackModel;
import shared.game.powerup.Collidable;
import shared.game.powerup.Powerup;
import shared.game.presets.PresetTiles;
import shared.game.presets.PresetTracks;

/**
 * The Class keeping track of the games playing field.
 * 
 * @author Florian
 *
 */
public class VisualRaceTrack {

	private Image[] tiles;
	private final Image[] carImages = new Image[4 * RaceTrackModel.NUMBER_OF_CARS];
	private static Image[] powerupImages;
	private Image checkpointFeedback;
	private boolean[] solid, transparent;
	private int[] checkpointNumber;
	private double[] frictionCoefficients;

	private static String trackName;

	public static String randomString;

	public static final byte RACETRACK_SMALL = 0, RACETRACK_GRASS = 1,
			RACETRACK_SAND = 2, RACETRACK_ICE = 3, RANDOM = 4, GET_RANDOM = 6, RACETRACK_DBIS = 5;

	private RaceTrackModel model;

	public VisualRaceTrack(RaceTrackModel model){
		loadTiles();
		loadCarImages();
		loadOtherImages();
		if(model == null){
			System.err.println("Cannot load model null. Using small instead");
			model = PresetTracks.SMALL_MODEL;
		}
		this.model = model;
	}


	public static ImageIcon[] getCarImageArray() {
		ImageIcon[] carII = new ImageIcon[5];
		carII[0] = scaleImageIcon(new ImageIcon(
				VisualRaceTrack.class.getResource("/images/car1Red.png")));
		carII[1] = scaleImageIcon(new ImageIcon(
				VisualRaceTrack.class.getResource("/images/car2Blue.png")));
		carII[2] = scaleImageIcon(new ImageIcon(
				VisualRaceTrack.class
						.getResource("/images/car3Green.png")));
		carII[3] = scaleImageIcon(new ImageIcon(
				VisualRaceTrack.class
						.getResource("/images/car4Yellow.png")));
		carII[4] = scaleImageIcon(new ImageIcon(
				VisualRaceTrack.class.getResource("/images/car5Red.png")));
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
		for (int j = 1; j <= RaceTrackModel.NUMBER_OF_CARS; j++) { // j is the amount of different
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
		tiles = new Image[PresetTiles.TILES.length];
		solid = new boolean[PresetTiles.TILES.length];
		checkpointNumber = new int[PresetTiles.TILES.length];
		frictionCoefficients = new double[PresetTiles.TILES.length];
		transparent = new boolean[PresetTiles.TILES.length];
		for (int i = 0; i < PresetTiles.TILES.length; i++) {
			fetch = new ImageIcon(getClass().getResource(
					"/images/" + PresetTiles.TILES[i].getFileName()
							+ ".png"));
			tiles[i] = fetch.getImage();
			solid[i] = PresetTiles.TILES[i].getSolid();
			checkpointNumber[i] = PresetTiles.TILES[i].getCheckpointNumber();
			frictionCoefficients[i] = PresetTiles.TILES[i].getFrictionCoefficient();
			transparent[i] = PresetTiles.TILES[i].getTransparent();
		}
	}


	public Image getCarImage(int carIndex) {
		return carImages[carIndex];
	}

	public Image getTileImage(int index) {
		return tiles[index];
	}

	public Image getTile(int x, int y) {
		return getTileImage(model.getMap()[x][y]);
	}

	public boolean getSolid(int x, int y) {
		return solid[model.getMap()[x][y]];
	}

	public boolean getTransparent(int x, int y) {
		return transparent[model.getMap()[x][y]];
	}

	public int getCheckpointNumber(int x, int y) {
		return checkpointNumber[model.getMap()[x][y]];
	}

	public double getfrictionCoefficient(int x, int y) {
		return frictionCoefficients[model.getMap()[x][y]];
	}

	public int numberOfTiles() {
		return tiles.length;
	}

	public int getStartingPositionX(int carIndex) {
		return model.getStartingPositions()[carIndex][0];
	}

	public int getStartingPositionY(int carIndex) {
		return model.getStartingPositions()[carIndex][1];
	}

	public Image getDefaultTile() {
		return tiles[model.getDefaultTile()];
	}

	public double getDefaultTileFriction() {
		return frictionCoefficients[model.getDefaultTile()];
	}

	public int getTrackWidth() {
		return model.getMap().length;
	}

	public int getTrackHeight() {
		return model.getMap()[0].length;
	}

	public int getMaxCheckpoint() {
		return model.getNumbersOfCheckpoints();
	}

	public int getMaxLap() {
		return model.getNumberOfLaps();
	}

	public int numberOfItemBoxes() {
		return model.getItemBoxPositions().length;
	}

	public int getItemBoxPositionX(int itemBoxID) {
		return model.getItemBoxPositions()[itemBoxID][0];
	}

	public int getItemBoxPositionY(int itemBoxID) {
		return model.getItemBoxPositions()[itemBoxID][1];
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
			case RACETRACK_DBIS:
				trackName = "RACETRACK_DBIS";
				return "DBIS Test Track";
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
