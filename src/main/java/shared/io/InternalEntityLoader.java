package shared.io;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemAlreadyExistsException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import shared.game.model.RaceTrackModel;

/**
 * {@link EntityLoader} for internal (i.e. within JAR) / "vanilla" data.
 *
 * @author loris.sauter
 */
public class InternalEntityLoader implements EntityLoader{

  private static InternalEntityLoader instance;

  public static InternalEntityLoader getInstance(){
    if(instance == null){
      instance = new InternalEntityLoader();
    }
    return instance;
  }

  private InternalEntityLoader(){
    // Singleton constructor
//    try {
//      basePath = new File(getClass().getClassLoader().getResource("data").toURI());
//    } catch (URISyntaxException e) {
//      throw new RuntimeException("Couldn't setup basepath", e);
//    }
    basePath = null;
  }


  @Override
  public RaceTrackModel loadRacetrack(String path) throws IllegalArgumentException, IOException {
    return OM.readValue(resolveRaceTrack(path).toUri().toURL(), RaceTrackModel.class);
  }

  @Override
  public Path raceTrackFolder() {
    String targetFolder = "data/tracks";
    try {
      URI uri = getClass().getClassLoader().getResource(targetFolder).toURI();
      Path folder;
      if(uri.getScheme().equals("jar")){
        FileSystem fs;
        try{
          fs = FileSystems.newFileSystem(uri, Collections.<String, Object>emptyMap());
        }catch(FileSystemAlreadyExistsException ex){
          fs = FileSystems.getFileSystem(uri);
        }
        folder = fs.getPath(targetFolder);
      }else{
        folder = Paths.get(uri);
      }
      return folder;
    } catch (URISyntaxException | IOException e) {
      throw new RuntimeException("Couldn't provide racetrack folder", e);
    }
  }

  @Override
  public Path basePath() {
    return basePath;
  }

  private final Path basePath;
  private static final ObjectMapper OM = new ObjectMapper();
}
