package shared.io;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.FileFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.swing.ImageIcon;
import shared.game.model.RaceTrackModel;
import shared.game.model.TileModel;

/**
 * Interface for classes loading entities
 *
 * @author loris.sauter
 */
public interface EntityLoader {

  String JSON_EXTENSION = "json";
  String PNG_EXTENSION = "png";
  String RACETRACK_FOLDER = "tracks";
  String TILE_FOLDER = "tiles";
  String SPRITES_FOLDER = "images";

  ObjectMapper OM = new ObjectMapper();
  FileFilter JSON_FILTER = pathname -> pathname.canRead() && pathname.getAbsolutePath().endsWith(
      JSON_EXTENSION);
  Predicate<Path> JSON_PATH_FILTER = path -> path.toString().endsWith(JSON_EXTENSION);

  /**
   * Loads the {@link RaceTrackModel} from the given path, relative to the {@link
   * EntityLoader#basePath()}
   *
   * @param path The relative path to the {@link RaceTrackModel} to load. The actual path is
   * resolved as {@link EntityLoader#basePath()} + {@link EntityLoader#RACETRACK_FOLDER} + path
   * @return The loaded {@link RaceTrackModel}
   * @throws IllegalArgumentException If the given path did not lead to a proper {@link
   * RaceTrackModel}
   * @throws IOException If something went wrong during loading
   */
  default RaceTrackModel loadRacetrack(String path) throws IllegalArgumentException, IOException {
    return OM.readValue(resolveRaceTrack(path).toUri().toURL(), RaceTrackModel.class);
  }

  default TileModel loadTile(String path) throws IllegalArgumentException, IOException {
    return OM.readValue(resolveTile(path).toUri().toURL(), TileModel.class);
  }

  /**
   * Load all {@link RaceTrackModel} the {@link EntityLoader} has access to.
   *
   * @return All tracks the entity loader has access to, or an empty list if none were found
   */
  default List<RaceTrackModel> loadRaceTracks() throws IOException {
    if (Files.exists(raceTrackFolder())) {
      return Files.list(raceTrackFolder()).filter(JSON_PATH_FILTER).map(f -> {
        try {
          return loadRacetrack(f.getFileName().toString());
        } catch (IOException e) {
          throw new RuntimeException("Error while loading " + f, e);
        }
      }).collect(
          Collectors.toList());

    } else {
      return Collections.emptyList();
    }
  }

  default List<TileModel> loadTiles() throws IOException {
    if (Files.exists(tileFolder())) {
      return Files.list(tileFolder()).filter(JSON_PATH_FILTER).map(f -> {
        try {
          return loadTile(f.getFileName().toString());
        } catch (IOException e) {
          throw new RuntimeException("Error while loading " + f, e);
        }
      }).collect(Collectors.toList());

    } else {
      return Collections.emptyList();
    }
  }

  /**
   * Returns the base path of this entity loader. It will laod entities relative to this path
   *
   * @return The base path of this entity loader.
   */
  Path basePath();

  Path spritesBasePath();

  /**
   * Resolves the given racetrack path
   */
  default Path resolveRaceTrack(String path) {
    return raceTrackFolder().resolve(path);
  }

  default Path resolveTile(String path) {
    return tileFolder().resolve(path);
  }

  default Path raceTrackFolder() {
    return basePath().resolve(RACETRACK_FOLDER);
  }

  default Path tileFolder() {
    return basePath().resolve(TILE_FOLDER);
  }

  default Path resolveSprite(String path){
    return spritesBasePath().resolve(path);
  }

  default ImageIcon loadSprite(String name){
    try {
      return new ImageIcon(resolveSprite(name+"."+PNG_EXTENSION).toUri().toURL());
    } catch (MalformedURLException e) {
      throw new RuntimeException("Could not load sprite "+name, e);
    }
  }

}
