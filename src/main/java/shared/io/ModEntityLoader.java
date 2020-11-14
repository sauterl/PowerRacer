package shared.io;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Loads modded entities. Basically whatever is found in {@code <cwd>/mods}
 *
 * <p>
 *   <h1>A word about modding</h1>
 *   Remember to mirror what is modded on the serverside to all clients!
 * </p>
 * @author loris.sauter
 */
public class ModEntityLoader implements EntityLoader {

  private static ModEntityLoader instance;
  private final Path basePath;


  private ModEntityLoader() {
    basePath = Paths.get("mods");
  }

  public static ModEntityLoader getInstance() {
    if (instance == null) {
      instance = new ModEntityLoader();
    }
    return instance;
  }

  @Override
  public Path basePath() {
    return basePath;
  }


}
