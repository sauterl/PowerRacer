package shared.io;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import shared.game.model.RaceTrackModel;

/**
 * Interface for classes loading entities
 *
 * @author loris.sauter
 */
public interface EntityLoader {

  String EXTENSION = "json";
  String RACETRACK_FOLDER = "tracks";
  String TILE_FOLDER = "tiles";
  ObjectMapper OM = new ObjectMapper();
  FileFilter JSON_FILTER = pathname -> pathname.canRead() && pathname.getAbsolutePath().endsWith(EXTENSION);
  Predicate<Path> JSON_PATH_FILTER = path -> path.toString().endsWith(EXTENSION);

  /**
   * Loads the {@link RaceTrackModel} from the given path, relative to the {@link
   * EntityLoader#basePath()}
   *
   * @param path The relative path to the {@link RaceTrackModel} to load. The actual path is resolved as {@link EntityLoader#basePath()} + {@link EntityLoader#RACETRACK_FOLDER} + path
   * @return The loaded {@link RaceTrackModel}
   * @throws IllegalArgumentException If the given path did not lead to a proper {@link
   * RaceTrackModel}
   * @throws IOException If something went wrong during loading
   */
  default RaceTrackModel loadRacetrack(String path) throws IllegalArgumentException, IOException{
    return OM.readValue(resolveRaceTrack(path).toUri().toURL(), RaceTrackModel.class);
  }

  /**
   * Load all {@link RaceTrackModel} the {@link EntityLoader} has access to.
   * @return All tracks the entity loader has access to, or an empty list if none were found
   * @throws IOException
   */
  default List<RaceTrackModel> loadRaceTracks() throws IOException {
    return Files.list(raceTrackFolder()).filter(JSON_PATH_FILTER).map(f -> {
      try {
        return loadRacetrack(f.getFileName().toString());
      } catch (IOException e) {
        throw new RuntimeException("Error while loading "+f, e);
      }
    }).collect(
        Collectors.toList());
  }

  /**
   * Returns the base path of this entity loader. It will laod entities relative to this path
   *
   * @return The base path of this entity loader.
   */
  Path basePath();

  /**
   * Resolves the given racetrack path
   * @param path
   * @return
   */
  default Path resolveRaceTrack(String path){
    return raceTrackFolder().resolve(path);
  }

  default Path raceTrackFolder(){
    return basePath().resolve(RACETRACK_FOLDER);
  }



}
