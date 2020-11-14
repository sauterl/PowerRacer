package test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import shared.game.model.RaceTrackModel;
import shared.game.presets.PresetTiles;

/**
 * TODO: Write JavaDoc
 *
 * @author loris.sauter
 */
public class Sandbox {

  public static void main(String[] args) throws IOException {
    ObjectMapper om = new ObjectMapper();
    om.enable(SerializationFeature.INDENT_OUTPUT);

    serialiseAllTiles(om);
    //serialiseTrack(om);
  }

  public static void serialiseTrack(ObjectMapper om) throws IOException {
    om.writeValue(new File("mods/tracks/track-test.json"), createOtherTrack());
  }

  public static void serialiseAllTiles(ObjectMapper om) throws IOException{
    Arrays.stream(PresetTiles.TILES).forEach( tile -> {
          try {
            om.writeValue(new File("src/main/resources/data/tiles/"+tile.getFileName()+".json"), tile);
          } catch (IOException e) {
            throw new RuntimeException("Error during serialisation", e);
          }
        }
    );
  }

  private static RaceTrackModel createOtherTrack() {
    byte[][] map = {{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 1, 1, 1, 1, 1, 7, 1, 1, 1, 1, 1, 1, 1, 0, 0},
        {0, 1, 1, 1, 1, 1, 7, 1, 1, 1, 1, 1, 1, 1, 0, 0},
        {0, 1, 1, 1, 1, 1, 7, 1, 1, 1, 1, 1, 1, 1, 0, 0},
        {0, 1, 1, 1, 1, 1, 29, 1, 1, 1, 1, 1, 1, 1, 0, 0},
        {0, 1, 1, 1, 1, 1, 29, 1, 1, 1, 1, 1, 1, 1, 0, 0},
        {0, 1, 1, 1, 1, 1, 29, 1, 1, 1, 1, 1, 1, 1, 0, 0},
        {0, 1, 1, 1, 1, 1, 29, 1, 1, 1, 1, 1, 1, 1, 0, 0},
        {0, 1, 1, 1, 1, 1, 29, 1, 1, 1, 1, 1, 1, 1, 0, 0},
        {0, 1, 1, 1, 1, 1, 29, 1, 1, 1, 1, 1, 1, 1, 0, 0},
        {0, 1, 1, 1, 1, 1, 4, 1, 1, 1, 1, 1, 1, 1, 0, 0},
        {0, 1, 1, 1, 1, 1, 4, 1, 1, 1, 1, 1, 1, 1, 0, 0},
        {0, 1, 1, 1, 1, 1, 4, 1, 1, 1, 1, 1, 1, 1, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}};

    return new RaceTrackModel(
        "Test Drive",
        (byte) 0,
        new int[][]{{3, 7}, {5, 7}, {3, 9},
            {5, 9}},
        null, 1, 5,
        map,
        (byte) 5
    );
  }


}
