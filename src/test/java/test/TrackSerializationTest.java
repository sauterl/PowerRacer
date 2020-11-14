package test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.File;
import java.io.IOException;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import shared.game.presets.PresetTracks;
import shared.game.model.RaceTrackModel;
import shared.io.EntityManager;
import shared.io.InternalEntityLoader;
import shared.io.ModEntityLoader;

/**
 * TODO: Write JavaDoc
 *
 * @author loris.sauter
 */
public class TrackSerializationTest {

  @Test
  public void testMarshallUnMarshalling() throws IOException {
    ObjectMapper om = new ObjectMapper();
    om.enable(SerializationFeature.INDENT_OUTPUT);

    File f = new File("track-small.json");

    om.writeValue(f, PresetTracks.SMALL_MODEL);

    RaceTrackModel model = om.readValue(f, RaceTrackModel.class);

    Assert.assertEquals(PresetTracks.SMALL_MODEL, model);
  }

  @Test
  public void testInternalEntityLoaderForSmallTrack() throws IOException {
    RaceTrackModel model = InternalEntityLoader.getInstance().loadRacetrack("track-small.json");

    Assert.assertEquals(PresetTracks.SMALL_MODEL, model);
  }

  @Test
  public void testInternalEntityLoaderFindAllFourDefaultTracks() throws IOException {
    List<RaceTrackModel> tracks = InternalEntityLoader.getInstance().loadRaceTracks();
    Assert.assertEquals(4, tracks.size());
  }

  @Test
  public void testModEntityLoaderForDBISTrack() throws IOException{
    RaceTrackModel model = ModEntityLoader.getInstance().loadRacetrack("track-dbis.json");

    Assert.assertEquals(PresetTracks.DBIS_MODEL, model);
  }

  @Test
  public void testEntityManagerFoundAll(){
    EntityManager.getInstance().loadTracks();
    Assert.assertEquals(5, EntityManager.getInstance().getTracks().size());
  }

}
