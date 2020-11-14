package test;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import shared.game.model.TileModel;
import shared.game.presets.PresetTiles;
import shared.io.InternalEntityLoader;

/**
 * TODO: Write JavaDoc
 *
 * @author loris.sauter
 */
public class TileLoadingTest {

  @Test
  public void testTileLoadingSimple() throws IOException {
    ObjectMapper om = new ObjectMapper();
    TileModel model = om.readValue(new File("src/main/resources/data/tiles/sand.json"), TileModel.class);

    Assert.assertEquals(PresetTiles.SAND, model);
  }

  @Test
  public void testTileLoadingEntityLoaderAsphalt() throws IOException {
    TileModel model = InternalEntityLoader.getInstance().loadTile("asphalt.json");

    Assert.assertEquals(PresetTiles.ASPHALT, model);
  }

}
