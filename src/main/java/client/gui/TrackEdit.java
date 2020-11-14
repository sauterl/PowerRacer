package client.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import shared.game.presets.PresetTracks;
import shared.game.VisualRaceTrack;

/**
 * @author Florian
 *
 */
public class TrackEdit extends Component {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	JFrame frame;
	VisualRaceTrack track;
	int x, y, scroll, select, magnification, mx, my, targetX, targetY,
			defaultTile;
	byte[][] tileMap;
	boolean shift, shiftToggle, targetSet, c;

	public static void main(String[] args) throws InterruptedException {

		Scanner scan = new Scanner(System.in);
		boolean inputOk = false;
		int width = 0, height = 0;

		System.out.println("Please enter track size as: x,y");
		while (!inputOk) {
			String line = scan.nextLine();
			try {
				width = Integer.parseInt(line.split(",")[0]);
				height = Integer.parseInt(line.split(",")[1]);
				inputOk = true;
			} catch (NumberFormatException e) {
				System.out.println("Invalid input!");
			} catch (IndexOutOfBoundsException e) {
				System.out.println("Invalid input!");
			}
		}

		scan.close();
		TrackEdit trackEdit = new TrackEdit(width, height);

		while (true) {
			trackEdit.repaint();
			Thread.sleep(100);
		}
	}

	TrackEdit(int width, int height) {

		frame = new JFrame("Track Editor");
		frame.getContentPane().add(this);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1280, 720);
		// frame.pack();
		frame.setResizable(true);
		frame.setMinimumSize(new Dimension(200, 100));
		frame.getContentPane().setBackground(Color.WHITE);
		frame.addKeyListener(new AL());
		this.addMouseListener(new ML());
		this.addMouseWheelListener(new MWL());

		track = new VisualRaceTrack(PresetTracks.SMALL_MODEL);

		tileMap = new byte[width][height];
		magnification = 2;
		// tileMap = PresetTracks.RACETRACK_SMALL;
		// defaultTile = 23;//ice
		defaultTile = 2;// sand

		frame.setVisible(true);
	}

	/*
	 * Constructor for the random map generator, the testmap gets passed from
	 * other class and tileMap is set as testmap
	 */
	TrackEdit(int width, int height, byte[][] testMap) {

		frame = new JFrame("Track Editor");
		frame.getContentPane().add(this);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1280, 720);
		// frame.pack();
		frame.setResizable(true);
		frame.setMinimumSize(new Dimension(200, 100));
		frame.getContentPane().setBackground(Color.WHITE);
		frame.addKeyListener(new AL());
		this.addMouseListener(new ML());
		this.addMouseWheelListener(new MWL());
		track = new VisualRaceTrack(PresetTracks.SMALL_MODEL);
		tileMap = new byte[width][height];
		magnification = 1;
		tileMap = testMap;

		frame.setVisible(true);
	}

	@Override
	public void paint(Graphics g) {

		Image saveImage = frame.createImage(
				tileMap.length * magnification * 16, tileMap[0].length
						* magnification * 16);
		Graphics sg = saveImage.getGraphics();
		for (int i = 0; i < tileMap.length; i++) {
			for (int j = 0; j < tileMap[0].length; j++) {
				try {
					sg.drawImage(track.getTileImage(defaultTile), magnification
							* 16 * i, magnification * 16 * j, magnification
							* 16 * i + magnification * 16, magnification * 16
							* j + magnification * 16, 0, 0, 16, 16, null);
					sg.drawImage(track.getTileImage(tileMap[i][j]),
							magnification * 16 * i, magnification * 16 * j,
							magnification * 16 * i + magnification * 16,
							magnification * 16 * j + magnification * 16, 0, 0,
							16, 16, null);
				} catch (NullPointerException e) {

				}
			}
		}
		g.drawImage(saveImage, 36 + x, y, null);
		if (c) {
			try {
				// retrieve image
				BufferedImage bi = (BufferedImage) saveImage;
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

		g.setColor(Color.DARK_GRAY);
		g.fillRect(0, 0, 36, frame.getHeight());
		g.setColor(Color.RED);
		g.fillRect(0, scroll + select * 36, 36, 36);
		g.setColor(Color.BLACK);
		g.fillRect(2, scroll + select * 36 + 2, 32, 32);

		for (int i = 0; i < track.numberOfTiles(); i++) {
			g.drawImage(track.getTileImage(i), 2, 2 + i * 36 + scroll, 2 + 32,
					2 + i * 36 + scroll + 32, 0, 0, 16, 16, null);
		}
	}

	// Listeners
	public class AL extends KeyAdapter {
		public void keyPressed(KeyEvent e) {
			int keyCode = e.getKeyCode();

			if (keyCode == KeyEvent.VK_UP) {
				y += magnification * 16;
			}
			if (keyCode == KeyEvent.VK_DOWN) {
				y -= magnification * 16;
			}
			if (keyCode == KeyEvent.VK_LEFT) {
				x += magnification * 16;
			}
			if (keyCode == KeyEvent.VK_RIGHT) {
				x -= magnification * 16;
			}
			if (keyCode == KeyEvent.VK_EQUALS || keyCode == KeyEvent.VK_NUMPAD0) {
				magnification++;
			}
			if (keyCode == KeyEvent.VK_MINUS && magnification > 1) {
				magnification--;
			}
			if (keyCode == KeyEvent.VK_P) {
				StringBuilder sb = new StringBuilder(tileMap.length
						* tileMap[0].length * 4);
				sb.append("{");
				String prefixi = "";
				for (int i = 0; i < tileMap.length; i++) {
					sb.append(prefixi);
					prefixi = ",";
					sb.append("{");
					String prefixj = "";
					for (int j = 0; j < tileMap[0].length; j++) {
						sb.append(prefixj);
						prefixj = ",";
						sb.append(tileMap[i][j]);
					}
					sb.append("}");
				}
				sb.append("}");
				System.out.println(sb);
			}
			if (keyCode == KeyEvent.VK_SHIFT) {
				if (!shiftToggle) {
					shiftToggle = true;
					if (shift) {
						shift = false;
					} else {
						shift = true;
					}
				}
			}
		}

		public void keyReleased(KeyEvent e) {
			int keyCode = e.getKeyCode();
			if (keyCode == KeyEvent.VK_SHIFT) {
				shiftToggle = false;
			}
			if (keyCode == KeyEvent.VK_C) {
				c = true;
			}
		}
	}

	public class ML extends MouseAdapter {
		public void mousePressed(MouseEvent e) {
			mx = e.getX();
			my = e.getY();
			if (mx < 36) {
				if ((my - scroll) / 36 < track.numberOfTiles()) {
					select = (my - scroll) / 36;
				}
			} else {
				if ((mx - 36 - x) / (magnification * 16) < tileMap.length
						&& (mx - 36 - x) / (magnification * 16) >= 0
						&& (my - y) / (magnification * 16) < tileMap[0].length
						&& (my - y) / (magnification * 16) >= 0) {
					tileMap[(mx - 36 - x) / (magnification * 16)][(my - y)
							/ (magnification * 16)] = (byte) select;
					System.out.println((mx - 36 - x) / (magnification * 16)
							+ "," + (my - y) / (magnification * 16));

					targetX = (mx - 36 - x) / (magnification * 16);
					targetY = (my - y) / (magnification * 16);
					targetSet = true;

				}
			}
		}

		public void mouseReleased(MouseEvent e) {
			mx = e.getX();
			my = e.getY();
			if (mx < 36) {

			} else {
				if ((mx - 36 - x) / (magnification * 16) < tileMap.length
						&& (mx - 36 - x) / (magnification * 16) >= 0
						&& (my - y) / (magnification * 16) < tileMap[0].length
						&& (my - y) / (magnification * 16) >= 0) {

					if (targetSet) {
						int minX, minY, maxX, maxY;
						if (targetX > (mx - 36 - x) / (magnification * 16)) {
							minX = (mx - 36 - x) / (magnification * 16);
							maxX = targetX;
						} else {
							maxX = (mx - 36 - x) / (magnification * 16);
							minX = targetX;
						}
						if (targetY > (my - y) / (magnification * 16)) {
							minY = (my - y) / (magnification * 16);
							maxY = targetY;
						} else {
							maxY = (my - y) / (magnification * 16);
							minY = targetY;
						}
						for (int i = minX; i <= maxX; i++) {
							for (int j = minY; j <= maxY; j++) {
								tileMap[i][j] = (byte) select;
							}
						}
						targetSet = false;
					}

				}
			}
			targetSet = false;
		}
	}

	public class MWL implements MouseWheelListener {
		public void mouseWheelMoved(MouseWheelEvent e) {
			int rot = -e.getWheelRotation();
			if (scroll + rot <= 0) {
				scroll += (rot*2);
			} else {
				scroll = 0;
			}
		}
	}
}
