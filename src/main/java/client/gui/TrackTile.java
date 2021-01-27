package client.gui;

/**
 * Class defining the different track segments available to the RandomMapGenerator.
 * <p>
 * TODO: Clean up and allow specification of track segments
 *
 * @author Simeon
 */
public class TrackTile {
	byte[][] tile = new byte[8][8];
	int checkpointnum;
	int enter;
	int exit;

	public TrackTile(int checkpoint, int enter, int exit) {
		getCheckpoint(checkpoint);
		this.enter = enter;
		this.exit = exit;
		drawTile();
	}

	private void drawTile() {
		if ((enter == 0 && exit == 2) || (exit == 0 && enter == 2)) {
			// Straight from top to bottom
			for (int i = 0; i < 8; i++) {
				for (int j = 2; j < 6; j++) {
					if (i == 2 && checkpointnum != -1) {
						tile[j][i] = (byte) checkpointnum;
					} else if (j == 2) {
						tile[j][i] = 17;
					} else if (j == 5) {
						tile[j][i] = 18;
					} else {
						tile[j][i] = 1;
					}

				}
			}
		} else if ((enter == 1 && exit == 3) || (exit == 1 && enter == 3)) {
			// Straight from left to right
			for (int i = 0; i < 8; i++) {
				for (int j = 2; j < 6; j++) {
					if (i == 2 && checkpointnum != -1) {
						tile[i][j] = (byte) checkpointnum;
					} else if (j == 2) {
						tile[i][j] = 15;
					} else if (j == 5) {
						tile[i][j] = 16;
					} else {
						tile[i][j] = 1;
					}
				}
			}

		} else {
			// Curves
			byte[][] temp = new byte[8][8];
			for (int i = 2; i < 8; i++) {
				for (int j = 2; j < 8; j++) {
					temp[i][j] = 1;
				}
			}
			temp[2][2] = 0;
			temp[7][6] = 0;
			temp[6][7] = 0;
			temp[7][7] = 0;

			// Return rotated curves
			if (enter == 2 && exit == 1 || enter == 1 && exit == 2) {
				tile = temp;
				tile[6][6] = 26;
				tile[3][2] = 24;
				tile[2][3] = 24;
				tile[4][2] = 15;
				tile[5][2] = 15;
				tile[6][2] = 15;
				tile[7][2] = 15;
				tile[2][7] = 17;
				tile[2][6] = 17;
				tile[2][5] = 17;
				tile[2][4] = 17;
				tile[5][7] = 18;
				tile[7][5] = 16;
			}
			if (enter == 0 && exit == 1 || enter == 1 && exit == 0) {
				for (int i = 0; i < 8; i++) {
					for (int j = 0; j < 8; j++) {
						tile[i][j] = temp[i][7 - j];
					}
				}
				tile[6][1] = 25;
				tile[3][5] = 27;
				tile[2][4] = 27;
				tile[4][5] = 16;
				tile[5][5] = 16;
				tile[6][5] = 16;
				tile[7][5] = 16;
				tile[2][0] = 17;
				tile[2][1] = 17;
				tile[2][2] = 17;
				tile[2][3] = 17;
				tile[5][0] = 18;
				tile[7][2] = 15;
			}
			if (enter == 0 && exit == 3 || enter == 3 && exit == 0) {
				for (int i = 0; i < 8; i++) {
					for (int j = 0; j < 8; j++) {
						tile[i][j] = temp[7 - i][7 - j];
					}
				}
				tile[1][1] = 24;
				tile[5][4] = 26;
				tile[4][5] = 26;
				tile[3][5] = 16;
				tile[2][5] = 16;
				tile[1][5] = 16;
				tile[0][5] = 16;
				tile[5][0] = 18;
				tile[5][1] = 18;
				tile[5][2] = 18;
				tile[5][3] = 18;
				tile[2][0] = 17;
				tile[0][2] = 15;
			}
			if (enter == 2 && exit == 3 || enter == 3 && exit == 2) {
				for (int i = 0; i < 8; i++) {
					System.arraycopy(temp[7 - i], 0, tile[i], 0, 8);
				}
				tile[1][6] = 27;
				tile[4][2] = 25;
				tile[5][3] = 25;
				tile[3][2] = 15;
				tile[2][2] = 15;
				tile[1][2] = 15;
				tile[0][2] = 15;
				tile[5][7] = 18;
				tile[5][6] = 18;
				tile[5][5] = 18;
				tile[5][4] = 18;
				tile[2][7] = 17;
				tile[0][5] = 16;
			}

			if (checkpointnum != -1) {
				temp[7][2] = (byte) checkpointnum;
				temp[7][3] = (byte) checkpointnum;
				temp[7][4] = (byte) checkpointnum;
				temp[7][5] = (byte) checkpointnum;
			}
		}
	}

	private void getCheckpoint(int num) {
		switch (num) {
			case 0:
				checkpointnum = 7;
				break;
			case 1:
				checkpointnum = 4;
				break;
			case 2:
				checkpointnum = 5;
				break;
			case 3:
				checkpointnum = 6;
				break;
			case 4:
				checkpointnum = 8;
				break;
			case 5:
				checkpointnum = 9;
				break;
			case 6:
				checkpointnum = 10;
				break;
			case 7:
				checkpointnum = 11;
				break;
			case 8:
				checkpointnum = 12;
				break;
			case 9:
				checkpointnum = 13;
				break;
			case 10:
				checkpointnum = 14;
				break;
			case 11:
				checkpointnum = 15;
				break;
			default:
				checkpointnum = -1;
		}

	}

	public byte getArray(int i, int j) {
		return this.tile[i][j];
	}

	public int getExit() {
		return exit;
	}

	public int getEnter() {
		return enter;
	}
}
