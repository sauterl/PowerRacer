/**
 * 
 */
package client.gui;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import shared.game.PowerRacerGame;
import shared.game.powerup.Collidable;
import shared.game.powerup.Powerup;

/**
 * The Game's Graphics engine. In control of repainting the screen and
 * periodically updating the game.
 * 
 * @author Florian
 *
 */
public class Camera extends Component {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	SoundManager sm = new SoundManager(true); // set true for sounds
	private int music;

	ClientGUI clientGUI;

	JFrame frame;
	PowerRacerGame game;
	int carIndex, windowWidth, windowHeight, countdown;
	Image dbImage;
	Graphics dbg;
	// RedrawThread redraw;
	UpdateThread update;
	int[][] carPositions;
	private Font font = new Font("Arial", Font.BOLD, 25),
			countDownFont = new Font("Arial", Font.BOLD, 40),
			scoreboardFont = new Font("Courier", Font.BOLD, 40);
	private boolean control, complete, c, showNames = true, fullscreen;
	private static final String[] scoreboardPrefixes = { "1st:", "2nd:",
			"3rd:", "4th:" };

	private ConcurrentLinkedQueue<Powerup> powerupVFXList = new ConcurrentLinkedQueue<Powerup>();

	// private Boolean updateInProgress;
	// Image trackImage;

	public static final int RENDER_WIDTH = 1280, RENDER_HEIGHT = 720,
			TILE_SIDE_LENGTH = RENDER_WIDTH / 15;
	private static int zoomLevel = 25, renderTileSideLength = RENDER_WIDTH
			/ zoomLevel, carTileSideLength = (renderTileSideLength / 16) * 24;

	private int checkpoint, checkpointFeedback, maxCheckpoint, maxLap;

	/**
	 * The Camera Constructor.
	 * 
	 * @param game
	 *            the {@link PowerRacerGame} object this Camera is monitoring
	 * @param windowWidth
	 *            the player selected horizontal resolution
	 * @param windowHeight
	 *            the player selected vertical resolution
	 * @param clientGUI
	 *            the creating {@link ClientGUI}
	 */
	public Camera(PowerRacerGame game, int windowWidth, int windowHeight,
			ClientGUI clientGUI) {
		maxCheckpoint = game.getMaxCheckpoint();
		maxLap = game.getMaxLap();

		music = new Random().nextInt(2) * 7;

		this.clientGUI = clientGUI;
		this.game = game;
		carIndex = game.getCarIndex();
		this.windowWidth = windowWidth;
		this.windowHeight = windowHeight;

		frame = new JFrame("Power Racer");
		frame.getContentPane().add(this);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setPreferredSize(new Dimension(windowWidth, windowHeight));
		frame.setResizable(false);
		frame.setUndecorated(true);
		frame.addKeyListener(new AL());
		frame.setIgnoreRepaint(true);
		frame.pack();
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setLocation(dim.width / 2 - frame.getSize().width / 2, dim.height
				/ 2 - frame.getSize().height / 2);

		// trackImage = drawTrack();

		frame.setVisible(true);

		carPositions = new int[game.getNumberOfPlayers()][2];
		// redraw = new RedrawThread(frame, 25, updateInProgress);
		update = new UpdateThread(game, 25, frame);
	}

	/**
	 * The modified paint method painting the final image to the screen.
	 * <p>
	 * Creates new Image and calls the paintComponent method on its graphics.
	 * Saves the Image to the working directory as a screenshot if c is true.
	 */
	@Override
	public void paint(Graphics g) {
		dbImage = frame.createImage(RENDER_WIDTH, RENDER_HEIGHT);
		dbg = dbImage.getGraphics();
		paintComponent(dbg);
		if (fullscreen) {
			Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
			g.fillRect(0, 0, dim.width, dim.height);
			g.drawImage(dbImage, 0, 0, (int) dim.getWidth(),
					(int) (((dim.getWidth() / 16) * 9)), 0, 0, RENDER_WIDTH,
					RENDER_HEIGHT, null);
		} else {
			g.drawImage(dbImage, 0, 0, windowWidth, windowHeight, 0, 0,
					RENDER_WIDTH, RENDER_HEIGHT, null);
		}
		if (c) {
			try {
				// retrieve image
				BufferedImage bi = (BufferedImage) dbImage;
				DateFormat dateFormat = new SimpleDateFormat(
						"dd.MM.yyyy HH:mm:ss");
				Date date = new Date();
				File outputfile = new File(dateFormat.format(date) + ".png");
				ImageIO.write(bi, "png", outputfile);
			} catch (IOException e) {
				System.out.println("ERROR");
			}
			c = false;
		}

	}

	/**
	 * Paints the game onto the given Graphics. This method first draws the
	 * {@link RaceTrack}, then the {@link Collidable}s, then the {@link Car}s,
	 * then the {@link Powerup}s in the VFX list and finally the HUD.
	 * 
	 * @param g
	 *            the Graphics the game will be drawn on
	 */
	private void paintComponent(Graphics g) {
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, RENDER_WIDTH, RENDER_HEIGHT);

		for (int i = 0; i < carPositions.length; i++) {
			carPositions[i][0] = (int) ((game.getCarX(i) / (RENDER_WIDTH / 15)) * renderTileSideLength);
			carPositions[i][1] = (int) ((game.getCarY(i) / (RENDER_WIDTH / 15)) * renderTileSideLength);
		}

		int x = carPositions[carIndex][0];
		int y = carPositions[carIndex][1];

		int xi = (x - RENDER_WIDTH / 2) / renderTileSideLength;
		int xf = (x + RENDER_WIDTH / 2) / renderTileSideLength;
		int yi = (y - RENDER_HEIGHT / 2) / renderTileSideLength;
		int yf = (y + RENDER_HEIGHT / 2) / renderTileSideLength;

		for (int i = xi - 1; i <= xf; i++) {
			for (int j = yi - 1; j <= yf; j++) {
				try {
					if (game.getTransparent(i, j)) {
						g.drawImage(game.getDefaultTile(), i
								* renderTileSideLength - x + RENDER_WIDTH / 2,
								j * renderTileSideLength - y + RENDER_HEIGHT
										/ 2, renderTileSideLength,
								renderTileSideLength, null);
					}
					g.drawImage(game.getTile(i, j), i * renderTileSideLength
							- x + RENDER_WIDTH / 2, j * renderTileSideLength
							- y + RENDER_HEIGHT / 2, renderTileSideLength,
							renderTileSideLength, null);
				} catch (IndexOutOfBoundsException e) {
					g.drawImage(game.getDefaultTile(), i * renderTileSideLength
							- x + RENDER_WIDTH / 2, j * renderTileSideLength
							- y + RENDER_HEIGHT / 2, renderTileSideLength,
							renderTileSideLength, null);
				}
			}
		}
		// g.drawImage(trackImage, -x,-y, null);

		for (Collidable c : game.getCollidables()) {
			if (c.isEnabled()) {
				int colx = (int) ((c.getX() / (RENDER_WIDTH / 15)) * renderTileSideLength)
						- x + RENDER_WIDTH / 2 - carTileSideLength / 2;
				int coly = (int) ((c.getY() / (RENDER_WIDTH / 15)) * renderTileSideLength)
						- y + RENDER_HEIGHT / 2 - carTileSideLength / 2;
				if (isInCameraBounds(colx, coly)) {
					g.drawImage(rotateCollidable(c), colx, coly, null);
				}
			}
		}

		for (int i = 0; i < game.getNumberOfPlayers(); i++) {
			g.drawImage(rotateCar(i), carPositions[i][0] - x + RENDER_WIDTH / 2
					- carTileSideLength / 2, carPositions[i][1] - y
					+ RENDER_HEIGHT / 2 - carTileSideLength / 2, null);
		}

		// Draw VFX
		if (!game.getPause()) {
			for (Powerup p : powerupVFXList) {
				p.paintVFX(g);
				if (p.paintIsDone()) {
					powerupVFXList.remove(p);
				}
			}
		}
		// Draw HUD
		g.setColor(Color.BLACK);
		g.setFont(font);

		if (checkpoint != game.getCarCheckpoint(carIndex)) {
			checkpointFeedback = 10;
		}
		if (checkpointFeedback > 0) {
			g.drawImage(fadeCheckpointFeedback(), RENDER_WIDTH / 2
					- renderTileSideLength, RENDER_HEIGHT / 2
					- renderTileSideLength, 2 * renderTileSideLength,
					2 * renderTileSideLength, null);
			checkpointFeedback--;
		}
		checkpoint = game.getCarCheckpoint(carIndex);

		if (!game.getBotOn()) {
			g.drawString("Checkpoint: " + checkpoint + "/" + maxCheckpoint, 20,
					25);
			g.drawString("Lap: " + game.getCarLap(carIndex) + "/" + maxLap, 20,
					45);
		} else {
			g.drawString("Finished!", 20, 25);
		}
		String[] playerNames = game.getPlayerNames();
		if (showNames) {
			for (int i = 0; i < playerNames.length; i++) {
				if (playerNames[i].length() > 8) {
					playerNames[i] = playerNames[i].substring(0, 7) + "\u2026";
				}
				if (i != carIndex) {
					Rectangle2D stringBounds = g.getFontMetrics()
							.getStringBounds(playerNames[i], g);
					int dispX = (int) stringBounds.getWidth() / 2;
					g.setColor(new Color(255, 255, 255, 150));
					g.fillRect(carPositions[i][0] - x + RENDER_WIDTH / 2
							- dispX, carPositions[i][1] - y + RENDER_HEIGHT / 2
							- (int) stringBounds.getHeight()
							- carTileSideLength / 2,
							(int) stringBounds.getWidth(),
							(int) stringBounds.getHeight());
					g.setColor(Color.BLACK);
					g.drawString(playerNames[i], carPositions[i][0] - x
							+ RENDER_WIDTH / 2 - dispX, carPositions[i][1] - y
							+ RENDER_HEIGHT / 2 - carTileSideLength / 2 - 5);
				}
			}
		}
		int oldCountdown = countdown;
		countdown = game.getCountdown();
		if (countdown > 0) {
			if (oldCountdown != countdown) {
				sm.playSound(5);
			}
			g.setFont(countDownFont);
			g.setColor(Color.RED);
			Rectangle2D stringBounds = g.getFontMetrics().getStringBounds(
					countdown + "", g);
			int dispX = (int) stringBounds.getWidth() / 2;
			int dispY = (int) stringBounds.getHeight() / 2;
			g.drawString(countdown + "", RENDER_WIDTH / 2 - dispX,
					RENDER_HEIGHT / 2 - dispY);
		} else
			if (countdown > -40) {
				if (countdown == 0) {
					sm.playSound(6);
					// loop mainsong after last beep
				}
				g.setFont(countDownFont);
				g.setColor(Color.RED);
				Rectangle2D stringBounds = g.getFontMetrics().getStringBounds(
						"GO!", g);
				int dispX = (int) stringBounds.getWidth() / 2;
				int dispY = (int) stringBounds.getHeight() / 2;
				g.drawString("GO!", RENDER_WIDTH / 2 - dispX, RENDER_HEIGHT / 2
						- dispY);
				game.setCountdown(countdown - 1);
				if (countdown == -39) {
					sm.loopSound(music);
				}
			}
		g.drawImage(game.getPowerupImage(), RENDER_WIDTH - TILE_SIDE_LENGTH, 0,
				TILE_SIDE_LENGTH, TILE_SIDE_LENGTH, null);

		complete = game.getComplete();
		if (complete) {
			String[] scoreboard = game.getScoreboard();
			int[] times = game.getTimes();
			g.setColor(Color.DARK_GRAY);
			g.fillRect(RENDER_WIDTH / 4, RENDER_HEIGHT / 4, RENDER_WIDTH / 2,
					RENDER_HEIGHT / 2);
			g.setColor(Color.WHITE);
			g.fillRect(RENDER_WIDTH / 4 + 5, RENDER_HEIGHT / 4 + 5,
					RENDER_WIDTH / 2 - 10, RENDER_HEIGHT / 2 - 10);
			g.setFont(scoreboardFont);
			g.setColor(Color.BLACK);
			for (int i = 0; i < scoreboard.length; i++) {
				try {
					if (scoreboard[i].length() > 8) {
						g.drawString(
								scoreboardPrefixes[i]
										+ scoreboard[i].substring(0, 7)
										+ "\u2026", RENDER_WIDTH / 4 + 20,
								RENDER_HEIGHT / 4 + 50 + i * 80);
					} else {
						g.drawString(scoreboardPrefixes[i] + scoreboard[i],
								RENDER_WIDTH / 4 + 20, RENDER_HEIGHT / 4 + 50
										+ i * 80);
					}
				} catch (NullPointerException e) {
					System.out.println("Beep!");
				}
				int seconds = (int) TimeUnit.MILLISECONDS.toSeconds(times[i]);
				int milliseconds = times[i] % 1000;
				g.drawString(
						seconds + "." + String.format("%03d", milliseconds)
								+ "s", RENDER_WIDTH / 2 + 60, RENDER_HEIGHT / 4
								+ 50 + i * 80);
			}
			g.setFont(font);
			Rectangle2D stringBounds = g.getFontMetrics().getStringBounds(
					"Press x to return to lobby.", g);
			int dispX = (int) stringBounds.getWidth() / 2;
			g.drawString("Press x to return to lobby.", RENDER_WIDTH / 2
					- dispX, 3 * RENDER_HEIGHT / 4 - 15);
		}

		if (game.getPause()) {
			g.setFont(countDownFont);
			Rectangle2D stringBounds = g.getFontMetrics().getStringBounds(
					"A PLAYER HAS LEFT THE GAME", g);
			int dispX = (int) stringBounds.getWidth() / 2;
			int dispY = (int) stringBounds.getHeight() / 2;
			int posX = RENDER_WIDTH / 2 - dispX;
			int posY = RENDER_HEIGHT / 2 - dispY;
			g.setColor(Color.BLACK);
			g.fillRect(posX - 10, posY - 10, 2 * dispX + 20, 2 * dispY + 20);
			g.setColor(Color.WHITE);
			g.fillRect(posX - 5, posY - 5, 2 * dispX + 10, 2 * dispY + 10);
			g.setColor(Color.BLACK);
			g.drawString("A PLAYER HAS LEFT THE GAME", posX, posY
					+ (int) (1.7 * dispY));

			g.setFont(font);
			Rectangle2D stringBounds2 = g.getFontMetrics().getStringBounds(
					"Press x to return to lobby.", g);
			int dispX2 = (int) stringBounds2.getWidth() / 2;
			g.setColor(Color.WHITE);
			g.fillRect(RENDER_WIDTH / 2 - dispX2 - 5, 3 * RENDER_HEIGHT / 4
					- 20 - (int) stringBounds2.getHeight(),
					(int) stringBounds2.getWidth() + 10,
					(int) stringBounds2.getHeight() + 10);
			g.setColor(Color.BLACK);
			g.drawString("Press x to return to lobby.", RENDER_WIDTH / 2
					- dispX2, 3 * RENDER_HEIGHT / 4 - 15);
		}
	}

	/**
	 * Checks if the given point (x,y) lies within the bounds of the Camera and
	 * therefore needs to be drawn.
	 * 
	 * @param x
	 *            the horizontal position of the point
	 * @param y
	 *            the vertical position of the point
	 * @return whether or not the point (x,y) is within draw distance
	 */
	private boolean isInCameraBounds(int x, int y) {
		return y > -carTileSideLength && y < RENDER_HEIGHT + carTileSideLength
				&& x > -carTileSideLength
				&& x < RENDER_WIDTH + carTileSideLength;
	}

	/**
	 * Rotates the image of the {@link Car} according to its actual rotation.
	 * 
	 * @param carIndex
	 *            the carIndex of the requested {@link Car}
	 * @return the correctly rotated image of the {@link Car}
	 */
	private Image rotateCar(int carIndex) {
		BufferedImage si = new BufferedImage(carTileSideLength,
				carTileSideLength, BufferedImage.TYPE_INT_ARGB);
		Graphics2D sg = (Graphics2D) si.getGraphics();
		AffineTransform at = new AffineTransform();
		at.translate(carTileSideLength / 2, carTileSideLength / 2);
		at.rotate(game.getCarRotation(carIndex) + Math.PI / 2);
		sg.setTransform(at);
		sg.drawImage(game.getCarImage(carIndex), -renderTileSideLength / 2,
				-renderTileSideLength / 2, renderTileSideLength,
				renderTileSideLength, null);
		return si;
	}

	/**
	 * Rotates the image of the {@link Collidable} according to its actual
	 * rotation.
	 * 
	 * @param c
	 *            the {@link Collidable} to be rotated
	 * @return the correctly rotated image of the {@link Collidable}
	 */
	private Image rotateCollidable(Collidable c) {
		BufferedImage si = new BufferedImage(carTileSideLength,
				carTileSideLength, BufferedImage.TYPE_INT_ARGB);
		Graphics2D sg = (Graphics2D) si.getGraphics();
		AffineTransform at = new AffineTransform();
		at.translate(carTileSideLength / 2, carTileSideLength / 2);
		at.rotate(c.getRotation() + Math.PI / 2);
		sg.setTransform(at);
		sg.drawImage(c.getImage(), -renderTileSideLength / 2,
				-renderTileSideLength / 2, renderTileSideLength,
				renderTileSideLength, null);
		return si;
	}

	/**
	 * Fades the checkpoint feedback.
	 * 
	 * @return the faded checkpoint image.
	 */
	private Image fadeCheckpointFeedback() {
		BufferedImage si = new BufferedImage(2 * renderTileSideLength,
				2 * renderTileSideLength, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = (Graphics2D) si.getGraphics();
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
				(float) checkpointFeedback / 10));
		g2d.drawImage(game.track.getCheckpointFeedback(), 0, 0,
				2 * renderTileSideLength, 2 * renderTileSideLength, null);
		return si;
	}

	// private Image drawTrack() {
	// BufferedImage si = new BufferedImage(renderTileSideLength
	// * game.getTrackWidth(), renderTileSideLength
	// * game.getTrackHeight(), BufferedImage.TYPE_INT_ARGB);
	// Graphics2D sg = (Graphics2D) si.getGraphics();
	// for (int i = 0; i < game.getTrackWidth(); i++) {
	// for (int j = 0; j < game.getTrackHeight(); j++) {
	// sg.drawImage(game.getTile(i, j), i * renderTileSideLength, j
	// * renderTileSideLength, renderTileSideLength,
	// renderTileSideLength, null);
	// }
	// }
	// return si;
	// }

	/**
	 * Terminates the camera, closes the update thread and sets used resources
	 * free.
	 */
	public void terminate() {
		// redraw.terminate();
		update.terminate();
		frame.dispose();
	}

	/**
	 * The Keyboardlistener for the {@link Camera}
	 * 
	 * @author Florian
	 *
	 */
	private class AL extends KeyAdapter {
		public void keyPressed(KeyEvent e) {
			int keyCode = e.getKeyCode();
			control = game.getControl();

			if (control) {
				if (keyCode == KeyEvent.VK_UP || keyCode == KeyEvent.VK_W) {
					game.setCarInputUp(carIndex, true);
				}
				if (keyCode == KeyEvent.VK_DOWN || keyCode == KeyEvent.VK_S) {
					game.setCarInputDown(carIndex, true);
					// sm.playSound(3);
				}
				if (keyCode == KeyEvent.VK_LEFT || keyCode == KeyEvent.VK_A) {
					game.setCarInputLeft(carIndex, true);
				}
				if (keyCode == KeyEvent.VK_RIGHT || keyCode == KeyEvent.VK_D) {
					game.setCarInputRight(carIndex, true);
				}
				if (keyCode == KeyEvent.VK_SPACE || keyCode == KeyEvent.VK_V) {
					if (game.getPowerup() != null) {
						powerupVFXList.add(game.getPowerup());
						game.setSpace(true);
					}
				}
				if (keyCode == KeyEvent.VK_Q) {
					sm.loopSound(2);
				}

			} else
				if (complete || game.getPause()) {
					if (keyCode == KeyEvent.VK_X) {
						clientGUI.returnToLobby();
						sm.stopSound(music);
					}
				}

			if ((keyCode == KeyEvent.VK_EQUALS || keyCode == KeyEvent.VK_PLUS || keyCode == KeyEvent.VK_P)
					&& zoomLevel > 10) {
				zoomLevel -= 5;
				renderTileSideLength = RENDER_WIDTH / zoomLevel;
				carTileSideLength = (renderTileSideLength / 16) * 24;
			}
			if ((keyCode == KeyEvent.VK_MINUS || keyCode == KeyEvent.VK_O)
					&& zoomLevel < 40) {
				zoomLevel += 5;
				renderTileSideLength = RENDER_WIDTH / zoomLevel;
				carTileSideLength = (renderTileSideLength / 16) * 24;
			}
		}

		public void keyReleased(KeyEvent e) {
			int keyCode = e.getKeyCode();

			if (keyCode == KeyEvent.VK_UP || keyCode == KeyEvent.VK_W) {
				game.setCarInputUp(carIndex, false);
			}
			if (keyCode == KeyEvent.VK_DOWN || keyCode == KeyEvent.VK_S) {
				game.setCarInputDown(carIndex, false);
			}
			if (keyCode == KeyEvent.VK_LEFT || keyCode == KeyEvent.VK_A) {
				game.setCarInputLeft(carIndex, false);
			}
			if (keyCode == KeyEvent.VK_RIGHT || keyCode == KeyEvent.VK_D) {
				game.setCarInputRight(carIndex, false);
			}
			if (keyCode == KeyEvent.VK_Q) {
				sm.stopSound(2);
				sm.playSound(3);
			}
			if (keyCode == KeyEvent.VK_C) {
				c = true;
			}
			if (keyCode == KeyEvent.VK_M) {
				sm.toggleMusic(music);
			}
			if (keyCode == KeyEvent.VK_T) {
				if (showNames) {
					showNames = false;
				} else {
					showNames = true;
				}
			}
			if (keyCode == KeyEvent.VK_1) {
				frame.setLocation(0, 0);
			}
			if (keyCode == KeyEvent.VK_2) {
				Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
				frame.setLocation(dim.width - frame.getWidth(), 0);
			}
			if (keyCode == KeyEvent.VK_3) {
				Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
				frame.setLocation(0, dim.height - frame.getHeight());
			}
			if (keyCode == KeyEvent.VK_4) {
				Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
				frame.setLocation(dim.width - frame.getWidth(), dim.height
						- frame.getHeight());
			}
			if (keyCode == KeyEvent.VK_5) {
				Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
				frame.setLocation(dim.width / 2 - frame.getSize().width / 2,
						dim.height / 2 - frame.getSize().height / 2);
			}
			if (keyCode == KeyEvent.VK_F) {
				Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
				if (fullscreen) {
					frame.setVisible(false);
					frame.pack();
					frame.setLocation(
							dim.width / 2 - frame.getSize().width / 2,
							dim.height / 2 - frame.getSize().height / 2);
					frame.setVisible(true);
				} else {
					frame.setLocation(0, 0);
					frame.setSize(dim);
				}
				fullscreen = !fullscreen;
			}
		}
	}

	public void addVFX(Powerup vfx) {
		powerupVFXList.add(vfx);
	}
}

// class RedrawThread extends Thread {
// boolean stop;
// JFrame frame;
// int redrawSleep;
// private long startTime;
//
// // private Boolean updateInProgress;
//
// public RedrawThread(JFrame frame, int framesPerSecond,
// Boolean updateInProgress) {
// this.frame = frame;
// this.redrawSleep = 1000 / framesPerSecond;
// this.start();
// // this.updateInProgress = updateInProgress;
// }
//
// @Override
// public void run() {
// startTime = System.currentTimeMillis();
// while (!stop) {
// try {
// frame.repaint();
// while (System.currentTimeMillis() < startTime + redrawSleep) {
// Thread.sleep(1);
// }
// } catch (InterruptedException e) {
// }
// startTime += redrawSleep;
// }
// }
//
// public void terminate() {
// stop = true;
// interrupt();
// }
// }

/**
 * The thread keeping track of updating both the camera and the game logic.
 * Compensates for lost time by setting one start time and updating until the
 * quota has been fulfilled.
 * 
 * @author Florian
 *
 */
class UpdateThread extends Thread {
	boolean stop;
	PowerRacerGame game;
	int updateSleep;
	private long startTime;
	// private Boolean updateInProgress;
	JFrame frame;

	public UpdateThread(PowerRacerGame game, int updateSleep, JFrame frame) {
		this.game = game;
		this.updateSleep = updateSleep;
		// this.updateInProgress = updateInProgress;
		this.frame = frame;
		this.start();
	}

	@Override
	public void run() {
		startTime = System.currentTimeMillis();
		while (!stop) {
			try {
				game.update();
				frame.repaint();
				while (System.currentTimeMillis() - startTime < updateSleep) {
					Thread.sleep(1);
				}
			} catch (InterruptedException e) {
			}
			startTime += updateSleep;
		}
	}

	public void terminate() {
		stop = true;
		interrupt();
	}
}