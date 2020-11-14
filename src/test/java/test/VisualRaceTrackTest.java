package test;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import client.gui.RandomMapGenerator;
import shared.game.PresetTracks;
import shared.game.VisualRaceTrack;

public class VisualRaceTrackTest {

	static VisualRaceTrack[] tracks;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		VisualRaceTrack.randomString = RandomMapGenerator
				.trackToString(new RandomMapGenerator(64));

		tracks = new VisualRaceTrack[PresetTracks.PRESET_TRACKS.length];
		for (byte b = 0; b < tracks.length; b++) {
			tracks[b] = new VisualRaceTrack(PresetTracks.presetFromId(b));
		}
	}

	@Test
	public void testGetCarImage() {
		for (VisualRaceTrack track : tracks) {
			for (int i = 0; i < 20; i++) {
				assertNotNull("Car Image is null!", track.getCarImage(i));
			}
		}
	}

	@Test
	public void testGetTileImage() {
		for (VisualRaceTrack track : tracks) {
			for (int i = 0; i < track.numberOfTiles(); i++) {
				assertNotNull("Tile Image " + i + " is null!",
						track.getTileImage(i));
			}
		}
	}

	@Test
	public void testGetTile() {
		for (VisualRaceTrack track : tracks) {
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
		for (VisualRaceTrack track : tracks) {
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
		for (VisualRaceTrack track : tracks) {
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
		for (VisualRaceTrack track : tracks) {
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
		for (VisualRaceTrack track : tracks) {
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
			for (VisualRaceTrack track : tracks) {
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
		for (VisualRaceTrack track : tracks) {
			assertNotNull("Default tile Image is null!", track.getDefaultTile());
		}
	}

	@Test
	public void testGetDefaultTileFriction() {
		try {
			for (VisualRaceTrack track : tracks) {
				track.getDefaultTile();
			}
		} catch (Exception e) {
			fail("Exception when accessing default tile friction!");
		}
	}

}
