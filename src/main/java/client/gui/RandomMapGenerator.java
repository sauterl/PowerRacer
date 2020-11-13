package client.gui;

import java.util.Random;

import shared.game.PresetTracks;

/**
 * @author Simeon
 *
 */
public class RandomMapGenerator {
	private static int minCheck = 5;
	private byte[][] a;
	private String[][] paintedTiles;
	private int size;
	private int enter = 3;
	private byte xpos = 16;
	private byte ypos = 16;
	private byte startx = xpos;
	private byte starty = ypos;
	private Random ran = new Random();
	// the overflowcounter checks if there are still paths available
	private int overFlowCounter;
	private int checkpointCounter;
	private int checkpointNum;
	private int itemCounter;
	private int itemNum;
	private int searchFlowCounter;
	private int[][] itemBoxPositions;
	private boolean[] check;
	// dir is the direction to go for the next tile
	private int dir;
	private int[][] startingPositions = new int[][] {
			{ startx + 1, starty + 2 }, { startx, starty + 3 },
			{ startx + 1, starty + 4 }, { startx, starty + 5 } };

	/**
	 * This method resets all the values to their standard value. This method is
	 * called if there is an error while creating a map as well as if the
	 * checkpointnum is smaller then the minimum checkpoint num (if the track is
	 * to short).
	 */
	private void initialize() {
		check = new boolean[4];
		itemBoxPositions = new int[48][48];
		enter = 3;
		xpos = startx;
		ypos = starty;
		overFlowCounter = 0;
		checkpointCounter = 0;
		itemCounter = 1;
		itemNum = 0;
		checkpointNum = 0;
		searchFlowCounter = 0;
		paintedTiles = new String[this.size / 8][this.size / 8];
		this.a = new byte[this.size][this.size];
		loadTrackTile(0, enter, 1);
	}

	public RandomMapGenerator(int size) {
		// System.out.println("Creating a Random Track...");
		setSize(size);
		minCheck = setCheckpoints();
		initialize();
		createMap();
		// arrToString(paintedTiles);
		// System.out.println("Track created.");
		// new TrackEdit(this.width, this.height, a);
	}

	private int setCheckpoints() {
		int num;
		num = size / 8;
		if (num < 3) {
			num = 3;
		}
		if (num > 10) {
			num = 10;
		}
		return num;
	}

	/**
	 * This Method actually creates the map Therefore it has a while loop that
	 * runs as long as the the position does not equal the tile left to the
	 * starting position. This method also determines if and what kind of
	 * checkpoint will be set on the tile.
	 */
	private void createMap() {
		while (!(xpos == startx - 8 && ypos == starty)) {
			setDirArray(check, true);
			try {
				dir = getNextDir(check);
			} catch (Exception e) {
				// System.out.println("No Dir Reset");
				initialize();
				continue;
			}
			// Draw itemboxes on every 3rd straight tile
			if (itemCounter == 3) {
				// check if tile is straight from top to bottom
				if (enter == 0 && dir == 2 || enter == 2 && dir == 0) {
					for (int j = 0; j < 4; j++) {
						itemBoxPositions[itemNum][0] = xpos + 2 + j;
						itemBoxPositions[itemNum][1] = ypos + 5;
						itemNum++;
					}
					itemCounter = 0;
				} else
					// check if tile is straight from left to right
					if (enter == 1 && dir == 3 || enter == 3 && dir == 1) {
						for (int i = 0; i < 4; i++) {
							itemBoxPositions[itemNum][0] = xpos + 5;
							itemBoxPositions[itemNum][1] = ypos + 2 + i;
							itemNum++;
						}
						itemCounter = 0;
					}
			} else {
				itemCounter++;
			}
			// Set checkpoint on every third tile
			if (checkpointCounter >= 3 && !(xpos == startx && ypos == starty)
					&& checkpointNum < 11) {
				checkpointNum++;
				loadTrackTile(checkpointNum, enter, dir);
				checkpointCounter = 0;

			} else {
				checkpointCounter += 1;
				loadTrackTile(-1, enter, dir);
			}
			if (xpos == startx - 8 && ypos == starty
					&& checkpointNum < minCheck || checkpointNum > 10) {
				// System.out.println("Wrong Checkpoint reset");
				initialize();
			}
		}
		loadTrackTile(-1, enter, 1);
		// System.out.println("letztes Tile gesetzt!");
		// Minimum amount of checkpoints

	}

	/**
	 * This method throws an exception if there is no direction available!
	 */
	private int getNextDir(boolean[] check) throws Exception {
		overFlowCounter += 1;
		int num = randInt(0, 3);
		if (overFlowCounter > 30) {
			throw new Exception();
		}
		if (check[num]) {
			overFlowCounter = 0;
			return num;
		} else {
			return getNextDir(check);
		}
	}

	/**
	 * This method checks if there is still a path available from the actual
	 * position to the startpoint
	 */
	private boolean isPathAvailable(int direction) {
		byte x = xpos;
		byte y = ypos;
		int entertemp = enter;
		setNextRound(direction);
		try {
			boolean[] check = new boolean[4];
			while (!(xpos == startx - 8 && ypos == starty)) {
				if (searchFlowCounter > 25) {
					overFlowCounter = 0;
					return true;
				}
				searchFlowCounter += 1;
				setDirArray(check, false);
				int dir = getNextDir(check);
				setNextRound(dir);
			}
		} catch (Exception e) {
			return false;
		} finally {
			xpos = x;
			ypos = y;
			enter = entertemp;
		}
		return true;
	}

	/**
	 * this method check which direction would go out of bounds therefore it
	 * calls the method checkBorder which has the corresponding algorithm
	 */
	private boolean[] setDirArray(boolean[] check, boolean recursive) {
		for (int i = 0; i < 4; i++) {
			check[i] = checkBorder(i, recursive);
		}
		return check;
	}

	/**
	 * Has the algorithm to check if dir would lead to OutOfBoundsException it
	 * has the option recursive, to draw on the painted tiles array if necessary
	 */
	private boolean checkBorder(int i, boolean recursive) {

		if (i == 0) {
			if (ypos - 8 < 0) {
				return false;
			}
			if (paintedTiles[xpos / 8][(ypos / 8) - 1] != null) {
				return false;
			}
		}
		if (i == 1) {
			if (xpos + 9 >= size) {
				return false;
			}
			if (paintedTiles[(xpos / 8) + 1][(ypos / 8)] != null) {
				return false;
			}
		}
		if (i == 2) {
			if (ypos + 9 >= size) {
				return false;
			}
			if (paintedTiles[xpos / 8][(ypos / 8) + 1] != null) {
				return false;
			}
		}
		if (i == 3) {
			if (xpos - 8 < 0) {
				return false;
			}
			if (paintedTiles[(xpos / 8) - 1][(ypos / 8)] != null) {
				return false;
			}
		}
		if (recursive) {
			if (!isPathAvailable(i)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Sets the track size to something dividable by 8 because all tiles have
	 * the size 8 also sets default size to 32 if small numbers are inserted
	 */
	private void setSize(int size) {

		if (size < 32) {
			size = 32;
		}
		this.size = (size / 8) * 8;
	}

	/**
	 * actually draws the loaded Tile on the main byte-array it also calls
	 * nextRound to set the correct envirement for the next step
	 */
	private void loadTrackTile(int checkpoint, int enter, int exit) {

		TrackTile t = new TrackTile(checkpoint, enter, exit);
		paintedTiles[xpos / 8][ypos / 8] = t.getEnter() + "_" + t.getExit()
				+ "_" + checkpoint;
		// System.out.println(xpos + "/" + ypos + "\t" + t.getEnter()+"/"
		// +t.getExit());
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				if (t.getArray(i, j) != 0) {
					a[i + xpos][j + ypos] = t.getArray(i, j);
				}
			}
		}
		setNextRound(t.exit);

	}

	/**
	 * this method sets the variables for the next step mainly it changes the
	 * value of the enter var it also paints on the painted Tiles array
	 */
	private void setNextRound(int exit) {

		if (exit == 0) {
			ypos -= 8;
			enter = 2;
		}
		if (exit == 1) {
			xpos += 8;
			enter = 3;
		}
		if (exit == 2) {
			ypos += 8;
			enter = 0;
		}
		if (exit == 3) {
			xpos -= 8;
			enter = 1;
		}
	}

	/**
	 * returns a random int in a given range
	 */
	public byte randInt(int min, int max) {
		int randomNum = ran.nextInt((max - min) + 1) + min;
		return (byte) randomNum;
	}

	@SuppressWarnings("unused")
	private void arrToString(String[][] a) {
		for (int i = 0; i < a.length; i++) {
			for (int j = 0; j < a.length; j++) {
				System.out.print("[(" + j + ")(" + i + ")" + a[j][i] + "]\t");
			}
			System.out.println();
		}
	}

	public int getCheckpoints() {
		return checkpointNum;
	}

	public byte[][] getTrack() {
		return a;
	}

	public String[][] getTiles() {
		return paintedTiles;
	}

	public int[][] getStartingPositions() {
		return startingPositions;
	}

	/**
	 * returns the correct size of the itempositions
	 */
	public int[][] getItemPositions() {
		int size = 0;
		while (itemBoxPositions[size][0] != 0) {
			size += 1;
		}
		int[][] returnBox = new int[size][2];
		for (int i = 0; i < size; i++) {
			returnBox[i][0] = itemBoxPositions[i][0];
			returnBox[i][1] = itemBoxPositions[i][1];

		}

		return returnBox;
	}

	/**
	 * This method generates a Track out of a paintedTiles array
	 */
	public static byte[][] reverseTrack(String[][] track) {
		byte[][] reverse = new byte[track.length * 8][track.length * 8];
		for (int i = 0; i < track.length; i++) {
			for (int j = 0; j < track.length; j++) {
				if (track[i][j] != null) {
					if (!track[i][j].equals("null")) {

						String[] parts = track[i][j].split("_");
						TrackTile t = new TrackTile(Integer.parseInt(parts[2]),
								Integer.parseInt(parts[0]),
								Integer.parseInt(parts[1]));
						for (int k = 0; k < 8; k++) {
							for (int k2 = 0; k2 < 8; k2++) {
								reverse[8 * i + k][8 * j + k2] = t.getArray(k,
										k2);
							}

						}
					}
				}
			}
		}
		return reverse;
	}

	/**
	 * Creates a string out of the paintedTiles Array
	 */
	public static String trackToString(RandomMapGenerator rmg) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < rmg.getTiles().length; i++) {
			for (int j = 0; j < rmg.getTiles().length; j++) {
				sb.append(rmg.getTiles()[i][j]);
				sb.append("&");
			}
			sb.append("%");
		}
		sb.append("/");
		sb.append(rmg.getCheckpoints());
		sb.append("/");
		for (int i = 0; i < rmg.getStartingPositions().length; i++) {
			sb.append(rmg.getStartingPositions()[i][0]);
			sb.append("&");
			sb.append(rmg.getStartingPositions()[i][1]);
			sb.append("%");
		}
		sb.append("/");
		for (int i = 0; i < rmg.getItemPositions().length; i++) {
			sb.append(rmg.getItemPositions()[i][0]);
			sb.append("&");
			sb.append(rmg.getItemPositions()[i][1]);
			sb.append("%");
		}
		// System.out.println(sb.toString());
		return sb.toString();
	}

	/**
	 * splits a String into a StringArray
	 */
	public static byte[][] stringToTrack(String s) {
		try {
			String[] items = s.split("/");
			String[] rows = items[0].split("%");
			String[][] trackArray = new String[rows.length][rows.length];
			for (int i = 0; i < rows.length; i++) {
				// System.out.println("row: " + rows[i]);
				String[] elements = rows[i].split("&");
				for (int j = 0; j < elements.length; j++) {
					// System.out.println("element: " + elements[j]);
					trackArray[i][j] = elements[j];
				}
			}
			byte[][] track = reverseTrack(trackArray);
			return track;
		} catch (Exception e) {
			return PresetTracks.RACETRACK_SAND;
		}

	}

	public static int stringToCheckpoints(String s) {
		try {
			String[] items = s.split("/");
			// System.out.println(Integer.parseInt(items[1]));
			return Integer.parseInt(items[1]);
		} catch (Exception e) {
			return 6;
		}
	}

	public static int[][] stringToStartingPositions(String s) {
		try {
			String[] items = s.split("/");
			int[][] pos = new int[8][2];
			String[] rows = items[2].split("%");
			for (int i = 0; i < rows.length; i++) {
				String[] place = rows[i].split("&");
				pos[i][0] = Integer.parseInt(place[0]);
				pos[i][1] = Integer.parseInt(place[1]);
				pos[i + rows.length][0] = Integer.parseInt(place[0]) - 1;
				pos[i + rows.length][1] = Integer.parseInt(place[1]);
				// System.out.println( Integer.parseInt(place[0]) + "/" +
				// Integer.parseInt(place[1]));

			}
			return pos;
		} catch (Exception e) {
			return new int[][] { { 28, 2 }, { 27, 3 }, { 28, 4 }, { 27, 5 } };

		}

	}

	public static int[][] stringToItemPositions(String s) {
		try {
			String[] items = s.split("/");
			String[] rows = items[3].split("%");
			int[][] pos = new int[rows.length][2];
			for (int i = 0; i < rows.length; i++) {
				String[] place = rows[i].split("&");
				pos[i][0] = Integer.parseInt(place[0]);
				pos[i][1] = Integer.parseInt(place[1]);
				// System.out.println( Integer.parseInt(place[0]) + "/" +
				// Integer.parseInt(place[1]));
			}
			return pos;
		} catch (Exception e) {
			return new int[][] { { 47, 38 }, { 46, 38 }, { 45, 38 },
					{ 44, 38 }, { 32, 36 }, { 33, 36 }, { 34, 36 }, { 35, 36 },
					{ 4, 36 }, { 5, 36 }, { 6, 36 }, { 7, 36 }, { 8, 36 },
					{ 17, 5 }, { 17, 4 }, { 17, 3 }, { 17, 2 } };
		}
	}

	public static void main(String[] args) {
		RandomMapGenerator r = new RandomMapGenerator(64);
		stringToItemPositions(trackToString(r));
		new TrackEdit(48, 48, stringToTrack(trackToString(r)));
	}
}