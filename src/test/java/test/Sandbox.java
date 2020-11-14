package test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.File;
import java.io.IOException;
import shared.game.PresetTracks;

/**
 * TODO: Write JavaDoc
 *
 * @author loris.sauter
 */
public class Sandbox {

  public static void main(String[] args) throws IOException {
    ObjectMapper om = new ObjectMapper();
    om.enable(SerializationFeature.INDENT_OUTPUT);

    om.writeValue(new File("track-dbis.json"), PresetTracks.DBIS_MODEL);
  }

}
