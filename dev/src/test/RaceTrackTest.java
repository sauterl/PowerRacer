package test;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import client.gui.RandomMapGenerator;
import shared.game.RaceTrack;

public class RaceTrackTest {

	static RaceTrack[] tracks;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		RaceTrack.randomString = RandomMapGenerator
				.trackToString(new RandomMapGenerator(64));

		tracks = new RaceTrack[RaceTrack.TRACK_NAMES.length];
		for (byte b = 0; b < RaceTrack.TRACK_NAMES.length; b++) {
			tracks[b] = new RaceTrack(b);
		}
	}

	@Test
	public void testGetCarImage() {
		for (RaceTrack track : tracks) {
			for (int i = 0; i < 20; i++) {
				assertNotNull("Car Image is null!", track.getCarImage(i));
			}
		}
	}

	@Test
	public void testGetTileImage() {
		for (RaceTrack track : tracks) {
			for (int i = 0; i < track.numberOfTiles(); i++) {
				assertNotNull("Tile Image " + i + " is null!",
						track.getTileImage(i));
			}
		}
	}

	@Test
	public void testGetTile() {
		for (RaceTrack track : tracks) {
			for (int x = 0; x < track.getTrackWidth(); x++) {
				for (int y = 0; y < track.getTrackHeight(); y++) {
					assertNotNull("Tile " + x + " " + y + " is null!",
							track.getTile(x, y));
				}
			}
		}
	}

	@Test
	public void testGetSolid() {
		for (RaceTrack track : tracks) {
			for (int x = 0; x < track.getTrackWidth(); x++) {
				for (int y = 0; y < track.getTrackHeight(); y++) {
					try {
						track.getSolid(x, y);
					} catch (Exception e) {
						fail("Exception when accessing solid at " + x + " " + y);
					}
				}
			}
		}
	}

	@Test
	public void testGetTransparent() {
		for (RaceTrack track : tracks) {
			for (int x = 0; x < track.getTrackWidth(); x++) {
				for (int y = 0; y < track.getTrackHeight(); y++) {
					try {
						track.getTransparent(x, y);
					} catch (Exception e) {
						fail("Exception when accessing transparent at " + x
								+ " " + y);
					}
				}
			}
		}
	}

	@Test
	public void testGetCheckpointNumber() {
		for (RaceTrack track : tracks) {
			for (int x = 0; x < track.getTrackWidth(); x++) {
				for (int y = 0; y < track.getTrackHeight(); y++) {
					try {
						track.getCheckpointNumber(x, y);
					} catch (Exception e) {
						fail("Exception when accessing checkpoint number at "
								+ x + " " + y);
					}
				}
			}
		}
	}

	@Test
	public void testGetfrictionCoefficient() {
		for (RaceTrack track : tracks) {
			for (int x = 0; x < track.getTrackWidth(); x++) {
				for (int y = 0; y < track.getTrackHeight(); y++) {
					try {
						track.getfrictionCoefficient(x, y);
					} catch (Exception e) {
						fail("Exception when accessing friction coefficient at "
								+ x + " " + y);
					}
				}
			}
		}
	}

	@Test
	public void testGetStartingPositions() {
		try {
			for (RaceTrack track : tracks) {
				for (int i = 0; i < 4; i++) {
					track.getStartingPositionX(i);
					track.getStartingPositionY(i);
				}
			}
		} catch (Exception e) {
			fail("Exception when accessing starting positions!");
		}
	}

	@Test
	public void testGetDefaultTile() {
		for (RaceTrack track : tracks) {
			assertNotNull("Default tile Image is null!", track.getDefaultTile());
		}
	}

	@Test
	public void testGetDefaultTileFriction() {
		try {
			for (RaceTrack track : tracks) {
				track.getDefaultTile();
			}
		} catch (Exception e) {
			fail("Exception when accessing default tile friction!");
		}
	}

}
